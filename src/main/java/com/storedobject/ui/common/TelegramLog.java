package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.telegram.Telegram;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

public class TelegramLog extends DataForm implements Transactional {

    private DatePeriodField periodField;
    private ChoiceField statusField;

    public TelegramLog() {
        super("Telegram Message Log");
    }

    @Override
    protected void buildFields() {
        periodField = new DatePeriodField("Period");
        addField(periodField);
        statusField = new ChoiceField("Delivery Status", new String[] { "All", "Sent", "Not sent" });
        addField(statusField);
    }

    @Override
    protected boolean process() {
        close();
        //noinspection resource
        new Report(getApplication(), periodField.getValue(), statusField.getValue()).execute();
        return true;
    }

    private class Report extends PDFReport {

        private final DatePeriod period;
        private final int status;

        public Report(Device device, DatePeriod period, int status) {
            super(device);
            this.period = period;
            this.status = status;
        }

        @Override
        public Object getTitleText() {
            Text t = new Text();
            t.append(16, PDFFont.BOLD).append("Telegram Message Log (Delivery Status: ")
                    .append(statusField.getChoice()).append(")").newLine().append(10, PDFFont.BOLD)
                    .newLine(true).append("Period: ").append(period);
            return t;
        }

        @Override
        public void generateContent() {
            TransactionManager tm = getTransactionManager();
            PDFTable table = createTable(80, 20, 10);
            addTitle(table, "Message");
            addTitle(table, "Sent at");
            addTitle(table, "Status");
            table.setHeaderRows(1);
            StringBuilder c = new StringBuilder("SentAt");
            c.append(period.getDBTimeCondition(tm));
            if(status == 1) {
                c.append(" AND Sent");
            } else if(status == 2) {
                c.append(" AND NOT Sent");
            }
            Person person;
            for(Telegram mail: StoredObject.list(Telegram.class, c.toString(), "SentAt DESC")) {
                Contact contact = StoredObject.get(Contact.class, "Contact LIKE '" + mail.getTelegramNumber() + "/%'");
                if(contact != null) {
                    person = contact.getMaster(Person.class);
                } else {
                    person = null;
                }
                addToTable(table, "Created at " + DateUtility.formatWithTimeHHMM(tm.date(mail.getCreatedAt())) +
                        "\nTo: " + (person == null ? mail.getTelegramNumber() : (" <" + person.getName() + ">")) +
                        "\n" + mail.getMessage());
                addToTable(table, mail.getSent() ? DateUtility.formatWithTimeHHMM(tm.date(mail.getSentAt())) : "Not sent");
                addToTable(table, mail.getSent() ? "Sent" : mail.getErrorValue());
            }
            add(table);
        }

        private void addTitle(PDFTable table, String string) {
            table.addCell(createCenteredCell(createTitleText(string)));
        }
    }
}
