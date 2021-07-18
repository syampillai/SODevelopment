package com.storedobject.core;

public interface HasContacts {

    default String getName() { return  null; }

    default ObjectIterator<ContactType> listContactTypes() {
        return null;
    }

    default ObjectIterator<Contact> listContacts() {
        return null;
    }

    default String getContact(String contactType) {
        return "";
    }

    default String getContactRaw(String contactType) {
        return null;
    }

    default Contact getContactObject(String contactType) {
        return null;
    }

    default Id getContactOwnerId() {
        return ((StoredObject)this).getId();
    }

    default int getContactGroupingCode() {
        return 0;
    }

    default void setContact(Transaction transaction, String contactType, String contactValue) throws Exception {
    }
}