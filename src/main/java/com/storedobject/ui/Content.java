package com.storedobject.ui;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.TransactionManager;

import java.io.InputStream;

public abstract class Content implements ContentProducer {

    private final InputStream stream;

    public Content(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public void produce() {
    }

    @Override
    public final InputStream getContent() {
        return stream;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
    }
}