package com.storedobject.ui;

import com.storedobject.common.GeneratedImage;
import com.storedobject.core.*;
import com.storedobject.mail.Mail;
import com.storedobject.office.CSVReport;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;

import java.awt.*;
import java.util.Currency;

@SuppressWarnings("DataFlowIssue")
public class Test extends View implements Transactional {

    public Test() {
        super("Image Test");
        setComponent(new Button("Test", e -> attach()));
        Money m = new Money(100, "USD");
        System.err.println(m.getSellingRate(Currency.getInstance("INR"), getTransactionManager().getEntity()));
        System.err.println(m.getSellingRate(Currency.getInstance("USD"), getTransactionManager().getEntity()));
        System.err.println(m + ", " + m.toLocal(getTransactionManager()));
    }

    private void attach() {
        Mail mail = StoredObject.get(Mail.class, "Message='Hai'");
        if(mail == null) {
            message("No draft found");
            return;
        }
        message("Draft retrieved");
        mail.attach(getTransactionManager(), new PersonListCSV(getApplication()));
        mail.attach(getTransactionManager(), new ImageProducer(new CarParkImage(SOServlet.getImage("background").getFile())));
        message("Attachment done");
    }

    private static class CarParkImage extends GeneratedImage {

        public CarParkImage(StreamData imageStream) {
            super(imageStream.getContent());
        }

        @Override
        public void generateContent(Graphics graphics) {
            graphics.setColor(Color.red);
            graphics.drawString("Hello", 10, 10);
        }
    }

    private static class PersonListCSV extends CSVReport {

        public PersonListCSV(Device device) {
            super(device, 5);
        }

        @Override
        public void generateContent() throws Exception {
            setValues("Title", "First Name", "Last Name", "Date of Birth", "Age");
            writeRow();
            for (Person p : StoredObject.list(Person.class)) {
                setValues(
                        p.getTitleValue(), p.getFirstName(), p.getLastName(), p.getDateOfBirth(), p.getAge());
                writeRow();
            }
        }
    }
}