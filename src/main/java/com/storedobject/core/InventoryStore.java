package com.storedobject.core;

import java.math.BigDecimal;

/**
 * Represents a store where items can stocked at the bin locations ({@link InventoryBin}) of the store.
 *
 * @author Syam
 */
public class InventoryStore extends StoredObject implements OfEntity {

    public InventoryStore() {
    }

    public static void columns(Columns columns) {
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
    }

    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return new Id();
    }

    public SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public static <T extends InventoryStore> T get(SystemEntity systemEntity, String name) {
        //noinspection unchecked
        return (T) new InventoryStore();
    }

    public static <T extends InventoryStore> ObjectIterator<T> list(SystemEntity systemEntity, String name) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryStore> ObjectIterator<T> list(String name) {
        return ObjectIterator.create();
    }

    public final InventoryStoreBin getStoreBin() {
        return new InventoryStoreBin();
    }

    public static InventoryStore getStore(Id id) {
        return new InventoryStore();
    }

    public InventoryBin findBin(InventoryItemType partNumber) {
        return new InventoryBin();
    }

    public InventoryBin findBin(InventoryItem item) {
        return new InventoryBin();
    }
}