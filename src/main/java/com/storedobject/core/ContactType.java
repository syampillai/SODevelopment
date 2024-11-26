package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigInteger;

public final class ContactType extends StoredObject {

	private static final String[] typeValues = new String[] {
			"Phone",
			"Email",
			"Address",
			"Other",
			"Telegram",
	};
	private int displayOrder;
	private String name;
	private int type = 3;
	private int groupingCode = 0;

	public ContactType() {
	}

	public static void columns(Columns columns) {
		columns.add("DisplayOrder", "int");
		columns.add("Name", "text");
		columns.add("Type", "int");
		columns.add("GroupingCode", "int");
	}

	public static void indices(Indices indices) {
		indices.add("lower(Name),GroupingCode", true);
	}

    @Override
	public String getUniqueCondition() {
        return "lower(Name)='" + name.trim().toLowerCase() + "'";
    }

	public static int hints() {
		return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
	}

	public static String browseOrder() {
		return "DisplayOrder";
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(type == 4 && groupingCode != 0) {
			throw new Invalid_State("Telegram can be added to persons only");
		}
		if(type == 4) {
			name = "Telegram";
		}
		if(!created() && type == 4) {
			ContactType ct = get(ContactType.class, "Type=4");
			if(ct != null) {
				throw new Invalid_State("Telegram contact type already exists");
			}
		}
		if(StringUtility.isWhite(name)) {
			name = getTypeValue();
		}
		if(getContactGroup() == null) {
			throw new SOException("Undefined Grouping Code");
		}
		super.validateData(tm);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static String[] getTypeValues() {
		return typeValues;
	}

	public static String getTypeValue(int value) {
		String[] s = getTypeValues();
		return s[value % s.length];
	}

	public String getTypeValue() {
		return getTypeValue(type);
	}

	@Column(required = false)
	public int getGroupingCode() {
		return groupingCode;
	}

	public void setGroupingCode(int groupingCode) {
		this.groupingCode = groupingCode;
	}

	// For internal use only.
	static BigInteger getIdValue(String name, String tableName) {
		RawSQL q = new RawSQL("SELECT Id FROM " + tableName + "Type WHERE lower(name)='" + name.toLowerCase() + "'");
		try {
			q.execute();
			return q.getResult().getBigDecimal(1).toBigInteger();
		} catch(Exception e) {
			return BigInteger.ZERO;
		} finally {
			q.close();
		}
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(required = false)
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public String toString() {
		return name;
	}

	public ContactGroupingCode getContactGroup() {
		return get(ContactGroupingCode.class, "GroupingCode=" + groupingCode);
	}

	public Class<? extends HasContacts> getContactClass() {
		return getContactGroup().getContactDataClass();
	}

	public static ContactType createForTelegram(TransactionManager tm) {
		return createFor(tm,4);
	}

	static ContactType createFor(TransactionManager tm, int type) {
		ContactType ct = StoredObject.get(ContactType.class, "Type=" + type + " AND GroupingCode=0");
		if(ct != null) {
			return ct;
		}
		try {
			ContactGroupingCode cgc = StoredObject.get(ContactGroupingCode.class,
					"ContactClass='" + Person.class.getName() + "'");
			if (cgc == null) {
				cgc = new ContactGroupingCode();
				cgc.setContactClass(Person.class.getName());
				if (tm.transact(cgc::save) != 0) {
					return null;
				}
			}
			ct = new ContactType();
			ct.setType(type);
			ct.setDisplayOrder(Integer.MAX_VALUE - 10);
			ct.setGroupingCode(0);
			if (tm.transact(ct::save) == 0) {
				return ct;
			}
		} catch (Exception ignored) {
		}
		return null;
	}
}
