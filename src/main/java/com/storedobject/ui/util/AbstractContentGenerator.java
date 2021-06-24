package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.core.ContentProducer;
import com.storedobject.ui.Application;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public abstract class AbstractContentGenerator extends Thread {

    public static final String PDF_CONTENT = "application/pdf";
    long fileId;
    final ContentProducer producer;
    final Application application;
    private boolean generating = true;
    private final Consumer<AbstractContentGenerator> inform;

    protected AbstractContentGenerator(Application application, ContentProducer producer,
                                       Consumer<AbstractContentGenerator> inform) {
        this.application = application;
        this.producer = producer;
        this.inform = inform;
    }

    @Override
    public long getId() {
        return fileId;
    }

    String getContentType() {
        String ct = producer.getContentType();
        int p = ct.indexOf(';');
        if(p > 0) {
            ct = ct.substring(0, p).trim();
        }
        return ct;
    }

    public String getExt() {
        String e = producer.getFileExtension();
        return (e.startsWith(".") ? "" : ".") + e;
    }

    String getFile() {
        String f = producer.getFileName();
        if(f != null && f.length() < 3) {
            f += "-so";
        }
        return f == null ? ("so" + fileId) : f;
    }

    public File createFile() {
        try {
            File file = File.createTempFile(getFile(), getExt());
            file.deleteOnExit();
            IO.copy(getContentStream(), IO.getOutput(file));
            return file;
        } catch (Exception e) {
            application.log(e);
            Application.warning(e);
        }
        return null;
    }

    public InputStream getContentStream() {
        try {
            int time = 100;
            InputStream in;
            while ((in = producer.getContent()) == null && time-- > 0) {
                if(isAlive()) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            return new IS(in);
        } catch (Exception e) {
            application.log(e);
            Application.warning(e);
            generated();
        }
        return null;
    }

    private class IS extends FilterInputStream {

        protected IS(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            super.close();
            generated();
        }
    }

    public abstract DownloadStream getContent() throws Exception;

    void generated() {
        synchronized(producer) {
            if(!generating) {
                return;
            }
            generating = false;
        }
        if(inform != null) {
            application.access(() -> inform.accept(this));
        }
    }

    public ContentProducer getProducer() {
        return producer;
    }
}