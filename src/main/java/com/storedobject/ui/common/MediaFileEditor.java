package com.storedobject.ui.common;

import com.storedobject.core.MediaFile;
import com.storedobject.ui.ObjectEditor;

public class MediaFileEditor extends ObjectEditor<MediaFile> {

    public MediaFileEditor() {
        super(MediaFile.class);
    }

    public MediaFileEditor(int actions) {
        super(MediaFile.class, actions);
    }

    public MediaFileEditor(int actions, String caption) {
        super(MediaFile.class, actions, caption);
    }

    public MediaFileEditor(String className) throws Exception {
        super(className);
    }
}