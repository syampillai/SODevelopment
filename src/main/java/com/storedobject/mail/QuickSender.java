package com.storedobject.mail;

/**
 * Interface to send out an SMS quickly.
 *
 * @author Syam
 */
@FunctionalInterface
public interface QuickSender {

    /**
     * Send an email.
     *
     * @param email Recipient email address..
     * @param message Message text.
     * @param subject Subject - could be null.
     * @param replyTo Reply to address - could be null.
     * @return True if sent successfully.
     */
    boolean send(String email, String message, String subject, String replyTo);

    /**
     * Send an email.
     *
     * @param email Recipient email address..
     * @param message Message text.
     * @param subject Subject - could be null.
     * @param replyTo Reply to address - could be null.
     * @param otp OTP value if needs to be included. The message will contain a string containing the word OTP in
     *            angle brackets that can be replaced with this while sending out the message.
     * @return True if sent successfully.
     */
    default boolean send(String email, String message, String subject, String replyTo, String otp) {
        return send(email, message, subject, replyTo, otp, null);
    }

    /**
     * Send an email.
     *
     * @param email Recipient email address..
     * @param message Message text.
     * @param subject Subject - could be null.
     * @param replyTo Reply to address - could be null.
     * @param otp OTP value if needs to be included. The message will contain a string containing the word OTP in
     *            angle brackets that can be replaced with this while sending out the message.
     * @param customTag Custom tag set if any.
     * @return True if sent successfully.
     */
    default boolean send(String email, String message, String subject, String replyTo, String otp, String customTag) {
        return send(email, message.replace("<OTP>", otp), subject, replyTo);
    }
}
