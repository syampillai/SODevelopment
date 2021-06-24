package com.storedobject.office;

import com.storedobject.core.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ODT<T> implements ContentProducer, Closeable {

    public ODT() {
        this((StreamData)null, null);
    }

    public ODT(Id templateId) {
        this(templateId, null);
    }

    public ODT(Id templateId, Object filler) {
        this((StreamData)null, filler);
        setTemplate(templateId);
    }

    public ODT(StreamData streamData) {
        this(streamData, null);
    }

    public ODT(StreamData streamData, Object filler) {
    }

    public Device getDevice() {
        return null;
    }

    public void setTemplate(StreamData template) {
    }

    public void setTemplate(Id templateId) {
    }

    public final void setRawOutput(boolean rawOutput) {
    }

    public final boolean isRawOutput() {
        return false;
    }

    @Override
    public final InputStream getContent() throws IOException {
        return null;
    }

    @Override
    public final String getContentType() {
        return null;
    }

    @Override
    public final String getFileExtension() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
    }

    @Override
    public TransactionManager getTransactionManager() {
        return null;
    }

    public Throwable getException() {
        return null;
    }

    public final void debug() {
    }

    public final void setIterator(Iterator<T> iterator) {
    }

    public void reportingIteratorValue(T value) {
    }

    @Override
    public void produce() {
    }

    public Object getFiller() {
        return null;
    }

    public void setFiller(Object filler) {
    }

    @Override
    public void close() {
    }
}
