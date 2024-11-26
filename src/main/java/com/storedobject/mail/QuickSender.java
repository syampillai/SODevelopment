package com.storedobject.mail;

import com.storedobject.core.ApplicationServer;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.SOException;
import com.storedobject.core.StoredObject;

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
     * @param otp OTP value if it needs to be included. The message will contain a string containing the word OTP in
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
     * @param otp OTP value if it needs to be included. The message will contain a string containing the word OTP in
     *            angle brackets that can be replaced with this while sending out the message.
     * @param customTag Custom tag set if any.
     * @return True if sent successfully.
     */
    default boolean send(String email, String message, String subject, String replyTo, String otp, String customTag) {
        return send(email, message.replace("<OTP>", otp), subject, replyTo);
    }

    /**
     * Creates an instance of QuickSender by loading the class associated with the "application.quick.mail" property.
     *
     * @return An instance of QuickSender.
     * @throws SOException If there is an error creating the instance.
     */
    static QuickSender create() throws SOException {
        String logicName = ApplicationServer.getGlobalProperty("application.quick.mail", "", true);
        if(!logicName.isBlank()) {
            return (QuickSender) JavaClassLoader.createInstance(logicName);
        }
        Sender sender = StoredObject.get(Sender.class, "SenderGroup.Alert AND Status=0", true);
        if(sender == null) {
            sender = StoredObject.get(Sender.class, "Status=0", true);
        }
        if(sender == null) {
            throw new SOException("No active mail sender found");
        }
        Sender finalSender = sender;
        return (email, message, subject, replyTo) -> {
            try {
                finalSender.sendTestMail(email, subject, message);
            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }
}
