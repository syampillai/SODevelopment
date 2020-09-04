package com.storedobject.core;

import java.io.InputStream;

public class StreamDataContent implements ContentProducer {

    private final StreamData sd;

    public StreamDataContent(StreamData sd) {
        this.sd = sd;
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
        return null;
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
