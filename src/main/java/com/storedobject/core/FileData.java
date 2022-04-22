package com.storedobject.core;

import java.math.BigDecimal;

public class FileData extends StoredObject implements Detail, HasParents, ContentType {

    public FileData() {
    }

    public FileData(String name) {
    }
    
    public FileData(FileData link) {
    }

    public FileData(String name, FileData link) {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
		return null;
    }

    public final void setFile(Id fileId) {
    }

    public void setFile(BigDecimal idValue) {
    }

    public void setFile(StreamData file) {
    }

    public Id getFileId() {
		return null;
    }

    public StreamData getFile() {
		return null;
    }

    public boolean isEmpty() {
        return false;
    }

    public FileData restore(Transaction transaction) throws Exception {
        return this;
    }
    
    public FileData getPreviousVersion() {
        return null;
    }

    public void replaceWith(Transaction transaction, FileData file) throws Exception {
    }
    
    public void unlinkFrom(Transaction transaction, FileFolder folder) throws Exception {
    }

    @Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return true;
	}
	
    public void saveInFolder(Transaction transaction, String folderPath) throws Exception {
    }
	
	public String getContentType() {
		return null;
	}
	
	public String getFileExtension() {
		return null;
	}
	
    public static boolean validName(String name) {
    	return false;
    }
	
	public static FileData get(String path) {
		return null;
	}

	public static FileData create(String path, StreamData streadData, Transaction transaction) throws Exception {
		return null;
	}
	
    public void circulate(Transaction transaction) throws Exception {
    }
    
    public void circulate(Transaction transaction, SystemUserGroup group) throws Exception {
    }

    public void circulate(Transaction transaction, SystemUser user) throws Exception {
    }

    public FileFolder getFolder() {
		return null;
    }
    
    public FileFolder getRootFolder() {
		return null;
    }
    
    public FileCirculation getCirculation(Person person) {
		return null;
    }
    
    public FileCirculation getCirculation(Id personId) {
		return null;
    }
    
    public ComputedDate getExpiryDate() {
		return null;
    }

    public String getReadStamp() {
        return Math.random() > 0.5 ? "" : null;
    }

    public ComputedDate getReadBefore() {
		return null;
    }

    public ComputedMinute getReadBeforeTime() {
		return null;
    }
    
    public String getDetails() {
    	return null;
    }
    
    public void view(Device device) {
    }

    public void linkTo(Transaction transaction, StoredObject object, String attribute) throws Exception {
    }

    public void linkTo(Transaction transaction, String link) throws Exception {
    }
}