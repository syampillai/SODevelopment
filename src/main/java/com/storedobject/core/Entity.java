package com.storedobject.core;

public final class Entity extends StoredObject implements HasContacts, RequiresApproval {

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

    public void setCountry(String country) {
    }

    public String getCountry() {
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
}
