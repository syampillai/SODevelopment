package com.storedobject.ui;

import com.storedobject.core.Account;
import com.storedobject.ui.accounts.AccountField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private final AccountField<Account> accountField = new AccountField<>("Account");

    public Test() {
        super("Test");
        add(accountField);
    }

    @Override
    protected boolean process() {
        Account account = accountField.getAccount();
        if(account != null) {
            message(account.toDisplay());
        }
        return false;
    }
}