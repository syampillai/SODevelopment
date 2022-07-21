package com.storedobject.core;

import com.storedobject.core.annotation.Column;

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

    @Column(order = 400)
    public int getGroupingCode() {
        return groupingCode;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(groupingCode < 1) {
            throw new Invalid_Value("Grouping Code");
        }
        checkForDuplicate("ContactClass");
        checkForDuplicate("GroupingCode");
        if (getContactDataClass() == null) {
            throw new Invalid_Value("Contact Class");
        }
        super.validateData(tm);
    }

    public Class<? extends HasContacts> getContactDataClass() {
        try {
            //noinspection unchecked
            return (Class<? extends HasContacts>) JavaClassLoader.getLogic(contactClass);
        } catch (Throwable ignored) {
        }
        return null;
    }
}
