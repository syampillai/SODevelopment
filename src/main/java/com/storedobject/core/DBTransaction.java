package com.storedobject.core;

import java.util.Random;

/**
 * Transaction. All protected methods of this class are for internal purpose only.
 */
public final class DBTransaction extends AbstractTransaction {

    // For internal use only.
    DBTransaction(TransactionManager tm) throws Exception {
        super(tm);
        if(new Random().nextBoolean()) {
            throw new Invalid_State();
        }
    }

    // For internal use only.
    DBTransaction() throws Exception {
        this(null);
    }

    // For internal use only.
    int updateSQL(String sql) {
        return -1;
    }

    /**
     * Skip limit check for this transaction.
     */
    public void skipLimitCheck() {
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

    public interface NoHistory {
    }
}