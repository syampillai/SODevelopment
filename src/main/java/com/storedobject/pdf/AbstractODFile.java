package com.storedobject.pdf;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;
import com.storedobject.core.TransactionManager;

public abstract class AbstractODFile implements ContentProducer, Closeable {

	protected StreamData template;

	public AbstractODFile() {
		this((StreamData)null);
	}

	public AbstractODFile(Id templateId) {
		this((StreamData)null);
		setTemplate(templateId);
	}

	public AbstractODFile(StreamData streamData) {
	}
	
	public void setTemplate(StreamData template) {
	}

	public void setTemplate(Id templateId) {
	}
	
	public void setRawOutput() {
	}

	public boolean isError() {
		return false;
	}
	
	protected abstract String getRawFileExtension();
	
	protected abstract String getRawContentType();
	
	@Override
	public void execute() {
	}
	
	protected abstract void process(String fileName) throws Throwable;
	
	public static void convertToPDF(String fileName) throws Throwable {
	}

	@Override
	public InputStream getContent() throws Exception {
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
	public final String getFileName() {
		return null;
	}

	@Override
	public void setTransactionManager(TransactionManager tm) {
	}

	@Override
	public void close() throws IOException {
	}

	public static String getWorkingDirectory() {
		return null;
	}

	public static void setWorkingDirectory(String workingDirectory) {
	}
	
	public void setPageRange(int pageStart, int pageEnd) {
	}
	
	public void setPagesToSkip(int... pagesToSkip) {
	}
}