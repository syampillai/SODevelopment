package com.storedobject.core;

public abstract class InventoryStore extends StoredObject {

    public InventoryStore() {
    }

    public java.lang.String getName() {
        return null;
    }

    public void setName(java.lang.String name) {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
    }

	public static <T extends InventoryStore> T get(String name) {
    	return null;
    }

	public static <T extends InventoryStore> ObjectIterator<T> list(String name) {
    	return null;
    }
	
	public boolean getMainStore() {
		return false;
	}
	
    public static InventoryStore findMainStore() {
    	return null;
    }
}
