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

    public ObjectSearchField(Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        this(null, objectClass, allowAny, allowAdd);
    }

    public ObjectSearchField(String label, Class<T> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny,false);
    }

    public ObjectSearchField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd) {
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