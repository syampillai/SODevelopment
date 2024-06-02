package com.storedobject.core;

import com.storedobject.common.Country;
import com.storedobject.core.annotation.Column;

/**
 * This class represents a legal business entity.
 */
public final class Entity extends StoredObject implements HasContacts, HasName {

	private String name = "", location = "", country;

	/**
	 * Constructs an entity
	 *
	 * @param name Name of the entity
	 * @param location Location of this entity.
	 */
	public Entity(String name, String location) {
		this.name = name;
		this.location = location;
	}

	/**
	 * Constructor for internal use only
	 */
	public Entity() {
	}

	public static void columns(Columns columns) {
		columns.add("Name", "text");
		columns.add("Location", "text");
        columns.add("Country", "country");
	}
	
	public static String[] displayColumns() {
		return new String[] { "Name", "Location", "Country" };
	}

	public static String[] searchColumns() {
		return new String[] { "Name" };
	}

	public static void indices(Indices indices) {
		indices.add("lower(Name),lower(Location),lower(Country)", true);
		indices.add("lower(Location),lower(Name)", false);
	}

    @Override
	public String getUniqueCondition() {
        return "lower(Name)=" + trim(name) + " AND lower(Location)=" + trim(location) + " AND lower(Country)=" + trim(country);
    }

    private static String trim(String s) {
		return "'" + s.trim().toLowerCase().replace("'", "''") + "'";
	}

	/**
	 * Gets the name of the Entity
	 *
	 * @return The name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the Entity.
	 *
	 * @param name The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the location of the Entity.
	 *
	 * @return The location.
	 */
	@Column(required = false)
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location of the Entity.
	 *
	 * @param location The location value.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(style = "(country)")
    public String getCountry() {
        return country;
    }

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(StringUtility.isWhite(name)) {
			throw new Invalid_Value("Name");
		}
		name = name.trim();
		if(location == null) {
			location = "";
		}
		location = location.trim();
		if(StringUtility.isBlank(country)) {
			country = tm.getCountry();
		}
		country = Country.check(country);
		super.validateData(tm);
	}

	@Override
	public String toDisplay() {
		return name + (location.isEmpty() ? "" : (", " + location)) + ", " + country;
	}
	
	@Override
	public String toString() {
		return name;
	}

    public static Entity get(String name) {
    	return StoredObjectUtility.get(Entity.class, "Name", name);
    }

    public static ObjectIterator<Entity> list(String name) {
    	return StoredObjectUtility.list(Entity.class, "Name", name);
    }

	@Override
	public Contact getContactObject(Id contactTypeId) {
		Contact c = listLinks(Contact.class, "Type=" + contactTypeId).single(false);
		if(c == null) {
			try(ObjectIterator<EntityRole> roles
						= list(EntityRole.class, "Person=" + getId(), true)) {
				for(EntityRole role: roles) {
					c = role.listLinks(Contact.class, "Type=" + contactTypeId).single(false);
					if(c != null) {
						break;
					}
				}
			}
		}
		return c;
	}
}
