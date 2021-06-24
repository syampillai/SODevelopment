package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;
import java.util.function.Predicate;

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

    public void moved(InventoryLocation from, InventoryLocation to) throws Exception {
    }

    public static InventoryItem get(String serialNumber, String partNumber) {
        return r == 0 ? null : new InventoryItem();
    }

    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
        return r == 0 ? null : new InventoryItem();
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

    public final ObjectIterator<InventoryFitmentPosition> listAllFitmentPositions() {
        return ObjectIterator.create();
    }

    public final ObjectIterator<InventoryFitmentPosition> listFitmentPositions() {
        return ObjectIterator.create();
    }

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass) {
        return ObjectIterator.create();
    }

    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass, Predicate<O> filter) {
        return ObjectIterator.create();
    }

    public boolean isAssemblyIncomplete() {
        return new Random().nextBoolean();
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

    public static void saveExternal(Transaction transaction, InventoryItem item, Entity externalEntity) throws Exception {
    }

    public final int getAssemblyLevel() {
        return new Random().nextInt();
    }
}
