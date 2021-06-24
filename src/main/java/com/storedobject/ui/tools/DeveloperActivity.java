package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DeveloperActivity extends DataForm {

    private final ObjectField<SystemUser> userField;
    private final DatePeriodField periodField = new DatePeriodField("Period");
    private final boolean developer;

    public DeveloperActivity() {
        this(true);
    }

    DeveloperActivity(boolean developer) {
        super(developer ? "Developer Activity" : "User Log");
        this.developer = developer;
        userField = new ObjectField<>((developer ? "Develop" : "Us") + "er (Leave it blank for all)",
                SystemUser.class);
        addField(userField, periodField);
    }

    @Override
    protected boolean process() {
        close();
        getApplication();
        new Report(userField.getObject(), periodField.getValue()).execute();
        return true;
    }

    private class Report extends PDFReport {

        private final AbstractPeriod<?> period;
        private final SystemUser su;

        public Report(SystemUser su, AbstractPeriod<?> period) {
            super(getApplication());
            this.su = su;
            this.period = period;
            setTitleText((developer ? "Developer Activity" : "User Log") + " for the Period " + period);
        }

        @Override
        public void generateContent() throws Exception {
            ObjectIterator<SystemUser> users;
            users = su == null ? StoredObject.list(SystemUser.class) : ObjectIterator.create(su);
            ObjectIterator<DeveloperLog> logs;
            for(SystemUser u: users) {
                if(!developer) {
                    printLog(u, true);
                    continue;
                }
                logs = StoredObject.list(DeveloperLog.class,
                        "Developer=" + u.getPersonId() + " AND ActionedAt" + period.getDBTimeCondition(),
                        "Developer,ActionedAt DESC");
                PDFTable table = createTable(35, 10, 10);
                Stream.of("Worked on", "Activity", "At").
                        forEach(t -> table.addCell(createCell(createTitleText(t)), c -> {
                            c.setGrayFill(0.9f);
                            return c;
                        }));
                table.setHeaderRows(1);
                int rows = 0;
                for(DeveloperLog log : logs) {
                    if(rows == 0) {
                        printUser(u);
                    }
                    table.addCell(createCell(trim(log.getSourceCode().getName(), 3)));
                    table.addCell(createCell(log.getActionValue()));
                    table.addCell(createCell(DateUtility.trimMillis(log.getActionedAt())));
                    if((++rows %  80) == 0) {
                        addTable(table);
                    }
                }
                if(rows > 0) {
                    addTable(table);
                    addGap(5);
                    printLog(u, false);
                }
            }
        }

        private String trim(String s, int count) {
            if(count <= 0) {
                return s;
            }
            int p = s.indexOf('.');
            return p < 0 ? s : trim(s.substring(p + 1), --count);
        }

        @Override
        public int getPageOrientation() {
            return ORIENTATION_LANDSCAPE;
        }

        private void printLog(SystemUser u, boolean printUser) {
            PDFTable table = createTable(20, 20, 20, 40);
            Stream.of("In", "Out", "IP Address", "Application").
                    forEach(t -> table.addCell(createCell(createTitleText(t)), c -> {
                        c.setGrayFill(0.9f);
                        return c;
                    }));
            table.setHeaderRows(1);
            AtomicInteger count = new AtomicInteger(0);
            u.getSessionLog(period).forEach(s -> {
                if(printUser && count.get() == 0) {
                    printUser(u);
                }
                count.incrementAndGet();
                table.addCell(createCell(DateUtility.trimMillis(s.getInTime())));
                Timestamp ts = s.getOutTime();
                if(ts != null) {
                    ts = DateUtility.trimMillis(ts);
                }
                table.addCell(createCell(Objects.requireNonNullElse(ts, "")));
                table.addCell(createCell(s.getIPAddress()));
                table.addCell(createCell(trim(s.getApplication())));
                if((count.get() % 80) == 0) {
                    addTable(table);
                }
            });
            if(count.get() > 0) {
                addTable(table);
            }
        }

        private String trim(String s) {
            if(s.length() < 60) {
                return s;
            }
            return s.substring(0, 57) + "...";
        }

        private void printUser(SystemUser su) {
            PDFTable table = createTable(1);
            table.addCell(createCenteredCell(
                    createTitleText((developer ? "Develop" : "Us") + "er: " + su.getPerson().getName() +
                            " (" + su.getLogin() + ")")),
                    c -> {
                        c.setGrayFill(0.9f);
                        return c;
                    });
            addGap(5);
            add(table);
            addGap(5);
        }
    }
}
