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
                "Quantity",
                "ItemType",
        };
    }

    @Override
    public String getItemType() {
        InventoryItem item = getItem();
        return item.isConsumable() ? "Consumable" : "Loaned out";
    }

    @Override
    protected void move(InventoryTransaction transaction, InventoryItem item, InventoryLocation toLocation, Entity toEntity) {
        transaction.loanTo(item, getQuantity(), null, toEntity);
    }
}
