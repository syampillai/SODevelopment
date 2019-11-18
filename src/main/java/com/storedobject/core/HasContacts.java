package com.storedobject.core;

public interface HasContacts extends AbstractHasContacts {

    @Override
    default ObjectIterator<ContactType> listContactTypes() {
        return null;
    }

    @Override
    default ObjectIterator<Contact> listContacts() {
        return null;
    }
}