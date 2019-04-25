package com.storedobject.core;

public final class ObjectHint {
	
	public static final int SMALL = 1, SMALL_LIST = 2;
	
	public static boolean isSmall(Class<? extends StoredObject> objectClass) {
		return false;
	}
	
	public static boolean isSmallList(Class<? extends StoredObject> objectClass, boolean any) {
		return false;
	}
}
