package com.storedobject.ui.util;

import com.storedobject.core.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ContactData implements StoredObjectLink<Contact> {

    private final List<ContactType> types;
    private final Contact[] contacts;
    private final String[] oldValue;
    private final boolean[] deleted;
    private StoredObject master;

    public ContactData(List<ContactType> types) {
        this.types = types;
        contacts = new Contact[types.size()];
        deleted = new boolean[types.size()];
        oldValue = new String[types.size()];
    }

    private int indexOf(Contact c) {
        return indexOf(c.getTypeId());
    }

    private int indexOf(ContactType ct) {
        return indexOf(ct.getId());
    }

    private int indexOf(Id ctId) {
        int i = 0;
        for(ContactType t: types) {
            if(t.getId().equals(ctId)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void setMaster(StoredObject master) {
        if(master == this.master) {
            return;
        }
        this.master = master;
        Arrays.fill(deleted, false);
        Arrays.fill(contacts, null);
        Arrays.fill(oldValue, null);
        if(master != null) {
            ((HasContacts) master).listContacts().forEach(c -> {
                int i = indexOf(c.getTypeId());
                if(i >= 0) {
                    contacts[i] = c;
                    oldValue[i] = c.getContact();
                }
            });
        }
        for(int i = 0; i < types.size(); i++) {
            if(contacts[i] == null) {
                contacts[i] = create(types.get(i));
            }
        }
    }

    private Contact create(ContactType ct) {
        Contact c = new Contact();
        c.setContact("");
        c.setType(ct.getId());
        return c;
    }

    @Override
    public StoredObject getMaster() {
        if(master == null) {
            return null;
        }
        Id ownerId = ((HasContacts)master).getContactOwnerId();
        return ownerId == null ? master : (ownerId.equals(master.getId()) ? master : StoredObject.get(ownerId));
    }

    public boolean ownedByMaster() {
        return master == getMaster();
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getName() {
        return "$c";
    }

    @Override
    public boolean contains(Object contact) {
        return streamAll().anyMatch(f -> f == contact);
    }

    @Override
    public boolean isAdded(Contact contact) {
        int i = indexOf(contact);
        return i >= 0 && !deleted[i] && oldValue[i] == null;
    }

    @Override
    public boolean isDeleted(Contact contact) {
        int i = indexOf(contact);
        return i >= 0 && deleted[i];
    }

    @Override
    public boolean isEdited(Contact contact) {
        int i = indexOf(contact);
        return i >= 0 && !deleted[i] && oldValue[i] != null && !contacts[i].getContact().equals(oldValue[i]);
    }

    @Override
    public Stream<Contact> streamAll() {
        return Stream.of(contacts).filter(this::consider);
    }

    private boolean consider(Contact c) {
        if(!c.created()) {
            return true;
        }
        for(int i = 0; i < contacts.length; i++) {
            if(contacts[i] == c) {
                return !deleted[i];
            }
        }
        return true;
    }

    @Override
    public int size() {
        return (int)streamAll().count();
    }

    @Override
    public boolean append(Contact contact) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Contact contact) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Contact contact) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean undelete(Contact contact) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(Contact contact) {
        throw new UnsupportedOperationException();
    }

    String getContactValue(ContactType ct) {
        return contacts[indexOf(ct)].getContact();
    }

    void setContactValue(ContactType ct, String value) {
        int i = indexOf(ct);
        if(i < 0) {
            return;
        }
        if(value == null || value.isEmpty()) {
            deleted[i] = true;
            return;
        }
        deleted[i] = false;
        contacts[i].setContact(value);
    }
}