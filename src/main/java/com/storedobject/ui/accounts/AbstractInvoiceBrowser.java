package com.storedobject.ui.accounts;

import com.storedobject.accounts.Invoice;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.SearchBuilder;

public abstract class AbstractInvoiceBrowser<I extends Invoice> extends ObjectBrowser<I> {

    public AbstractInvoiceBrowser(Class<I> objectClass) {
        super(objectClass);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, String caption) {
        super(objectClass, caption);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns) {
        super(objectClass, browseColumns);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, filterColumns);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, searchBuilder);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, actions, searchBuilder);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions) {
        super(objectClass, browseColumns, actions);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, actions, filterColumns);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, actions, searchBuilder);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        super(objectClass, browseColumns, actions, caption);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(objectClass, browseColumns, actions, filterColumns, caption);
    }

    public AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption) {
        super(objectClass, browseColumns, actions, searchBuilder, caption);
    }

    protected AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    protected AbstractInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, searchBuilder, caption, allowedActions);
    }

    public AbstractInvoiceBrowser(String className) throws Exception {
        super(className);
    }
}
