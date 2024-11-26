package com.storedobject.sms;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.SOException;

/**
 * Interface to send out an SMS quickly.
 *
 * @author Syam
 */
@FunctionalInterface
public interface QuickSender {

    /**
     * Send an SMS.
     *
     * @param mobileNumber Mobile number.
     * @param message Message text.
     * @return True if sent successfully.
     */
    boolean send(String mobileNumber, String message);

    /**
     * Send an SMS.
     *
     * @param mobileNumber Mobile number.
     * @param message Message text.
     * @param otp OTP value if it needs to be included. The message will contain a string containing the word OTP in
     *            angle brackets that can be replaced with this while sending out the message.
     * @return True if sent successfully.
     */
    default boolean send(String mobileNumber, String message, String otp) {
        return send(mobileNumber, message, otp, null);
    }

    /**
     * Send an SMS.
     *
     * @param mobileNumber Mobile number.
     * @param message Message text.
     * @param otp OTP value if it needs to be included. The message will contain a string containing the word OTP in
     *            angle brackets that can be replaced with this while sending out the message.
     * @param customTag Custom tag set if any.
     * @return True if sent successfully.
     */
    default boolean send(String mobileNumber, String message, String otp, String customTag) {
        return send(mobileNumber, message.replace("<OTP>", otp));
    }

    /**
     * Creates an instance of QuickSender by loading the class associated with the "application.quick.sms" property.
     *
     * @return An instance of QuickSender.
     * @throws SOException If there is an error creating the instance.
     */
    static QuickSender create() throws SOException {
        return (QuickSender) JavaClassLoader.createInstanceFromProperty("application.quick.sms");
    }
}
