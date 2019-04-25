package com.storedobject.core;

import java.math.BigDecimal;

public class Contact extends StoredObject implements Detail {

	public Contact(Id typeId, String value) {
	}

	public Contact() {
	}

	public static void columns(Columns columns) {
	}

	public static String[] displayColumns() {
		return null;
	}

	public static String browseOrder() {
		return null;
	}

	protected Class<? extends ContactType> getTypeClass() {
		return null;
    }

    public void setType(Id typeId) {
    }

    public void setType(BigDecimal idValue) {
    }

    public void setType(ContactType type) {
    }

    public Id getTypeId() {
		return null;
    }

    public ContactType getType() {
		return null;
    }

	public void setValue(String value) {
	}

	public String getValue() {
		return null;
	}

	public void setRemarks(String remarks) {
	}

	public String getRemarks() {
		return null;
	}

	@Override
	public void copyValuesFrom(Detail detail) {
	}

	@Override
	public Id getUniqueId() {
		return null;
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return false;
	}
}
