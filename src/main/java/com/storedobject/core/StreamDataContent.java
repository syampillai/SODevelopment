package com.storedobject.core;

import java.io.InputStream;

public class StreamDataContent implements ContentProducer {

    private final StreamData sd;
    private String fileName;

    public StreamDataContent(StreamData sd) {
        this(sd, null);
    }

    public StreamDataContent(StreamData sd, String fileName) {
        this.sd = sd;
        this.fileName = fileName;
    }

    @Override
    public void produce() {
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public InputStream getContent() throws Exception {
        return sd.getContent();
    }

    @Override
    public String getContentType() {
        return sd.getMimeType();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileExtension() {
        return sd.getFileExtension();
    }

    @Override
    public void setTransactionManager(TransactionManager tm) {
    }

    @Override
    public boolean isLink() {
        return sd.isLink();
    }

    @Override
    public String getLink() {
        return sd.getLink();
    }
}
