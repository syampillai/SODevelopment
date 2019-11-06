package com.storedobject.ui.util;

import com.storedobject.core.StreamData;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.TranslatedField;

public class AttachmentField extends TranslatedField<StreamAttachment, StreamData> {

    @SuppressWarnings("ConstantConditions")
    public AttachmentField(String label, ObjectField.Type type, StreamAttachment attachment) {
        super(null, null, null);
    }

    public StreamAttachment getAttachment() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    void updated() {
    }
}