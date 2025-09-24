package com.storedobject.core;

public class InventoryLoanOutItem extends InventoryReturnItem {

    public InventoryLoanOutItem() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] browseColumns() {
        return new String[] {
                "Item.PartNumber.Name AS Item",
                "Item.PartNumber.PartNumber AS Part Number",
                "Item.SerialNumber AS Serial/Batch Number",
                "OriginalItem.PartNumber.PartNumber AS Original Part Number",
                "OriginalItem.SerialNumber AS Original Serial/Batch Number",
                "Quantity",
                "ItemType",
        };
    }

    @Override
    public String getItemType() {
        InventoryItem item = getItem();
        return item.isConsumable() ? "Consumable" : "Loaned out";
    }
}
