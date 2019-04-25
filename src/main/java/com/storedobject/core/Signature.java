package com.storedobject.core;

import java.math.BigDecimal;

public class Signature extends StoredObject {

    public Signature() {
    }

    public static void columns(Columns columns) {
    }

    public void setLogin(Id loginId) {
    }

    public void setLogin(BigDecimal idValue) {
    }

    public void setLogin(SystemUser login) {
    }

    public Id getLoginId() {
        return null;
    }

    public SystemUser getLogin() {
        return null;
    }

    public void setSignature(Id signatureId) {
    }

    public void setSignature(BigDecimal idValue) {
    }

    public void setSignature(StreamData signature) {
    }

    public Id getSignatureId() {
        return null;
    }

    public StreamData getSignature() {
        return null;
    }
    
    public static Signature get(SystemUser systemUser) {
    	return null;
    }
    
    public static Signature get(Person person) {
    	return null;
    }
}