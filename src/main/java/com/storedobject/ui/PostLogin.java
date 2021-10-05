package com.storedobject.ui;

import com.storedobject.core.TransactionManager;

/**
 * If class that implements this interface is configured, it will be instantiated and queried when the user
 * logs in.
 *
 * @author Syam
 */
public interface PostLogin {

    /**
     * Check whether the user can really use the current application that he/she has already logged in. If
     * <code>false</code> is returned from this, {@link #informUser()} will be invoked to inform this fact to the user
     * or close the application so that access is restricted.
     *
     * @param tm Current transaction manager.
     *
     * @return True/false.
     */
    boolean canLogin(TransactionManager tm);

    /**
     * This method is invoked only if {@link #canLogin(TransactionManager)} returns <code>false</code>. Typically,
     * the user may be notified about it and the application may be closed at this point.
     */
    void informUser();
}
