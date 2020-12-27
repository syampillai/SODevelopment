package com.storedobject.ui.tools;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.Query;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.SystemUser;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.CompoundField;
import com.storedobject.vaadin.DataForm;

public class TransactionLog extends DataForm {

    private ObjectField<SystemUser> suField;
    private SystemUser su;
    private DatePeriodField periodField;
    private ChoiceField printChoiceField;

    public TransactionLog() {
        super("Transaction Log");
    }

    @Override
    protected void buildFields() {
        suField = new ObjectField<>("System User", SystemUser.class);
        setRequired(suField);
        addField(suField);
        addField(new CompoundField("", new ELabel("Leave it blank for all users")));
        addField(periodField = new DatePeriodField("From"));
        addField(printChoiceField = new ChoiceField("Print", new String[] { "Summary", "Date",  "Time"}));
    }

    @SuppressWarnings("resource")
    @Override
    protected boolean process() {
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
                return createTitleText("Trasaction Log\n" + periodField.getValue(), 16);
            }
            return createTitleText("Trasaction Log of " + su + "\n" + periodField.getValue(), 16);
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
                    if(su == null) {
                        relativeWidths = new int[] { 50, 50 };
                    } else {
                        relativeWidths = new int[] { 100 };
                    }
                    break;
                default:
                    return;
            }
            PDFTable table = createTable(relativeWidths);
            if(su == null) {
                table.addCell(createTitleText("Users"));
            }
            switch(p) {
                case 0:
                    break;
                case 1:
                    table.addCell(createTitleText("Date"));
                    break;
                case 2:
                    table.addCell(createTitleText("Time"));
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
