package com.storedobject.core;

import com.storedobject.common.HasName;

public abstract class Name extends StoredObject implements HasName {

    public Name() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }
}