package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;
import com.vaadin.flow.component.Component;

public class FileField extends AbstractObjectField<StreamData> {

    public FileField() {
        this(ObjectField.Type.FILE);
    }

    public FileField(String caption) {
        this(caption, ObjectField.Type.FILE);
    }

    public FileField(ObjectField.Type... types) {
        super(StreamData.class,false);
    }

    public FileField(String label, ObjectField.Type... types) {
        super(StreamData.class,false);
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

    @Override
    public void setReadOnly(boolean readOnly) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public void addContentType(String... contentType) {
    }

    public void removeContentType(String contentType) {
    }

    public boolean isContentTypeAllowed(String contentType) {
        return false;
    }

    public boolean isImage() {
        return false;
    }

    public boolean isAudio() {
        return false;
    }

    public boolean isVideo() {
        return false;
    }

    public void disallowLinking() {
    }

    public void disallowDownload() {
    }

    public String getFileName() {
        return null;
    }
}