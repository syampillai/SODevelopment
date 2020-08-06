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

    public void setPartNumber(Id partNumberId) {
    }

    public void setPartNumber(BigDecimal idValue) {
    }

    public void setPartNumber(InventoryItemType partNumber) {
    }

    public Id getPartNumberId() {
        return new Id();
    }

    public InventoryItemType getPartNumber() {
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

    public void setLocation(InventoryLocation location) {
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
        return Count.ONE;
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
        setOwner(new Id(idValue));
    }

    public void setOwner(Entity owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    public Id getOwnerId() {
        return new Id();
    }

    public Entity getOwner() {
        return new Entity();
    }

    public static InventoryItem get(String serialNumber, String partNumber) {
        return new InventoryItem();
    }

    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
        return new InventoryItem();
    }

    public static InventoryItem getByPartNumberId(String serialNumber, Id partNumber) {
        return new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber) {
        //noinspection unchecked
        return (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
        //noinspection unchecked
        return (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber, boolean any) {
        //noinspection unchecked
        return (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
        //noinspection unchecked
        return (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumberId(Class<T> itemClass, String serialNumber, Id partNumber) {
        //noinspection unchecked
        return (T)new InventoryItem();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
        return ObjectIterator.create();
    }

    public final boolean canStore(InventoryLocation location) {
        return getPartNumber().canStore(location);
    }

    /**
     * <p>Is this item is a serialized item?</p>
     * <p>A serialized item has a unique serial number (mostly assigned by the manufacturer itself). The item is
     * always tracked by the serial number in the system.</p>
     *
     * @return True or false.
     */
    public final boolean isSerialized() {
        return getPartNumber().isSerialized();
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
        return getPartNumber().isExpendable();
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
        return getPartNumber().isConsumable();
    }

    /**
     * <p>Is this item a tool?</p>
     * <p>A tool is always tracked when issued to a "Maintenance Unit"</p>
     *
     * @return True or false.
     */
    public final boolean isTool() {
        return getPartNumber().isTool();
    }

    /**
     * Is shelf-life applicable?
     *
     * @return True or false.
     */
    public final boolean isShelfLifeApplicable() {
        return getPartNumber().isShelfLifeApplicable();
    }

    /**
     * Get the shelf-life of this item.
     *
     * @return Date of expiry if shelf-life is applicable, otherwise <code>null</code>.
     */
    public Date getShelfLife() {
        return null;
    }

    public final Class<? extends InventoryItemType> getPartNumberType() {
        return InventoryItemType.class;
    }

    public final Class<? extends InventoryItemType> getItemType() {
        return InventoryItemType.class;
    }

    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
    }

    public void checkUnit(Quantity quantity) throws Invalid_State {
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber, String serialNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber, String serialNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(InventoryItemType partNumber, String serialNumber) {
        return ObjectIterator.create();
    }

    public final String getPartNumberName() {
        return "";
    }

    public final String getPartNumberShortName() {
        return "";
    }

    public final String getSerialNumberName() {
        return "";
    }

    public final String getSerialNumberShortName() {
        return "";
    }
}
