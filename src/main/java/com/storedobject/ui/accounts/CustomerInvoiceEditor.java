package com.storedobject.ui.accounts;

import com.storedobject.accounts.CustomerInvoice;

public class CustomerInvoiceEditor extends BaseCustomerInvoiceEditor<CustomerInvoice> {

    public CustomerInvoiceEditor() {
        super(CustomerInvoice.class);
    }

    public CustomerInvoiceEditor(int actions) {
        super(CustomerInvoice.class, actions);
    }

    public CustomerInvoiceEditor(int actions, String caption) {
        super(CustomerInvoice.class, actions, caption);
    }

    public CustomerInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected CustomerInvoiceEditor(int actions, String caption, String allowedActions) {
        super(CustomerInvoice.class, actions, caption, allowedActions);
    }
}
