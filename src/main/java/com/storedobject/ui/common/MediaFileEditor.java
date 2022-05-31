package com.storedobject.ui.common;

import com.storedobject.core.MediaFile;
import com.storedobject.ui.FileField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.util.SOServlet;
import com.vaadin.flow.component.HasValue;

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

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("File".equals(fieldName)) {
            ((FileField)((ObjectField<?>)field).getField()).allowDownload();
        }
        super.customizeField(fieldName, field);
    }

    @Override
    public void saved(MediaFile object) {
        SOServlet.removeCache(object);
    }
}