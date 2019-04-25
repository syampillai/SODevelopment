package com.storedobject.core;

public class SubscriptionItemType extends InventoryItemType {

    public SubscriptionItemType() {
    }

    public static void columns(Columns columns) {
    }
    
    @Override
    public final boolean isSerialized() {
    	return true;
    }
    
    public static Class<SubscriptionItem> getItemType() {
    	return null;
    }
    
    public static SubscriptionItemType get(String name) {
    	return null;
    }

    public static ObjectIterator <SubscriptionItemType> list(String name) {
    	return null;
    }
}