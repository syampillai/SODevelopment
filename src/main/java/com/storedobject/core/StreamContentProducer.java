package com.storedobject.core;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public abstract class StreamContentProducer implements ContentProducer, Closeable {

	protected OutputStream out;

	public StreamContentProducer() {
	}
	
	public StreamContentProducer(OutputStream out) {
	}

	@Override
	public void produce() {
	}

    @Override
	public String getFileName() {
    	return null;
    }

	public abstract void generateContent() throws Exception;

	@Override
	public InputStream getContent() throws Exception {
		return null;
	}

	@Override
	public void setTransactionManager(TransactionManager tm) {
	}
	
	public TransactionManager getTransactionManager() {
		return null;
	}

	protected Writer getWriter() {
		return null;
	}

	@Override
	public void close() {
	}
}