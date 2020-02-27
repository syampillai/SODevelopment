package com.storedobject.core;

public interface Authenticator {
    boolean login(Id user, char[] password) throws Exception;
    boolean changePassword(char[] currentPassword, char[] newPassword) throws Exception;
    boolean resetPassword(char[] newPassword) throws Exception;
}