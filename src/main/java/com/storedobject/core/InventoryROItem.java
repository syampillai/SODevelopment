package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public final class InventoryROItem extends InventoryReturnItem {

    private String repairDetail;
    private Money costOfRepair = new Money();

    public InventoryROItem() {
    }

    public static void columns(Columns columns) {
        columns.add("RepairDetail", "text");
        columns.add("CostOfRepair", "money");
    }

    public static String[] browseColumns() {
        return new String[] {
                "Item.PartNumber.Name AS Item",
                "Item.PartNumber.PartNumber AS Part Number",
                "Item.SerialNumber AS Serial/Batch Number",
                "Quantity",
                "OriginalItem.PartNumber.PartNumber AS Original Part Number",
                "OriginalItem.SerialNumber AS Original Serial/Batch Number",
                "CostOfRepair",
                "RepairDetail",
                "ItemType",
        };
    }

    public void setRepairDetail(String repairDetail) {
        this.repairDetail = repairDetail;
    }

    @Column(order = 200, style = "(large)", required = false)
    public String getRepairDetail() {
        return repairDetail;
    }

    public void setCostOfRepair(Money costOfRepair) {
        this.costOfRepair = costOfRepair;
    }

    public void setCostOfRepair(Object moneyValue) {
        setCostOfRepair(Money.create(moneyValue));
    }

    @Column(order = 300, required = false)
    public Money getCostOfRepair() {
        return costOfRepair;
    }

    @Override
    public String getItemType() {
        InventoryItem item = getItem();
        return item.isConsumable() ? "Consumable" : (item.isRepairAllowed() ? "For repair" : "?");
    }
}
