package com.storedobject.ui.accounts;

import com.storedobject.accounts.CustomerInvoice;
import com.storedobject.ui.SearchBuilder;

public class CustomerInvoiceBrowser extends BaseCustomerInvoiceBrowser<CustomerInvoice> {
    
    public CustomerInvoiceBrowser() {
        super(CustomerInvoice.class);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns) {
        super(CustomerInvoice.class, browseColumns);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(CustomerInvoice.class, browseColumns, filterColumns);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, SearchBuilder<CustomerInvoice> searchBuilder) {
        super(CustomerInvoice.class, browseColumns, searchBuilder);
    }

    public CustomerInvoiceBrowser(int actions) {
        super(CustomerInvoice.class, actions);
    }

    public CustomerInvoiceBrowser(int actions, SearchBuilder<CustomerInvoice> searchBuilder) {
        super(CustomerInvoice.class, actions, searchBuilder);
    }

    public CustomerInvoiceBrowser(int actions, String caption) {
        super(CustomerInvoice.class, actions, caption);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions) {
        super(CustomerInvoice.class, browseColumns, actions);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(CustomerInvoice.class, browseColumns, actions, filterColumns);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<CustomerInvoice> searchBuilder) {
        super(CustomerInvoice.class, browseColumns, actions, searchBuilder);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, String caption) {
        super(CustomerInvoice.class, browseColumns, actions, caption);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(CustomerInvoice.class, browseColumns, actions, filterColumns, caption);
    }

    public CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<CustomerInvoice> searchBuilder, String caption) {
        super(CustomerInvoice.class, browseColumns, actions, searchBuilder, caption);
    }

    protected CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        super(CustomerInvoice.class, browseColumns, actions, filterColumns, caption, allowedActions);
    }

    protected CustomerInvoiceBrowser(Iterable<String> browseColumns, int actions, SearchBuilder<CustomerInvoice> searchBuilder, String caption, String allowedActions) {
        super(CustomerInvoice.class, browseColumns, actions, searchBuilder, caption, allowedActions);
    }

    public CustomerInvoiceBrowser(String className) throws Exception {
        super(className);
    }
}
