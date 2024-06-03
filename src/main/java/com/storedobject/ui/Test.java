package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.report.ObjectList;
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
            TransactionManager tm = getTransactionManager();
            tm.getUser().notify("TEST", tm, "Hello World 5", StoredObject.get(FileData.class),
                    new ObjectList<>(getApplication(), Person.class));
            return true;
        }
        return false;
    }
}