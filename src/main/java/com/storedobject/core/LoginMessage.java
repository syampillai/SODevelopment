package com.storedobject.core;

import java.sql.Timestamp;

public class LoginMessage extends StoredObject {

    public LoginMessage() {
    }

    public static void columns(Columns columns) {
    }

    public void setMessage(String message) {
    }

    public String getMessage() {
        return null;
    }

    public void setActive(boolean active) {
    }

    public boolean getActive() {
        return false;
    }

    public void setValidFrom(Timestamp validFrom) {
    }

    public Timestamp getValidFrom() {
        return null;
    }

    public void setValidTo(Timestamp validTo) {
    }

    public Timestamp getValidTo() {
        return null;
    }

    public void setLoginAlert(boolean loginAlert) {
    }

    public boolean getLoginAlert() {
    	return false;
    }

    public void setDisableLogin(boolean disableLogin) {
    }

	public void setPriority(int priority) {
	}

	public int getPriority() {
		return 0;
	}

	public static String[] getPriorityValues() {
		return new String[0];
	}

	public static String getPriorityValue(int value) {
    	return "";
	}

	public String getPriorityValue() {
		return "";
	}

	public boolean getDisableLogin() {
    	return false;
    }

    public void setShowEveryTime(boolean showEveryTime) {
    }

    public boolean getShowEveryTime() {
    	return false;
    }
    
    public void setProcessorLogic(TransactionManager tm, Class<?> processorClass) throws Exception {
    }

    public void setProcessorLogic(Transaction transaction, Class<?> processorClass) throws Exception {
    }
    
    public Logic getProcessorLogic() {
    	return null;
    }
    
    public void setGeneratedBy(TransactionManager tm, StoredObject generatedBy) throws Exception {
    }

    public void setGeneratedBy(Transaction transaction, StoredObject generatedBy) throws Exception {
    }

	public StoredObject getGeneratedBy() {
		return listGeneratedBy().findFirst();
	}

    public ObjectIterator<StoredObject> listGeneratedBy() {
    	return null;
    }
    
	public static ObjectIterator<LoginMessage> listMine(StoredObject generatedBy) {
		return null;
	}

	public boolean isReadBy(SystemUser user) {
		return true;
	}
	
	public boolean isReadBy(Person person) {
		return true;
	}

	public boolean isReadBy(PersonRole personRole) {
		return true;
	}
    
	public boolean isReadBy(Id personId) {
		return true;
	}
	
	public boolean isSentTo(SystemUser user) {
		return true;
	}
	
	public boolean isSentTo(Person person) {
		return true;
	}

	public boolean isSentTo(PersonRole personRole) {
		return true;
	}
    
	public boolean isSentTo(Id personId) {
		return true;
	}
	
	public LoginMessage escalate(TransactionControl transactionControl, Iterable<? extends StoredObject> persons, int days) {
		return null;
	}

	public static boolean showLoginMessages(MessageViewer viewer) {
    	return false;
    }

    public static void showMessages(ApplicationServer server, Id minId) {
    }

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person, StoredObject generatedBy) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person,
		Class<?> processorClass) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person,
		Class<?> processorClass, StoredObject generatedBy) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons) throws Exception {
		return null;
	}
	
	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
		Class<?> processorClass) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
			Class<?> processorClass, StoredObject generatedBy) throws Exception {
		return null;
	}
	
	public static LoginMessage alert(Transaction transaction, String message, StoredObject person, int validityInDays) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person,
		StoredObject generatedBy, int validityInDays) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person,
		Class<?> processorClass, int validityInDays) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, StoredObject person,
		Class<?> processorClass, StoredObject generatedBy, int validityInDays) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons, int validityInDays) throws Exception {
		return null;
	}
	
	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
		Class<?> processorClass, int validityInDays) throws Exception {
		return null;
	}

	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
			StoredObject generatedBy, int validityInDays) throws Exception {
		return null;
	}
	
	public static LoginMessage alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
			Class<?> processorClass, StoredObject generatedBy, int validityInDays) throws Exception {
		return null;
	}
}