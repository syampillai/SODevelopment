package com.storedobject.ui.accounts;

import com.storedobject.accounts.CustomerInvoice;
import com.storedobject.ui.SearchBuilder;

public class BaseCustomerInvoiceBrowser<I extends CustomerInvoice> extends AbstractInvoiceBrowser<I> {

    public BaseCustomerInvoiceBrowser(Class<I> objectClass) {
        super(objectClass);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, String caption) {
        super(objectClass, caption);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns) {
        super(objectClass, browseColumns);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, filterColumns);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, searchBuilder);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, actions, searchBuilder);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions) {
        super(objectClass, browseColumns, actions);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(objectClass, browseColumns, actions, filterColumns);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder) {
        super(objectClass, browseColumns, actions, searchBuilder);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        super(objectClass, browseColumns, actions, caption);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(objectClass, browseColumns, actions, filterColumns, caption);
    }

    public BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption) {
        super(objectClass, browseColumns, actions, searchBuilder, caption);
    }

    protected BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    protected BaseCustomerInvoiceBrowser(Class<I> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<I> searchBuilder, String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, searchBuilder, caption, allowedActions);
    }

    public BaseCustomerInvoiceBrowser(String className) throws Exception {
        super(className);
    }
}
