package com.storedobject.core;

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
}
