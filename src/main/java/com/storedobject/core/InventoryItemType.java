package com.storedobject.core;

import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An inventory item type denotes a "part number".
 *
 * @author Syam
 */
@SuppressWarnings("resource")
public class InventoryItemType extends StoredObject implements HasChildren {

    static final WeakHashMap<Id, List<InventoryItemType>> apns = new WeakHashMap<>();
    private String name;
    private String partNumber;
    private Quantity unitOfMeasurement = Quantity.create(Quantity.class);
    private Quantity unitOfIssue = Quantity.create(Quantity.class);
    private Money unitCost = new Money();
    private Quantity minimumStockLevel = Quantity.create(Quantity.class);
    private Quantity reorderPoint = Quantity.create(Quantity.class);
    private Quantity economicOrderQuantity = Quantity.create(Quantity.class);
    private int averageLeadTime;
    private String hSNCode, uNNumber;

    public InventoryItemType() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("PartNumber", "text");
        columns.add("UnitOfMeasurement", "quantity");
        columns.add("UnitOfIssue", "quantity");
        columns.add("UnitCost", "money");
        columns.add("HSNCode", "text");
        columns.add("UNNumber", "text");
        columns.add("MinimumStockLevel", "quantity");
        columns.add("ReorderPoint", "quantity");
        columns.add("EconomicOrderQuantity", "quantity");
        columns.add("AverageLeadTime", "int");
    }
    
    public static void indices(Indices indices) {
        indices.add("PartNumber", true);
        indices.add("lower(Name)", false);
    }

    @Override
    public String getUniqueCondition() {
        return "PartNumber='" + partNumber + "'";
    }

    public static String[] links() {
        return new String[] {
                "Alternate Part Numbers|com.storedobject.core.InventoryAPN|||0",
        };
    }

    public static String[] searchColumns() {
        return new String[] {
            "PartNumber",
            "Name",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
            "PartNumber",
            "Name",
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = toCode(partNumber);
    }

    @Column(style = "(code)")
    public String getPartNumber() {
        return partNumber;
    }

    public void setUnitOfMeasurement(MeasurementUnit unitOfMeasurement) {
        setUnitOfMeasurement(Quantity.create(0, unitOfMeasurement));
    }

    public void setUnitOfMeasurement(Quantity unitOfMeasurement) {
        if(unitOfMeasurement != null) {
            this.unitOfMeasurement = unitOfMeasurement.zero();
        }
    }

    public void setUnitOfMeasurement(Object value) {
        setUnitOfMeasurement(Quantity.create(value));
    }

    @Column(required = false)
    public Quantity getUnitOfMeasurement() {
        return unitOfMeasurement.zero();
    }

    public void setUnitOfIssue(MeasurementUnit unitOfIssue) {
        setUnitOfIssue(Quantity.create(0, unitOfIssue));
    }

    public void setUnitOfIssue(Quantity unitOfIssue) {
        if(unitOfIssue != null) {
            this.unitOfIssue = unitOfIssue.zero();
        }
    }

    public void setUnitOfIssue(Object value) {
        setUnitOfIssue(Quantity.create(value));
    }

    @Column(required = false)
    public Quantity getUnitOfIssue() {
        return unitOfIssue.zero();
    }

    public final MeasurementUnit getUnit() {
        return unitOfMeasurement.getUnit();
    }
    
    public void setUnitCost(Money unitCost) {
        if(unitCost == null || (unitCost.isZero() && unitCost.getCurrency() != Money.getDefaultCurrency())) {
            this.unitCost = new Money();
        } else {
            this.unitCost = unitCost;
        }
    }

    public void setUnitCost(Object moneyValue) {
        setUnitCost(Money.create(moneyValue));
    }

    @Column(required = false)
    public Money getUnitCost() {
        return unitCost;
    }
    
    private UnitCost uc() {
        return new UnitCost(unitCost, getUnit());
    }

    public UnitCost getUnitCost(MeasurementUnit unit) {
        return uc().getUnitCost(unit);
    }
    
    public Money getCost(Quantity quantity) {
        return uc().getCost(quantity);
    }

    public void setMinimumStockLevel(Quantity minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public void setMinimumStockLevel(Object value) {
        setMinimumStockLevel(Quantity.create(value));
    }

    @Column(required = false)
    public Quantity getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setReorderPoint(Quantity reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public void setReorderPoint(Object value) {
        setReorderPoint(Quantity.create(value));
    }

    @Column(required = false)
    public Quantity getReorderPoint() {
        return reorderPoint;
    }

    public void setEconomicOrderQuantity(Quantity economicOrderQuantity) {
        this.economicOrderQuantity = economicOrderQuantity;
    }

    public void setEconomicOrderQuantity(Object value) {
        setEconomicOrderQuantity(Quantity.create(value));
    }

    @Column(required = false)
    public Quantity getEconomicOrderQuantity() {
        return economicOrderQuantity;
    }

    @Column(order = 700, required = false, caption = "Average Lead Time (In Days)")
    public int getAverageLeadTime() {
        return averageLeadTime;
    }

    public void setAverageLeadTime(int averageLeadTime) {
        this.averageLeadTime = averageLeadTime;
    }

    public void setHSNCode(String hSNCode) {
        this.hSNCode = toCode(hSNCode);
    }

    @Column(required = false, style = "(code)")
    public String getHSNCode() {
        return hSNCode;
    }

    public void setUNNumber(String uNNumber) {
        this.uNNumber = toCode(uNNumber);
    }

    @Column(required = false, style = "(code)")
    public String getUNNumber() {
        return uNNumber;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(isSerialized()) {
            unitOfMeasurement = Count.ONE;
        }
        partNumber = toCode(partNumber);
        if(!deleted()) {
            if(partNumber.isEmpty()) {
                throw new Invalid_Value("Part Number");
            }
            String duplicateCondition = "PartNumber='" + partNumber + "'";
            if(!inserted()) {
                duplicateCondition += " AND T.Id<>" + getId();
            }
            if(count(InventoryItemType.class, duplicateCondition, true) > 0) {
                throw new Invalid_State("Duplicate P/N: " + list(InventoryItemType.class, duplicateCondition, true)
                        .findFirst().toDisplay());
            }
        }
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        if(deleted()) {
            checkDelete();
        }
        if(!deleted()) {
            checkUnit(unitOfIssue, "Unit of Issue");
            checkUnit(minimumStockLevel, "Minimum Stock Level");
            checkUnit(reorderPoint, "Reorder Point");
            checkUnit(economicOrderQuantity, "Economic Order Quantity");
            if(invalidHSN()) {
                throw new Invalid_Value("HSN Code");
            }
            if(invalidUNNumber()) {
                throw new Invalid_Value("UN Number");
            }
        }
        super.validateData(tm);
    }

    private boolean invalidHSN() {
        hSNCode = toCode(hSNCode);
        if(hSNCode.isEmpty()) {
            return false;
        }
        if(!StringUtility.isDigit(hSNCode)) {
            return true;
        }
        return switch (hSNCode.length()) {
            case 2, 6, 8, 10 -> false;
            default -> true;
        };
    }

    private boolean invalidUNNumber() {
        uNNumber = toCode(uNNumber);
        if(uNNumber.isEmpty()) {
            return false;
        }
        if(!StringUtility.isDigit(uNNumber)) {
            return true;
        }
        return uNNumber.length() != 4;
    }

    private void checkDelete() throws Invalid_State {
        Transaction t = getTransaction();
        InventoryItem item = list(t, InventoryItem.class, "PartNumber=" + getId(), true).findFirst();
        if(item != null) {
            throw new Invalid_State("Can not delete, an item of this type already exists - " + item.toDisplay());
        }
        InventoryAssembly assembly = list(t, InventoryAssembly.class, "ItemType=" + getId()).findFirst();
        if(assembly == null) {
            assembly = list(t, InventoryAssembly.class, "ParentItemType=" + getId()).findFirst();
        }
        if(assembly != null) {
            throw new Invalid_State("Can not delete, an assembly definition exists - " + assembly.toDisplay());
        }
        ObjectIterator<InventoryStore> stores = list(InventoryStore.class, true);
        for(InventoryStore store: stores) {
            ObjectIterator<InventoryPO> pos = list(InventoryPO.class,
                    "Store=" + store.getId() + " AND Status<4", true);
            for(InventoryPO po: pos) {
                ObjectIterator<InventoryPOItem> items = po.listItems();
                for(InventoryPOItem poi: items) {
                    if(poi.getPartNumberId().equals(getId())) {
                        IO.close(items, pos, stores);
                        throw new Invalid_State("Can't delete Purchase Order. " + po.getReference()
                                + " has this item.");
                    }
                }
            }
        }
        ObjectIterator<InventoryLocation> locations = list(InventoryVirtualLocation.class,
                "Type IN (4,5,9,11,12,13,16,17)").map(l -> l);
        locations = locations.add(list(InventoryStoreBin.class).map(l -> l));
        for(InventoryLocation location: locations) {
            ObjectIterator<MaterialRequest> mrs = list(MaterialRequest.class,
                    "ToLocation=" + location.getId() + " AND (Status>0 AND Status<3) OR Reserved", true);
            for(MaterialRequest mr: mrs) {
                ObjectIterator<MaterialRequestItem> items = mr.listLinks(MaterialRequestItem.class, true);
                for(MaterialRequestItem mri: items) {
                    if(mri.getPartNumberId().equals(getId())) {
                        IO.close(items, mrs, locations);
                        throw new Invalid_State("Can't delete Material Request. " + mr.getReference()
                                + " has this item.");
                    }
                }
            }
        }
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        InventoryItemType old = get(getClass(), getId(), true);
        if(old == null) {
            return;
        }
        int uType = old.unitOfMeasurement.getUnit().getType();
        if(uType != unitOfMeasurement.getUnit().getType()) {
            InventoryItem item = InventoryItem.listItems(this).filter(i -> {
                Quantity q = i.getQuantity();
                if(q.isZero()) {
                    return false;
                }
                return q.getUnit().getType() != uType;
            }).findFirst();
            if(item != null) {
                throw new SOException("Item with incompatible unit exists - " + item.toDisplay() + ", Quantity: "
                        + item.getQuantity());
            }
        }
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        if(child instanceof InventoryAPN) {
            InventoryItemType apn = ((InventoryAPN) child).getPartNumber();
            if(apn.isSerialized() != isSerialized() || apn.getClass() != getClass()) {
                throw new Invalid_State("Incompatible Alternate Part Number - '" + apn.toDisplay());
            }
        }
        super.validateChildAttach(child, linkType);
    }

    /**
     * Check whether the measurement unit of the given quantity compatible for this item or not.
     *
     * @param quantity Quantity to check.
     * @param name Name of the quantity (used to generate message of the exception).
     * @throws Invalid_State Throws if the measurement unit is not compatible.
     */
    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
        if(quantity.getUnit().getType() == unitOfMeasurement.getUnit().getType()) {
            return;
        }
        if(name == null) {
            throw new Invalid_State("Unit '" + unitOfMeasurement.getUnit() + "' of '" + this.toDisplay() +
                    "' is not compatible with '" + quantity.getUnit() + "'");
        }
        throw new Invalid_State("Measurement unit of " + name + " '" + quantity.getUnit().getUnit() +
                "' is not compatible with '" + unitOfMeasurement.getUnit().getUnit() + "'");
    }

    /**
     * Check whether the measurement unit of the given quantity compatible for this item or not.
     *
     * @param quantity Quantity to check.
     * @throws Invalid_State Throws if the measurement unit is not compatible.
     */
    public void checkUnit(Quantity quantity) throws Invalid_State {
        checkUnit(quantity, null);
    }

    /**
     * <p>Is this a serialized item?</p>
     * <p>A serialized item has a unique serial number (mostly assigned by the manufacturer itself). The item is
     * always tracked by the serial number in the system.</p>
     *
     * @return True or false.
     */
    public boolean isSerialized() {
        return false;
    }

    /**
     * <p>Is this an expendable item?</p>
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
     * <p>Is this a consumable item?</p>
     * <p>A consumable item (or a consumable) is an item that is once used, can not be recovered. Once issued from
     * stores, consumables gets incorporated into other items and loose their identity. An example of a consumable
     * is paint.</p>
     *
     * @return True or false.
     */
    public boolean isConsumable() {
        return getConsumable();
    }

    protected boolean getConsumable() {
        return false;
    }

    /**
     * <p>Is this item a tool?</p>
     * <p>A tool is always tracked when issued to a location other than another store.</p>
     *
     * @return True or false.
     */
    public boolean isTool() {
        return false;
    }

    /**
     * <p>Is this a repairable item?</p>
     * <p>Generally, serialized items are repairable but this method can return <code>false</code> if a serialized
     * item is not repairable. If this method returns <code>true</code> for non-serialized items, it will be
     * ignored by the {@link #isRepairAllowed()} method.</p>
     *
     * @return True or false.
     */
    public boolean isRepairable() {
        return true;
    }

    /**
     * <p>Is this a repairable item?</p>
     * <p>This method is used to check whether an item is repairable or not. It makes sure that the item
     * is a serialized items and its {@link #isRepairable()} returns <code>true</code>.</p>
     *
     * @return True or false.
     */
    public final boolean isRepairAllowed() {
        return isSerialized() && isRepairable();
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
     * Is this item an alternate part number for a given part number?
     *
     * @param anotherPartNumber Another part number.
     * @return True or false.
     */
    public final boolean isAPN(InventoryItemType anotherPartNumber) {
        return anotherPartNumber != null && !Id.isNull(anotherPartNumber.getId()) &&
                ((anotherPartNumber.getId().equals(getId())) || listAPNs().contains(anotherPartNumber));
    }

    /**
     * Is this item an alternate part number for a given item?
     *
     * @param anotherItem Another item.
     * @return True or false.
     */
    public final boolean isAPN(InventoryItem anotherItem) {
        return anotherItem != null && isAPN(anotherItem.getPartNumber());
    }

    /**
     * is shelf-life applicable?
     *
     * @return True or false.
     */
    public boolean isShelfLifeApplicable() {
        return false;
    }

    @Override
    public String toString() {
        return getName() + ", " + getPartNumberShortName() + " " + partNumber;
    }

    /**
     * Create an item of this type.
     *
     * @return Item created.
     * @param <T> Type of the item.
     */
    @SuppressWarnings("unchecked")
    public final <T extends InventoryItem> T createItem() {
        try {
            T item = (T) getItemType().getDeclaredConstructor().newInstance();
            item.setPartNumber(this);
            return item;
        } catch (Exception error) {
            throw new SORuntimeException(error);
        }
    }

    /**
     * Create an item of this type.
     *
     * @param serialNumber Serial number to be set for the created item.
     * @return Item created. Null is returned if the item with the same serial number already exists.
     * @param <T> Type of the item.
     */
    public final <T extends InventoryItem> T createItem(String serialNumber) {
        T ii = createItem();
        serialNumber = toCode(serialNumber);
        if(isSerialized() && exists(ii.getClass(), "PartNumber=" + getId() + " AND SerialNumber='"
                + serialNumber.replace("'", "''") + "'")) {
            return null;
        }
        ii.setSerialNumber(serialNumber);
        return ii;
    }

    public final Class<? extends InventoryItem> getItemType() {
        String className = getClass().getName();
        if(className.endsWith("Type")) {
            try {
                //noinspection unchecked
                return (Class<? extends InventoryItem>) JavaClassLoader.getLogic(className.substring(0, className.length() - 4));
            } catch(Throwable ignored) {
            }
        }
        throw new SORuntimeException("Design error");
    }

    public static InventoryItemType get(String partNumber) {
        return getByPartNumber(InventoryItemType.class, partNumber, true);
    }

    public static ObjectIterator<? extends InventoryItemType> list(String partNumber) {
        return listByPartNumber(InventoryItemType.class, partNumber, true);
    }

    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber) {
        return getByPartNumber(itemClass, partNumber, false);
    }

    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        if(partNumber == null) {
            return null;
        }
        String code = toCode(partNumber);
        partNumber = partNumber.replace("'", "''").toLowerCase();
        T t = get(itemClass, "PartNumber='" + code + "'", any);
        if(t != null) {
            return t;
        }
        t = get(itemClass, "lower(Name)='" + partNumber + "'", any);
        if(t != null) {
            return t;
        }
        t = list(itemClass, "PartNumber LIKE '" + code + "%'", any).single(false);
        if(t != null) {
            return t;
        }
        return list(itemClass, "lower(Name) LIKE '" + partNumber + "%'", any).single(false);
    }

    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber) {
        return listByPartNumber(itemClass, partNumber, false);
    }
    
    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        if(partNumber == null) {
            return ObjectIterator.create();
        }
        String code = toCode(partNumber);
        partNumber = partNumber.replace("'", "''").toLowerCase();
        return list(itemClass, "PartNumber LIKE '" + code +
                "%' OR lower(Name) LIKE '" + partNumber + "%'", any);
    }

    /**
     * List stock for this part number in a given store.
     *
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public ObjectIterator<InventoryItem> listStock(InventoryStore store) {
        return listStock(null, store);
    }

    /**
     * List stock for this part number in a given store.
     *
     * @param serialNumber Serial number.
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public ObjectIterator<InventoryItem> listStock(String serialNumber, InventoryStore store) {
        return InventoryItem.listStock(this, serialNumber, store);
    }

    /**
     * List stock for this part number at a given location.
     *
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public ObjectIterator<InventoryItem> listStock(InventoryLocation location) {
        return listStock(null, location);
    }

    /**
     * List stock for this part number at a given location.
     *
     * @param serialNumber Serial number.
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public ObjectIterator<InventoryItem> listStock(String serialNumber, InventoryLocation location) {
        return InventoryItem.listStock(this, serialNumber, location);
    }

    /**
     * List all the items of this part number. It will return even the items fitted on assemblies,
     * items sent for repair etc. However, items that are already scrapped/consumed will not be included.
     *
     * @return List of items.
     */
    public ObjectIterator<InventoryItem> listItems() {
        return listItems(null, false);
    }

    /**
     * List all the items of this given part number. It will return even the items fitted on assemblies,
     * items sent for repair etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param condition Additional condition if any. Could be null.
     * @return List of items.
     */
    public ObjectIterator<InventoryItem> listItems(String condition) {
        return listItems(condition, false);
    }

    /**
     * List all the items of this given part number. It will return even the items fitted on assemblies,
     * items sent for repair etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param includeZeros Whether to include zero quantity items (in the case of non-serialized items) or not.
     * @return List of items.
     */
    public ObjectIterator<InventoryItem> listItems(boolean includeZeros) {
        return listItems(null, includeZeros);
    }

    /**
     * List all the items of this given part number. It will return even the items fitted on assemblies,
     * items sent for repair etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param condition Additional condition if any. Could be null.
     * @param includeZeros Whether to include zero quantity items (in the case of non-serialized items) or not.
     * @return List of items.
     */
    public ObjectIterator<InventoryItem> listItems(String condition, boolean includeZeros) {
        return InventoryItem.listItems(this, condition, includeZeros);
    }

    private static <I extends InventoryItem> boolean checkStock(ObjectIterator<I> list, Quantity q) {
        try (list) {
            for (I i : list) {
                q = q.subtract(i.getQuantity());
                if (q.isNegative() || q.isZero()) {
                    return true;
                }
            }
        }
        return q.isNegative() || q.isZero();
    }

    /**
     * Check the availability of stock for this part number in a given store.
     *
     * @param store Store.
     * @param quantity Quantity to be checked.
     * @return True if the required quantity exists.
     */
    public boolean checkStock(InventoryStore store, Quantity quantity) {
        return checkStock(listStock(store), quantity);
    }

    /**
     * Check the availability of stock for this part number in a given store.
     *
     * @param store Store.
     * @param quantity Quantity to be checked.
     * @param serialNumber Serial number.
     * @return True if the required quantity exists.
     */
    public boolean checkStock(InventoryStore store, Quantity quantity, String serialNumber) {
        return checkStock(listStock(serialNumber, store), quantity);
    }

    /**
     * Check the availability of stock for this part number at a given location.
     *
     * @param location Location.
     * @param quantity Quantity to be checked.
     * @return True if the required quantity exists.
     */
    public boolean checkStock(InventoryLocation location, Quantity quantity) {
        return checkStock(listStock(location), quantity);
    }

    /**
     * Check the availability of stock for this part number at a given location.
     *
     * @param location Location.
     * @param quantity Quantity to be checked.
     * @param serialNumber Serial number.
     * @return True if the required quantity exists.
     */
    public boolean checkStock(InventoryLocation location, Quantity quantity, String serialNumber) {
        return checkStock(listStock(serialNumber, location), quantity);
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
        return isSerialized() ? "S/N" : "B/N";
    }

    /**
     * Is this a "blocked" part number? (P/N that should not be used any more due to safety reasons).
     *
     * @return True or false.
     */
    public boolean isBlocked() {
        return false;
    }

    /**
     * Is this an obsolete part number?
     *
     * @return True or false.
     */
    public boolean isObsolete() {
        return isBlocked();
    }

    /**
     * Can this type of item be stored at the given location?
     * <p>Note: This is the method to be used to check if this type of items can be binned at a specific location or not.
     * The other method {@link #canStore(InventoryLocation)} may be overridden by inherited classes and may
     * skip fundamental checks.</p>
     *
     * @param location Location.
     * @return True or false.
     */
    public final boolean canBin(InventoryLocation location) {
        return canStore(location);
    }

    /**
     * Can this type of item be stored at the given location?
     *
     * @param location Location.
     * @return True or false.
     */
    protected boolean canStore(InventoryLocation location) {
        return true;
    }

    /**
     * List of all (includes full-tree) assembly definitions under this item type.
     *
     * @return Iterator containing assembly definitions under this item type.
     */
    public final ObjectIterator<InventoryAssembly> listAssemblies() {
        return listAssemblies(null);
    }

    /**
     * List of all (includes full-tree) assembly definitions under this item type.
     *
     * @param filter Filter to be applied.
     * @return Iterator containing assembly definitions under this item type.
     */
    public final ObjectIterator<InventoryAssembly> listAssemblies(Predicate<InventoryAssembly> filter) {
        return listTree(listImmediateAssemblies(filter), InventoryAssembly::listImmediateAssemblies, filter);
    }

    /**
     * List of immediate assembly definitions under this item type.
     *
     * @return Iterator containing assembly definitions under this item type.
     */
    public final ObjectIterator<InventoryAssembly> listImmediateAssemblies() {
        return list(getTransaction(), InventoryAssembly.class, "ParentItemType=" + getId(),
                "ParentItemType,DisplayOrder").
                filter(ia -> {
                    ia.level = 1;
                    return true;
                } );
    }

    /**
     * List of immediate assembly definitions under this item type.
     *
     * @param filter Filter to be applied.
     * @return Iterator containing assembly definitions under this item type.
     */
    public final ObjectIterator<InventoryAssembly> listImmediateAssemblies(Predicate<InventoryAssembly> filter) {
        ObjectIterator<InventoryAssembly> i = listImmediateAssemblies();
        if(filter != null) {
            i = i.filter(filter);
        }
        return i;
    }

    /**
     * Get the list of APNs (alternate part numbers) for this part number.
     *
     * @return List containing APNs. (Will not contain blocked part numbers and the list is unmodifiable).
     */
    public final List<InventoryItemType> listAPNs() {
        return listAPNs(getId());
    }

    /**
     * Get the list of APNs (alternate part numbers) for the given part number {@link Id}.
     *
     * @param partNumberId {@link Id} for which APNs should be obtained.
     * @return List containing APNs. (Will not contain blocked part numbers and the list is unmodifiable).
     */
    public static List<InventoryItemType> listAPNs(Id partNumberId) {
        if(Id.isNull(partNumberId)) {
            return new ArrayList<>();
        }
        synchronized(apns) {
            List<InventoryItemType> list = apns.get(partNumberId);
            if(list == null) {
                list = partNumberId.listLinks(InventoryAPN.class).map(InventoryAPN::getPartNumber)
                        .filter(pn -> pn != null && !pn.isBlocked() && !partNumberId.equals(pn.getId())).toList();
                if(list.isEmpty()) {
                    list = Collections.emptyList();
                }
                list = Collections.unmodifiableList(list);
                apns.put(partNumberId, list);
            }
            return list;
        }
    }

    public void validateUoMCorrection(MeasurementUnit from, MeasurementUnit to) throws Exception {
        MeasurementUnit uom = unitOfMeasurement.getUnit();
        String m = " compatible with the unit '" + uom + "' (default unit of measurement for this item)";
        if(from.equals(unitOfMeasurement.getUnit())) {
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
        validateUoMCorrection(from, to);
        DBTransaction t = tm.createTransaction();
        try {
            correctUoM(t, from, to);
            for(InventoryItemType alt: listAPNs()) {
                alt.correctUoM(t, from, to);
            }
            t.commit();
            t = null;
        } catch(Throwable any) {
            t.rollback();
            t = null;
            throw any;
        } finally {
            if(t != null) {
                t.rollback();
            }
        }
    }

    private void correctUoM(DBTransaction t, MeasurementUnit from, MeasurementUnit to) throws Exception {
        InventoryUnitCorrection iuc = new InventoryUnitCorrection();
        iuc.internal = true;
        iuc.setItem(this);
        iuc.setPreviousUnit(Quantity.create(from));
        iuc.setCorrectedUnit(Quantity.create(to));
        iuc.save(t);
        boolean cd = !to.hasDecimals();
        RawSQL q = t.getSQL();
        String fromU = from.packingUnit == null ? from.getUnit() : ("#" + from.packingUnit),
                toU = to.packingUnit == null ? to.getUnit() : ("#" + to.packingUnit);
        updateUoM(q, "InventoryItem", fromU, toU, null, cd);
        updateUoM(q, "MaterialIssuedItem", fromU, toU, "Item", cd);
        updateUoM(q, "MaterialRequestItem", "Requested", fromU, toU, null, cd);
        updateUoM(q, "MaterialRequestItem", "Issued", fromU, toU, null, cd);
        updateUoM(q, "InventoryGRNItem", fromU, toU, null, cd);
        updateUoM(q, "InventoryStock", fromU, toU, "ItemType", cd);
        updateUoM(q, "InventoryLedger", fromU, toU, "ItemType", cd);
        updateUoM(q, "InventoryTransferItem", fromU, toU, "Item", cd);
        updateUoM(q, "InventoryPOItem", fromU, toU, null, cd);
        updateUoM(q, "InventoryPOItem", "Received", fromU, toU, null, cd);
    }

    private void updateUoM(RawSQL q, String table, String from, String to, String pn, boolean checkDecimals)
            throws Exception {
        updateUoM(q, table, "Quantity", from, to, pn, checkDecimals);
    }

    private void updateUoM(RawSQL q, String table, String quantity, String from, String to, String pn,
                           boolean checkDecimals) throws Exception {
        if(pn == null) {
            pn = "PartNumber";
        }
        String c;
        if(pn.equals("Item")) {
            c = " IN (SELECT Id FROM core.InventoryItem WHERE PartNumber=" + getId() + ")";
        } else {
            c = "=" + getId();
        }
        if(checkDecimals) {
            q.execute("SELECT (" + quantity + ").quantity, Id FROM core." + table + " WHERE (" + quantity + ").unit='"
                    + from + "' AND " + pn + c);
            if(!q.eoq()) {
                BigDecimal bd;
                ResultSet rs = q.getResult();
                while(!q.eoq()) {
                    bd = rs.getBigDecimal(1);
                    if(bd.scale() > 0 && bd.stripTrailingZeros().scale() > 0) {
                        String m = "Contains decimals - Id = " + rs.getBigDecimal(2).toBigInteger()
                                + ", Quantity = " + bd.toPlainString() + " in (" + table  + ")";
                        q.cancel();
                        throw new SOException(m);
                    }
                    q.skip();
                }
            }
        }
        q.executeUpdate("UPDATE core." + table + " SET " + quantity + "=((" + quantity + ").quantity,'" + to
                + "') " + "WHERE (" + quantity + ").unit='" + from + "' AND " + pn + c);
    }

    public void changeUnitOfMeasurement(TransactionManager tm, Quantity uom, Quantity uoi) throws Exception {
        if(!uom.isCompatible(uoi)) {
            throw new SOException("Incompatible units: " + uom.getUnit().getUnit() + ", " + uoi.getUnit().getUnit());
        }
        if(!uom.isCompatible(unitOfMeasurement)) {
            InventoryItem item = list(InventoryItem.class, "PartNumber=" + getId(), true).findFirst();
            if(item != null) {
                throw new Invalid_State("An item of this type already exists - " + item.toDisplay() + ", Quantity: "
                        + item.getQuantity());
            }
        }
        unitOfMeasurement = uom.zero();
        unitOfIssue = uoi.zero();
        MeasurementUnit mu = uom.getUnit();
        minimumStockLevel = Quantity.create(minimumStockLevel.getValue(), mu);
        reorderPoint = Quantity.create(reorderPoint.getValue(), mu);
        economicOrderQuantity = Quantity.create(economicOrderQuantity.getValue(), mu);
        tm.transact(this::save);
    }

    @Override
    public void migrate(TransactionManager tm, StoredObject migratedInstance) throws Exception {
        throw new Exception("Not allowed");
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
        doMigrate(tm, migratedType, itemConvertor, null);
    }

    void doMigrate(TransactionManager tm, InventoryItemType migratedType, Function<InventoryItem,
            InventoryItem> itemConvertor, ObjectIterator<InventoryItem> itemsToMigrate)
            throws Exception {
        ClassAttribute<?> ca = ClassAttribute.get(this);
        String delete = itemsToMigrate == null
                ? "DELETE FROM " + ca.moduleName + "." + ca.tableName + " WHERE Id=" + getId() : null;
        if(itemsToMigrate == null) {
            checkMigration(migratedType);
        }
        boolean wasSerialized = isSerialized();
        DBTransaction t = tm.createTransaction();
        if(itemsToMigrate == null) {
            doMigration(t, migratedType, false);
        }
        boolean serialized = migratedType.isSerialized();
        RawSQL sql = t.getSQL();
        try(ObjectIterator<InventoryItem> items = itemsToMigrate == null
                ? list(InventoryItem.class, "PartNumber=" + getId(), true) : itemsToMigrate) {
            InventoryItem ii;
            for(InventoryItem item : items) {
                item.getQuantity().canConvert(migratedType.getUnit());
                ii = itemConvertor.apply(item);
                if(!ii.getPartNumberId().equals(migratedType.getId())) {
                    throw new SOException("Incorrect type");
                }
                if(serialized && !wasSerialized) {
                    switch(ii.getSerialNumber()) {
                        case "", "N/A", "NA" -> ii.setSerialNumber("GENERATED-" + System.currentTimeMillis());
                    }
                    Quantity q = ii.getQuantity();
                    boolean ok = q.equals(Count.ONE);
                    if(!ok && q.equals(Count.ZERO)) {
                        InventoryLocation loc = ii.getLocation();
                        if (loc == null) {
                            throw new SOException("Invalid location for item - " + display(ii, tm));
                        }
                        ok = loc.infiniteSource() || loc.infiniteSink();
                    }
                    if(!ok) {
                        throw new SOException("Incorrect quantity for item - " + display(ii, tm));
                    }
                }
                item.checkMigration(ii);
                item.doMigration(t, ii, true);
                if(serialized && !wasSerialized) {
                    while(true) {
                        sql.execute("SELECT Count(*) FROM core.InventoryItem WHERE PartNumber="
                                + migratedType.getId() + " AND SerialNumber='" + ii.getSerialNumber() + "'");
                        if(sql.getResult().getInt(1) != 1) {
                            ii.setSerialNumber(ii.getSerialNumber() + "-DUPLICATE");
                            sql.executeUpdate("UPDATE core.InventoryItem SET SerialNumber='" + ii.getSerialNumber()
                                    + "' WHERE Id=" + ii.getId());
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        try {
            if(delete != null) {
                t.updateSQL(delete);
            }
            t.commit();
        } catch(Exception e) {
            t.rollback();
            throw e;
        } finally {
            sql.close();
        }
    }

    private String display(InventoryItem ii, TransactionManager tm) {
        SystemUser su = tm.getUser();
        String id = su.isAdmin() || su.isAppAdmin() ? (", System ID = " + ii.getId()) : "";
        return getPartNumberShortName() + " = " + getPartNumber() + ", " + getSerialNumberShortName()
                + " = " + ii.getSerialNumber() + ", Quantity = " + ii.getQuantity() + id;
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
            List<StoredObjectUtility.Link<?>> links = StoredObjectUtility.linkDetails(getClass());
            for(StoredObjectUtility.Link<?> link: links) {
                for(StoredObject child: link.list(this)) {
                    child.makeNew();
                    child.save(t);
                    a.addLink(t, child, link.getType());
                }
            }
            InventoryAPN ia = new InventoryAPN();
            ia.setPartNumber(a);
            ia.save(t);
            addLink(t, ia);
            t.commit();
            return get(getClass(), a.getId());
        } catch(Exception e) {
            if(t != null) {
                t.rollback();
            }
            throw e;
        }
    }

    /**
     * Get the tax category this type of item belongs to for the given tax region.
     *
     * @param taxRegion Tax region.
     * @return Tax category code.
     */
    public int getTaxCategory(TaxRegion taxRegion) {
        return 0;
    }
}