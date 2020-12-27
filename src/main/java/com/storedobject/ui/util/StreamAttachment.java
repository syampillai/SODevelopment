package com.storedobject.ui.util;

import com.storedobject.core.AttachmentDefinition;
import com.storedobject.core.FileData;
import com.storedobject.core.Id;

public class StreamAttachment {

    private AttachmentField field;
    private final StreamAttachmentData data;
    private final String name;
    private final Class<? extends FileData> fileClass;
    private final int attachmentType;
    private final boolean required;
    private boolean edited = false;
    private boolean deleted = false;
    private FileData oldValue, value;

    StreamAttachment(StreamAttachmentData data, AttachmentDefinition definition) {
        this.data = data;
        this.name = definition.getName();
        this.required = definition.getMandatory();
        this.fileClass = definition.getFileClass();
        this.attachmentType = definition.getType();
    }

    public void setField(AttachmentField field) {
        this.field = field;
    }

    public StreamAttachmentData getAttachmentData() {
        return data;
    }

    public Class<? extends FileData> getFileClass() {
        return fileClass;
    }

    public int getAttachmentType() {
        return attachmentType;
    }

    private void create() {
        try {
            value = fileClass.getDeclaredConstructor().newInstance();
        } catch (Throwable ignored) {
        }
        value.setName(name);
        if(oldValue != null) {
            value.setFile(oldValue.getFileId());
        }
        if(field != null) {
            field.update(this);
        }
    }

    void load() {
        if(data.getMaster() == null) {
            oldValue = null;
            create();
            return;
        }
        oldValue = data.getMaster().getFileData(name);
        reload();
    }

    private void reload() {
        edited = false;
        if(data.getMaster() == null) {
            oldValue = null;
            create();
            return;
        }
        if(oldValue == null) {
            create();
        } else {
            value = data.getMaster().getFileData(name);
            if(value.getClass() != fileClass) {
                create();
            } else {
                if (field != null) {
                    field.update(this);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public FileData getOldValue() {
        return oldValue;
    }

    public FileData getValue() {
        return value;
    }

    public FileData createValue() {
        if(value == null) {
            load();
        }
        return value;
    }

    boolean isAdded() {
        return oldValue == null || classChanged();
    }

    boolean isEdited() {
        if(deleted) {
            return false;
        }
        return !classChanged() && oldValue != null && (edited || !oldValue.getFileId().equals(value.getFileId()));
    }

    boolean isDeleted() {
        if(deleted) {
            return true;
        }
        return oldValue != null && (value == null || Id.isNull(value.getFileId()));
    }

    boolean isNull() {
        return oldValue == null && (value == null || Id.isNull(value.getFileId()));
    }

    void setEdited() {
        undelete();
        edited = oldValue != null;
    }

    void undelete() {
        if(!deleted) {
            return;
        }
        deleted = false;
        if(value != null && oldValue != null && Id.isNull(value.getFileId())) {
            value.setFile(oldValue.getFileId());
            field.update(this);
        }
    }

    void setDeleted() {
        deleted = true;
        if(value != null) {
            value.setFile((Id)null);
        }
    }

    boolean classChanged() {
        return oldValue != null && value != null && oldValue.getClass() != value.getClass();
    }

    public boolean isRequired() {
        return required;
    }
}
