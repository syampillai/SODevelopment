package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TransactionLog extends DataForm {

    private ObjectField<SystemUser> suField;
    private SystemUser su;
    private DatePeriodField periodField;
    private ChoiceField printChoiceField;

    public TransactionLog() {
        super("System Access Log");
    }

    @Override
    protected void buildFields() {
        suField = new ObjectField<>("System User", SystemUser.class);
        addField(suField);
        suField.setHelperText("Leave it blank for all users");
        addField(periodField = new DatePeriodField("From"));
        addField(printChoiceField = new ChoiceField("Print", new String[] { "Summary", "Date",  "Time"}));
    }

    @SuppressWarnings("resource")
    @Override
    protected boolean process() {
        close();
        su = suField.getObject();
        new Report().execute();
        return true;
    }

    private class Report extends PDFReport {

        private Report() {
            super(getApplication());
        }

        @Override
        public Object getTitleText() {
            if(su == null) {
                return createTitleText("System Access Log\n" + periodField.getValue(), 16);
            }
            return createTitleText("System Access Log of " + su + "\n" + periodField.getValue(), 16);
        }

        @Override
        public void generateContent() throws Exception {
            int[] relativeWidths;
            int p = printChoiceField.getValue();
            switch(p) {
                case 0:
                    relativeWidths = new int[] { 100 };
                    break;
                case 1:
                case 2:
                    relativeWidths = new int[] { 50, 50 };
                    break;
                default:
                    return;
            }
            PDFTable table = createTable(relativeWidths);
            table.addCell(createCell(createTitleText("User" + (su == null ? "s" : ""))));
            switch(p) {
                case 0:
                    break;
                case 1:
                    table.addCell(createCell(createTitleText("Date")));
                    break;
                case 2:
                    table.addCell(createCell(createTitleText("Time")));
                    break;
            }
            if(su == null || p > 0) {
                table.setHeaderRows(1);
            }
            DatePeriod dp = periodField.getValue();
            Timestamp t1 = DateUtility.startTime(dp.getFrom()), t2 = DateUtility.endTime(dp.getTo());
            Query q = StoredObjectUtility.getTransactionLog(suField.getObject(), t1, t2);
            if(su != null && p == 0) {
                table.addCell(createCell(q.hasNext() ? "Accessed" : "Not accessed"));
            } else {
                Id puid = null, uid;
                SystemUser u = null;
                if(p == 0) {
                    for(ResultSet rs: q) {
                        uid = new Id(rs.getBigDecimal(2));
                        if(puid == null || !puid.equals(uid)) {
                            u = StoredObject.getHistorical(SystemUser.class, uid);
                            table.addCell(createCell(u == null ? "Unknown" : u));
                            puid = uid;
                        }
                    }
                } else if(p == 1) {
                    Date pdate = null, date;
                    Timestamp time;
                    for(ResultSet rs: q) {
                        uid = new Id(rs.getBigDecimal(2));
                        if(puid == null || !puid.equals(uid)) {
                            u = StoredObject.getHistorical(SystemUser.class, uid);
                            puid = uid;
                            pdate = null;
                        }
                        time = rs.getTimestamp(3);
                        date = DateUtility.create(time);
                        if(pdate == null || !DateUtility.equals(pdate, date)) {
                            table.addCell(createCell(u == null ? "Unknown" : u));
                            table.addCell(createCell(date));
                            pdate = date;
                        }
                    }
                } else {
                    for(ResultSet rs: q) {
                        uid = new Id(rs.getBigDecimal(2));
                        if(puid == null || !puid.equals(uid)) {
                            u = StoredObject.getHistorical(SystemUser.class, uid);
                            puid = uid;
                        }
                        table.addCell(createCell(u == null ? "Unknown" : u));
                        table.addCell(createCell(DateUtility.format(rs.getTimestamp(3))));
                    }
                }
            }
            q.close();
            add(table);
        }
    }
}
