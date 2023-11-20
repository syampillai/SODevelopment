package com.storedobject.core;

public class AccountDocument extends Document<Account> {

    public AccountDocument() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    protected Class<Account> getOwnerClass() {
        return Account.class;
    }

    @Override
    protected boolean allowAny() {
        return true;
    }
}
