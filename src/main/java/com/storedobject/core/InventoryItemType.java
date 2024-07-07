package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public void setUnitOfIssue(MeasurementUnit unitOfIssue) {
    }

    public void setUnitOfIssue(Quantity unitOfIssue) {
    }

    public void setUnitOfIssue(Object value) {
    }

    @Column(required = false)
    public Quantity getUnitOfIssue() {
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

    public void setHSNCode(String hSNCode) {
    }

    public String getHSNCode() {
        return "";
    }

    public void setUNNumber(String uNNumber) {
    }

    public String getUNNumber() {
        return "";
    }

    public void changeUnitOfMeasurement(TransactionManager tm, Quantity uom, Quantity uoi) throws Exception {
        if(Math.random() > 0.5) {
            throw new Invalid_State();
        }
    }

    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
        if(Math.random() > 0.5) {
            throw new Invalid_State();
        }
    }

    public void checkUnit(Quantity quantity) throws Invalid_State {
        if(Math.random() > 0.5) {
            throw new Invalid_State();
        }
    }

    public boolean isSerialized() {
        return Math.random() > 0.5;
    }

    public boolean isExpendable() {
        return Math.random() > 0.5;
    }

    public boolean isConsumable() {
        return Math.random() > 0.5;
    }

    protected boolean getConsumable() {
        return false;
    }

    public boolean isTool() {
        return Math.random() > 0.5;
    }

    public boolean isRepairable() {
        return true;
    }

    public final boolean isRepairAllowed() {
        return isSerialized() && isRepairable();
    }

    public Id getCategoryId() {
        return Math.random() > 0.5 ? null : new Id();
    }

    public final boolean isAssembly() {
        return exists(InventoryAssembly.class, "ParentItemType=" + getId());
    }

    public boolean isAPN(InventoryItemType anotherPartNumber) {
        return false;
    }

    public final boolean isAPN(InventoryItem anotherItem) {
        return anotherItem != null && isAPN(anotherItem.getPartNumber());
    }

    public boolean isShelfLifeApplicable() {
        return Math.random() > 0.5;
    }

    @SuppressWarnings("unchecked")
    public final <T extends InventoryItem> T createItem() {
        return (T) new InventoryItem();
    }

    public final <T extends InventoryItem> T createItem(String serialNumber) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new InventoryItem() : null;
    }

    public final Class<? extends InventoryItem> getItemType() {
        return InventoryItem.class;
    }

    public static InventoryItemType get(String partNumber) {
        return Math.random() > 0.5 ? null : new InventoryItemType();
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

    public ObjectIterator<InventoryItem> listItems() {
        return listItems(null, false);
    }

    public ObjectIterator<InventoryItem> listItems(String condition) {
        return listItems(condition, false);
    }

    public ObjectIterator<InventoryItem> listItems(boolean includeZeros) {
        return listItems(null, includeZeros);
    }

    public ObjectIterator<InventoryItem> listItems(String condition, boolean includeZeros) {
        return InventoryItem.listItems(this, condition, includeZeros);
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

    public boolean isBlocked() {
        return false;
    }

    public boolean isObsolete() {
        return false;
    }

    public final boolean canBin(InventoryLocation location) {
        return true;
    }

    protected boolean canStore(InventoryLocation location) {
        return true;
    }

    public final ObjectIterator<InventoryAssembly> listAssemblies() {
        return ObjectIterator.create();
    }

    public final ObjectIterator<InventoryAssembly> listAssemblies(Predicate<InventoryAssembly> filter) {
        return ObjectIterator.create();
    }

    public final ObjectIterator<InventoryAssembly> listImmediateAssemblies() {
        return ObjectIterator.create();
    }

    public final ObjectIterator<InventoryAssembly> listImmediateAssemblies(Predicate<InventoryAssembly> filter) {
        return ObjectIterator.create();
    }

    public final List<InventoryItemType> listAPNs() {
        return new ArrayList<>();
    }

    public static List<InventoryItemType> listAPNs(Id partNumberId) {
        return new ArrayList<>();
    }

    public void validateUoMCorrection(MeasurementUnit from, MeasurementUnit to) throws Exception {
        MeasurementUnit uom = getUnitOfMeasurement().getUnit();
        String m = " compatible with the unit '" + uom + "' (default unit of measurement for this item)";
        if(from.equals(getUnitOfMeasurement().getUnit())) {
            throw new SOException("The unit '" + from + "' is same as the UoM of this item");
        }
        if(from.isCompatible(uom)) {
            throw new SOException("The unit '" + from + "' is" + m);
        }
        if(!to.isCompatible(uom)) {
            throw new SOException("The unit '" + to + "' is not" + m);
        }
        if(isSerialized() && !to.getUnit().equals("NO")) {
            throw new SOException("Wrong unit for serialized items - " + to);
        }
    }

    public void correctUoM(TransactionManager tm, MeasurementUnit from, MeasurementUnit to) throws Exception {
        if(Math.random() > 0.5) {
            throw new Invalid_State();
        }
    }

    /**
     * Migrate this item type to another. All the items of this type will also be migrated using the "item convertor".
     *
     * @param tm Transaction Manager.
     * @param migratedType Migrated item type.
     * @param itemConvertor Item convertor.
     * @throws Exception thrown for errors.
     */
    public void migrate(TransactionManager tm, InventoryItemType migratedType,
                        Function<InventoryItem, InventoryItem> itemConvertor) throws Exception {
        if(Math.random() > 0.5) {
            throw new Invalid_State();
        }
    }

    /**
     * Create an APN for this P/N.
     * <p>Note: All attributes of this P/N will be automatically copied.</p>
     *
     * @param apn The P/N of the APN.
     * @param tm Transaction manager.
     * @return Newly created P/N that is already added as an APN to this.
     * @exception Exception is thrown if any DB errors occur while creating the APN.
     */
    public InventoryItemType createAPN(String apn, TransactionManager tm) throws Exception {
        Transaction t = null;
        try {
            t = tm.createTransaction();
            InventoryItemType a = get(getClass(), getId());
            a.makeNew();
            a.setPartNumber(apn);
            a.save(t);
            InventoryAPN ia = new InventoryAPN();
            ia.setPartNumber(a);
            ia.save(t);
            addLink(t, ia);
            List<StoredObjectUtility.Link<?>> links = StoredObjectUtility.linkDetails(getClass());
            for(StoredObjectUtility.Link<?> link: links) {
                for(StoredObject child: link.list(this)) {
                    child.makeNew();
                    child.save(t);
                    a.addLink(t, child, link.getType());
                }
            }
            t.commit();
            return get(getClass(), a.getId());
        } catch(Exception e) {
            if(t != null) {
                t.rollback();
            }
            throw e;
        }
    }
}