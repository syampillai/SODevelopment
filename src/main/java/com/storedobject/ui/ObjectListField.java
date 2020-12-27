package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.ComboField;

import java.util.ArrayList;
import java.util.Collection;

public class ObjectListField<T extends StoredObject> extends ComboField<T> {

    private final Class<T> objectClass;

    public ObjectListField(Class<T> objectClass) {
        this(null, objectClass, null);
    }

    public ObjectListField(String label, Class<T> objectClass) {
        this(label, objectClass, null);
    }

    public ObjectListField(Class<T> objectClass, Collection<T> list) {
        this(null, objectClass, list);
    }

    public ObjectListField(String label, Class<T> objectClass, Collection<T> list) {
        super(label, list == null ? new ArrayList<>() : list);
        this.objectClass = objectClass;
        setItemLabelGenerator(StoredObject::toDisplay);
    }

    public final Class<T> getObjectClass() {
        return objectClass;
    }
}
