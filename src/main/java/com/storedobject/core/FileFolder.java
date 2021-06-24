package com.storedobject.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FileFolder extends StoredObject implements Detail, HasChildren {

    public FileFolder() {
    }
    
    public static FileFolder create(FileFolder... folders) {
    	return null;
    }
    
    public static FileFolder create(Iterator<FileFolder> folders) {
    	return null;
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    @Override
	public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }
    
    public static FileFolder createFolders(Transaction transaction, String path) throws Exception {
        return null;
    }
    
	public static FileFolder get(String path) {
        return null;
	}

	public static FileFolder get(String path, FileFolder parent) {
        return null;
	}
	
	public FileFolder getFolder() {
		return null;
	}
	
	public FileFolder getRootFolder() {
        return null;
	}
	
	public List<DocumentConfiguration> getConfiguration() {
        return new ArrayList<>();
	}

    public void resetCache() {
    }

	public boolean matches(FileData file) {
		return false;
	}
	
    public void circulate(Transaction transaction, boolean recursive) throws Exception {
    }
    
    public ObjectIterator<FileFolder> listFolders() {
    	return null;
    }
    
    public ObjectIterator<FileFolder> listFolders(String condition) {
    	return null;
    }
    
    public ObjectIterator<FileFolder> listFolders(String condition, String orderBy) {
    	return null;
    }

    public ObjectIterator<FileData> listFiles() {
    	return null;
    }
    
    public ObjectIterator<FileData> listFiles(String condition) {
    	return null;
    }
    
    public ObjectIterator<FileData> listFiles(String condition, String orderBy) {
    	return null;
    }
    
    public boolean isRoot() {
    	return false;
    }
}