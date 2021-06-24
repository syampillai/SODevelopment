package com.storedobject.core;

public class DocumentConfiguration extends StoredObject {

    public DocumentConfiguration() {
    }

    public static void columns(Columns columns) {
    }

    public static DocumentConfiguration get(String name) {
        return null;
    }

    public static ObjectIterator < DocumentConfiguration > list(String name) {
        return null;
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setFileClass(String fileClass) {
    }

    public String getFileClass() {
        return null;
    }
    
    public boolean matches(FileData file) {
    	return false;
    }
}
