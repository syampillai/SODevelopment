package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;
import com.storedobject.vaadin.ImageButton;
import com.vaadin.flow.component.Component;

public abstract class MediaField extends AbstractObjectField<StreamData> {

    public MediaField(String label) {
        super(StreamData.class, false);
    }

    public static <O extends StoredObject> boolean canCreate(Class<O> objectClass) {
        return ObjectGetField.canCreate(objectClass);
    }

    @Override
    protected Component createPrefixComponent() {
        return null;
    }

    @Override
    protected StreamData generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(StreamData value) {
    }

    @Override
    protected void setModelValue(StreamData value, boolean fromClient) {
    }

    protected ImageButton addExtraButton(@SuppressWarnings("unused") StreamData value) {
        return null;
    }

    public void selectFrontCamera() {
    }

    public void selectRearCamera() {
    }
}