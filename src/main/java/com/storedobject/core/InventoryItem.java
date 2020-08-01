package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Denotes an item in the inventory.
 *
 * @author Syam
 */
public class InventoryItem extends StoredObject {

    public InventoryItem() {
    }

    public static void columns(Columns columns) {
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

    public void setSerialNumber(String serialNumber) {
    }

    public String getSerialNumber() {
        return "";
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
    }

    public void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return new Id();
    }

    public InventoryStore getStore() {
        return new InventoryStore();
    }

    public void setLocation(Id locationId) {
    }

    public void setLocation(BigDecimal idValue) {
    }

    public void setLocation(InventoryBin location) {
    }

    public Id getLocationId() {
        return new Id();
    }

    public InventoryLocation getLocation() {
        return new InventoryBin();
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

    public UnitCost getUnitCost() {
        return new UnitCost(new Money(), Count.ONE);
    }

    public UnitCost getUnitCost(MeasurementUnit unit) {
        return getUnitCost().getUnitCost(unit);
    }

    public Money getCost(Quantity quantity) {
        return getUnitCost().getCost(quantity);
    }

    public void setInTransit(boolean inTransit) {
    }

    public boolean getInTransit() {
        return true;
    }

    public void setOwner(Id ownerId) {
    }

    public void setOwner(BigDecimal idValue) {
    }

    public void setOwner(Entity owner) {
    }

    public Id getOwnerId() {
        return new Id();
    }

    public Entity getOwner() {
        return new Entity();
    }

    public static InventoryItem get(String serialNumber, String itemType) {
        return new InventoryItem();
    }

    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType itemType) {
        return new InventoryItem();
    }

    public static InventoryItem getByItemTypeId(String serialNumber, Id itemType) {
        return new InventoryItem();
    }

    public static <T extends InventoryItem> T getByItemType(Class<T> itemClass, String serialNumber, String itemType) {
        //noinspection unchecked
        return (T) new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType itemType) {
        //noinspection unchecked
        return (T) new InventoryItem();
    }

    public static <T extends InventoryItem> T getByItemType(Class<T> itemClass, String serialNumber, String itemType, boolean any) {
        //noinspection unchecked
        return (T) new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType itemType, boolean any) {
        //noinspection unchecked
        return (T) new InventoryItem();
    }

    public static <T extends InventoryItem> T getByItemTypeId(Class<T> itemClass, String serialNumber, Id itemType) {
        //noinspection unchecked
        return (T) new InventoryItem();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType itemType) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType itemType, boolean any) {
        return ObjectIterator.create();
    }

    /**
     * <p>Is this item is a serialized item?</p>
     * <p>A serialized item has a unique serial number (mostly assigned by the manufacturer itself). The item is
     * always tracked by the serial number in the system.</p>
     *
     * @return True or false.
     */
    public final boolean isSerialized() {
        return getItemType().isSerialized();
    }

    /**
     * <p>Is this item is an expendable item?</p>
     * <p>Items (such as nut, bolt, rivet etc.) for which (1) no authorized repair procedure exists, and/or
     * (2) the cost of repair would exceed cost of its replacement. Expendable items are usually considered to be
     * consumed when issued and are not recorded as returnable inventory.</p>
     *
     * @return True or false.
     */
    public final boolean isExpendable() {
        return getItemType().isExpendable();
    }

    /**
     * <p>Is this item is a consumable item?</p>
     * <p>A consumable item (or a consumable) is an item that is once used, can not be recovered. Once issued from
     * stores, consumables gets incorporated into other items and loose their identity. An example of a consumable
     * is paint.</p>
     *
     * @return True or false.
     */
    public final boolean isConsumable() {
        return getItemType().isConsumable();
    }

    /**
     * <p>Is this item a tool?</p>
     * <p>A tool is always tracked when issued to a "Maintenance Unit"</p>
     *
     * @return True or false.
     */
    public final boolean isTool() {
        return getItemType().isTool();
    }

    public static Class<? extends InventoryItemType> getItemTypeClass() {
        return InventoryItemType.class;
    }

    /**
     * Is shelf-life applicable?
     *
     * @return True or false.
     */
    public final boolean isShelfLifeApplicable() {
        return getItemType().isShelfLifeApplicable();
    }

    /**
     * Get the shelf-life of this item.
     *
     * @return Date of expiry if shelf-life is applicable, otherwise <code>null</code>.
     */
    public Date getShelfLife() {
        return null;
    }

    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
        getItemType().checkUnit(quantity, name);
    }

    public void checkUnit(Quantity quantity) throws Invalid_State {
        getItemType().checkUnit(quantity, null);
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T itemType, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T itemType, String serialNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T itemType, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T itemType, String serialNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }
}
