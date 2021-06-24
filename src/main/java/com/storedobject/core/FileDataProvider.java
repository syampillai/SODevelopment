package com.storedobject.core;

public class FileDataProvider implements com.storedobject.core.StreamDataProvider {

    public FileDataProvider(java.lang.String p1) {
        this();
    }

    public FileDataProvider(java.io.File p1) {
        this();
    }

    private FileDataProvider() {
    }

    public java.io.InputStream getStream(com.storedobject.core.StreamData p1) throws com.storedobject.core.Data_Not_Changed, java.lang.Exception {
        return null;
    }

    public void writeStream(com.storedobject.core.StreamData p1, java.io.OutputStream p2) throws java.lang.Exception {
    }
}
