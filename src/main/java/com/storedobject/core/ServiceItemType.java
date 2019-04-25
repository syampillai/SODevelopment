package com.storedobject.core;

public class ServiceItemType extends InventoryItemType {

    public ServiceItemType() {
    }

    public static void columns(Columns columns) {
    }
    
    public static Class<ServiceItem> getItemType() {
    	return ServiceItem.class;
    }
    
    public static ServiceItemType get(String name) {
        return InventoryItemType.getByPartNumber(ServiceItemType.class, name);
    }

    public static ObjectIterator <ServiceItemType> list(String name) {
        return InventoryItemType.listByPartNumber(ServiceItemType.class, name);
    }
}