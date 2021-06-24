package com.storedobject.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

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

    public static InventoryAssembly get(String pn) {
        return list(InventoryAssembly.class).single(false);
    }

    public static ObjectIterator<InventoryAssembly> list(String pn) {
        return ObjectIterator.create();
    }

    public boolean canFit(InventoryItemType partNumber) {
        return false;
    }

    public boolean canFit(Id partNumberId) {
        return partNumberId.get().equals(BigInteger.ONE);
    }

    public ObjectIterator<InventoryAssembly> listImmediateAssemblies() {
        return ObjectIterator.create();
    }

    public ObjectIterator<InventoryAssembly> listAssemblies() {
        return ObjectIterator.create();
    }

    public int getLevel() {
        return new Random().nextInt();
    }
}
