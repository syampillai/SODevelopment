package com.storedobject.core;

public interface Authenticator {
    boolean login(Id user, char[] password) throws Exception;
    boolean changePassword(String currentPassword, String newPassword) throws Exception;
    boolean resetPassword(String newPassword) throws Exception;
}