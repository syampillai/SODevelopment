package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("RedundantThrows")
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

    public String getSerialNumberDisplay() {
        return "";
    }

    public void setBatchTag(String batchTag) {
    }

    public String getBatchTag() {
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

    public String getLocationDisplay() {
        return "";
    }

    void location(InventoryLocation location) {
    }

    void quantity(Quantity quantity) {
    }

    public InventoryLocation getRealLocation() {
        return getLocation();
    }

    public boolean isAvailableAt(InventoryLocation location) {
        return false;
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

    public UnitCost getUnitCost(boolean guessIfZero) {
        return getUnitCost();
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

    /**
     * Update the cost of this item.
     *
     * @param tm Transaction manager.
     * @param newCost New cost.
     * @param updateAll Update all items or not (applicable for serialized items only).
     * @throws Exception if error occurs while updating.
     * @return True if updated.
     */
    public boolean updateCost(TransactionManager tm, Money newCost, boolean updateAll) throws Exception {
        return Math.random() > 0.5;
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

    public void setGRN(Id grnId) {
    }

    public void setGRN(BigDecimal idValue) {
        setGRN(new Id(idValue));
    }

    public void setGRN(InventoryGRN grn) {
    }

    public final Id getGRNId() {
        return new Id();
    }

    public final InventoryGRN getGRN() {
        return Math.random() > 0.5 ? new InventoryGRN() : null;
    }

    public void moved(InventoryLocation from, InventoryLocation to) throws Exception {
    }

    public static InventoryItem get(String serialNumber, String partNumber) {
        return r == 0 ? null : new InventoryItem();
    }

    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
        //noinspection unchecked
        return r == 0 ? null : (T)new InventoryItem();
    }

    public static InventoryItem getByPartNumberId(String serialNumber, Id partNumber) {
        return new Random().nextBoolean() ? new InventoryItem() : null;
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

    public final boolean canBin(InventoryLocation location) {
        return getPartNumber().canStore(location);
    }

    protected boolean canStore(InventoryLocation location) {
        return canBin(location);
    }

    public boolean isServiceable() {
        return true;
    }

    public boolean isBlocked() {
        return getPartNumber().isBlocked();
    }

    public boolean isObsolete() {
        return getPartNumber().isObsolete();
    }

    public final boolean isSerialized() {
        return getPartNumber().isSerialized();
    }

    public final boolean isExpendable() {
        return getPartNumber().isExpendable();
    }

    public final boolean isConsumable() {
        return getPartNumber().isConsumable();
    }

    public final boolean isTool() {
        return getPartNumber().isTool();
    }

    public boolean isRepairable() {
        return true;
    }

    public final boolean isRepairAllowed() {
        return isSerialized() && isRepairable();
    }

    public final boolean isShelfLifeApplicable() {
        return getPartNumber().isShelfLifeApplicable();
    }

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
        return listItems(partNumber, null, false);
    }

    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, String condition) {
        return listItems(partNumber, condition, false);
    }

    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, boolean includeZeros) {
        return listItems(partNumber, null, includeZeros);
    }

    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, String condition, boolean includeZeros) {
        return ObjectIterator.create();
    }

    public InventoryItem getParentItem() {
        return r == 0 ? null : new InventoryItem();
    }

    public InventoryItem getGrandParentItem() {
        return getParentItem();
    }

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

    public final ObjectIterator<InventoryFitmentPosition> listFitmentPositions() {
        return ObjectIterator.create();
    }

    public final ObjectIterator<InventoryFitmentPosition> listImmediateFitmentPositions() {
        return ObjectIterator.create();
    }

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass) {
        return ObjectIterator.create();
    }

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass, Predicate<O> filter) {
        return ObjectIterator.create();
    }

    /**
     * List of assembly items under this item.
     *
     * @return Iterator containing assembly items under this item.
     */
    public final ObjectIterator<InventoryItem> listAssemblies() {
        return ObjectIterator.create();
    }

    /**
     * List of assembly items under this item.
     *
     * @param filter Filter to be applied.
     * @return Iterator containing assembly items under this item.
     */
    public final ObjectIterator<InventoryItem> listAssemblies(Predicate<InventoryItem> filter) {
        return ObjectIterator.create();
    }

    public boolean isAssemblyIncomplete() {
        return new Random().nextBoolean();
    }

    /**
     * List the assembly positions where items are missing.
     *
     * @return Assembly positions where items are missing.
     */
    public ObjectIterator<InventoryFitmentPosition> listMissingAssemblies() {
        return ObjectIterator.create();
    }

    public boolean wasDataPicked() {
        return wasDataPicked(0);
    }

    public boolean wasDataPicked(int stepsBackward) {
        return new Random().nextBoolean();
    }

    public boolean cameAsAssemblyPart() {
        return new Random().nextBoolean();
    }

    public final int getAssemblyLevel() {
        return new Random().nextInt();
    }

    /**
     * Get that description of the status of this item (serviceability, storage condition etc.)
     * @return Status description.
     */
    public String getStatusDescription() {
        return "";
    }

    /**
     * Get the PO through which this item was procured. If this item was repaired by a repair organization later,
     * this information will not be available anymore, and you should see {@link #getRO()}.
     *
     * @return PO if available.
     */
    public final InventoryPO getPO() {
        return Math.random() > 0.5 ? null : new InventoryPO();
    }

    /**
     * Get the RO through which this item was repaired. If this item was repaired more than once,
     * the latest RO is returned.
     *
     * @return RO if available.
     */
    public final InventoryRO getRO() {
        return Math.random() > 0.5 ? null : new InventoryRO();
    }

    /**
     * Create the GRN for this item. This method is used only for creating GRNs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param store Store.
     * @param grnDate GRN date (Receipt date)
     * @param grnNumber GRN number (if zero is passed, a new number is generated).
     * @param invoiceDate Invoice date.
     * @param invoiceReference Invoice reference (supplier's invoice number).
     * @param supplier Supplier.
     * @return GRN
     * @throws Exception if transaction errors occur.
     */
    public InventoryGRN createGRN(Transaction transaction, InventoryStore store, Date grnDate, int grnNumber,
                                  Date invoiceDate, String invoiceReference, Entity supplier) throws Exception {
        return createGRN(transaction, store, grnDate, grnNumber, invoiceDate, invoiceReference, supplier, 0);
    }

    /**
     * Create the GRN for this item. This method is used only for creating GRNs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param store Store.
     * @param grnDate GRN date (Receipt date)
     * @param grnNumber GRN number (if zero is passed, a new number is generated).
     * @param invoiceDate Invoice date.
     * @param invoiceReference Invoice reference (supplier's invoice number).
     * @param supplier Supplier.
     * @param grnType GRN Type - 0:Purchase, 1:External Owner, 2:Loaned from, 3:Items Repaired by, 4:Sales Return
     * @return GRN
     * @throws Exception if transaction errors occur.
     */
    public InventoryGRN createGRN(Transaction transaction, InventoryStore store, Date grnDate, int grnNumber,
                                  Date invoiceDate, String invoiceReference, Entity supplier, int grnType)
            throws Exception {
        InventoryGRN grn = getGRN();
        if(grn != null) {
            throw new SOException("GRN already exists - " + grn.toDisplay());
        }
        return new InventoryGRN();
    }

    /**
     * Attach the PO for this item. This method is used only for creating POs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param po PO to attach.
     * @throws Exception if transaction errors occur.
     */
    public void attachPO(Transaction transaction, InventoryPO po) throws Exception {
    }

    /**
     * Attach the RO for this item. This method is used only for creating ROs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param ro RO to attach.
     * @throws Exception if transaction errors occur.
     */
    public void attachRO(Transaction transaction, InventoryRO ro) throws Exception {
    }

    public void changePartNumber(Transaction transaction, InventoryItemType newPartNumber) throws Exception {
    }

    /**
     * Resurrect an item so that the same P/N and S/N can be used again.
     *
     * @param cost Cost.
     * @param location New location.
     */
    public void resurrect(Money cost, InventoryLocation location) {
    }

    /**
     * Migrate this item to another type.
     *
     * @param tm Transaction Manager.
     * @param migratedType Migrated item type.
     * @param itemConvertor Item convertor.
     * @throws Exception thrown for errors.
     */
    public void migrate(TransactionManager tm, InventoryItemType migratedType,
                        Function<InventoryItem, InventoryItem> itemConvertor) throws Exception {
        if(getPartNumberId().equals(migratedType.getId())) {
            throw new Invalid_State("Same type");
        }
    }
 }
