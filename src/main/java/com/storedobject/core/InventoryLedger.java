package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public final class InventoryLedger extends StoredObject {

    public static final Date dataPickupDate = DateUtility.create(2000, 1, 1);

    public InventoryLedger() {
    }

    public static void columns(Columns columns) {
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public void setItem(Id itemId) {
    }

    public void setItem(BigDecimal idValue) {
    }

    public void setItem(InventoryItem item) {
    }

    public Id getItemId() {
        return new Id();
    }

    public InventoryItem getItem() {
        return new InventoryItem();
    }

    public InventoryItem getItemFromHistory() {
        return getItem();
    }

    public void setItemType(Id itemTypeId) {
    }

    public void setItemType(BigDecimal idValue) {
    }

    public void setItemType(InventoryItemType itemType) {
    }

    public Id getItemTypeId() {
        return new Id();
    }

    public InventoryItemType getItemType() {
        return new InventoryItemType();
    }

    public InventoryItemType getItemTypeFromHistory() {
        return getItemType();
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return Count.ZERO;
    }

    public void setCost(Money cost) {
    }

    public void setCost(Object moneyValue) {
    }

    public Money getCost() {
        return new Money();
    }

    public void setLocationFrom(Id locationId) {
    }

    public void setLocationFrom(BigDecimal idValue) {
    }

    public void setLocationFrom(InventoryLocation location) {
    }

    public Id getLocationFromId() {
        return new Id();
    }

    public InventoryLocation getLocationFrom() {
        return new InventoryStoreBin();
    }

    public void setLocationTo(Id locationId) {
    }

    public void setLocationTo(BigDecimal idValue) {
    }

    public void setLocationTo(InventoryLocation location) {
    }

    public Id getLocationToId() {
        return new Id();
    }

    public InventoryLocation getLocationTo() {
        return new InventoryBin();
    }

    public String getReference() {
        return "";
    }

    public void setReference(String reference) {
    }

    public void updateReference(TransactionManager tm, String newReference) throws Exception {
    }

    public static QuantityWithCost getOpeningStock(InventoryItemType itemType, Date date, InventoryLocation location) {
        return new QuantityWithCost(Count.ZERO, new Money());
    }

    public static QuantityWithCost getClosingStock(InventoryItemType itemType, Date date, InventoryLocation location) {
        return new QuantityWithCost(Count.ZERO, new Money());
    }
}
