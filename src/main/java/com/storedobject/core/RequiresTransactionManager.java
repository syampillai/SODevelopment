package com.storedobject.core;

/**
 * Interface for objects that require a transaction manager.
 *
 * @author Syam
 */
public interface RequiresTransactionManager {

    /**
     * Sets the transaction manager for this object. The transaction manager
     * is responsible for managing transaction boundaries.
     *
     * @param tm the transaction manager to be set
     */
    void setTransactionManager(TransactionManager tm);

    /**
     * Retrieves the transaction manager associated with this object.
     *
     * @return the transaction manager responsible for managing transaction boundaries
     */
    TransactionManager getTransactionManager();
}
