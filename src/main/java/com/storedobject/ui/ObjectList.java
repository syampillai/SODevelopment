package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectListProvider;

public class ObjectList<T extends StoredObject, F> extends ObjectListProvider<T, F> {

    public ObjectList(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectList(Class<T> objectClass, boolean allowAny) {
        super(objectClass);
        setAllowAny(allowAny);
    }
}
