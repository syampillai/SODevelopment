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
     * Get the OTP tag that needs to be used when sending OTP.
     *
     * @return OTP tag. (Default value is "FP").
     */
    default String getOTPTag() {
        return "FP";
    }

    /**
     * Get the template t be used when sending OTP messages while verifying.
     *
     * @return The name of the OTP message template.
     */
    default String getOTPTemplate() {
        return null;
    }

    /**
     * Get the "exit site" to exit the application when failed to send OTP due to technical errors.
     *
     * @return Exit site. (Default is null).
     */
    default String getOTPExitSite() {
        return null;
    }

    /**
     * Get the "error message" to be shown when failed to send OTP due to technical errors. Application will exit
     * after showing this message. (Specify {@link #getOTPExitSite()} if you want to exit to another site).
     *
     * @return Error message. (Default is null).
     */
    default String getOTPErrorMessage() {
        return null;
    }

    /**
     * Get the OTP timeout in seconds.
     *
     * @return OTP Timeout. (Default is 180 seconds).
     */
    default int getOTPTimeout() {
        return 180;
    }

    /**
     * This method is invoked when a "password change" is carried out successfully.
     */
    default void passwordChangeSucceeded() {
    }

    /**
     * Get the message to be shown when a "password change" is done successfully.
     *
     * @return Message to display.
     */
    default String getSuccessMessage() {
        return null;
    }

    /**
     * This method is invoked when a "password change" fails.
     */
    default void passwordChangeFailed() {
    }

    /**
     * Get the message to be shown when a "password change" fails.
     *
     * @return Message to display.
     */
    default String getFailureMessage() {
        return null;
    }

    /**
     * Get the caption used when changing the password.
     *
     * @return Caption text.
     */
    default String getPasswordChangeCaption() {
        return null;
    }

    /**
     * Whether to allow username change while setting the password or not.
     *
     * @return True/false.
     */
    default boolean allowNameChange() {
        return false;
    }


    /**
     * Get the site to go when password is successfully set.
     *
     * @return Site.
     */
    default String getExitSite() {
        return null;
    }
}
