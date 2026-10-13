package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectAdder;
import com.storedobject.ui.util.RequiresMaster;

import java.util.ArrayList;
import java.util.List;

public class ContactField extends ObjectField<Contact> implements RequiresMaster {

    private Person master;
    private String allowedTypes;

    public ContactField() {
        this((String) null);
    }

    public ContactField(String label) {
        this(label, null);
    }

    public ContactField(List<Contact> list) {
        this(null, list);
    }

    public ContactField(String label, List<Contact> list) {
        this(label, (Iterable<Contact>) list);
    }

    public ContactField(Iterable<Contact> list) {
        this(null, list);
    }

    public ContactField(String label, Iterable<Contact> list) {
        super(label, new CField());
        if (list != null) {
            getField().load(ObjectIterator.create(list.iterator()));
        }
        addValueChangeListener(e -> findMaster());
    }

    public void setAllowedTypes(String typeDescription) {
        if(typeDescription == null || typeDescription.isBlank()) {
            return;
        }
        List<ContactType> allowedTypes = new ArrayList<>();
        typeDescription = typeDescription.toLowerCase();
        for(ContactType t: StoredObject.list(ContactType.class)) {
            if(typeDescription.contains(t.getTypeValue().toLowerCase())) {
                allowedTypes.add(t);
            }
        }
        if(!allowedTypes.isEmpty()) {
            setAllowedTypes(allowedTypes);
        }
    }

    public void setAllowedTypes(Id... typeIds) {
        if (typeIds == null || typeIds.length == 0) {
            if (allowedTypes == null) return;
            allowedTypes = null;
        } else {
            StringBuilder s = new StringBuilder();
            for (Id id : typeIds) {
                if (s.isEmpty()) {
                    s.append(id);
                } else {
                    s.append(',').append(id);
                }
            }
            allowedTypes = "T.Type IN(" + s + ")";
        }
        HasContacts m = master;
        setMaster((HasContacts) null);
        setMaster(m);
    }

    public void setAllowedTypes(List<ContactType> types) {
        setAllowedTypes(types == null ? null : types.stream().map(ContactType::getId).toArray(Id[]::new));
    }

    @Override
    public void setMaster(StoredObject master) {
        switch (master) {
            case null -> setMaster((HasContacts) null);
            case HasContacts hc -> setMaster(hc);
            case ContactOwner co -> setMaster(co.getContactOwner());
            default -> {
            }
        }
    }

    private void setMaster(HasContacts master) {
        if(master instanceof PersonRole r) {
            master = r.getPerson();
        }
        if (this.master == null && master == null) {
            return;
        }
        if (this.master != null && master != null && master.getId() != null && master.getId().equals(this.master.getId())) {
            return;
        }
        if(master != null) {
            if (master instanceof Person p) {
                this.master = p;
            } else {
                throw new SORuntimeException("Contact should be attached to a person only - Invalid - " + StringUtility.makeLabel(master.getClass()));
            }
        }
        ObjectIterator<Contact> contacts = master == null ? ObjectIterator.create() : master.listContacts(allowedTypes);
        getField().load(contacts);
    }

    private void findMaster() {
        Id v = getValue();
        if (Id.isNull(v)) {
            setMaster((HasContacts) null);
            return;
        }
        if (((ObjectComboField<Contact>) getField()).items().filter(c -> c.getId().equals(v)).findFirst().orElse(null) != null) {
            return;
        }
        StoredObject m = v.getMaster(Person.class);
        if (m != null) {
            setMaster((HasContacts) m);
        }
    }

    private static class CField extends ObjectComboField<Contact> {

        CField() {
            super(Contact.class, new ArrayList<>(), false);
        }

        @Override
        protected ObjectAdder<Contact> getObjectAdder() {
            return null;
        }

        @Override
        public boolean isAllowAny() {
            return false;
        }
    }
}
