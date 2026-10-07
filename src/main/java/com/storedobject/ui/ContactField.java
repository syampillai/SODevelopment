package com.storedobject.ui;

import com.storedobject.core.Contact;
import com.storedobject.core.HasContacts;
import com.storedobject.core.ObjectIterator;

import java.util.List;

public class ContactField extends ObjectField<Contact> {

    private HasContacts master;

    public ContactField() {
        this((String)null);
    }

    public ContactField(String label) {
        super(label, Contact.class, false, Type.CHOICE, true);
    }

    public ContactField(List<Contact> list) {
        super(list);
    }

    public ContactField(Iterable<Contact> list) {
        super(list);
    }

    public ContactField(String label, List<Contact> list) {
        super(label, Contact.class, list, false);
    }

    public void setMaster(HasContacts master) {
        this.master = master;
        getField().load(master == null ? ObjectIterator.create() : master.listContacts());
    }
}
