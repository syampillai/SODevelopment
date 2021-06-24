package com.storedobject.mail;

@FunctionalInterface
public interface QuickSender {

    boolean send(String email, String message, String subject, String replyTo);

    default boolean send(String email, String message, String subject, String replyTo, String otp) {
        return send(email, message.replace("<OTP>", otp), subject, replyTo);
    }
}
