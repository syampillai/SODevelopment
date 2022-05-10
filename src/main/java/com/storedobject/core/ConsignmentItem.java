package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class ConsignmentItem extends StoredObject implements Detail {

    private Id itemId;
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money unitCost = new Money();
    private int boxNumber;

    public ConsignmentItem() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Quantity", "quantity");
        columns.add("UnitCost", "money");
        columns.add("BoxNumber", "int");
    }

    public void setItem(Id itemId) {
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItem item) {
        setItem(item == null ? null : item.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getItemId() {
        return itemId;
    }

    public InventoryItem getItem() {
        return getRelated(InventoryItem.class, itemId, true);
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Column(order = 200)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setUnitCost(Money unitCost) {
        this.unitCost = unitCost;
    }

    public void setUnitCost(Object moneyValue) {
        setUnitCost(Money.create(moneyValue));
    }

    @Column(required = false, order = 300)
    public Money getUnitCost() {
        return unitCost;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    @Column(order = 400, caption = "Packet/Box Number")
    public int getBoxNumber() {
        return boxNumber;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(boxNumber <= 0) {
            throw new Invalid_Value("Box Number");
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, false);
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return Consignment.class.isAssignableFrom(masterClass);
    }

    @Override
    public Id getUniqueId() {
        return itemId;
    }

    public ConsignmentPacket getPacket() {
        return getMaster(Consignment.class, true).listLinks(ConsignmentPacket.class, "Number=" + boxNumber)
                .single(false);
    }
}
