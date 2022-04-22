package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.sms.SMSMessage;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

@SuppressWarnings("resource")
public class SMSLog extends DataForm implements Transactional {

    private BooleanField delivered;
    private DatePeriodField periodField;

    public SMSLog() {
        super("SMS Log");
    }

    @Override
    protected void buildFields() {
        DatePeriod p = new DatePeriod(DateUtility.addYear(DateUtility.today(), - 1), DateUtility.today());
        periodField = new DatePeriodField("Period", p);
        addField(periodField);
        delivered = new BooleanField("Delivered");
        addField(delivered);
    }

    @Override
    protected boolean process() {
        close();
        new SMSLog.Report(getApplication()).execute();
        return true;
    }

    private class Report extends PDFReport {

        private final DatePeriod period;

        public Report(Device device) {
            super(device, false);
            period = periodField.getValue();
        }

        @Override
        public int getPageOrientation() {
            return ORIENTATION_LANDSCAPE;
        }

        @Override
        public Object getTitleText() {
            Text t = new Text();
            t.append(16, PDFFont.BOLD).append("SMS Log");
            if(delivered.getValue()) {
                t.append(" (Status: Delivered)");
            }
            t.newLine().append(10, PDFFont.BOLD).newLine(true).append("Period: ").append(period);
            return t;
        }

        @Override
        public void generateContent() {
            TransactionManager tm = getTransactionManager();
            PDFTable table = createTable(10, 30, 10, 10);
            table.addCell(createCenteredCell(createTitleText("Mobile Number", 10)));
            table.addCell(createCell(createTitleText("Message", 10)));
            table.addCell(createCenteredCell(createTitleText("Date & Time", 10)));
            table.addCell(createCenteredCell(createTitleText("Delivery Status", 10)));
            StringBuilder c = new StringBuilder("CreatedAt");
            c.append(period.getDBTimeCondition(tm));
            if(delivered.getValue()) {
                c.append(" AND Delivered");
            }
            boolean delivered;
            String date;
            for(SMSMessage sms: StoredObject.list(SMSMessage.class, c.toString())) {
                delivered = sms.getDelivered();
                table.addCell(createCenteredCell(sms.getMobileNumber()));
                date = DateUtility.format(tm.date(sms.getCreatedAt()));
                table.addCell(createCell("Created at " + date.replace(date.substring(17, 30), "") +
                        "\n" + sms.getMessage()));
                date = DateUtility.format(tm.date(sms.getSentAt()));
                table.addCell(createCenteredCell(delivered ? date.replace(date.substring(17, 30), "") : "N/A"));
                table.addCell(createCenteredCell(delivered ? "Delivered" : "Not Delivered"));
            }
            add(table);
        }
    }
}