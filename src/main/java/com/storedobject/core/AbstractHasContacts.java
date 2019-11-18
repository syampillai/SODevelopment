package com.storedobject.core;

public interface AbstractHasContacts {

    default ObjectIterator<? extends ContactType> listContactTypes() {
        return null;
    }

    default ObjectIterator<? extends Contact> listContacts() {
        return null;
    }

    default String getContact(String contactType) {
        return null;
    }

    default Id getContactOwnerId() {
        return null;
    }

    default int getContactGroupingCode() {
        return 0;
    }

    default Class<? extends Contact> getContactClass() {
        return null;
    }
}