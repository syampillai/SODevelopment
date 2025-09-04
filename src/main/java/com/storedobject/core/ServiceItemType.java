package com.storedobject.core;

public class ServiceItemType extends AbstractServiceItemType {

    public ServiceItemType() {
    }

    public static void columns(Columns columns) {
    }
    
    public static String[] browseColumns() {
        return new String[] {
            "PartNumber as SAC-",
            "Name.Name as Name"
        };
    }
    
    public static ServiceItemType get(String name) {
        return InventoryItemType.getByPartNumber(ServiceItemType.class, name);
    }

    public static ObjectIterator <? extends ServiceItemType> list(String name) {
        return InventoryItemType.listByPartNumber(ServiceItemType.class, name);
    }
    
    public static void customizeMetadata(UIFieldMetadata md) {
        switch (md.getFieldName()) {
            case "PartNumber" -> md.setCaption("SAC-");
            case "HSNCode" -> md.setCaption("SAC");
        }
    }
}