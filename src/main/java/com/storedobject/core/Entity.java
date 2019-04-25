package com.storedobject.core;

import java.math.BigDecimal;

public class Entity extends StoredObject {

	public static Entity dash;

	public Entity(String name, String location) {
	}

	public Entity() {
	}

	public static void columns(Columns columns) {
	}
	
    @Override
	public String getUniqueCondition() {
        return null;
    }

	public String getName() {
        return null;
	}

	public void setName(String name) {
	}

	public String getLocation() {
        return null;
	}

	public void setLocation(String location) {
	}

    public void setCountry(Id countryId) {
    }

    public void setCountry(BigDecimal idValue) {
    }

    public void setCountry(Country country) {
    }

    public Id getCountryId() {
        return null;
    }

    public Country getCountry() {
        return null;
    }

    public static Entity get(String name) {
        return null;
    }

    public static ObjectIterator<Entity> list(String name) {
        return null;
    }
    
    public SystemEntity getSystemEntity() {
        return null;
    }
    
    public String getContact(String contactType) {
        return null;
    }
    
    public String getContact(Id contactTypeId) {
        return null;
    }
    
    public static String getContact(Id entityId, String contactType) {
        return null;
    }
    
    public static String getContact(Id entityId, Id contactTypeId) {
        return null;
    }
}
