package com.storedobject.core;

import com.storedobject.common.Executable;

import java.io.InputStream;
import java.io.OutputStream;

public interface ContentProducer extends Executable {
	
	public InputStream getContent() throws Exception;
    public String getContentType();
    public String getFileExtension();
    public String getFileName();
    public void setTransactionManager(TransactionManager tm);
    
    public default TransactionManager getTransactionManager() {
    	return null;
    }
    
    public default SystemEntity getSystemEntity() {
    	TransactionManager tm = getTransactionManager();
    	return tm == null ? null : tm.getEntity();
    }
    
    public default Entity getEntity() {
    	SystemEntity se = getSystemEntity();
    	return se == null ? null : se.getEntity();
    }
    
    public default boolean isLink() {
    	return false;
    }
    
    public default String getLink() {
    	return null;
    }
    
    public default InputStream extractContent() throws Exception {
    	produce();
    	return getContent();
    }
    
    public default StreamDataProvider getStreamDataProvider() {
    	StreamDataProvider sp = new StreamDataProvider() {
			
			@Override
			public void writeStream(StreamData streamData, OutputStream output) throws Exception {
			}
			
			@Override
			public InputStream getStream(StreamData streamData) throws Data_Not_Changed, Exception {
				produce();
				return getContent();
			}
		};
		return sp;
    }
    
    public default StreamData getStreamData() {
    	StreamData sd = new StreamData();
    	sd.setContentType(getContentType());
    	sd.setStreamDataProvider(getStreamDataProvider());
    	return sd;
    }
    
    public default void produce() {
    	execute();
    }
    
    public default FileData saveTo(String folderPath, Transaction transaction) throws Exception {
    	return FileData.create(folderPath, getStreamData(), transaction);
    }    
    
    public default FileData saveTo(FileData fileData, Transaction transaction) throws Exception {
    	StreamData sd = getStreamData();
    	sd.save(transaction);
    	fileData.setFile(sd);
    	fileData.save(transaction);
    	return fileData;
    }
    
    public default FileData saveTo(String folderPath, TransactionManager tm) throws Exception {
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
    
    public default FileData saveTo(FileData fileData, TransactionManager tm) throws Exception {
    	tm.transact(t -> saveTo(fileData, t));
    	return fileData;
    }
}