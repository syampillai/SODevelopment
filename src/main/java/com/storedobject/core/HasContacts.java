package com.storedobject.core;

import com.storedobject.common.Address;

import java.util.List;

/**
 * Definition of "contact" interface. A {@link StoredObject} that implements this interface keeps contact
 * information (See {@link Contact}) as links under it. Different {@link StoredObject} classes may keep different
 * sets of contact information types (See {@link ContactType}) and that is determined by the value returned by the
 * method {@link #getContactGroupingCode()}. The default value returned by the {@link #getContactGroupingCode()}
 * method is 0 and that is what the {@link Person} class uses. So, if you don't implement different code in
 * your class, it will share the same set of contact types with {@link Person} class.
 * <p>{@link Entity} and {@link EntityRole} classes also return 0 from the method
 * {@link #getContactGroupingCode()} but since {@link EntityRole} is always extended by your custom class,
 * you can override that to return any specific value.</p>
 *
 * @author Syam
 */
public interface HasContacts {

    /**
     * Get the Id.
     *
     * @return Id
     */
    Id getId();

    /**
     * Name of the contact. The default implementation returns <code>null</code>.
     *
     * @return Name of the contact.
     */
    default String getName() {
        return null;
    }

    /**
     * List all the supported contact types. This method should return a constant list.
     *
     * @return Stream of contact types supported by this class.
     */
    default ObjectIterator<ContactType> listContactTypes() {
        return StoredObject.list(ContactType.class, "GroupingCode=" + getContactGroupingCode(), "DisplayOrder");
    }

    /**
     * List all contact instances of this class instance.
     *
     * @return Stream of contact instances.
     */
    default ObjectIterator<Contact> listContacts() {
        Id id = getContactOwnerId();
        if (id == null) {
            return ObjectIterator.create();
        }
        return id.listLinks(Contact.class, "Type.GroupingCode=" + getContactGroupingCode(), "Type.DisplayOrder");
    }

    /**
     * Get the contact value (human-friendly) for a specific type of contact.
     *
     * @param contactType Type of contact ("email", "address", "mobile" etc. defined in the contact type).
     * @return Contact value or null if it doesn't exist.
     */
    default String getContact(String contactType) {
        Contact c = getContactObject(contactType);
        return c == null ? null : c.getContactValue();
    }

    /**
     * Get the contact value (human-friendly) for a specific type of contact.
     *
     * @param contactType Type of contact.
     * @return Contact value or null if it doesn't exist.
     */
    default String getContact(ContactType contactType) {
        Contact c = getContactObject(contactType);
        return c == null ? null : c.getContactValue();
    }

    /**
     * Get the raw contact value (encoded in some cases) for a specific type of contact.
     *
     * @param contactType Type of contact ("email", "address", "mobile" etc. defined in the contact type).
     * @return Raw value of the contact or null if it doesn't exist.
     */
    default String getContactRaw(String contactType) {
        Contact c = getContactObject(contactType);
        return c == null ? null : c.getContact();
    }

    /**
     * Get the raw contact value (encoded in some cases) for a specific type of contact.
     *
     * @param contactType Type of contact.
     * @return Raw value of the contact or null if it doesn't exist.
     */
    default String getContactRaw(ContactType contactType) {
        Contact c = getContactObject(contactType);
        return c == null ? null : c.getContact();
    }

    /**
     * Get the contact value for a specific type of contact.
     *
     * @param contactType Type of contact ("email", "address", "mobile" etc. defined in the contact type).
     * @return Contact or null if it doesn't exist.
     */
    default Contact getContactObject(String contactType) {
        Id id = getContactOwnerId();
        if (id == null || contactType == null) {
            return null;
        }
        contactType = contactType.toLowerCase().replace("'", "''");
        String c = "lower(Type.Name)='" + contactType + "' AND Type.GroupingCode=" + getContactGroupingCode();
        Contact contact = getContactOwnerId().listLinks(Contact.class, c).limit(1).findFirst();
        if (contact != null) {
            return contact;
        }
        int type = switch (contactType.toLowerCase()) {
            case "phone" -> 0;
            case "email" -> 1;
            case "address" -> 2;
            case "other" -> 3;
            default -> -1;
        };
        if (type == -1) {
            return null;
        }
        c = "Type.Type=" + type + " AND Type.GroupingCode=" + getContactGroupingCode();
        return id.listLinks(Contact.class, c).limit(1).findFirst();
    }

    /**
     * Get the contact value for a specific type of contact.
     *
     * @param contactType Type of contact.
     * @return Contact or null if it doesn't exist.
     */
    default Contact getContactObject(ContactType contactType) {
        Id id = getContactOwnerId();
        if (id == null || contactType == null || contactType.getGroupingCode() != getContactGroupingCode()) {
            return null;
        }
        String c = "Type=" + contactType.getId();
        return id.listLinks(Contact.class, c).limit(1).findFirst();
    }

