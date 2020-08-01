package com.storedobject.core;

public class ServiceItem extends InventoryItem {

    public ServiceItem() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    public ServiceItemType getPartNumber() {
        return (ServiceItemType) super.getPartNumber();
    }

    public static Class <? extends ServiceItemType> getItemType() {
        return ServiceItemType.class;
    }

    public static ServiceItem get(String serial, ServiceItemType itemType) {
        return InventoryItem.get(ServiceItem.class, serial, itemType);
    }

    public static ObjectIterator <? extends ServiceItem> list(String serial, ServiceItemType itemType) {
        return InventoryItem.list(ServiceItem.class, serial, itemType);
    }
}