package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.core.ContentProducer;
import com.storedobject.ui.Application;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractContentGenerator extends Thread {

    long fileId;
    final ContentProducer producer;
    final Application application;
    private boolean generating = true;
    private final Consumer<AbstractContentGenerator> inform;
    private final Consumer<Long> timeTracker;
    private long startedAt = 0;

    protected AbstractContentGenerator(Application application, ContentProducer producer,
                                       Consumer<AbstractContentGenerator> inform, Consumer<Long> timeTracker) {
        this.application = application;
        this.producer = producer;
        this.inform = inform;
        this.timeTracker = timeTracker;
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
        String fileName = producer.getFileName();
        if(fileName != null && fileName.length() < 3) {
            fileName += "-so";
        }
        fileName = fileName == null ? ("so" + fileId) : fileName;
        return fileName.replace('/', '_')
                .replace(':', '_')
                .replace('?', '_')
                .replace('*', '_')
                .replace(';', '_');
    }

    public File createFile() {
        try {
            File file = File.createTempFile(getFile(), getExt());
            file.deleteOnExit();
            IO.copy(getContentStream(), IO.getOutput(file));
            return file;
        } catch (Exception e) {
            application.log(e);
            application.showNotification(e);
        }
        return null;
    }

    public InputStream getContentStream() {
        startedAt = System.currentTimeMillis();
        try {
            int time = 180;
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
            application.showNotification(e);
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
        if(timeTracker != null) {
            timeTracker.accept(System.currentTimeMillis() - startedAt);
        }
    }

    public ContentProducer getProducer() {
        return producer;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        AbstractContentGenerator that = (AbstractContentGenerator) o;
        return fileId == that.fileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }
}