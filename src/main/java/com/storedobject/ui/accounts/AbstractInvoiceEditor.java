package com.storedobject.ui.accounts;

import com.storedobject.accounts.Invoice;
import com.storedobject.ui.ObjectEditor;

public abstract class AbstractInvoiceEditor<I extends Invoice> extends ObjectEditor<I> {

    public AbstractInvoiceEditor(Class<I> objectClass) {
        super(objectClass);
    }

    public AbstractInvoiceEditor(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractInvoiceEditor(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public AbstractInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected AbstractInvoiceEditor(Class<I> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }
}
