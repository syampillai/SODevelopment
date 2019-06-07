package com.storedobject.ui;

import com.storedobject.core.Contact;
import com.storedobject.core.Person;
import com.storedobject.core.PersonRole;
import com.storedobject.core.StoredObject;
import com.storedobject.mail.Mail;
import com.storedobject.mail.SenderGroup;
import com.storedobject.vaadin.DataForm;

public class MailForm extends DataForm implements Transactional {

    public MailForm() {
        this("Mail");
    }

    public MailForm(String caption) {
        super(caption, "Send", "Cancel", false);
    }

    public void setAllowAttachments(boolean allow) {
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected void buildButtons() {
    }

    @Override
    protected boolean process() {
        return true;
    }

    public void addOtherAttachments(@SuppressWarnings("unused") Mail mail) {
    }

    public <T extends StoredObject> void setAllowedAddresses(Iterable<T> persons) {
    }

    public <T extends StoredObject> void addAllowedAddresses(Iterable<T> persons) {
    }

    public <T extends StoredObject> void setAllowedAddresses(Class<T> personClass) {
    }

    public <T extends StoredObject> void addAllowedAddresses(Class<T> personClass) {
    }

    public <T extends StoredObject> void setAddress(Iterable<T> persons) {
    }

    public <T extends StoredObject> void addAddress(Iterable<T> persons) {
    }

    public <T extends StoredObject> void setAddress(Class<T> personClass) {
    }

    public <T extends StoredObject> void addAddress(Class<T> personClass) {
    }

    public void setAddress(Person person) {
    }

    public void addAddress(Person person) {
    }

    public void setAddress(PersonRole person) {
    }

    public void addAddress(PersonRole person) {
    }

    public void setAllowedAddress(String email) {
    }

    public void addAllowedAddress(String email) {
    }

    public void setAddress(String email) {
    }

    public void addAddress(String email) {
    }

    public <T extends StoredObject> void setAllowedCCs(Iterable<T> persons) {
    }

    public <T extends StoredObject> void addAllowedCCs(Iterable<T> persons) {
    }

    public <T extends StoredObject> void setAllowedCCs(Class<T> personClass) {
    }

    public <T extends StoredObject> void addAllowedCCs(Class<T> personClass) {
    }

    public <T extends StoredObject> void setCC(Iterable<T> persons) {
    }

    public <T extends StoredObject> void addCC(Iterable<T> persons) {
    }

    public <T extends StoredObject> void setCC(Class<T> personClass) {
    }

    public <T extends StoredObject> void addCC(Class<T> personClass) {
    }

    public void setCC(Person person) {
    }

    public void addCC(Person person) {
    }

    public void setCC(PersonRole person) {
    }

    public void addCC(PersonRole person) {
    }

    public void setAllowedCC(String email) {
    }

    public void addAllowedCC(String email) {
    }

    public void setCC(String email) {
    }

    public void addCC(String email) {
    }

    public void setSubject(String subject) {
    }

    public void setContent(String content) {
    }

    public SenderGroup getSenderGroup() {
        return null;
    }

    public void setSenderGroup(SenderGroup senderGroup) {
    }

    public class Address {

        private Address(Person p) {
        }

        private Address(PersonRole p) {
            this(p.getPerson());
        }


        private Address(Contact contact) {
            this(contact.getValue());
        }

        private Address(StoredObject so) {
        }

        private Address(String email) {
        }

        public boolean isValid() {
            return false;
        }
    }
}
