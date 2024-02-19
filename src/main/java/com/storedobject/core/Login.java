package com.storedobject.core;

import java.util.Random;

public final class Login {

    public Login(Device device) {
        this(device, null);
    }

    public Login(Device device, MessageViewer messageViewer) {
    }

    public boolean login(String user, char[] password) {
        return false;
    }

    public boolean login(String user, char[] password, int authenticatorCode) {
        return false;
    }

    public boolean login(String user, char[] password, boolean lockIfFailed) {
        return false;
    }

    public boolean login(String user, char[] password, int authenticatorCode, boolean lockIfFailed) {
        return false;
    }

    public boolean login(WebBiometric biometric, String biometricID, String type, String authenticatorData,
                         String clientDataJSON, String signature, String userHandle, boolean lockIfFailed) {
        return false;
    }

    public boolean login(String loginBlock) {
        return false;
    }

    public boolean login(String loginBlock, String via) {
        return false;
    }

    public boolean forgotPassword(String user) {
        return false;
    }

    public boolean isBlocked() {
        return false;
    }

    public boolean canRetry() {
        return false;
    }

    public boolean isLoggedIn() {
        return false;
    }

    public boolean isWelcomePassword() {
        return false;
    }

    public void close() {
    }

    public int getType() {
        return new Random().nextInt();
    }

    public void setType(int type) {
    }
}
