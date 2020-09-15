package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Denotes an item in the inventory.
 *
 * @author Syam
 */
public class InventoryItem extends StoredObject {

    private static final int r = new Random().nextInt();

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

    /**
     * This will be invoked whenever this item is moved from one location to another. This is called from
     * within the transaction. So, {@link #getTransaction()} will return the current transaction.
     *
     * @param from Location from.
     * @param to Location to.
     * @throws Exception An exception may be raised if the move not legal.
     */
    public void moved(InventoryLocation from, InventoryLocation to) throws Exception {
    }

    public static InventoryItem get(String serialNumber, String partNumber) {
        return r == 0 ? null : new InventoryItem();
    }

    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
        return r == 0 ? null : new InventoryItem();
    }

    public static InventoryItem getByPartNumberId(String serialNumber, Id partNumber) {
        return new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber, boolean any) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static <T extends InventoryItem> T getByPartNumberId(Class<T> itemClass, String serialNumber, Id partNumber) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
        return ObjectIterator.create();
    }

    public boolean canStore(InventoryLocation location) {
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

    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber) {
        return listStock(partNumber, null, (InventoryStore) null);
    }

    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber) {
        return ObjectIterator.create();
    }

    /**
     * Get the parent item on which this item is fitted on.
     *
     * @return Parent item if exists.
     */
    public InventoryItem getParentItem() {
        return r == 0 ? null : new InventoryItem();
    }

    /**
     * Get the parent/grand-parents item on which this item is fitted on.
     *
     * @param itemClass Type of parent/grand-parent to look for.
     * @return Parent item if exists.
     */
    public <I extends InventoryItem> InventoryItem getParentItem(Class<I> itemClass) {
        //noinspection unchecked
        return r == 0 ? null : (I)new InventoryItem();
    }

    public final InventoryLocation getPreviousLocation() {
        InventoryLedger move = get(InventoryLedger.class, "Item=" + getId(), "Date,TranId");
        return move == null ? null : move.getLocationFrom();
    }

    public final InventoryLocation getPreviousLocation(int stepsBackward) {
        return new InventoryBin();
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

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass) {
        return ObjectIterator.create();
    }

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass, Predicate<O> filter) {
        return ObjectIterator.create();
    }
}
