package com.storedobject.ui;

import com.storedobject.common.GeneratedImage;
import com.storedobject.core.ImageProducer;
import com.storedobject.core.StreamData;
import com.storedobject.mail.Mail;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;

import java.awt.*;

public class Test extends View implements Transactional {

    public Test() {
        super("Image Test");
        setComponent(new Button("Image", e -> download()));
    }

    private void download() {
        //noinspection DataFlowIssue
        new Mail().attach(getTransactionManager(), new ImageProducer(new CarParkImage(SOServlet.getImage("background").getFile())));
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
}