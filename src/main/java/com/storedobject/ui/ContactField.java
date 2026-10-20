package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectAdder;
import com.storedobject.ui.util.RequiresMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContactField extends ObjectField<Contact> implements RequiresMaster {

    private Person master;
    private String allowedTypes;
    private Consumer<RequiresMaster> reloadMaster;

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
        ((CField) getField()).contactField = this;
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

    public void setAllowedTypes(List<ContactType> types) {
        String allowed = null;
        if (types == null || types.isEmpty()) {
            if (allowedTypes == null) return;
            allowedTypes = null;
        } else {
            StringBuilder ids = new StringBuilder(), ts = new StringBuilder();
            for (ContactType ct : types) {
                if (ids.isEmpty()) {
                    ids.append(ct.getId());
                    ts.append(ct.getType());
                } else {
                    ids.append(',').append(ct.getId());
                    ts.append(',').append(ct.getType());
                }
            }
            allowedTypes = "T.Type IN(" + ids + ")";
            allowed = "T.Type IN(" + ts + ")";
        }
        reloadMaster();
        ObjectEditor<Contact> oe = ((CField)getField()).getContactEditor();
        //noinspection unchecked
        ((ObjectField<ContactType>)oe.getField("Type")).setFilter(allowed);
    }

    @Override
    public void setReloadListener(Consumer<RequiresMaster> reloadMaster) {
        this.reloadMaster = reloadMaster;
    }

    @Override
    public Person getMaster() {
        return master;
    }

    @Override
    public void reloadMaster() {
        HasContacts m = master;
        master = null;
        setMaster(m);
    }

    void doMasterReload() {
        reloadMaster();
        if(reloadMaster != null) {
            reloadMaster.accept(this);
        }
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
        } else {
            this.master = null;
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

        private ContactField contactField;
        private ObjectAdder<Contact> objectAdder;

        CField() {
            super(Contact.class, new ArrayList<>(), true);
        }

        @Override
        protected ObjectAdder<Contact> getObjectAdder() {
            if(objectAdder == null) {
                getContactEditor();
            }
            return objectAdder;
        }

        @Override
        void addNew() {
            closeDropDown(() -> objectAdder.addToMaster(() -> contactField.master, 0));
        }

        @Override
        public boolean isAllowAny() {
            return false;
        }

        ObjectEditor<Contact> getContactEditor() {
            if(objectAdder == null) {
                objectAdder = ObjectAdder.create(c -> contactField.doMasterReload(), this);
                objectAdder.getObjectEditor().setWindowMode(true);
            }
            return objectAdder.getObjectEditor();
        }
    }
}
