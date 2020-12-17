package com.storedobject.core;

public interface Authenticator {

    boolean login(Id passwordOwner, char[] password) throws Exception;

    default boolean login(Id passwordOwner, char[] password, int authenticatorCode) throws Exception {
        return login(passwordOwner, password);
    }

    boolean changePassword(Id passwordOwner, char[] currentPassword, char[] newPassword) throws Exception;

    boolean resetPassword(Id passwordOwner) throws Exception;
}
