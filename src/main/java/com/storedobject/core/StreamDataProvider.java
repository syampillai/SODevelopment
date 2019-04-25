package com.storedobject.core;

public interface StreamDataProvider {

    public java.io.InputStream getStream(com.storedobject.core.StreamData p1) throws com.storedobject.core.Data_Not_Changed, java.lang.Exception;

    public void writeStream(com.storedobject.core.StreamData p1, java.io.OutputStream p2) throws java.lang.Exception;
}
