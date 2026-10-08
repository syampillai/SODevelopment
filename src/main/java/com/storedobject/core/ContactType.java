package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a specific type of contact information, such as Phone, Email, Address, etc.
 * This class is finalized and extends the functionality of StoredObject.
 * It handles creation, validation, and retrieval of contact types.
 * The class also manages display order, grouping codes, and type-specific constraints.
 * <pre>
 * Key functionalities include:
 * - Validation of contact type data with custom rules.
 * - Retrieval of unique conditions for database operations.
 * - Static utilities for managing and creating predefined contact types.
 * - Support for grouping and contact data categorization through grouping codes.
 * </pre>
 *
 * @author Syam
 */
public final class ContactType extends StoredObject {

	private static final Map<Id, ContactType> types = new HashMap<>();
	private static final String[] typeValues = new String[] {
			"Phone",
			"Email",
			"Address",
			"Other",
			"Telegram",
			"Android",
			"iOS (Apple)"
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
		boolean unique = false;
		name = switch (type) {
			case 4, 5, 6 -> {
				unique = true;
				displayOrder = Integer.MAX_VALUE - 6 - type;
				groupingCode = 0;
				yield getTypeValue();
			}
			default -> name;
		};
		if(!created() && unique) {
			ContactType ct = get(ContactType.class, "Type=" + type);
			if(ct != null) {
				throw new Invalid_State(getTypeValue() + " contact type already exists");
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

	public static ContactType createForAndroid(TransactionManager tm) {
		return createFor(tm,5);
	}

	public static ContactType createForIOS(TransactionManager tm) {
		return createFor(tm,6);
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
			if (tm.transact(ct::save) == 0) {
				return ct;
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	@Override
	public void saved() throws Exception {
		types.remove(getId());
	}

	/**
	 * Retrieves a ContactType object for the specified ID.
	 * If the object is not found in the existing cache, it is loaded from the database
	 * and then cached for future use.
	 *
	 * @param id The unique identifier of the ContactType to be retrieved.
	 * @return The ContactType associated with the given ID, or null if no such object exists.
	 */
	public static ContactType getFor(Id id) {
		ContactType t = types.get(id);
		if(t != null) {
			return t;
		}
		t = StoredObject.get(ContactType.class, id);
		if(t != null) {
			types.put(id, t);
		}
		return t;
	}
}
