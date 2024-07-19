package com.storedobject.ui.accounts;

import com.storedobject.accounts.SupplierInvoice;

public class SupplierInvoiceEditor extends BaseSupplierInvoiceEditor<SupplierInvoice> {

    public SupplierInvoiceEditor() {
        super(SupplierInvoice.class);
    }

    public SupplierInvoiceEditor(int actions) {
        super(SupplierInvoice.class, actions);
    }

    public SupplierInvoiceEditor(int actions, String caption) {
        super(SupplierInvoice.class, actions, caption);
    }

    public SupplierInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected SupplierInvoiceEditor(int actions, String caption, String allowedActions) {
        super(SupplierInvoice.class, actions, caption, allowedActions);
    }
}
