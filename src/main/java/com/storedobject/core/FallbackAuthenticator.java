package com.storedobject.core;

/**
 * Unlike {@link Authenticator}, if this is plugged in as the external authenticator, it will be used only as
 * a fallback authenticator. That means, the {@link #login(Id, char[])} and {@link #login(Id, char[], int)} methods
 * will be invoked only if the internal authentication fails. The methods {@link #changePassword(Id, char[], char[])}
 * and {@link #resetPassword(Id)} are never called.
 * <p>If the internal authentication succeeds and it will still invoke the {@link #login(Id, char[], int)} method to
 * check the extra "authenticator code". In that case, the password passed will be <code>null</code>.</p>
 *
 * @author Syam
 */
public interface FallbackAuthenticator extends Authenticator {

    @Override
    default boolean changePassword(Id passwordOwner, char[] currentPassword, char[] newPassword) throws Exception {
        return true;
    }

    @Override
    default boolean resetPassword(Id passwordOwner) throws Exception {
        return true;
    }

    /**
     * Check if the password owner exists.
     *
     * @param passwordOwner {@link Id} of the password owner.
     * @return True/false.
     */
    boolean exists(Id passwordOwner);
}
