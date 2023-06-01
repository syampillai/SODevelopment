package com.storedobject.core;

/**
 * Transaction. All protected methods of this class are for internal purpose only.
 */
public final class DBTransaction extends AbstractTransaction {

    // For internal use only.
    DBTransaction(TransactionManager tm) throws Exception {
        super(tm);
    }

    // For internal use only.
    DBTransaction() throws Exception {
        this(null);
    }

    @Override
    public <T extends StoredObject> T get(T object) {
        return null;
    }

    @Override
    public <T extends StoredObject> T get(Class<T> objectClass, Id objectId) {
        return null;
    }

    // For internal use only.
    @Override
    public StoredObject get(Id objectId) {
        return null;
    }

    /**
     * See if this Id is involved in this transaction or not.
     *
     * @param id Id to be checked.
     * @return True if involved.
     */
    @Override
    public boolean isInvolved(Id id) {
        return false;
    }

    // For internal use only.
    @SuppressWarnings("unused")
    void credit(JournalVoucher object, int entrySerial, Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception {
    }

    /**
     * Commit the transaction.
     * @throws Exception Any
     */
    @Override
    public void commit() throws Exception {
    }

    /**
     * Rollback the transaction.
     */
    @Override
    public void rollback() {
    }

    /**
     * See if this transaction is active or not.
     * @return True if active. False if already committed or rolled back.
     */
    @Override
    public boolean isActive() {
        return false;
    }
}