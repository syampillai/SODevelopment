package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.office.Excel;
import com.storedobject.report.ObjectList;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.ui.common.ExcelDataUpload;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.apache.poi.ss.usermodel.Cell;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;

import static com.storedobject.core.EditorAction.*;

public class SystemUtility extends View implements CloseableView, Transactional {

    private final TextField select, where, orderBy;
    private final TextField rawCommand;
    private final Checkbox any;
    private final ClassNameField from;
    private final Button executeSQL;
    private final Button clear;
    private final Button pdf;
    private final Button downloadExcelData;
    private final Button downloadData;
    private final Button updateData;
    private final Button loadRaw;
    private final Button executeRaw;
    private final Button viewTranRaw;
    private final Button downloadConnInfo;
    private final LongField rawId;
    private final LongField rawTranId;
    private final boolean isAdmin;
    private Class<? extends StoredObject> objectClass;
    private final IntegerField connectionAge;
    private final TextArea speech;

    public SystemUtility() {
        super("Utilities");
        isAdmin = getTransactionManager().getUser().isAdmin();
        select = new TextField("SELECT");
        any = new Checkbox("ANY");
        from = new ClassNameField("FROM");
        from.setWidth("60em");
        where = new TextField("WHERE");
        orderBy = new TextField("ORDER BY");
        FormLayout form = new FormLayout();
        form.setColumns(1);
        form.add(label("Execute SQL"));
        form.add(select);
        form.add(any);
        form.add(from);
        form.add(where);
        form.add(orderBy);
        ButtonLayout buttons = new ButtonLayout();
        form.add(buttons);
        buttons.add(executeSQL = new Button("OK", this));
        buttons.add(clear = new Button("Clear", this));
        buttons.add(pdf = new Button("PDF", this));
        buttons.add(downloadExcelData = new Button("Excel", this));
        buttons.add(downloadData = new Button("Download", this));
        buttons.add(updateData = new ConfirmButton("Update", "lightbulb", this));
        form.add(label("Execute Logic"));
        form.add(rawCommand = new TextField("Command"));
        buttons = new ButtonLayout();
        buttons.add(executeRaw = new Button("Execute Command", this));
        form.add(buttons);
        form.add(label("View Object"));
        rawId = new LongField(0L, 10);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Raw Object Id: "), rawId, loadRaw = new Button("View", this));
        form.add(buttons);
        form.add(label("View Transaction"));
        rawTranId = new LongField(0L, 10);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Raw Transaction Id: "),
                rawTranId,
                viewTranRaw = new Button("View", this));
        form.add(buttons);
        form.add(label("Upload Data"));
        DataLoader dataLoader = new DataLoader(getApplication());
        UploadField dataUploadField = new UploadField(dataLoader::process);
        dataUploadField.getUploadComponent().setMaxFileSize(100000000);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Excel Data: "),
                new Button("Excel", e -> new ExcelDataUpload().execute()),
                new ELabel("Raw Data: "),
                dataUploadField);
        form.add(buttons);
        form.add(label("Debug SQL Connections"));
        buttons = new ButtonLayout();
        BooleanField connectionDebug;
        buttons.add(new ELabel("Debug"), connectionDebug = new BooleanField(SQLConnector.debug));
        connectionDebug.addValueChangeListener(e -> SQLConnector.debug = e.getValue());
        buttons.add(new ELabel("Age in Minutes:"), connectionAge = new IntegerField(5, 4));
        buttons.add(downloadConnInfo = new Button("Download", this));
        form.add(buttons);
        form.add(label("Miscellaneous"));
        speech = new TextArea("Text to Speak out");
        form.add(speech);
        buttons = new ButtonLayout();
        buttons.add(new Button("Speak out", VaadinIcon.VOLUME_DOWN, e -> speakOut()));
        form.add(buttons);
        ELabelField appDetails = new ELabelField("Application Details");
        Application.get().information(appDetails);
        form.add(appDetails);
        setComponent(form);
        setFirstFocus(from);
    }

    private void speakOut() {
        if(!getApplication().isSpeakerOn()) {
            warning("Please toggle speaker output");
            return;
        }
        speak(speech.getValue());
    }

    private H3 label(String label) {
        return new H3(label);
    }

    private int browserActions() {
        return (isAdmin ? DELETE : 0) | VIEW | PDF | AUDIT | (any.getValue() ? ALLOW_ANY : 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return super.getApplication();
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "resource" })
    @Override
    public void clicked(Component c) {
        if(c == downloadConnInfo) {
            TextContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() {
                    Writer w = getWriter();
                    SQLConnector.getDebugInfo(connectionAge.getValue()).forEach(d -> {
                        try {
                            w.write(d);
                            w.write("\n\n");
                        } catch (IOException ignored) {
                        }
                    });
                }
            };
            getApplication().view(cp);
            return;
        }
        if(c == clear) {
            select.setValue("");
            where.setValue("");
            orderBy.setValue("");
            from.focus();
            return;
        }
        if(c == loadRaw) {
            Id id = new Id(new BigInteger("" + rawId.getValue()));
            StoredObject so = StoredObject.get(id);
            if(so == null) {
                warning("No object found for Id = " + id);
                return;
            }
            if(so instanceof StreamData) {
                getApplication().view("Id " + so.getId() + ", Transaction " + so.getTransactionId(), (StreamData)so);
                return;
            }
            getApplication().view(StringUtility.makeLabel(so.getClass()) + " (Id " + so.getId() + ", Transaction " + so.getTransactionId() + ")", so);
            return;
        }
        if(c == viewTranRaw) {
            long t = rawTranId.getValue();
            TransactionInformation ti = TransactionInformation.get(new BigInteger("" + t));
            if(ti == null) {
                warning("Transaction not found: " + t);
                return;
            }
            StringBuilder s = new StringBuilder("Transaction: ");
            s.append(t).append("\n");
            ti.dump(s);
            TextArea ta = new TextArea();
            ta.setWidthFull();
            ta.setValue(s.toString());
            View.createCloseableView(ta, "Transaction " + t).execute();
            return;
        }
        if(c == executeRaw) {
            String command = rawCommand.getValue().trim();
            getApplication().getServer().execute(command);
            return;
        }
        objectClass = from.getObjectClass();
        if(objectClass == null) {
            error("Class not found: " + from.getValue());
            return;
        }
        if(c == downloadData) {
            final Class<? extends StoredObject> clazz = objectClass;
            TextContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    Writer w = getWriter();
                    for(StoredObject so: StoredObject.list(clazz, where.getValue().trim(), any.getValue())) {
                        so.save(w);
                    }
                }
            };
            getApplication().view(cp);
            return;
        }
        if(c == updateData) {
            new Update().execute();
            return;
        }
        String cols = select.getValue().trim();
        if(c == executeSQL) {
            StringList columns;
            if(cols.isEmpty()) {
                columns = StoredObjectUtility.browseColumns(objectClass);
                select.setValue(columns.toString(", "));
            } else {
                if(cols.startsWith("/")) {
                    new QueryGrid(StoredObject.query(objectClass, cols)).execute();
                    return;
                }
                columns = StringList.create(cols);
            }
            Browser b = new Browser(objectClass, columns);
            b.load(where.getValue().trim(), orderBy.getValue().trim());
            b.execute();
            return;
        }
        if(c == pdf) {
            new ObjectList(getApplication(), objectClass, any.getValue(),
                    cols.startsWith("/") ? StringList.EMPTY : StringList.create(cols)) {

                @Override
                public String getOrderBy() {
                    return orderBy.getValue().trim();
                }

                @Override
                public String getExtraCondition() {
                    return where.getValue();
                }
            }.execute();
            return;
        }
        if(c == downloadExcelData) {
            if(cols.isEmpty()) {
                ClassAttribute<? extends StoredObject> ca = StoredObjectUtility.classAttribute(objectClass);
                StringBuilder sb = new StringBuilder();
                assert ca != null;
                for(String s: ca.getAttributes()) {
                    sb.append(",").append(s);
                }
                cols = sb.substring(1);
                select.setValue(cols);
            }
            Query query = StoredObject.query(objectClass, cols, where.getValue().trim(), orderBy.getValue().trim(), any.getValue());
            if(cols.startsWith("/")) {
                cols = cols.substring(1);
            }
            StringList columns = StringList.create(cols);
            Excel excel = new Excel() {
                @Override
                public void generateContent() throws Exception {
                    columns.forEach(c -> setCellValue(getNextCell(), c));
                    getNextRow();
                    Cell cell;
                    Object value;
                    boolean first = true;
                    for(ResultSet rs: query) {
                        int colCount = rs.getMetaData().getColumnCount();
                        for(int c = 1; c <= colCount; c++) {
                            cell = getNextCell();
                            setCellValue(cell, value = rs.getObject(c));
                            if(Utility.isRightAligned(value)) {
                                cell.setCellStyle(getRightAlignedStyle());
                                if(first) {
                                    getCell(getCellIndex(), getRowIndex() - 1).setCellStyle(getRightAlignedStyle());
                                }
                            }
                        }
                        first = false;
                        getNextRow();
                    }
                    workbook.setSheetName(0, StringUtility.makeLabel(objectClass));
                }
            };
            getApplication().view(excel);
        }
    }

    class DataLoader implements Comparator<CharSequence> {

        private ObjectViewer view = null;
        private final Application a;

        private DataLoader(Application a) {
            this.a = a;
        }

        public void process(InputStream in, @SuppressWarnings("unused") String mimeType) {
            try {
                int count = StoredObject.load(getApplication().getTransactionManager(), in, this);
                a.access(() -> Application.message("Entries created: " + count));
            } catch (Exception e) {
                a.access(() -> Application.error(e));
            }
        }

        @Override
        public int compare(CharSequence o1, CharSequence o2) {
            if(view == null) {
                a.access(() -> {
                   view = new ObjectViewer();
                   view.execute();
                });
            }
            view.setValues(o1, o2);
            while(view.getDecision() < 0) {
                Thread.yield();
            }
            return view.getDecision();
        }
    }

    static class ObjectViewer extends View {

        private final TextArea left, right;
        private final Button yes;
        private final Button no;
        private int decision = -1;

        public ObjectViewer() {
            super("Verify");
            left = new TextArea();
            left.setEnabled(false);
            right = new TextArea();
            right.setEnabled(false);
            Div g = new Div();
            ButtonLayout b = new ButtonLayout();
            b.add(new ELabel("Are these same?"));
            b.add(no = new Button("No", this));
            no.setEnabled(false);
            b.add(yes = new Button("Yes", this));
            yes.setEnabled(false);
            g.add(b);
            SplitLayout hp = new SplitLayout(left, right);
            hp.setOrientation(SplitLayout.Orientation.HORIZONTAL);
            g.add(hp);
            setComponent(g);
        }

        @Override
        public void clicked(Component c) {
            if(c == no) {
                decision = 1;
            } else {
                decision = 0;
            }
            no.setEnabled(false);
            yes.setEnabled(false);
        }

        protected void setValues(CharSequence o1, CharSequence o2) {
            decision = -1;
            left.setValue(o1.toString());
            right.setValue(o2.toString());
            yes.setEnabled(true);
            no.setEnabled(true);
        }

        protected int getDecision() {
            return decision;
        }
    }

    private static String className(Object object) {
        return className(object.getClass());
    }

    private static String className(Class<?> klass) {
        String s = klass.getName();
        boolean core = s.startsWith("com.storedobject.");
        if(!core && StringUtility.getCharCount(s, '.') <= 3) {
            return s;
        }
        s = s.substring(s.indexOf('.') + 1);
        s = s.substring(s.indexOf('.') + 1);
        if(!core) {
            s = s.substring(s.indexOf('.') + 1);
        }
        return s;
    }

    private class Browser<T extends StoredObject> extends ObjectBrowser<T> {

        private Button links, masters, count;

        private Browser(Class<T> objectClass, StringList columns) {
            super(objectClass, columns, browserActions(), className(objectClass));
        }

        private Browser(Class<T> objectClass) {
            super(objectClass, (isAdmin ? DELETE : 0) | VIEW | PDF | AUDIT, className(objectClass));
        }

        @Override
        protected void createExtraButtons() {
            links = new Button("Links", "children", this);
            masters = new Button("Masters", "angle-double-up", this);
            count = new Button("Entry Count", "hash", e -> message("Entries: " + size()));
        }

        @Override
        protected void addExtraButtons() {
            buttonPanel.add(links);
            buttonPanel.add(masters);
            buttonPanel.add(count);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void clicked(Component c) {
            if(c == links || c == masters) {
                T selected = getSelected();
                if(selected == null) {
                    return;
                }
                ArrayList<Integer> types = new ArrayList<>();
                ObjectIterator<StoredObject> objects;
                if(c == links) {
                    objects = selected.listLinks(StoredObject.class, true);
                } else {
                    objects = selected.listMasters(StoredObject.class, true);
                }
                Browser<StoredObject> b;
                int family, count = 0;
                for(StoredObject so: objects) {
                    ++count;
                    family = StoredObjectUtility.family(so.getClass());
                    if(types.contains(family)) {
                        continue;
                    }
                    types.add(family);
                    b = new Browser(so.getClass());
                    b.setCaption(className(so) + " - " + (c == links ? "Link" : "Master") + "s of " + selected.getId() + "/" + selected.getTransactionId() + " " + className(getObjectClass()));
                    if(c == links) {
                        b.setObjects((Iterable<StoredObject>)selected.listLinks(so.getClass()));
                    } else {
                        b.setObjects((Iterable<StoredObject>)selected.listMasters(so.getClass()));
                    }
                    b.execute();
                }
                if(count == 0) {
                    warning("No " + (c == links ? "links" : "masters") + " found");
                }
                return;
            }
            if(c == view) {
                T selected = getSelected();
                if(selected != null) {
                    getObjectEditor().setCaption(className(selected) + " Id " + selected.getId() + " Transaction " + selected.getTransactionId());
                }
            }
            super.clicked(c);
        }
    }

    private class Update extends DataForm {

        private RadioChoiceField batch;

        public Update() {
            super("Update Entries");
        }

        @Override
        protected void buildFields() {
            batch = new RadioChoiceField("Update Mode", new String[] { "Batch", "Entry by entry" });
            addField(batch);
            ok.setText("Proceed");
        }

        @Override
        protected boolean process() {
            boolean batch = this.batch.getValue() == 0;
            int count = 0;
            Transaction t = null;
            try (ObjectIterator<? extends StoredObject> list = StoredObject.list(objectClass, where.getValue().trim(),
                    orderBy.getValue().trim(), any.getValue())) {
                if (batch) {
                    t = getTransactionManager().createTransaction();
                }
                for (StoredObject so : list) {
                    if (!batch) {
                        t = getTransactionManager().createTransaction();
                    }
                    so.save(t);
                    if (!batch) {
                        t.commit();
                    }
                    ++count;
                }
                if (batch) {
                    t.commit();
                }
            } catch (Exception e) {
                if (t != null) {
                    t.rollback();
                }
                error(e);
            }
            tray("Entries updated: " + count);
            return true;
        }
    }
}
