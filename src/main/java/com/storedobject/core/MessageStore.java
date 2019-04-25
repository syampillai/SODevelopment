package com.storedobject.core;

import java.math.BigDecimal;

public class MessageStore extends StoredObject {

    public MessageStore() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
    }

    @Override
	public String getUniqueCondition() {
        return null;
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
    }

    public void setPerson(Person person) {
    }

    public Id getPersonId() {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public void setCode(String code) {
    }

    public String getCode() {
        return null;
    }

    public void setContact(Id contactId) {
    }

    public void setContact(BigDecimal idValue) {
    }

    public void setContact(Contact contact) {
    }

    public Id getContactId() {
        return null;
    }

    public Contact getContact() {
        return null;
    }

    public void setMessage(String message) {
    }

    public String getMessage() {
        return null;
    }
}
