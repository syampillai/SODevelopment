package com.storedobject.ui.account;

import com.storedobject.core.Account;
import com.storedobject.core.DatePeriod;
import com.storedobject.vaadin.DataForm;

public class Statement extends DataForm {

    public Statement() {
        super("Account Statement");
    }

    public Statement(Account account) {
        this();
    }

    public Statement(Account account, DatePeriod datePeriod) {
        this();
    }

    @Override
    protected boolean process() {
        return true;
    }
}