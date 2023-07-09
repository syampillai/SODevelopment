package com.storedobject.core;

import java.math.BigDecimal;

public abstract class PersonRole extends StoredObject implements OfEntity, HasContacts, RequiresApproval {

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
        return new Id();
    }

    public Person getPerson() {
        return new Person();
    }
    
    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return new Id();
    }

    @Override
	public SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }
    
    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person, boolean any) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId, boolean any) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
        if(Math.random() > 0.5) {
            return null;
        }
        //noinspection unchecked
        return (T) new PersonRole() {};
    }

    public static <T extends PersonRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return ObjectIterator.create();
    }
    
    public static <T extends PersonRole> ObjectIterator<T> listByName(final SystemEntity systemEntity, final Class<T> roleClass, String name, final boolean any) {
        return ObjectIterator.create();
    }

    @Override
    public Id getContactOwnerId() {
        return new Id();
    }

    @Override
    public String getName() {
        return getPerson().getName();
    }

    /**
     * Check if this role is currently active or not.
     *
     * @return True/false.
     */
    public boolean isActive() {
        return true;
    }
}