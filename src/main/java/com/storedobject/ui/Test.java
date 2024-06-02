package com.storedobject.ui;

import com.storedobject.core.Account;
import com.storedobject.core.TransactionManager;
import com.storedobject.ui.accounts.AccountField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements Transactional {

    private final AccountField<Account> accountField = new AccountField<>("Account");

    public Test() {
        super("Test");
        add(accountField);
    }

    @Override
    protected boolean process() {
        Account account = accountField.getAccount();
        if (account == null) {
            String s = "Hello World";
            message(s);
            TransactionManager tm = getTransactionManager();
            tm.getUser().notify("DEFAULT", tm, s);
            return true;
        }
        return false;
    }
}