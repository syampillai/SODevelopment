package com.storedobject.core;

/**
 * An inventory item type denotes a "part number".
 *
 * @author Syam
 */
public class InventoryItemType extends StoredObject implements HasChildren {

    public InventoryItemType() {
    }

    public static void columns(Columns columns) {
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
    }

    public void setPartNumber(String partNumber) {
    }

    public String getPartNumber() {
        return "";
    }

    public void setUnitOfMeasurement(MeasurementUnit unitOfMeasurement) {
    }

    public void setUnitOfMeasurement(Quantity unitOfMeasurement) {
    }

    public void setUnitOfMeasurement(Object value) {
    }

    public Quantity getUnitOfMeasurement() {
        return Count.ZERO;
    }

    public final MeasurementUnit getUnit() {
        return Count.ZERO.getUnit();
    }

    public void setUnitCost(Money unitCost) {
    }

    public void setUnitCost(Object moneyValue) {
    }

    public Money getUnitCost() {
        return new Money();
    }

    public UnitCost getUnitCost(MeasurementUnit unit) {
        return new UnitCost(new Money(), Count.ONE);
    }

    public Money getCost(Quantity quantity) {
        return new Money();
    }

    public void setMinimumStockLevel(Quantity minimumStockLevel) {
    }

    public void setMinimumStockLevel(Object value) {
    }

    public Quantity getMinimumStockLevel() {
        return Count.ZERO;
    }

    public void setReorderPoint(Quantity reorderPoint) {
    }

    public void setReorderPoint(Object value) {
    }

    public Quantity getReorderPoint() {
        return Count.ZERO;
    }

    public void setEconomicOrderQuantity(Quantity economicOrderQuantity) {
    }

    public void setEconomicOrderQuantity(Object value) {
    }

    public Quantity getEconomicOrderQuantity() {
        return Count.ZERO;
    }

    public int getAverageLeadTime() {
        return 0;
    }

    public void setAverageLeadTime(int averageLeadTime) {
    }

    public int getRepairTurnaroundTime() {
        return 0;
    }

    public void setRepairTurnaroundTime(int repairTurnAroundTime) {
    }

    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
    }

    public void checkUnit(Quantity quantity) throws Invalid_State {
    }

    /**
     * <p>Is this item is a serialized item?</p>
     * <p>A serialized item has a unique serial number (mostly assigned by the manufacturer itself). The item is
     * always tracked by the serial number in the system.</p>
     *
     * @return True or false.
     */
    public boolean isSerialized() {
        return false;
    }

    /**
     * <p>Is this item is an expendable item?</p>
     * <p>Items (such as nut, bolt, rivet etc.) for which (1) no authorized repair procedure exists, and/or
     * (2) the cost of repair would exceed cost of its replacement. Expendable items are usually considered to be
     * consumed when issued and are not recorded as returnable inventory.</p>
     *
     * @return True or false.
     */
    public boolean isExpendable() {
        return false;
    }

    /**
     * <p>Is this item is a consumable item?</p>
     * <p>A consumable item (or a consumable) is an item that is once used, can not be recovered. Once issued from
     * stores, consumables gets incorporated into other items and loose their identity. An example of a consumable
     * is paint.</p>
     *
     * @return True or false.
     */
    public boolean isConsumable() {
        return false;
    }

    /**
     * <p>Is this item a tool?</p>
     * <p>A tool is always tracked when issued to a "Maintenance Unit"</p>
     *
     * @return True or false.
     */
    public boolean isTool() {
        return false;
    }

    /**
     * Get the category Id of this item. A category Id could be anything an organization may want to maintain
     * items in some special category groups. For example, in an airline store, items may be categorized based on
     * the aircraft fleet and in that case, even if some items can be used across the fleets, it may be still
     * maintained per fleet.
     *
     * @return Category Id. Default is <code>null</code>.
     */
    public Id getCategoryId() {
        return null;
    }

    /**
     * Is this item an assembly?
     *
     * @return True or false.
     */
    public final boolean isAssembly() {
        return exists(InventoryAssembly.class, "ParentItemType=" + getId());
    }

    /**
     * Is this item an alternate part number for a given item.
     *
     * @param alternatePartNumber An alternate item.
     * @return True or false.
     */
    public boolean isAPN(InventoryItemType alternatePartNumber) {
        return false;
    }

    /**
     * Is this item an alternate part number for a given item.
     *
     * @param alternatePart An alternate item.
     * @return True or false.
     */
    public final boolean isAPN(InventoryItem alternatePart) {
        return alternatePart != null && isAPN(alternatePart.getPartNumber());
    }

    /**
     * is shelf-life applicable?
     *
     * @return True or false.
     */
    public boolean isShelfLifeApplicable() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final <T extends InventoryItem> T createItem() {
        return (T) new InventoryItem();
    }

    public final Class<? extends InventoryItem> getItemType() {
        return InventoryItem.class;
    }

    public static InventoryItemType get(String partNumber) {
        return new InventoryItemType();
    }

    public static ObjectIterator<? extends InventoryItemType> list(String partNumber) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber) {
        //noinspection unchecked
        return (T) new InventoryItemType();
    }

    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        //noinspection unchecked
        return (T) new InventoryItemType();
    }

    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber) {
        return ObjectIterator.create();
    }

    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        return ObjectIterator.create();
    }

    public ObjectIterator<InventoryItem> listStock(InventoryStore store) {
        return ObjectIterator.create();
    }

    public ObjectIterator<InventoryItem> listStock(String serialNumber, InventoryStore store) {
        return ObjectIterator.create();
    }

    public ObjectIterator<InventoryItem> listStock(InventoryLocation location) {
        return ObjectIterator.create();
    }

    public ObjectIterator<InventoryItem> listStock(String serialNumber, InventoryLocation location) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber) {
        return ObjectIterator.create();
    }

    public static ObjectIterator<InventoryItem> listStock(InventoryItemType partNumber, String serialNumber) {
        return ObjectIterator.create();
    }

    public boolean checkStock(InventoryStore store, Quantity quantity) {
        return true;
    }

    public boolean checkStock(InventoryStore store, Quantity quantity, String serialNumber) {
        return true;
    }

    public boolean checkStock(InventoryLocation location, Quantity quantity) {
        return true;
    }

    public boolean checkStock(InventoryLocation location, Quantity quantity, String serialNumber) {
        return true;
    }

    public String getPartNumberName() {
        return "Part Number";
    }

    public String getPartNumberShortName() {
        return "P/N";
    }

    public String getSerialNumberName() {
        return isSerialized() ? "Serial Number" : "Serial/Lot/Batch Number";
    }

    public String getSerialNumberShortName() {
        return "S/N";
    }

    public boolean canStore(InventoryLocation location) {
        return true;
    }

    public final ObjectIterator<InventoryAssembly> listAssemblies() {
        return ObjectIterator.create();
    }
}