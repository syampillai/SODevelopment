package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;

@FunctionalInterface
public interface RequiresMaster {
    void setMaster(StoredObject master);
}
