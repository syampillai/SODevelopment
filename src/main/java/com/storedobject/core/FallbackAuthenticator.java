package com.storedobject.core;

/**
 * Unlike {@link Authenticator}, if this is plugged in as the external authenticator, it will be used only as
 * a fallback authenticator. That means, the {@link #login(StoredObject, char[])} and
 * {@link #login(StoredObject, char[], int)} methods will be invoked only if the internal authentication fails.
 * The methods {@link #changePassword(StoredObject, char[], char[])} and {@link #resetPassword(StoredObject)} are
 * never called.
 * <p>If the internal authentication succeeds and it will still invoke the {@link #login(StoredObject, char[], int)}
 * method to check the extra "authenticator code". In that case, the password passed will be <code>null</code>.</p>
 *
 * @author Syam
 */
public interface FallbackAuthenticator extends Authenticator {

    @Override
    default boolean changePassword(StoredObject passwordOwner, char[] currentPassword, char[] newPassword)
            throws Exception {
        return true;
    }

    @Override
    default boolean resetPassword(StoredObject passwordOwner) throws Exception {
        return true;
    }

    /**
     * Check if the password owner exists.
     *
     * @param passwordOwner The password owner.
     * @return True/false.
     */
    boolean exists(StoredObject passwordOwner);
}
