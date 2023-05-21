package com.storedobject.core;

import java.math.BigDecimal;

public final class Contact extends StoredObject implements Detail {

	public Contact(Id typeId, String contact) {
	}

	public Contact() {
	}

	public static void columns(Columns columns) {
	}

	public void setType(Id typeId) {
	}

	public void setType(BigDecimal idValue) {
	}

	public void setType(ContactType type) {
	}

	public Id getTypeId() {
		return new Id();
	}

	public ContactType getType() {
		return new ContactType();
	}

	public void setContact(String contact) {
	}

	public String getContact() {
		return "";
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return false;
	}

	public String getContactValue() {
		return "";
	}

	public String getValue() {
		return "";
	}
}