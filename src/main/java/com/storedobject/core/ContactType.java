package com.storedobject.core;

public final class ContactType extends StoredObject {

    public ContactType() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public static String[] getTypeValues() {
        return null;
    }

    public static String getTypeValue(int value) {
        return null;
    }

    public String getTypeValue() {
        return null;
    }

    public int getGroupingCode() {
        return 0;
    }

    public void setGroupingCode(int groupingCode) {
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }

    public ContactGroupingCode getContactGroup() {
        return new ContactGroupingCode();
    }

    public Class<? extends HasContacts> getContactClass() {
        return getContactGroup().getContactDataClass();
    }
}
