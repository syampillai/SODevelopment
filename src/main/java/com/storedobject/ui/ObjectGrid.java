package com.storedobject.ui;

import com.storedobject.core.ObjectCacheList;
import com.storedobject.core.ObjectList;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectGrid<T extends StoredObject> extends ObjectListGrid<T> implements ObjectGridData<T, T> {

    static final String NOTHING_SELECTED = "Nothing selected";
    ObjectSetter<T> objectSetter;
    private List<ObjectChangedListener<T>> objectChangedListeners;

    public ObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(new ObjectCacheList<>(objectClass, any), columns);
    }

    public ObjectGrid(ObjectList<T> objectList, Iterable<String> columns) {
        super(objectList, columns);
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        if(objectChangedListeners == null && create) {
            objectChangedListeners = new ArrayList<>();
        }
        return objectChangedListeners;
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
        this.objectSetter = setter;
    }

    public void search() {
        if(objectSetter != null) {
            search(objectSetter);
        }
    }
}