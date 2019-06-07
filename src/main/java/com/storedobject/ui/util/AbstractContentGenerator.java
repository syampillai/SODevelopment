package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.core.ContentProducer;
import com.storedobject.ui.Application;

import java.io.File;
import java.io.InputStream;

public abstract class AbstractContentGenerator extends Thread {

    public static final String PDF_CONTENT = "application/pdf";
    long fileId;
    final ContentProducer producer;
    final Application application;

    protected AbstractContentGenerator(Application application, ContentProducer producer) {
        this.application = application;
        this.producer = producer;
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
        return f == null ? ("so" + fileId) : f;
    }

    public File createFile() {
        try {
            File file = File.createTempFile("so" + fileId, getExt());
            file.deleteOnExit();
            IO.copy(getContentStream(), IO.getOutput(file));
            return file;
        } catch (Exception e) {
            Application.error(e);
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
            return in;
        } catch (Exception e) {
            Application.error(e);
        }
        return null;
    }

    public abstract DownloadStream getContent() throws Exception;
}