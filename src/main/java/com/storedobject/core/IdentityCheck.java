package com.storedobject.core;

import java.util.Random;

/**
 * Interface to identify a {@link SystemUser}. This is typically used for carrying out a secured action.
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

    /**
     * Whether the OTP-based checking should be skipped or not. A custom implementation of this method can verify the
     * identity of the user through other means and return <code>true</code> so that OTP verification can be skipped.
     *
     * @param device Device on which identification process is on.
     * @return True/false.
     */
    default boolean skipOTP(Device device) {
        return false;
    }

    /**
     * Carry out the secured action.
     *
     * @param action Action to carry out.
     */
    default void doAction(Runnable action) {
        action.run();
    }

    /**
     * Generate a prefix with a specific format.
     * The prefix consists of three random uppercase letters followed by a colon.
     * Certain inappropriate prefixes like "ASS", "FUK", etc., are avoided and regenerated.
     *
     * @return A randomly generated prefix in the format "XXX:" where XXX are uppercase letters.
     */
    static String generatePrefix() {
        Random random = new Random();
        char[] p = new char[3];
        for(int i = 0; i < 3; i++) {
            p[i] = (char) ('A' + random.nextInt(26));
        }
        String s = new String(p);
        return switch(s) {
            case "ASS", "FUK", "FUC", "PIG", "OTP", "TIT" -> generatePrefix();
            default -> s + ":";
        };
    }

    /**
     * Generates a 6-digit One-Time Password (OTP).
     *
     * @return A randomly generated 6-digit OTP.
     */
    static int generateOTP() {
        Random random = new Random();
        int o = 0;
        while(!(o > 100000 && o < 1000000)) {
            o = random.nextInt();
        }
        return o;
    }
}
