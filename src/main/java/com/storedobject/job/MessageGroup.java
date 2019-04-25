package com.storedobject.job;

import java.math.BigDecimal;

import com.storedobject.core.Columns;
import com.storedobject.core.Id;
import com.storedobject.core.MessageTemplate;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionControl;
import com.storedobject.core.TransactionManager;

public class MessageGroup extends StoredObject {

    public MessageGroup() {
    }

    public static void columns(Columns columns) {
    }

    public static MessageGroup get(String name) {
    	return null;
    }

    public static ObjectIterator < MessageGroup > list(String name) {
    	return null;
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setTemplate(Id templateId) {
    }

    public void setTemplate(BigDecimal idValue) {
    }

    public void setTemplate(MessageTemplate template) {
    }

    public Id getTemplateId() {
        return null;
    }

    public MessageTemplate getTemplate() {
    	return null;
    }

    public ObjectIterator<Person> getMembers() {
    	return null;
    }
    
	public void send(Person person, TransactionManager tm, Object... messageParameters) throws Throwable {
	}

	public void send(TransactionControl tc, Object... messageParameters) throws Throwable {
	}
	
	public void send(Person person, TransactionControl tc, Object... messageParameters) throws Throwable {
	}
    
	public static void send(String groupName, TransactionManager tm, Object... messageParameters) throws Throwable {
	}
    
	public static void send(String groupName, TransactionControl tc, Object... messageParameters) throws Throwable {
	}
    
	public static void send(String groupName, Person person, TransactionManager tm, Object... messageParameters) throws Throwable {
	}
    
	public static void send(String groupName, Person person, TransactionControl tc, Object... messageParameters) throws Throwable {
	}

	public void loginAlert(TransactionManager tm, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
	
	public void loginAlert(TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(String groupName, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlertViaTemplate(String templateName, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(MessageTemplate template, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
	
	public void loginAlert(TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(String groupName, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlertViaTemplate(String templateName, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(MessageTemplate template, TransactionManager tm, Class<?> processorLogic, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}

	public void loginAlert(TransactionManager tm, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(String groupName, TransactionManager tm, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlertViaTemplate(String templateName, TransactionManager tm, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(MessageTemplate template, TransactionManager tm, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(String groupName, TransactionManager tm, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlertViaTemplate(String templateName, TransactionManager tm, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(MessageTemplate template, TransactionManager tm, StoredObject generatedBy, int validityDays, Object... messageParameters) throws Throwable {
	}
	
	public void loginAlert(TransactionManager tm, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(String groupName, TransactionManager tm, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlertViaTemplate(String templateName, TransactionManager tm, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
    
	public static void loginAlert(MessageTemplate template, TransactionManager tm, StoredObject generatedBy, Object... messageParameters) throws Throwable {
	}
}
