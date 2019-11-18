package com.storedobject.core;

public interface HasBusinessContacts extends AbstractHasContacts {

    @Override
    default ObjectIterator<BusinessContactType> listContactTypes() {
        return null;
    }

    @Override
    default ObjectIterator<BusinessContact> listContacts() {
        return null;
    }

    @Override
    default Class<? extends Contact> getContactClass() {
        return null;
    }
}