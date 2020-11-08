package com.storedobject.ui.util;

import com.storedobject.core.AttachmentDefinition;
import com.storedobject.core.FileData;

public class StreamAttachment {

    StreamAttachment(StreamAttachmentData data, AttachmentDefinition definition) {
    }

    public void setField(AttachmentField field) {
    }

    public StreamAttachmentData getAttachmentData() {
        return null;
    }

    public Class<? extends FileData> getFileClass() {
        return null;
    }

    public int getAttachmentType() {
        return 1;
    }

    void load() {
    }

    public String getName() {
        return null;
    }

    public FileData getOldValue() {
        return null;
    }

    public FileData getValue() {
        return null;
    }

    public FileData createValue() {
        return null;
    }

    boolean isAdded() {
        return false;
    }

    boolean isEdited() {
        return false;
    }

    boolean isDeleted() {
        return false;
    }

    boolean isNull() {
        return false;
    }

    void setEdited() {
    }

    boolean classChanged() {
        return false;
    }

    public boolean isRequired() {
        return false;
    }
}
