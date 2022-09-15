package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.tools.ColumnDefinition;
import com.storedobject.tools.IndexDefinition;
import com.storedobject.tools.LinkDefinition;
import com.storedobject.tools.TableDefinition;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DeveloperActivity extends DataForm implements Transactional {

    private final ObjectField<SystemUser> userField;
    private final DatePeriodField periodField = new DatePeriodField("Period");
    private final TextField serverField = new TextField("Server");
    private final boolean developer;
    private final SystemUserGroup devGroup;
    private final TransactionManager tm;
    private final Set<Id> changedObjects = new HashSet<>();

    public DeveloperActivity() {
        this(true);
    }

    DeveloperActivity(boolean developer) {
        super(developer ? "Developer Activity" : "User Log");
        this.developer = developer;
        userField = new ObjectField<>((developer ? "Develop" : "Us") + "er (Leave it blank for all)",
                SystemUser.class);
        addField(userField, periodField, serverField);
        if(developer) {
            devGroup = StoredObject.list(SystemUserGroup.class)
                    .find(g -> g.getName().equalsIgnoreCase("Developer"));
        } else {
            devGroup = null;
        }
        tm = getTransactionManager();
    }

    @Override
    protected boolean process() {
        close();
        getApplication();
        //noinspection resource
        new Report(userField.getObject(), periodField.getValue(), serverField.getValue()).execute();
        return true;
    }

    private class Report extends PDFReport {

        private final AbstractPeriod<?> period;
        private final SystemUser su;
        private final String server;

        public Report(SystemUser su, AbstractPeriod<?> period, String server) {
            super(getApplication());
            this.su = su;
            this.period = period;
            this.server = server == null || server.isBlank() ? "" : server.trim();
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
                    table.addCell(createCell(DateUtility.trimMillis(tm.date(log.getActionedAt()))));
                    if((++rows %  80) == 0) {
                        add(table);
                    }
                }
                if(rows > 0 || isDev(u)) {
                    if(rows == 0) {
                        printUser(u);
                        table.addRowCell(createCell("_"));
                    }
                    add(table);
                    addGap(5);
                    printLog(u, false);
                }
            }
        }

        private boolean isDev(SystemUser u) {
            return devGroup != null && u.existsLink(devGroup);
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
            u.getSessionLog(period, server).forEach(s -> {
                if(printUser && count.get() == 0) {
                    printUser(u);
                }
                count.incrementAndGet();
                table.addCell(createCell(DateUtility.trimMillis(tm.date(s.getInTime()))));
                Timestamp ts = s.getOutTime();
                if(ts != null) {
                    ts = DateUtility.trimMillis(tm.date(ts));
                }
                table.addCell(createCell(Objects.requireNonNullElse(ts, "")));
                table.addCell(createCell(s.getIPAddress()));
                table.addCell(createCell(trim(s.getApplication())));
                StringBuilder changes = new StringBuilder();
                u.getLogicHit(s).forEach(h -> {
                    if(changes.length() > 0) {
                        changes.append('\n');
                    }
                    Logic logic = h.getLogic();
                    changes.append(h.isExecuted() ? "Executed" : "Accessed").append(" [")
                            .append(logic == null ? "* * *" : logic.getTitle()).append("] at ")
                            .append(DateUtility.formatWithTimeHHMM(DateUtility.trimMillis(tm.date(h.getHitTime()))));
                });
                u.getChangeLog(s).forEach(so -> {
                    String changed = changedObjects.contains(so.getId()) ? null : changed(so);
                    if(changed != null) {
                        if(changes.length() > 0) {
                            changes.append('\n');
                        }
                        changes.append(changed);
                    }
                });
                changedObjects.clear();
                if(changes.length() > 0) {
                    table.addRowCell(createCell(changes));
                    count.incrementAndGet();
                }
                if((count.get() % 80) == 0) {
                    add(table);
                }
            });
            if(count.get() > 0) {
                add(table);
            }
        }

        private String changed(StoredObject so) {
            if(so != null) {
                changedObjects.add(so.getId());
            }
            if(so instanceof ColumnDefinition || so instanceof IndexDefinition || so instanceof LinkDefinition) {
                so = so.getMaster(TableDefinition.class);
                if(so == null || changedObjects.contains(so.getId())) {
                    return null;
                }
                changedObjects.add(so.getId());
            }
            if(so instanceof TableDefinition td) {
                return trim(td.getClassName(), 3) + " (Structure modified)";
            }
            if(so instanceof JavaClass jc) {
                return trim(jc.getName(), 3) + " (Logic modified)";
            }
            if(so == null || so instanceof DeveloperLog || so instanceof StreamData || so instanceof JavaInnerClass) {
                return null;
            }
            return trim(so.getClass().getName(), 3) + " (Modified)";
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
