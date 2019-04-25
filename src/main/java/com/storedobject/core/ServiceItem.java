package com.storedobject.core;

public class ServiceItem extends InventoryItem {

    public ServiceItem() {
    }

    public static void columns(Columns columns) {
    }
    
    @Override
    public ServiceItemType getPartNumber() {
        return null;
    }

    public static Class <ServiceItemType> getPartNumberType() {
        return null;
    }

    public static ServiceItem get(String serial, ServiceItemType itemType) {
        return null;
    }

    public static ObjectIterator <ServiceItem> list(String serial, ServiceItemType itemType) {
        return null;
    }
}