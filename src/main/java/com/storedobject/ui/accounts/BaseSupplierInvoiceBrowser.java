package com.storedobject.ui.accounts;

import com.storedobject.accounts.SupplierInvoice;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.SearchBuilder;

public class BaseSupplierInvoiceBrowser<I extends SupplierInvoice> extends ObjectBrowser<I> {

    public BaseSupplierInvoiceBrowser(Class<I> objectClass) {
        super(objectClass);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, String caption) {
        super(objectClass, caption);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns) {
        super(objectClass, browseColumns);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, filterColumns);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, searchBuilder);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, actions, searchBuilder);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions) {
        super(objectClass, browseColumns, actions);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, actions, filterColumns);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, actions, searchBuilder);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        super(objectClass, browseColumns, actions, caption);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(objectClass, browseColumns, actions, filterColumns, caption);
    }

    public BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption) {
        super(objectClass, browseColumns, actions, searchBuilder, caption);
    }

    protected BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    protected BaseSupplierInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, searchBuilder, caption, allowedActions);
    }

    public BaseSupplierInvoiceBrowser(String className) throws Exception {
        super(className);
    }
}
