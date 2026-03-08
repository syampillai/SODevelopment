package com.storedobject.core;

/**
 * Interface for locating and retrieving an {@link Account} related to an object instance. Such an object should
 * implement this interface to provide a way to find the account associated with it.
 * <p></p>
 * The AccountFinder interface defines a single method  {@link #getAccount(TransactionManager)} that
 * takes a TransactionManager as a parameter and returns an instance
 * of Account. The implementation of this interface determines
 * the criteria and logic used to locate the account. An implementation may create an instance using
 * the TransactionManager passed as the parameter if an account is not found.
 *
 * @author Syam
 */
@FunctionalInterface
public interface AccountFinder {

    /**
     * Finds an EntityAccount.
     *
     * @param tm TransactionManager to use for creating an instance if not found
     * @return An {@link Account} instance or null if not found
     * @throws Exception if the account cannot be found and cannot be created
     */
    Account getAccount(TransactionManager tm) throws Exception;
}
