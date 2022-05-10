package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class InventoryGRNItem extends StoredObject implements Detail {

    private Id partNumberId;
    private InventoryItemType partNumber;
    private String serialNumber;
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money unitCost = new Money();
    private boolean inspected = false;

    public InventoryGRNItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setPartNumber(Id partNumberId) {
        if(partNumber != null && (Id.isNull(partNumberId) || !partNumberId.equals(this.partNumberId))) {
            partNumber = null;
        }
        this.partNumberId = partNumberId;
    }

    public void setPartNumber(BigDecimal idValue) {
        setPartNumber(new Id(idValue));
    }

    public void setPartNumber(InventoryItemType partNumber) {
        setPartNumber(partNumber == null ? null : partNumber.getId());
    }

    public Id getPartNumberId() {
        return partNumberId;
    }

    public InventoryItemType getPartNumber() {
        return partNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(order = 200, required = false)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Column(order = 300)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setUnitCost(Money unitCost) {
        this.unitCost = unitCost;
    }

    public void setUnitCost(Object moneyValue) {
        setUnitCost(Money.create(moneyValue));
    }

    public Money getUnitCost() {
        return unitCost;
    }

    public void setBin(Id binId) {
    }

    public void setBin(BigDecimal idValue) {
        setBin(new Id(idValue));
    }

    public void setBin(InventoryBin bin) {
        setBin(bin == null ? null : bin.getId());
    }

    public Id getBinId() {
        return new Id();
    }

    public InventoryBin getBin() {
        return Math.random() > 0.5 ? new InventoryBin() : null;
    }

    public void setItem(Id itemId) {
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItem item) {
        setItem(item == null ? null : item.getId());
    }

    public Id getItemId() {
        return new Id();
    }

    public InventoryItem getItem() {
        return Math.random() > 0.5 ? new InventoryItem() : null;
    }

    public void setInspected(boolean inspected) {
        this.inspected = inspected;
    }

    public boolean getInspected() {
        return inspected;
    }

    public void inspect(Transaction transaction) throws Exception {
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryGRN.class == masterClass;
    }

    public void splitQuantity(Transaction transaction, Quantity quantityToSplit) throws Exception {
    }

    /**
     * Update various attribute values.
     *
     * @param tm Transaction manager.
     * @param newQuantity New quantity (Could be null if change not required).
     * @param newUnitCost New unit cost (Could be null if change not required).
     * @param newSerialNumber New serial number (Could be null if change not required).
     * @throws Exception If error occurs while updating the value.
     * @return True if the values are updated.
     */
    public boolean updateValues(TransactionManager tm, Quantity newQuantity, Money newUnitCost, String newSerialNumber)
            throws Exception {
        return Math.random() > 0.5;
    }

    public InventoryPOItem getPOItem() {
        return Math.random() > 0.5 ? new InventoryPOItem() : null;
    }
}
