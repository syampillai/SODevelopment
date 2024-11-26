package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.lang.reflect.Modifier;

public final class ContactGroupingCode extends StoredObject {

    private String contactClass = Person.class.getName();
    private int groupingCode;

    public ContactGroupingCode() {
    }

    public static void columns(Columns columns) {
        columns.add("ContactClass", "text");
        columns.add("GroupingCode", "int");
    }

    public static void indices(Indices indices) {
        indices.add("ContactClass", true);
        indices.add("GroupingCode", true);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setContactClass(String contactClass) {
        this.contactClass = contactClass;
    }

    @Column(order = 300)
    public String getContactClass() {
        return contactClass;
    }

    public void setGroupingCode(int groupingCode) {
        this.groupingCode = groupingCode;
    }

    @Column(order = 400, required = false)
    public int getGroupingCode() {
        return groupingCode;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(Person.class.getName().equals(contactClass)) {
            groupingCode = 0;
        }
        if(groupingCode < 0) {
            throw new Invalid_Value("Grouping Code");
        }
        if(groupingCode == 0 && !Person.class.getName().equals(contactClass)) {
            throw new Invalid_Value("Grouping Code");
        }
        checkForDuplicate("ContactClass");
        checkForDuplicate("GroupingCode");
        Class<? extends HasContacts> hcc = getContactDataClass(false);
        if(hcc != null && Modifier.isAbstract(hcc.getModifiers())) {
            throw new Invalid_State("Contact class can't be abstract");
        }
        if (hcc == null) {
            throw new Invalid_Value("Contact Class");
        }
        super.validateData(tm);
    }

    public Class<? extends HasContacts> getContactDataClass() {
        return getContactDataClass(true);
    }

    private Class<? extends HasContacts> getContactDataClass(boolean checkAbstract) {
        try {
            //noinspection unchecked
            Class<? extends HasContacts> cc = (Class<? extends HasContacts>) JavaClassLoader.getLogic(contactClass);
            if(checkAbstract && Modifier.isAbstract(cc.getModifiers())) {
                return null;
            }
            HasContacts hc = cc.getDeclaredConstructor().newInstance();
            if(hc.getContactGroupingCode() != groupingCode) {
                return null;
            }
            return cc;
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(exists(ContactType.class, "GroupingCode=" + groupingCode)) {
            throw new SOException("Can't delete, contact types exist");
        }
    }
}
