package com.storedobject.ui.accounts;

import com.storedobject.accounts.CustomerInvoice;

public class BaseCustomerInvoiceEditor<I extends CustomerInvoice> extends AbstractInvoiceEditor<I> {

    public BaseCustomerInvoiceEditor(Class<I> objectClass) {
        super(objectClass);
    }

    public BaseCustomerInvoiceEditor(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public BaseCustomerInvoiceEditor(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public BaseCustomerInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected BaseCustomerInvoiceEditor(Class<I> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }
}
