package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FileCirculation extends StoredObject implements Detail {

    public FileCirculation() {
    }

    public static void columns(Columns columns) {
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

    public void setStatus(int status) {
    }

    public int getStatus() {
    	return 0;
    }

    public static String[] getStatusValues() {
    	return null;
    }

    public static String getStatusValue(int value) {
    	return null;
    }

    public String getStatusValue() {
    	return null;
    }

    public void setComments(String comments) {
    }

    public String getComments() {
    	return null;
    }

    public void setCirculatedAt(Timestamp circulatedAt) {
    }

    public Timestamp getCirculatedAt() {
    	return null;
    }

    public void setReadAt(Timestamp readAt) {
    }

    public Timestamp getReadAt() {
    	return null;
    }

    @Override
    public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
    	return false;
    }
}