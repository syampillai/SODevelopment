package com.storedobject.core;

public final class Ledger implements java.util.Iterator < com.storedobject.core.LedgerEntry >, java.lang.Iterable < com.storedobject.core.LedgerEntry >, java.io.Closeable {

    public Ledger(com.storedobject.core.Account p1, com.storedobject.core.DatePeriod p2) {
        this();
    }

    private Ledger() {
    }

    protected void finalize() {
    }

    public boolean hasNext() {
        return false;
    }

    public java.util.Iterator < com.storedobject.core.LedgerEntry > iterator() {
        return null;
    }

    public com.storedobject.core.LedgerEntry next() {
        return null;
    }

    public void remove() {
    }

    public void close() {
    }

    public java.sql.Date getDate() {
        return null;
    }

    public com.storedobject.core.Money getBalance() {
        return null;
    }

    public com.storedobject.core.Money getLocalCurrencyBalance() {
        return null;
    }

    public com.storedobject.core.Account getAccount() {
        return null;
    }

    public com.storedobject.core.DatePeriod getPeriod() {
        return null;
    }
}
