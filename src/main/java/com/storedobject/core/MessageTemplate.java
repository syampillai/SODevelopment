package com.storedobject.core;

import java.math.BigDecimal;
import java.util.List;

public class MessageTemplate extends StoredObject {

    public MessageTemplate() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
    }

    @Override
	public String getUniqueCondition() {
        return null;
    }

    public void setCode(String code) {
    }

    public String getCode() {
        return null;
    }

    public void setContactType(Id contactTypeId) {
    }

    public void setContactType(BigDecimal idValue) {
    }

    public void setContactType(ContactType contactType) {
    }

    public Id getContactTypeId() {
        return null;
    }

    public ContactType getContactType() {
        return null;
    }

    public void setTemplate(String template) {
    }

    public String getTemplate() {
        return null;
    }

    public static MessageTemplate get(String code) {
        return null;
    }
    
    protected static String toString(Object parameter) {
        return null;
    }
    
    public Contact getContact(Person person) {
    	return null;
    }
    
    public String createMessage(Object... parameters) {
		return null;
    }
    
    public String createSubject(Object... parameters) {
    	return null;
    }
    
    public String createEmailAddress(Object... parameters) {
    	return null;
    }
    
    public Class<?> createProcessorLogic(Object... parameters) {
    	return null;
    }
    
    public StoredObject createGeneratedBy(Object... parameters) {
    	return null;
    }
    
	public static List<Id> send(MessageTemplate template, TransactionControl tc, Person person, Object... messageParameters) throws Throwable {
		return null;
	}
    
	public static List<Id> send(MessageTemplate template, TransactionControl tc, Iterable<Person> persons, Object... messageParameters) throws Throwable {
		return null;
	}
    
	public static List<Id> send(String templateName, TransactionControl tc, Person person, Object... messageParameters) throws Throwable {
		return null;
	}
    
	public static List<Id> send(String templateName, TransactionControl tc, Iterable<Person> persons, Object... messageParameters) throws Throwable {
		return null;
	}
    
	public Id send(TransactionControl tc, Person person, Object... messageParameters) throws Throwable {
		return null;
	}
    
	public List<Id> send(TransactionControl tc, Iterable<Person> persons, Object... messageParameters) throws Throwable {
		return null;
	}
}