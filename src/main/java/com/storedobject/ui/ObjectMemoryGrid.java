package com.storedobject.ui;

import com.storedobject.core.ObjectMemoryList;
import com.storedobject.core.StoredObject;

import java.util.List;

/**
 * Grid that can be used shows a list of {@link StoredObject} instances. This implements {@link List} but cannot handle
 * big-sized lists because all instances are kept in the memory.
 * <p>This is an in-memory version of {@link ObjectListGrid}.</p>
 *
 * @param <T> Type of object instance.
 * @author Syam
 */
public class ObjectMemoryGrid<T extends StoredObject> extends ObjectListGrid<T> {

    public ObjectMemoryGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectMemoryGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectMemoryGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectMemoryGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(new ObjectMemoryList<>(objectClass, any), columns);
    }
}
