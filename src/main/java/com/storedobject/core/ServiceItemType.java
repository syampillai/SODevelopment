package com.storedobject.core;

public class ServiceItemType extends InventoryItemType {

    public ServiceItemType() {
    }

    public static void columns(Columns columns) {
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return "";
    }

    @Override
    public final boolean isExpendable() {
        return false;
    }

    @Override
    public final boolean isConsumable() {
        return false;
    }

    @Override
    public final boolean isTool() {
        return false;
    }

    public static ServiceItemType get(String name) {
        return InventoryItemType.getByPartNumber(ServiceItemType.class, name);
    }

    public static ObjectIterator <? extends ServiceItemType> list(String name) {
        return InventoryItemType.listByPartNumber(ServiceItemType.class, name);
    }
}