package com.storedobject.core;

public interface HasContacts {

    default ObjectIterator<ContactType> listContactTypes() {
        return null;
    }

    default ObjectIterator<Contact> listContacts() {
        return null;
    }

    default String getContact(String contactType) {
        return null;
    }

    default Id getContactOwnerId() {
        return ((StoredObject)this).getId();
    }

    default int getContactGroupingCode() {
        return 0;
    }
}