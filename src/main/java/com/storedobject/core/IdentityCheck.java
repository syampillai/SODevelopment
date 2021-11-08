package com.storedobject.core;

/**
 * Interface to identify a {@link SystemUser}.
 *
 * @author Syam
 */
public interface IdentityCheck {

    /**
     * Set the user to identify.
     *
     * @param user User to identify.
     */
    void setUser(SystemUser user);

    /**
     * Get the user to identify.
     *
     * @return  User to identify.
     */
    SystemUser getUser();

    /**
     * Get the email of this user.
     *
     * @return Email or null if no email is available.
     */
    default String getEmail() {
        SystemUser u = getUser();
        return u == null ? null : u.getPerson().getContact("email");
    }

    /**
     * Get the mobile number of this user.
     *
     * @return Mobile number or null if no mobile number is available.
     */
    default String getMobile() {
        SystemUser u = getUser();
        return u == null ? null : u.getPerson().getContact("mobile");
    }

    /**
     * Check whether the same OPT should be sent to both email and mobile for verification.
     *
     * @return True/false.
     */
    default boolean isSingleOTP() {
        return false;
    }

    /**
     * Get the template t be used when sending OTP messages while verifying.
     *
     * @return The name of the OTP message template.
     */
    default String getOTPTemplate() {
        return null;
    }
}
