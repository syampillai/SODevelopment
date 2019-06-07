package com.storedobject.ui.common;

import com.storedobject.sms.SMSMessage;

public class SMSMessageEditor extends MessageEditor<SMSMessage> {

    public SMSMessageEditor(Class<SMSMessage> objectClass) {
        super(objectClass);
    }

    public SMSMessageEditor(Class<SMSMessage> objectClass, int actions) {
        super(objectClass, actions);
    }

    public SMSMessageEditor(Class<SMSMessage> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public SMSMessageEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected void formConstructed() {
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
