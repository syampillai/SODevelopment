package com.storedobject.ui;

import com.storedobject.common.Barcode;
import com.storedobject.common.IO;
import com.storedobject.common.ImageUtility;
import com.storedobject.common.InputOutputStream;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.MediaCapture;
import com.storedobject.vaadin.VideoCapture;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Div;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class BarcodeScanner extends DataForm {

    private Consumer<String> consumer;
    private VideoCapture video;
    private Capture capture;

    public BarcodeScanner() {
        this((Consumer<String>)null);
    }

    public BarcodeScanner(HasText hasText) {
        this(hasText::setText);
    }

    public BarcodeScanner(Consumer<String> consumer) {
        super("Scan Barcode");
        this.consumer = consumer;
        setButtonsAtTop(true);
    }

    @Override
    protected HasComponents createLayout() {
        return new Div();
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setVisible(false);
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        video = new VideoCapture();
        video.addStatusChangeListener(v -> {
            if(!v.isPreviewing()) {
                getApplication().access(this::close);
            }
        });
        add(video);
    }

    private void valueRead(String value) {
        if(consumer != null) {
            consumer.accept(value);
        }
        close();
    }

    public void setConsumer(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        super.execute(parent, doNotLock);
        getApplication().startPolling(this);
        video.preview();
        video.getElement().setAttribute("controls", false);
        capture = new Capture();
    }

    @Override
    public void clean() {
        getApplication().stopPolling(this);
        super.clean();
        if(capture != null) {
            capture.done = true;
            IO.close(capture.inout.getOutputStream());
        }
    }

    @Override
    protected boolean process() {
        return false;
    }

    private class Capture implements Runnable {

        private Decode decode = null;
        private boolean done = false;
        private final InputOutputStream inout = new InputOutputStream();

        private Capture() {
            Thread.startVirtualThread(this);
        }

        @Override
        public void run() {
            inout.setReusable(true);
            new DataReader();
            while(!done) {
                if (decode != null) {
                    Thread.yield();
                    continue;
                }
                decode = new Decode();
                getApplication().access(() -> video.savePicture(decode));
            }
            IO.close(inout.getOutputStream());
        }
    }

    private class DataReader implements Runnable {

        private DataReader() {
            Thread.startVirtualThread(this);
        }

        @Override
        public void run() {
            InputStream in = capture.inout.getInputStream();
            String data;
            BufferedImage image;
            while(!capture.done) {
                try {
                    image = ImageIO.read(in);
                    data = Barcode.read(image);
                    in.close();
                    if(!data.isEmpty() && data.equals(Barcode.read(ImageUtility.clip(image, 10)))) {
                        capture.done = true;
                        final String d = data;
                        getApplication().access(() -> valueRead(d));
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private class Decode implements MediaCapture.DataReceiver {

        @Override
        public OutputStream getOutputStream(String mimeType) {
            return capture.inout.getOutputStream();
        }

        @Override
        public void finished() {
            IO.close(capture.inout.getOutputStream());
            capture.decode = null;
        }

        @Override
        public void aborted() {
        }
    }
}
