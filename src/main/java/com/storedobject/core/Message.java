package com.storedobject.core;

import java.sql.Timestamp;

public abstract class Message extends StoredObject {

    public Message() {
    }
    
    public Message(String message) {
    }

    public static void columns(Columns columns) {
    }

    public static String[] protectedColumns() {
    	return null;
    }

    public void setMessage(String message) {
    }

    public String getMessage() {
        return null;
    }

    public void setMessageID(String messageID) {
    }

    public String getMessageID() {
        return null;
    }

    public void setCreatedAt(Timestamp createdAt) {
    }

    public Timestamp getCreatedAt() {
        return null;
    }

    public void setSent(boolean sent) {
    }

    public boolean getSent() {
        return false;
    }

    public void setSentAt(Timestamp sentAt) {
    }

    public Timestamp getSentAt() {
        return null;
    }

    public void setError(int error) {
    }

    public int getError() {
        return 0;
    }
    
    public void sent(int error) {
    }
    
    public boolean sent(TransactionControl tc, int error) {
    	return false;
    }
}
