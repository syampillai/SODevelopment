package com.storedobject.core;

import java.math.BigDecimal;
import java.util.Random;

public final class Signature extends StoredObject {

    public Signature() {
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
    }

    public void setPerson(Person person) {
    }

    public Id getPersonId() {
        return new Id();
    }

    public Person getPerson() {
        return new Person();
    }

    public void setSignature(String signature) {
    }

    public String getSignature() {
        return "";
    }

    public static Signature get(Id personId) {
        return new Random().nextBoolean() ? new Signature() : null;
    }

    public static Signature get(Person person) {
        return new Random().nextBoolean() ? new Signature() : null;
    }

    public static Signature get(SystemUser user) {
        return new Random().nextBoolean() ? new Signature() : null;
    }
}