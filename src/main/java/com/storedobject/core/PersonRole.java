package com.storedobject.core;

import java.math.BigDecimal;

public class PersonRole extends StoredObject implements OfEntity {

    public PersonRole() {
    }

    public static void columns(Columns columns) {
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
    }

    public void setPerson(Person organization) {
    }

    public Id getPersonId() {
        return null;
    }

    public Person getPerson() {
        return null;
    }
    
    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return null;
    }

    @Override
	public SystemEntity getSystemEntity() {
        return null;
    }

    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person) {
        return null;
    }
    
    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person, boolean any) {
        return null;
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId) {
        return null;
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId, boolean any) {
        return null;
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return null;
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
        return null;
    }

    public static <T extends PersonRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return null;
    }
    
    public static <T extends PersonRole> ObjectIterator<T> listByName(final SystemEntity systemEntity, final Class<T> roleClass, String name, final boolean any) {
        return null;
    }
    
    public String getContact(String contactType) {
        return null;
    }
    
    public String getContact(Id contactTypeId) {
        return null;
    }
    
    public static String getContact(Id personId, String contactType) {
        return null;
    }
    
    public static String getContact(Id personId, Id contactTypeId) {
        return null;
    }
}