package com.storedobject.core;

/**
 * Interface for defining an external authenticator. If an external authenticator is configured for a particular
 * "password owner", that authenticator will be used for authenticating that type of "password owners". For example,
 * to log in to the application, the "password owner" is {@link SystemUser} and if an external authenticator is
 * configured for {@link SystemUser} class that will be used instead of the built-in one.
 *
 * @author Syam
 */
public interface Authenticator {

    /**
     * Authenticate the given password owner.
     *
     * @param passwordOwner The password owner.
     * @param password Password.
     * @return True if authenticated successfully.
     * @throws Exception If any exception occurred. For example, the login is currently blocked.
     */
    boolean login(StoredObject passwordOwner, char[] password) throws Exception;

    /**
     * Authenticate the given password owner.
     *
     * @param passwordOwner The password owner.
     * @param password Password.
     * @param authenticatorCode Extra authenticator code required - maybe, from a second device. The
     *                          default implementation ignores this and invokes the
     *                          {@link #login(StoredObject, char[])} method.
     * @return True if authenticated successfully.
     * @throws Exception If any exception occurred. For example, the login is currently blocked.
     */
    default boolean login(StoredObject passwordOwner, char[] password, int authenticatorCode) throws Exception {
        return login(passwordOwner, password);
    }

    /**
     * Change the current password to a new one.
     *
     * @param passwordOwner The password owner.
     * @param currentPassword Current password.
     * @param newPassword New password
     * @return True if changed successfully.
     * @throws Exception If any exception occurred.
     */
    boolean changePassword(StoredObject passwordOwner, char[] currentPassword, char[] newPassword) throws Exception;

    /**
     * Reset the password to the initial default password.
     *
     * @param passwordOwner The password owner.
     * @return True if password reset is successful.
     * @throws Exception If any exception occurred.
     */
    boolean resetPassword(StoredObject passwordOwner) throws Exception;

    /**
     * This method is invoked when a user is locked because of invalid login attempts. So, this will be invoked only
     * for {@link SystemUser}s.
     *
     * @param passwordOwner The password owner.
     */
    default void locked(SystemUser passwordOwner) {
    }

    /**
     * This method is invoked when a locked user is unlocked. So, this will be invoked only for {@link SystemUser}s.
     *
     * @param passwordOwner The password owner.
     */
    default void unlocked(SystemUser passwordOwner) {
    }

    /**
     * This method is invoked when a user fails to log in. So, this will be invoked only for {@link SystemUser}s.
     *
     * @param passwordOwner The password owner. This could be <code>null</code> if no "Password Owner" exists
     *                                for the login name attempted.
     * @param user Login/user name tried.
     * @param ip IP address from which the login was attempted.
     * @param deviceIdentifier Device identifier.
     */
    default void loginFailed(SystemUser passwordOwner, String user, String ip, String deviceIdentifier) {
    }
}
