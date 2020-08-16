package com.storedobject.core;

import java.math.BigDecimal;

/**
 * <p>An item can be an assembly, composed of one or more sub-items and each sub-item
 * can also be an assembly.</p>
 * <p>This class represents definition of an assembly node.</p>
 *
 * @author Syam
 */
public final class InventoryAssembly extends StoredObject {

    public InventoryAssembly() {
    }

    public static void columns(Columns columns) {
    }

    public void setPosition(String position) {
    }

    public String getPosition() {
        return "";
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

    public void setParentItemType(Id parentItemTypeId) {
    }

    public void setParentItemType(BigDecimal idValue) {
    }

    public void setParentItemType(InventoryItemType itemType) {
    }

    public Id getParentItemTypeId() {
        return new Id();
    }

    public InventoryItemType getParentItemType() {
        return new InventoryItemType();
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return Count.ZERO;
    }

    public void setAccessory(boolean accessory) {
    }

    public boolean getAccessory() {
        return false;
    }

    public void setOptional(boolean optional) {
    }

    public boolean getOptional() {
        return false;
    }

    public void setDisplayOrder(int displayOrder) {
    }

    public int getDisplayOrder() {
        return 0;
    }
}
