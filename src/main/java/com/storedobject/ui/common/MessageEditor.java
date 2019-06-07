package com.storedobject.ui.common;

import com.storedobject.core.Message;
import com.storedobject.ui.ObjectEditor;

public abstract class MessageEditor<M extends Message> extends ObjectEditor<M> {

    public MessageEditor(Class<M> objectClass) {
        super(objectClass);
    }

    public MessageEditor(Class<M> objectClass, int actions) {
        super(objectClass, actions);
    }

    public MessageEditor(Class<M> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public MessageEditor(String className) throws Exception {
        super(className);
    }
}