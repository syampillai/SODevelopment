package com.storedobject.core;

import com.storedobject.common.HasName;

public abstract class Name extends StoredObject implements HasName {

    protected String name;

    public Name() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return name;
    }
}