package com.storedobject.core;

public abstract class AbstractTransaction implements Transaction {

    // For internal use only.
    AbstractTransaction(TransactionManager tm) {
    }

    @Override
    public void addCommitListener(CommitListener listener) {
    }

    @Override
    public void removeCommitListener(CommitListener listener) {
    }

    /**
     * Gets the current error in Transaction.
     * A transaction that was closed normally (committed) does not return any error.
     *
     * @return Error if any, otherwise null.
     */
    @Override
    public final Exception getError() {
        return null;
    }

    /**
     * Gets the Id of this transaction.
     *
     * @return Id
     */
    @Override
    public final Id getId() {
        return null;
    }

    /**
     * Gets the TransactionManager associated with this Transaction.
     *
     * @return TransactionManager
     */
    @Override
    public final TransactionManager getManager() {
        return null;
    }

    // For internal use only.
    abstract void credit(StoredObject object, int entrySerial, Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception;
}
