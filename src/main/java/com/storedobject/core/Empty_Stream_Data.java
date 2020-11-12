package com.storedobject.core;

public class Empty_Stream_Data extends Invalid_State {

    public Empty_Stream_Data(Class<? extends StoredObject> hostClass) {
        super("Data/File/Image not uploaded in '" + StringUtility.makeLabel(hostClass) + "'");
    }
}
