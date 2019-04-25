package com.storedobject.mail;

import com.storedobject.common.SOException;
import com.storedobject.core.*;

import java.math.BigDecimal;

public class Mail extends Message {

	public Mail() {
	}

	public static void columns(Columns columns) {
	}

    public void setSenderGroup(String senderGroupName) {
    }

    public void setSenderGroup(Id senderGroupId) {
    }

    public void setSenderGroup(BigDecimal idValue) {
    }

    public void setSenderGroup(SenderGroup senderGroup) {
    }

    public Id getSenderGroupId() {
    	return null;
    }

    public SenderGroup getSenderGroup() {
    	return null;
    }

    public void setToAddress(String toAddress) {
	}

	public String getToAddress() {
    	return null;
	}
	
    public void setCCAddress(String cCAddress) {
    }

    public String getCCAddress() {
    	return null;
    }

    public void setReplyToAddress(String replyToAddress) {
	}

	public String getReplyToAddress() {
    	return null;
	}
	
    public void setSubject(String subject) {
    }

    public String getSubject() {
    	return null;
    }

    public void setMessageType(String messageType) {
    }

    public String getMessageType() {
    	return null;
    }
	
	public void setErrorValue(String errorValue) {
	}

	public static String[] getErrorValues() {
    	return null;
	}

	public static String getErrorValue(int value) {
    	return null;
	}

	public String getErrorValue() {
    	return null;
	}
	
	public void ready() {
	}
	
	public boolean attach(TransactionManager tm, ContentProducer... content) {
		return true;
	}
	
	public boolean attach(TransactionManager tm, FileData... files) {
		return true;
	}
	
	public boolean attach(TransactionManager tm, Iterable<FileData> files) {
		return false;
	}
	
	public static void checkEmail(String email) throws Invalid_Value {
	}
	
	public static void checkEmail(String email, boolean allowEmpty) throws Invalid_Value {
	}
	
    public static Mail createAlert() throws SOException {
    	return null;
    }
	
	public static void alert(Transaction transaction, String message, StoredObject person) throws Exception {
	}

	public static void alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons) throws Exception {
	}
	
	public static void alert(Transaction transaction, String message, StoredObject person, String subject) throws Exception {
	}

	public static void alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons, String subject) throws Exception {
	}
}
