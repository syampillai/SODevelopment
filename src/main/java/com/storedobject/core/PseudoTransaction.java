package com.storedobject.core;

public final class PseudoTransaction extends AbstractTransaction {

    PseudoTransaction(TransactionManager tm) {
        super(tm);
    }

    @Override
    public <T extends StoredObject> T get(T object) {
        return null;
    }

    @Override
    public <T extends StoredObject> T get(Class<T> objectClass, Id objectId) {
        return null;
    }

    @Override
    public boolean isInvolved(Id id) {
        return false;
    }

    @Override
    public void commit() throws Exception {
    }

    public void commit(DBTransaction transaction) throws Exception {
    }

    @Override
    public void rollback() {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public Id getId(StoredObject object) {
        return null;
    }

    public void replace(Id idToReplace, StoredObject newObject) {
    }

    @Override
    void credit(StoredObject object, int entrySerial, Account account, Money amount, Money localCurrencyAmount, String narration) {
    }
}