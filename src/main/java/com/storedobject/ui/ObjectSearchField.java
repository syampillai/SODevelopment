package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.Component;

public class ObjectSearchField<T extends StoredObject> extends AbstractObjectField<T> {

    public ObjectSearchField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectSearchField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectSearchField(Class<T> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public ObjectSearchField(String label, Class<T> objectClass, boolean allowAny) {
        super(objectClass, allowAny);
    }

    @Override
    protected Component createPrefixComponent() {
        return null;
    }

    @Override
    protected T generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(T value) {
    }
}