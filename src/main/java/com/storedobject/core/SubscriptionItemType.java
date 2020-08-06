package com.storedobject.core;

public class SubscriptionItemType extends InventoryItemType {

    public SubscriptionItemType() {
        setUnitOfMeasurement(Count.ZERO);
        setMinimumStockLevel(Count.ONE);
        setReorderPoint(Count.ONE);
        setEconomicOrderQuantity(Count.ONE);
    }

    public static void columns(Columns columns) {
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return "";
    }

    @Override
    public final boolean isSerialized() {
        return true;
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

    public static SubscriptionItemType get(String name) {
        return InventoryItemType.getByPartNumber(SubscriptionItemType.class, name);
    }

    public static ObjectIterator <? extends SubscriptionItemType> list(String name) {
        return InventoryItemType.listByPartNumber(SubscriptionItemType.class, name);
    }
}