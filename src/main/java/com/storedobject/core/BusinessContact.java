package com.storedobject.core;

public class BusinessContact extends com.storedobject.core.Contact {

    public BusinessContact(com.storedobject.core.Id p1, java.lang.String p2) {
        this();
    }

    public BusinessContact() {
    }

    public com.storedobject.core.BusinessContactType getType() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public boolean isDetailOf(java.lang.Class <? extends com.storedobject.core.StoredObject > p1) {
        return false;
    }

    protected java.lang.Class <? extends com.storedobject.core.ContactType > getTypeClass() {
        return null;
    }
}
