package com.storedobject.ui.accounts;

import com.storedobject.accounts.SupplierInvoice;
import com.storedobject.ui.SearchBuilder;

public class SupplierInvoiceBrowser extends BaseSupplierInvoiceBrowser<SupplierInvoice> {

    public SupplierInvoiceBrowser() {
        super(SupplierInvoice.class);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns) {
        super(SupplierInvoice.class, browseColumns);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(SupplierInvoice.class, browseColumns, filterColumns);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, SearchBuilder<SupplierInvoice> searchBuilder) {
        super(SupplierInvoice.class, browseColumns, searchBuilder);
    }

    public SupplierInvoiceBrowser(int actions) {
        super(SupplierInvoice.class, actions);
    }

    public SupplierInvoiceBrowser(int actions, SearchBuilder<SupplierInvoice> searchBuilder) {
        super(SupplierInvoice.class, actions, searchBuilder);
    }

    public SupplierInvoiceBrowser(int actions, String caption) {
        super(SupplierInvoice.class, actions, caption);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions) {
        super(SupplierInvoice.class, browseColumns, actions);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(SupplierInvoice.class, browseColumns, actions, filterColumns);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<SupplierInvoice> searchBuilder) {
        super(SupplierInvoice.class, browseColumns, actions, searchBuilder);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, String caption) {
        super(SupplierInvoice.class, browseColumns, actions, caption);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(SupplierInvoice.class, browseColumns, actions, filterColumns, caption);
    }

    public SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<SupplierInvoice> searchBuilder, String caption) {
        super(SupplierInvoice.class, browseColumns, actions, searchBuilder, caption);
    }

    protected SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(SupplierInvoice.class, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    protected SupplierInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<SupplierInvoice> searchBuilder, String caption, String allowedActions) {
        super(SupplierInvoice.class, browseColumns, actions, searchBuilder, caption, allowedActions);
    }

    public SupplierInvoiceBrowser(String className) throws Exception {
        super(className);
    }
}
