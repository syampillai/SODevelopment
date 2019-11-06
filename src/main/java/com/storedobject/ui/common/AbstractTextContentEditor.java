package com.storedobject.ui.common;

import com.storedobject.core.TextContent;
import com.storedobject.ui.ObjectEditor;

public class AbstractTextContentEditor<T extends TextContent> extends ObjectEditor<T> {

    public AbstractTextContentEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public AbstractTextContentEditor(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractTextContentEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public AbstractTextContentEditor(String className) throws Exception {
        super(className);
    }
}