    /**
     * Get the contact value for a specific type of contact.
     *
     * @param contactTypeId Id of the contact type.
     * @return Contact or null if it doesn't exist.
     */
    default Contact getContactObject(Id contactTypeId) {
        return getContactObject(StoredObject.get(ContactType.class, contactTypeId));
    }

    /**
     * Get the {@link Id} of the object that owns the contact. (It is possible that some other related object
     * may be owing the contact information).
     *
     * @return By default, the same instance of the class owns the contact and thus, it returns
     * {@link StoredObject#getId()}.
     */
    default Id getContactOwnerId() {
        return ((StoredObject) this).getId();
    }

    /**
     * Get the contact grouping code of this class. This method should return a constant value.
     *
     * @return Default value is 0.
     */
    default int getContactGroupingCode() {
        return 0;
    }

    /**
     * Set a contact value for this instance.
     *
     * @param transaction  Transaction.
     * @param contactType  Type of contact.
     * @param contactValue Value of the contact (must be raw value). If <code>null</code> is passed, existing value
     *                     if any, will be deleted.
     * @throws Exception If contact information is invalid or any error occurs while saving to the database.
     * @deprecated User the {@link #setContact(TransactionManager, String, String)} instead.
     */
    @Deprecated
    default void setContact(Transaction transaction, String contactType, String contactValue) throws Exception {
        setContact(transaction.getManager(), contactType, contactValue);
    }

    /**
     * Set a contact value for this instance.
     *
     * @param tm Transaction manager.
     * @param contactType Type of contact.
     * @param contactValue Value of the contact (must be raw value). If <code>null</code> is passed, existing value
     *                     if any, will be deleted.
     * @throws Exception If contact information is invalid or any error occurs while saving to the database.
     */
    default void setContact(TransactionManager tm, String contactType, String contactValue) throws Exception {
        ContactType ct = StoredObject.get(ContactType.class, "lower(Name)='" +
                contactType.toLowerCase().replace("'", "''") + "' AND GroupingCode=" +
                getContactGroupingCode());
        if(ct == null) {
            throw new SOException("Contact type '" + contactType + "' not found");
        }
        List<Contact> cs = ((StoredObject)this).listLinks(Contact.class, "Type=" + ct.getId()).toList();
        if(cs.size() > 1) {
            throw new SOException("Multiple entries already exist for this contact type: " + contactType);
        }
        Contact c;
        if(cs.isEmpty()) {
            if(contactValue == null || contactValue.isBlank()) {
                return;
            }
            c = new Contact();
            c.setType(ct);
        } else {
            c = cs.get(0);
            if(contactValue == null || contactValue.isBlank()) {
                tm.transact(t -> ((StoredObject) this).removeLink(t, c));
                return;
            }
            if(c.getContactValue().equals(contactValue)) {
                return;
            }
        }
        c.setContact(contactValue);
        tm.transact(t  -> {
            c.save(t);
            if(cs.isEmpty()) {
                ((StoredObject)this).addLink(t, c);
            }
        });
    }

    /**
     * Get the address. (It will look for an address with address type name "Address").
     *
     * @return Address instance if available, otherwise <code>null</code>.
     */
    default Address getAddress() {
        return getAddress("Address");
    }

    /**
     * Get the address.
     *
     * @param addressTypeName Name of the address (as defined in the respective grouping).
     * @return Address instance if available, otherwise <code>null</code>.
     */
    default Address getAddress(String addressTypeName) {
        return Address.create(getContactRaw(addressTypeName));
    }

    /**
     * Get the email address. (It will look for an email address with type name "Email").
     *
     * @return Email address if available, otherwise <code>null</code>.
     */
    default String getEmail() {
        return getEmail("Email");
    }

    /**
     * Get the address.
     *
     * @param typeName Name of the email (as defined in the respective grouping).
     * @return Email address if available, otherwise <code>null</code>.
     */
    default String getEmail(String typeName) {
        String email = getContact(typeName);
        return email == null || email.isBlank() ? null : email;
    }

    /**
     * Get the phone number. (It will look for a phone number with type name "Phone").
     *
     * @return Phone number if available, otherwise <code>null</code>.
     */
    default long getPhone() {
        return getPhone("Phone");
    }

    /**
     * Get the address.
     *
     * @param typeName Name of the email (as defined in the respective grouping).
     * @return Phone number if available, otherwise <code>null</code>.
     */
    default long getPhone(String typeName) {
        return phoneToNumber(getContact(typeName));
    }

    static long phoneToNumber(String phone) {
        return phone == null || phone.isBlank() ? 0 : Long.parseLong(phone.replace(" ", "")
                .replace("-", "").replace("(", "")
                .replace(")", "").strip());
    }

    /**
     * Get the mobile number. (It will look for a phone number with type name "Mobile").
     *
     * @return Mobile number if available, otherwise <code>null</code>.
     */
    default long getMobile() {
        return getPhone("Mobile");
    }

    /**
     * Get the telegram contact.
     *
     * @return Telegram contact if available, otherwise <code>null</code>.
     */
    default String getTelegram() {
        String s = getContact("Telegram");
        return s == null || s.isBlank() || s.equals("Telegram") ? null : s;
    }
}