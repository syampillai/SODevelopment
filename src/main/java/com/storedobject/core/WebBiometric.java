package com.storedobject.core;

import java.math.BigDecimal;

public final class WebBiometric extends StoredObject {

    public WebBiometric() {
    }

    public static void columns(Columns columns) {
    }

    public void setBiometricID(String biometricID) {
    }

    public String getBiometricID() {
        return "";
    }

    public void setCounter(int counter) {
    }

    public int getCounter() {
        return 0;
    }

    public void setPublicKey(String publicKey) {
    }

    public String getPublicKey() {
        return "";
    }

    public void setAttestation(String attestation) {
    }

    public String getAttestation() {
        return "";
    }

    public void setLogin(Id loginId) {
    }

    public void setLogin(BigDecimal idValue) {

    }

    public void setLogin(SystemUser login) {
    }

    public Id getLoginId() {
        return new Id();
    }

    public SystemUser getLogin() {
        return new SystemUser();
    }

    public void setDisabled(boolean disabled) {
    }

    public boolean getDisabled() {
        return false;
    }

    public void setDeviceName(String deviceName) {
    }

    public String getDeviceName() {
        return "";
    }

    public boolean duplicateDeviceName(String deviceName) {
        return false;
    }

    public static WebBiometric get(SystemUser user, Id id) {
        return new WebBiometric();
    }

    public static ObjectIterator<WebBiometric> list(SystemUser user) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<WebBiometric> listDisabled(SystemUser user) {
        return ObjectIterator.create();
    }

    public String getChallenge() {
        return "";
    }

    public String getUserID() {
        return "";
    }

    public String getUserName() {
        return "";
    }

    public void setWebURL(String webURL) {
    }

    public boolean register(TransactionManager tm, String biometricID, String type,
                            String attestationObject, String clientDataJSON) {
        return false;
    }
}
