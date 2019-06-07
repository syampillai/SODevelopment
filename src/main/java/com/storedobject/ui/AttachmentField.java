package com.storedobject.ui;

public class AttachmentField extends FileField {

    public AttachmentField(String caption) {
        super(caption);
    }

    public AttachmentField(String caption, boolean allowRemove) {
        super(caption, allowRemove);
    }

    public AttachmentField(String caption, boolean allowRemove, boolean allowUpload) {
        super(caption, allowRemove, allowUpload);
    }

    public AttachmentField(String label, boolean allowRemove, boolean allowUpload, boolean imageOnly) {
        super(label, allowRemove, allowUpload, imageOnly);
    }
}