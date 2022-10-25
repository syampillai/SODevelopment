package com.storedobject.core;

import com.storedobject.common.Executable;

import java.io.InputStream;
import java.io.OutputStream;

public interface ContentProducer extends Executable, ContentType {

	void produce();
	InputStream getContent() throws Exception;
	String getFileExtension();
	String getFileName();
	void setTransactionManager(TransactionManager tm);

	default TransactionManager getTransactionManager() {
		return null;
	}

	default SystemEntity getSystemEntity() {
		TransactionManager tm = getTransactionManager();
		return tm == null ? null : tm.getEntity();
	}

	default Entity getEntity() {
		SystemEntity se = getSystemEntity();
		return se == null ? null : se.getEntity();
	}

	default InputStream extractContent() throws Exception {
		produce();
		return getContent();
	}

	default StreamDataProvider getStreamDataProvider() {
		return new StreamDataProvider() {

			@Override
			public void writeStream(StreamData streamData, OutputStream output) {
			}

			@Override
			public InputStream getStream(StreamData streamData) throws Exception {
				produce();
				return getContent();
			}
		};
	}

	default StreamData getStreamData() {
		StreamData sd = new StreamData();
		sd.setContentType(getContentType());
		sd.setStreamDataProvider(getStreamDataProvider());
		return sd;
	}

	default void execute() {
		produce();
	}

	default FileData saveTo(String folderPath, Transaction transaction) throws Exception {
		return FileData.create(folderPath, getStreamData(), transaction);
	}

	@SuppressWarnings("UnusedReturnValue")
	default FileData saveTo(FileData fileData, Transaction transaction) throws Exception {
		StreamData sd = getStreamData();
		sd.save(transaction);
		fileData.setFile(sd);
		fileData.save(transaction);
		return fileData;
	}

	default FileData saveTo(String folderPath, TransactionManager tm) throws Exception {
		Transaction t = null;
		try {
			t = tm.createTransaction();
			FileData fd = FileData.create(folderPath, getStreamData(), t);
			t.commit();
			return fd;
		} catch(Exception e) {
			if(t != null) {
				t.rollback();
			}
			throw e;
		}
	}

	default FileData saveTo(FileData fileData, TransactionManager tm) throws Exception {
		tm.transact(t -> saveTo(fileData, t));
		return fileData;
	}

	default String getLink() {
		return null;
	}

	/**
	 * This method may be called when the content generation is aborted due to some error.
	 * <p>Note: This could be invoked multiple times.</p>
	 *
	 * @param error Error.
	 */
	default void abort(Throwable error) {
	}

	/**
	 * Get the current error (set via {@link #abort(Throwable)}).
	 *
	 * @return Current error if any.
	 */
	default Throwable getError() {
		return null;
	}
}