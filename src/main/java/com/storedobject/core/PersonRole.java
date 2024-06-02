package com.storedobject.core;

import java.math.BigDecimal;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

public abstract class PersonRole extends StoredObject implements OfEntity, HasContacts, HasName {

    private Id personId;
    private Person person;
    private Id systemEntityId;
    private SystemEntity systemEntity;

    public PersonRole() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Person", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Person, SystemEntity, T_Family", true);
    }

    @Override
	public String getUniqueCondition() {
        return "Person=" + getPersonId() + " AND T_Family=" + family() + " AND SystemEntity=" + getSystemEntityId();
    }
    
    public static String[] displayColumns() {
        return new String[]
                { "Person.FirstName as Name", "Person.MiddleName as Middle Name", "Person.LastName as Last Name", };
    }

    public void setPerson(Id personId) {
        if(!loading() && !Id.equals(this.personId, personId)) {
            throw new Set_Not_Allowed("Person");
        }
        this.personId = personId;
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(Person person) {
        setPerson(person == null ? null : person.getId());
    }

    @Column(order = 200)
    @SetNotAllowed
    public Id getPersonId() {
        return personId;
    }

    public Person getPerson() {
        if(person == null || Id.isNull(person.getId())) {
            person = get(Person.class, personId);
        }
        return person;
    }
    
    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @Override
    @SetNotAllowed
	@Column(order = 100, caption = "Of")
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    @Override
	public SystemEntity getSystemEntity() {
        if(systemEntity == null || Id.isNull(systemEntity.getId())) {
            systemEntity = get(SystemEntity.class, systemEntityId);
        }
        return systemEntity;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            personId = tm.checkType(this, personId, Person.class, false);
            Transaction t = getTransaction();
            if (t != null) {
                if (Id.isNull(systemEntityId)) {
                    SystemEntity se = t.getManager().getEntity();
                    if (se != null) {
                        systemEntityId = se.getId();
                    }
                }
                systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class, false);
            } else {
                systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class, true);
            }
        }
        super.validateData(tm);
    }

    @Override
	public String toString() {
        return getPerson().toString();
    }
    
    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person) {
    	return get(systemEntity, roleClass, person, false);
    }
    
    public static <T extends PersonRole> T get(SystemEntity systemEntity, Class<T> roleClass, Person person,
                                               boolean any) {
    	return person == null ? null : getByPersonId(systemEntity, roleClass, person.getId(), any);
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId) {
    	return getByPersonId(systemEntity, roleClass, personId, false);
    }
    
    public static <T extends PersonRole> T getByPersonId(SystemEntity systemEntity, Class<T> roleClass, Id personId,
                                                         boolean any) {
    	return personId == null || roleClass == null ? null :
    		get(roleClass, "Person=" + personId + " AND SystemEntity=" + systemEntity.getId(), any);
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
    	return getByName(systemEntity, roleClass, name, false);
    }

    public static <T extends PersonRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name,
                                                     boolean any) {
    	T role = get(systemEntity, roleClass, Person.get(name), any);
    	return role == null ? listByName(systemEntity, roleClass, name, any).single(false) : role;
    }

    public static <T extends PersonRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass,
                                                                      String name) {
    	return listByName(systemEntity, roleClass, name, false);
    }
    
    public static <T extends PersonRole> ObjectIterator<T> listByName(final SystemEntity systemEntity,
                                                                      final Class<T> roleClass, String name,
                                                                      final boolean any) {
    	ObjectConverter<Person, T> ec = person -> get(systemEntity, roleClass, person, any);
		return Person.list(name).convert(ec);
    }

    @Override
    public Id getContactOwnerId() {
        return getContactGroupingCode() == 0 ? personId : getId();
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
