package com.storedobject.sms;

@FunctionalInterface
public interface QuickSender {

    boolean send(String mobileNumber, String message);

    default boolean send(String mobileNumber, String message, String otp) {
        return send(mobileNumber, message.replace("<OTP>", otp));
    }
}
