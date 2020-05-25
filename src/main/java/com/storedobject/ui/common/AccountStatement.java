package com.storedobject.ui.common;

import com.storedobject.core.Account;
import com.storedobject.core.DatePeriod;
import com.storedobject.vaadin.DataForm;

public class AccountStatement extends DataForm {

    public AccountStatement() {
        super("Account Statement");
    }

    public AccountStatement(Account account) {
        this();
    }

    public AccountStatement(Account account, DatePeriod datePeriod) {
        this();
    }

    @Override
    protected boolean process() {
        return true;
    }
}