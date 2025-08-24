package com.storedobject.ui.iot;

import com.storedobject.common.MathUtility;
import com.storedobject.core.*;
import com.storedobject.iot.Block;
import com.storedobject.iot.Data;
import com.storedobject.ui.Application;
import com.storedobject.ui.common.ExcelDataUpload;
import com.storedobject.vaadin.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UploadData extends SelectData {

    private int timeDifference;

    public UploadData() {
        this(null);
    }

    public UploadData(Block block) {
        super("Upload Data", block, true, true);
    }

    @Override
    protected boolean accept(Block block) throws Exception {
        timeDifference = block.getSite().getTimeDifference();
        return super.accept(block);
    }

    @Override
    protected Consumer<Data4Unit> getProcessor() {
        return d4u -> new DataView<>(d4u, false) {
            @Override
            protected void addExtraButtons(ButtonLayout buttonLayout) {
                buttonLayout.add(new Button("Upload Data File", e -> {
                    close();
                    new UploadDataFile(getApplication(), d4u).execute();
                }));
            }
        }.execute();
    }

    private class UploadDataFile extends ExcelDataUpload {

        private final Application application;
        private final Data4Unit data4Unit;

        public UploadDataFile(Application application, Data4Unit data4Unit) {
            super("Upload Data File");
            this.application = application;
            this.data4Unit = data4Unit;
        }

        @Override
        protected void buildFields() {
            super.buildFields();
            TextArea notes = new TextArea("Notes:");
            notes.setText("""
                    File format: Excel
                    The file should not contain a header row.
                    The file should contain two columns.
                    The first column will be used as the timestamp in local time zone.
                    The second column will be used as the numeric data.
                    If the file contains more than two columns, those columns will be ignored.
                    """);
            notes.setReadOnly(true);
            add(notes);
            setDataBoundary("A1:B1");
            setProcessingMessage("You may now process the data...");
        }

        @Override
        protected void processData(ArrayList<Object[]> data) {
            convertData(0, Long.class);
            for(Object[] d: data) {
                d[0] = (long)d[0] - timeDifference;
            }
            convertData(1, Double.class);
            application.access(() -> application.download(new DownloadSQLFile(data4Unit, data)));
        }

        private class DownloadSQLFile extends TextContentProducer {

            private final Data4Unit data4Unit;
            private final List<Object[]> data;

            private DownloadSQLFile(Data4Unit data4Unit, List<Object[]> data) {
                this.data4Unit = data4Unit;
                this.data = data;
            }

            @Override
            public String getFileName() {
                return "IOT-Update";
            }

            @Override
            public String getFileExtension() {
                return "SQL";
            }

            @Override
            public void generateContent() {
                if(data.isEmpty()) {
                    return;
                }
                long lowerTime = (long) data.getFirst()[0];
                long upperTime = (long) data.getLast()[0];
                ClassAttribute<?> ca = ClassAttribute.get(data4Unit.dataClass());
                String tableName = ca.getModuleName() + "." + ca.getTableName();
                double currentValue, value;
                String attribute = data4Unit.attributes().stream().skip(1).findFirst().orElse("");
                attribute = attribute.substring(0, attribute.indexOf(" AS "));
                Method m = ca.getMethod(attribute);
                QueryBuilder<?> qb = QueryBuilder.from(data4Unit.dataClass()).where(data4Unit.condition())
                        .orderBy("CollectedAt");
                Data data;
                int count = 0, i;
                try (ObjectIterator<?> list = qb.list()) {
                    long timestamp, t;
                    for (StoredObject so : list) {
                        ++count;
                        data = (Data) so;
                        try {
                            timestamp = data.getCollectedAt();
                            currentValue = (double) m.invoke(data);
                            getWriter().write("-- Time: "
                                    + DateUtility.format(new Timestamp(data.getCollectedAt() + timeDifference))
                                    + ", Current Value: " + currentValue + "\n");
                            if(timestamp < lowerTime || timestamp > upperTime) {
                                getWriter().write("-- Timestamp outside the value range provided\n\n");
                                continue;
                            }
                            value = Double.MIN_VALUE;
                            for(i = 0; i < this.data.size(); i++) {
                                t = (long) this.data.get(i)[0];
                                if(t == timestamp) {
                                    value = (double) this.data.get(i)[1];
                                    break;
                                }
                                if(t > timestamp) {
                                    value = (double) this.data.get(i)[1];
                                    value += (double) this.data.get(i - 1)[1];
                                    value /= 2.0;
                                    break;
                                }
                            }
                            if (MathUtility.equals(currentValue, value)) {
                                getWriter().write("-- Value unchanged\n\n");
                                continue;
                            }
                            if(value == Double.MIN_VALUE) {
                                getWriter().write("-- Value not found for this timestamp!\n\n");
                            } else {
                                getWriter().write("UPDATE " + tableName + " SET " + attribute
                                        + " = " + value + " WHERE Id = " + data.getId() + ";\n\n");
                            }
                        } catch (Exception e) {
                            redMessage(e);
                            break;
                        }
                    }
                    blueMessage("Entries affected: " + count);
                }
            }
        }
    }
}
