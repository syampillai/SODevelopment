package com.storedobject.accounts;

import com.storedobject.core.TransactionManager;

/**
 * Interface for locating and retrieving an EntityAccount.
 * <p></p>
 * The AccountFinder interface defines a single method `find` that
 * takes a TransactionManager as a parameter and returns an instance
 * of EntityAccount. The implementation of this interface determines
 * the criteria and logic used to locate the account. An implementation may create an instance using
 * the TransactionManager passed as the parameter if an account is not found.
 *
 * @author Syam
 */
public interface AccountFinder {

    /**
     * Finds an EntityAccount.
     * @param tm TransactionManager to use for creating an instance if not found
     * @return EntityAccount instance or null if not found
     */
    EntityAccount getAccount(TransactionManager tm);
}
