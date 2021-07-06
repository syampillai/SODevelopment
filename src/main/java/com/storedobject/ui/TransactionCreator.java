package com.storedobject.ui;

import com.storedobject.core.Transaction;

/**
 * A class that can create a transaction (typically a pseudo transaction).
 *
 * @author Syam
 */
public interface TransactionCreator {

    /**
     * Get the transaction.
     *
     * @param create True if transaction needs to be created if not exists
     * @return Transaction
     */
    Transaction getTransaction(boolean create);

    /**
     * Set a "Transaction Creator" so that {@link #getTransaction(boolean)} call will use it for creating new
     * transactions.
     *
     * @param transactionCreator Transaction creator
     */
    void setTransactionCreator(TransactionCreator transactionCreator);
}
