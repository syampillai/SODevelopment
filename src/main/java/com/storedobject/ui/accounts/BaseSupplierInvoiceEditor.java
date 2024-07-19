package com.storedobject.ui.accounts;

import com.storedobject.accounts.SupplierInvoice;

public class BaseSupplierInvoiceEditor<I extends SupplierInvoice> extends AbstractInvoiceEditor<I> {

    public BaseSupplierInvoiceEditor(Class<I> objectClass) {
        super(objectClass);
    }

    public BaseSupplierInvoiceEditor(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public BaseSupplierInvoiceEditor(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public BaseSupplierInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected BaseSupplierInvoiceEditor(Class<I> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }
}
