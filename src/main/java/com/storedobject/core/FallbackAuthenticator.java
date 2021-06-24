package com.storedobject.core;

public interface FallbackAuthenticator extends Authenticator {

    @Override
    default boolean changePassword(Id passwordOwner, char[] currentPassword, char[] newPassword) throws Exception {
        return true;
    }

    @Override
    default boolean resetPassword(Id passwordOwner) throws Exception {
        return true;
    }

    TransactionManager getTransactionManager();
}
