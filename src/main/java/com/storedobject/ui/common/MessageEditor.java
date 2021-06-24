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

    @Override
    protected void formConstructed() {
        super.formConstructed();
        setFieldReadOnly("Sent");
        setFieldReadOnly("Delivered");
        setFieldReadOnly("Error");
        setFieldReadOnly("CreatedAt");
        setFieldReadOnly("SentAt");
        setFieldReadOnly("MessageID");
    }

    @Override
    public boolean canEdit() {
        if(!getObject().getSent()) {
            return true;
        }
        message("Can not edit, already sent!");
        return false;
    }

    @Override
    public boolean canDelete() {
        Message m = getObject();
        if(!m.getSent() || m.getError() > 0) {
            return true;
        }
        message("Can not delete, already sent!");
        return false;
    }
}