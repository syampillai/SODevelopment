package com.storedobject.core;

public class SubscriptionItemType extends AbstractServiceItemType {
	
    public SubscriptionItemType() {
    }

    public static void columns(Columns columns) {
    }
    
    public static String[] browseColumns() {
        return new String[] {
            "PartNumber as SAC",
            "Name.Name as Name",
        };
    }
    
    @Override
    public final boolean isSerialized() {
    	return true;
    }

    public static SubscriptionItemType get(String name) {
        return InventoryItemType.getByPartNumber(SubscriptionItemType.class, name);
    }

    public static ObjectIterator <? extends SubscriptionItemType> list(String name) {
        return InventoryItemType.listByPartNumber(SubscriptionItemType.class, name);
    }

    public static void customizeMetadata(UIFieldMetadata md) {
        switch (md.getFieldName()) {
            case "PartNumber", "HSNCode" -> md.setCaption("SAC");
        }
    }

    @Override
	public String getSerialNumberName() {
		return "Subscription Number";
	}
}