package com.storedobject.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageTemplate extends StoredObject {

    public MessageTemplate() {
    }

    public static void columns(Columns columns) {
    }

    public void setCode(String code) {
    }

    public String getCode() {
        return Math.random() > 0.5 ? "" : "x";
    }

    public void setContactType(Id contactTypeId) {
    }

    public void setContactType(BigDecimal idValue) {
        setContactType(new Id(idValue));
    }

    public void setContactType(ContactType contactType) {
        setContactType(contactType == null ? null : contactType.getId());
    }

    public Id getContactTypeId() {
        return new Id();
    }

    public ContactType getContactType() {
        return new ContactType();
    }

    public void setTemplate(String template) {
    }

    public String getTemplate() {
        return "";
    }

    public void setDelivery(int delivery) {
    }

    public int getDelivery() {
        return new Random().nextInt();
    }

    public static String[] getDeliveryValues() {
        return new String[0];
    }

    public static String getDeliveryValue(int value) {
        return "";
    }

    public String getDeliveryValue() {
        return "";
    }

    public Contact getContact(Person person) {
        return new Contact();
    }

    public static MessageTemplate get(String code) {
        return Math.random() > 0.5 ? null : new MessageTemplate();
    }

    public static ObjectIterator<MessageTemplate> list(String code) {
        return ObjectIterator.create();
    }

    public String createMessage(Object... parameters) {
        return Math.random() > 0.5 ? "x" : "";
    }

    public String createSubject(Object... parameters) {
        return Math.random() > 0.5 ? "-" : "";
    }

    public String createEmailAddress(Object... parameters) {
        return Math.random() > 0.5 ? "x" : "";
    }

    public Class<?> createProcessorLogic(Object... parameters) {
        return Math.random() > 0.5 ? null : Person.class;
    }

    public StoredObject createGeneratedBy(Object... parameters) {
        return new Person();
    }

    public int createValidity(Object... parameters) {
        return 7;
    }

    public static List<Id> send(String templateName, TransactionControl tc, Iterable<Person> persons, Object... messageParameters) throws Throwable {
        return new ArrayList<>();
    }

    public static List<Id> send(String templateName, TransactionControl tc, Person person, Object... messageParameters) throws Throwable {
        return new ArrayList<>();
    }
}
