package com.storedobject.ui;

import com.storedobject.core.ObjectText;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.CustomField;

public class ObjectTextField<T extends StoredObject> extends CustomField<ObjectText<T>> {

    public ObjectTextField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectTextField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectTextField(Class<T> objectClass, boolean any) {
        this(null, objectClass, any);
    }

    public ObjectTextField(String label, Class<T> objectClass, boolean any) {
        super(new ObjectText<>());
    }

    public final Class<T> getObjectClass() {
        return null;
    }

    @Override
    protected ObjectText<T> generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(ObjectText<T> objectText) {
    }
}