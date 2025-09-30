package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public class InventorySaleItem extends InventoryTransferItem {

    private Money unitPrice = new Money();

    public InventorySaleItem() {
    }

    public static void columns(Columns columns) {
        columns.add("UnitPrice", "money");
    }

    public static String[] browseColumns() {
        return new String[] {
                "Item.PartNumber.Name AS Item",
                "Item.PartNumber.PartNumber AS Part Number",
                "Item.SerialNumberDisplay AS Serial/Batch Number",
                "Quantity",
                "UnitPrice",
                "Price",
        };
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setUnitPrice(Object moneyValue) {
        setUnitPrice(Money.create(moneyValue));
    }

    @Column(order = 400, required = false)
    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getPrice() {
        return unitPrice.multiply(getQuantity());
    }

    @Override
    protected void  move(InventoryTransaction transaction, InventoryItem item, InventoryLocation toLocation, Entity toEntity) {
        transaction.sale(item, getQuantity(), null, toEntity);
    }
}
