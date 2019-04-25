package com.storedobject.core;

public final class LedgerEntry {

    protected LedgerEntry(java.sql.Date p1, com.storedobject.core.Money p2, com.storedobject.core.Money p3) {
        this();
    }

    private LedgerEntry() {
    }

    public java.sql.Date getDate() {
        return null;
    }

    public com.storedobject.core.Money getAmount() {
        return null;
    }

    public java.lang.String getNarration() {
        return null;
    }

    protected void setRow(java.sql.ResultSet p1) throws java.lang.Exception {
    }

    public com.storedobject.core.Money getLocalCurrencyAmount() {
        return null;
    }
}
