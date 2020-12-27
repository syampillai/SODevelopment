package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.mail.Mail;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

public class MailLog extends DataForm implements Transactional {

    private DatePeriodField periodField;
    private ChoiceField statusField;

    public MailLog() {
        super("Mail Log");
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
        public int getPageOrientation() {
            return ORIENTATION_LANDSCAPE;
        }

        @Override
        public Object getTitleText() {
            Text t = new Text();
            t.append(16, PDFFont.BOLD).append("SMS Log (Delivery Status: ").append(statusField.getChoice()).append(")");
            t.newLine().append(10, PDFFont.BOLD).newLine(true).append("Period: ").append(period);
            return t;
        }

        @Override
        public void generateContent() {
            TransactionManager tm = getTransactionManager();
            PDFTable table = createTable(80, 10, 10);
            addTitle(table, "Message");
            addTitle(table, "Sent at");
            addTitle(table, "Status");
            StringBuilder c = new StringBuilder("SentAt");
            c.append(period.getDBTimeCondition(tm));
            if(status == 1) {
                c.append(" AND Sent");
            } else if(status == 2) {
                c.append(" AND NOT Sent");
            }
            Person person;
            String m;
            for(Mail mail: StoredObject.list(Mail.class, c.toString(), "SentAt DESC")) {
                Contact contact = StoredObject.get(Contact.class, "Contact='" + mail.getToAddress() + "'");
                if(contact != null) {
                    person = contact.getMaster(Person.class);
                } else {
                    person = null;
                }
                addToTable(table, "Created at " + DateUtility.format(tm.date(mail.getCreatedAt())) +
                        "\nTo: " + mail.getToAddress() +
                                (person == null ? "" : (" <" + person.getName() + ">")) +
                        "\nSubject: " + mail.getSubject() + "\n" + mail.getMessage());
                addToTable(table, mail.getSent() ? DateUtility.format(tm.date(mail.getSentAt())) : "Not sent");
                addToTable(table, mail.getSent() ? "Sent" : mail.getErrorValue());
            }
            add(table);
        }

        private void addTitle(PDFTable table, String string) {
            table.addCell(createCenteredCell(createTitleText(string)));
        }
    }
}
