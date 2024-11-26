package com.storedobject.core;

import com.storedobject.common.Address;
import com.storedobject.common.Email;
import com.storedobject.common.PhoneNumber;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.util.List;

public final class Contact extends StoredObject implements Detail {

	private Id typeId;
	private ContactType type;
	private String contact;

	public Contact(Id typeId, String contact) {
		this.typeId = typeId;
		this.contact = contact;
	}

	public Contact() {
	}

	public static void columns(Columns columns) {
		columns.add("Type", "Id");
		columns.add("Contact", "text");
	}

	public static void indices(Indices indices) {
		indices.add("lower(Contact)", false);
	}

	public static String[] displayColumns() {
		return new String[] { "Type.Name as Type", "Contact" };
	}

	public static String browseOrder() {
		return "Type.DisplayOrder";
	}

    @Override
	public void validateData(TransactionManager tm) throws Exception {
		if(!deleted()) {
			getType();
			if (StringUtility.isWhite(contact)) {
				if(type.getType() == 4) {
					contact = "Telegram";
				} else {
					throw new Invalid_Value("Contact/" + getType().getName());
				}
			}
			typeId = tm.checkType(this, typeId, ContactType.class);
			switch(type.getType()) {
				case 0 -> PhoneNumber.check(contact);
				case 1 -> Email.check(contact);
				case 2 -> Address.check(contact);
			}
		}
		super.validateData(tm);
	}

	public void setType(Id typeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
		this.type = null;
    }

    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public void setType(ContactType type) {
        setType(type == null ? null : type.getId());
    }

	@SetNotAllowed
    public Id getTypeId() {
        return typeId;
    }

    public ContactType getType() {
        if(type == null || !type.getId().equals(typeId)) {
        	type = get(getTransaction(), ContactType.class, typeId);
		}
        return type;
    }

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContact() {
		return contact;
	}

	@Override
	public Id getUniqueId() {
		return typeId;
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return HasContacts.class.isAssignableFrom(masterClass);
	}

	@Override
	public String toString() {
		return getType() + ": " + getContactValue();
	}

	public String getContactValue() {
		ContactType t = getType();
		if(t.getType() == 2) { // Address
			return Address.create(contact).toString();
		}
		return contact;
	}

	public String getValue() {
		return getContactValue();
	}

	public static List<Contact> list(String contact) {
		return list(Contact.class,
				"lower(Contact)='" + contact.toLowerCase().replace("'", "''") + "'")
				.toList();
	}
}
