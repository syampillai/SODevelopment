package com.storedobject.core;

import com.storedobject.common.DateUtility;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Denotes an item in the inventory.
 *
 * @author Syam
 */
@SuppressWarnings({"resource", "UnusedReturnValue"})
public class InventoryItem extends StoredObject implements HasInventoryItem {

    static final WeakHashMap<Id, InventoryItemType> cachePN = new WeakHashMap<>();
    private static final Map<Id, Entity> owners = new HashMap<>();
    private Id partNumberId;
    private InventoryItemType partNumber;
    private String serialNumber = "";
    private Id storeId;
    private Id locationId;
    private InventoryLocation location;
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money cost = new Money();
    private boolean inTransit = false;
    private Id ownerId = Id.ZERO;
    private Date purchaseDate = DateUtility.today();
    private Id gRNId = Id.ZERO;
    private String batchTag;
    boolean illegal = true;

    public InventoryItem() {
    }

    public static void columns(Columns columns) {
        columns.add("PartNumber", "id");
        columns.add("SerialNumber", "text");
        columns.add("Store", "id");
        columns.add("Location", "id");
        columns.add("Quantity", "quantity");
        columns.add("Cost", "money");
        columns.add("InTransit", "boolean");
        columns.add("Owner", "id");
        columns.add("PurchaseDate", "date");
        columns.add("GRN", "id");
        columns.add("BatchTag", "text");
    }

    public static void indices(Indices indices) {
        indices.add("PartNumber, Location");
        indices.add("PartNumber, SerialNumber, Location");
        indices.add("Store, PartNumber, Location");
        indices.add("Location");
    }

    public static String[] protectedColumns() {
        return new String[] { "Store", "Quantity", "Cost", "InTransit", "Owner", "PurchaseDate", "GRN", "BatchTag" };
    }

    public static String[] searchColumns() {
        return new String[] {
                "PartNumber.PartNumber as Part Number",
                "SerialNumber as Serial/Batch",
                "PartNumber.Name as Name",
                "Quantity",
                "InTransit",
                "Store.Name as Store",
                "Location.Name as Bin/Location",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "PartNumber.PartNumber as Part Number",
                "SerialNumberDisplay as Serial/Batch",
                "PartNumber.Name as Name",
                "StatusDescription AS Status",
                "Quantity",
                "InTransit",
                "Location.Name as Bin/Location",
        };
    }

    public void setPartNumber(Id partNumberId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Part Number");
        }
        this.partNumberId = partNumberId;
    }

    public void setPartNumber(BigDecimal idValue) {
        setPartNumber(new Id(idValue));
    }

    public void setPartNumber(InventoryItemType partNumber) {
        setPartNumber(partNumber == null ? null : partNumber.getId());
    }

    @SetNotAllowed
    @Column(order = 100, style = "(any)")
    public Id getPartNumberId() {
        return partNumberId;
    }

    public InventoryItemType getPartNumber() {
        if(partNumber == null) {
            synchronized (cachePN) {
                partNumber = cachePN.get(partNumberId);
                if(partNumber == null) {
                    partNumber = get(getTransaction(), InventoryItemType.class, partNumberId, true);
                    cachePN.put(partNumberId, partNumber);
                }
            }
        }
        return partNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = toCode(serialNumber);
    }

    @Column(order = 200, required = false, style = "(code)")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Get the serial number for display purposes.
     *
     * @return By default, the {@link #serialNumber} value is returned with the appropriate name prefix.
     * But it can be overridden to display other details.
     */
    public String getSerialNumberDisplay() {
        if(serialNumber == null || serialNumber.isBlank()) {
            return "";
        }
        return getSerialNumberShortName() + ": " + serialNumber;
    }

    /**
     * Get the serial number for display purposes.
     *
     * @param includePN Include the part number in the display string.
     * @return By default, the {@link #serialNumber} value is returned with the appropriate name prefix.
     * But it can be overridden to display other details.
     */
    public String getSerialNumberDisplay(boolean includePN) {
        String s = getSerialNumberDisplay();
        if(includePN) {
            s = getPartNumberShortName() + ": " + getPartNumber().getPartNumber() + ", " + s;
        }
        return s;
    }

    public void setBatchTag(String batchTag) {
        this.batchTag = batchTag;
    }

    public String getBatchTag() {
        return batchTag;
    }

    public void setStore(Id storeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Store");
        }
        this.storeId = storeId;
    }

    public void setStore(BigDecimal idValue) {
        setStore(new Id(idValue));
    }

    public void setStore(InventoryStore store) {
        setStore(store == null ? null : store.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", required = false)
    public Id getStoreId() {
        return storeId;
    }

    public InventoryStore getStore() {
        return InventoryStore.getStore(storeId);
    }

    public void setLocation(Id locationId) {
        this.locationId = locationId;
        this.location = null;
    }

    public void setLocation(BigDecimal idValue) {
        setLocation(new Id(idValue));
    }

    public void setLocation(InventoryLocation location) {
        if(!loading() && illegal) {
            if(!(location != null && location.getType() == 17)) {
                throw new Set_Not_Allowed("Location");
            }
        }
        setLocation(location == null ? null : location.getId());
        if(location != null && locationId.equals(location.getId())) {
            this.location = location;
        }
    }

    void location(InventoryLocation location) {
        this.location = location;
        this.locationId = location.getId();
    }

    void quantity(Quantity quantity) {
        if(this.quantity.equals(quantity)) {
            return;
        }
        UnitCost uc = getUnitCost(false).getUnitCost(quantity.getUnit());
        this.quantity = quantity;
        this.cost = uc.getCost(quantity);
    }

    @SetNotAllowed
    @Column(order = 300, style = "(any)", caption = "Bin/Location")
    public Id getLocationId() {
        return locationId;
    }

    public InventoryLocation getLocation() {
        return getLocation(getTransaction());
    }

    private InventoryLocation getLocation(Transaction transaction) {
        if(location == null) {
            synchronized (InventoryVirtualLocation.cache) {
                location = InventoryVirtualLocation.cache.get(locationId);
            }
            if(location == null) {
                location = get(transaction, InventoryLocation.class, locationId, true);
            }
            if(location instanceof InventoryVirtualLocation vl) {
                synchronized (InventoryVirtualLocation.cache) {
                    InventoryVirtualLocation.cache.put(locationId, vl);
                }
            }
        }
        return location;
    }

    /**
     * Get the name of the current location for display purposes.
     *
     * @return By default, this method returns the {@link #toDisplay()} string of the location
     * returned by the {@link #getLocation()} method. But this could be overridden to provide other information.
     */
    public String getLocationDisplay() {
        return getLocation().toDisplay();
    }

    /**
     * Get the real location of this item. (If the item is on an assembly, then, its grandparent's location is
     * returned).
     *
     * @return Real location of this item.
     */
    public InventoryLocation getRealLocation() {
        InventoryItem grandParent = getGrandParentItem();
        return grandParent == null ? getLocation() : grandParent.getLocation();
    }

    /**
     * Is this item available at the given location?
     * @param location Location to check.
     *
     * @return True if the item is currently available at the same location or in the same store.
     */
    public boolean isAvailableAt(InventoryLocation location) {
        if(location.getId().equals(locationId)) {
            return true;
        }
        if(location instanceof InventoryBin) {
            return ((InventoryBin) location).getStoreId().equals(storeId);
        }
        return false;
    }

    public void setQuantity(Quantity quantity) {
        if(!loading() && illegal) {
            throw new Set_Not_Allowed("Quantity");
        }
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Override
    public InventoryItem getItem() {
        return this;
    }

    @Override
    @SetNotAllowed
    public Quantity getQuantity() {
        return quantity;
    }

    public void setCost(Money cost) {
        if(!loading() && illegal) {
            throw new Set_Not_Allowed("Cost");
        }
        if(cost.isZero() && cost.getCurrency() != Money.getDefaultCurrency()) {
            this.cost = new Money();
        } else {
            if(cost.isNegative()) {
                cost = cost.zero();
            }
            this.cost = cost;
        }
    }

    public void setCost(Object moneyValue) {
        setCost(Money.create(moneyValue));
    }

    @SetNotAllowed
    public Money getCost() {
        return cost;
    }

    public UnitCost getUnitCost(boolean guessIfZero) {
        if((guessIfZero && cost.isZero()) || quantity.isZero()) {
            return getPartNumber().getUnitCost(quantity.getUnit());
        }
        return new UnitCost(cost, quantity.absolute());
    }

    public UnitCost getUnitCost() {
        return getUnitCost(true);
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
     * @throws Exception if an error occurs while updating.
     */
    public boolean updateCost(TransactionManager tm, Money newCost, boolean updateAll) throws Exception {
        if(newCost == null || newCost.isNegative()) {
            throw new Invalid_Value("New Cost");
        }
        if(newCost.equals(cost)) {
            return false;
        }
        boolean general = !isSerialized();
        if(updateAll && general) {
            throw new Invalid_State("All can't be updated");
        }
        DBTransaction transaction = tm.createTransaction();
        try {
            if(updateAll) {
                try(ObjectIterator<InventoryItem> items =
                            list(InventoryItem.class, "PartNumber=" + partNumberId + " AND Owner=" + ownerId,
                                    true)) {
                    InventoryLocation loc;
                    for(InventoryItem item : items) {
                        loc = item.getLocation();
                        if(loc.infiniteSource() || loc.infiniteSink()) {
                            continue;
                        }
                        item.updateCost(transaction, newCost);
                    }
                }
            } else {
                updateCost(transaction, newCost);
            }
            transaction.commit();
        } catch(Exception e) {
            transaction.rollback();
            throw e;
        }
        return true;
    }

    private void updateCost(DBTransaction transaction, Money newCost) throws Exception {
        InventoryGRN grn = getGRN();
        Date from = grn == null ? InventoryTransaction.dataPickupDate : grn.date;
        cost = newCost;
        save(transaction);
        newCost = newCost.convert(transaction.getManager().getCurrency());
        String condition = "Item=" + getId() + " AND Date>='" + Database.format(from) + "'";
        List<InventoryLedger> entries = list(InventoryLedger.class, condition).toList();
        Money c;
        Quantity q;
        boolean serialized = isSerialized();
        for(InventoryLedger m : entries) {
            q = m.getQuantity().convert(quantity.getUnit());
            if(serialized || q.equals(quantity)) {
                c = newCost;
            } else {
                c = newCost.multiply(q).divide(quantity);
            }
            m.illegal = false;
            m.setCost(c);
            m.save(transaction);
        }
        transaction.updateSQL("DELETE FROM core.InventoryStock WHERE ItemType=" + getId() + " AND Date>='"
                + Database.format(from) + "'");
    }

    public void setInTransit(boolean inTransit) {
        this.inTransit = inTransit;
    }

    public boolean getInTransit() {
        return inTransit;
    }

    public void setOwner(Id ownerId) {
        if(!loading() && illegal) {
            throw new Set_Not_Allowed("Owner");
        }
        this.ownerId = ownerId;
    }

    public void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    public void setOwner(Entity owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    @SetNotAllowed
    public Id getOwnerId() {
        return ownerId;
    }

    public Entity getOwner() {
        Entity e = owners.get(ownerId);
        if(e == null) {
            e = get(Entity.class, ownerId);
            if(e != null) {
                owners.put(ownerId, e);
            }
        }
        return e;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = new Date(purchaseDate.getTime());
    }

    public Date getPurchaseDate() {
        return new Date(purchaseDate.getTime());
    }

    public void setGRN(Id grnId) {
        this.gRNId = grnId;
    }

    public void setGRN(BigDecimal idValue) {
        setGRN(new Id(idValue));
    }

    public void setGRN(InventoryGRN grn) {
        setGRN(grn == null ? null : grn.getId());
    }

    @Column(required = false)
    public final Id getGRNId() {
        return gRNId;
    }

    public final InventoryGRN getGRN() {
        return Id.isNull(gRNId) ? null : getRelated(InventoryGRN.class, gRNId);
    }

    @Override
    void loadedCore() {
        illegal = true;
        super.loadedCore();
    }

    @Override
    public void reloaded() {
        super.reloaded();
        partNumber = null;
    }

    /**
     * This will be invoked whenever this item is moved from one location to another. This is called from
     * within the transaction. So, {@link #getTransaction()} can be used tp get the current transaction.
     *
     * @param from Location from.
     * @param to Location to.
     * @throws Exception An exception may be raised if the move is not legal.
     */
    @SuppressWarnings("RedundantThrows")
    public void moved(InventoryLocation from, InventoryLocation to) throws Exception {
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(getPartNumber() == null || !(getClass().getName() + "Type").equals(partNumber.getClass().getName())) {
            throw new Invalid_Value("Part Number");
        }
        if(partNumber.isSerialized() && serialNumber.isEmpty()) {
            throw new Invalid_Value("Serial Number");
        }
        if(partNumber == null) {
            throw new Invalid_Value("Part Number");
        }
        if(getLocation() == null) {
            throw new Invalid_Value("Bin/Location");
        }
        if(location instanceof InventoryBin) {
            storeId = ((InventoryBin) location).getStoreId();
        } else {
            storeId = Id.ZERO;
        }
        if(!canBin(location)) { // Binning rejected
            boolean binError = true;
            if(location instanceof InventoryBin && isSerialized()) { // It's in a store
                InventoryItem p = get(getClass(), getId());
                if(p != null && p.getLocationId().equals(locationId)) { // Bin not changed, rejection maybe due to change in serviceability
                    binError = false; // Allowing
                }
            }
            if(binError) {
                throw new Invalid_State("Item: " + toDisplay() + ", Not allowed here: " + location.toDisplay());
            }
        }
        MeasurementUnit u = partNumber.getUnitOfMeasurement().getUnit();
        if(u.obsolete) {
            throw new Invalid_State("Unit of Measurement is obsolete: " + u);
        }
        if(quantity.getUnit().getType() != u.getType()) {
            if(quantity.isZero()) {
                quantity = Quantity.create(u);
            } else {
                throw new Invalid_State("Unit of Quantity is not compatible with '" + u.getUnit() + "'" + " (Item: " +
                        toDisplay() + ")");
            }
        }
        if(partNumber.isSerialized()) {
            if(!quantity.equals(Count.ONE)) {
                throw new Invalid_State("Serialized items should have Quantity = 1 NO" + " (Item: " +
                        toDisplay() + ", Quantity = " + quantity + ")");
            }
            ObjectIterator<InventoryItem> iis;
            iis = list(InventoryItem.class, "PartNumber=" + partNumberId + " AND SerialNumber='" +
                    serialNumber + "'", true);
            for(InventoryItem ii: iis) {
                if(ii.getId().equals(getId())) {
                    continue;
                }
                iis.close();
                throw new Invalid_State("Duplicate inventory entry, Item: " + toDisplay());
            }
        }
        if(cost.isNegative()) {
            throw new Invalid_Value("Cost can not be negative: " + cost + " (Item: " + toDisplay() + ")");
        }
        if(quantity.isNegative()) {
            throw new Invalid_Value("Quantity can not be negative: " + quantity + " (Item: " + toDisplay() + ")");
        }
        if(quantity.isZero() && !cost.isZero()) {
            throw new Invalid_Value("Cost can not be more than zero when quantity is zero: " + cost + " (Item: " +
                    toDisplay() + ")");
        }
        if(location instanceof InventoryFitmentPosition) {
            if(isSerialized()) {
                InventoryItem fittedItem = ((InventoryFitmentPosition) location).getFittedItem(getTransaction());
                if(fittedItem != null && !fittedItem.getId().equals(getId())) {
                    throw new Invalid_State("Another item is fitted at that location - This item: " + toDisplay() +
                            ", Location: " + location.toDisplay());
                }
            }
            InventoryAssembly ia = ((InventoryFitmentPosition) location).getAssembly();
            if(quantity.isGreaterThan(ia.getQuantity())) {
                throw new Invalid_State("Invalid quantity (" + quantity + ") for assembly: " + ia);
            }
        }
        switch (location.getType()) {
            // Store
            case 0 -> {
                if (Id.isNull(ownerId)) { // Set to us if not yet done
                    ownerId = tm.getEntity().getEntityId();
                }
            }
            // Repair organization
            // Maintenance unit
            // Rented out to
            // Service unit
            // Repair unit
            case 3, 5, 8, 10, 11 -> {
            }
            // Production unit
            // Scrap
            // Inventory shortage
            // Initial inventory
            // Consumption
            case 4, 6, 7, 12, 16 -> {
                // Must be ours
                if (Id.isNull(ownerId)) {
                    ownerId = tm.getEntity().getEntityId();
                } else if (!ownerId.equals(tm.getEntity().getEntityId())) {
                    invalidOwner();
                }
            }
            // Supplier
            // Consumer
            case 1, 2 -> {
                if (Id.isNull(ownerId)) {
                    ownerId = location.getEntityId();
                }
                if (Id.isNull(ownerId)) {
                    ownerId = tm.getEntity().getEntityId();
                } else if (!ownerId.equals(location.getEntityId())) {
                    if (location.getType() == 1) { // Could be a purchase return
                        if (ownerId.equals(tm.getEntity().getEntityId())) { // Going from us
                            ownerId = location.getEntityId();
                            break;
                        }
                    }
                    invalidOwner();
                }
            }
            // Rented from
            // an External owner
            case 9, 17 -> {
                if (Id.isNull(ownerId)) {
                    ownerId = location.getEntityId();
                } else if (!location.getEntityId().equals(ownerId) && isSerialized()) {
                    invalidOwner();
                }
            }
        }
        ownerId = tm.checkType(this, ownerId, Entity.class, false);
        gRNId = tm.checkType(this, gRNId, InventoryGRN.class, true);
        super.validateData(tm);
    }

    private void invalidOwner() throws Invalid_State {
        Entity entity = getOwner();
        throw new Invalid_State("Invalid ownership specified for " + toDisplay() + ", Owner: " +
                (entity == null ? "None" : getOwner().toDisplay()));
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        InventoryItem old = hacked();
        if(updated()) {
            validateBin(old);
        }
        if(isSerialized() && getLocation().getType() == 15 && !serialNumber.endsWith("-THRASHED")) { // Thrashed
            serialNumber += "-THRASHED";
        }
    }

    private InventoryItem hacked() {
        if(!illegal) {
            return null;
        }
        String error = null;
        InventoryLocation location = getLocation(getTransaction());
        InventoryItem old = get(getClass(), getId());
        if(old == null) {
            if(!location.infiniteSource()) {
                error = "Location: " + location.toDisplay();
            }
        } else {
            if(!locationId.equals(old.locationId)) {
                if(old.getLocation().getType() == 15) { // From recycled
                    old.illegal = false;
                    old.setLocation(locationId);
                    old.quantity = quantity;
                }
            }
            if(!quantity.equals(old.quantity)) {
                error = "Quantity changed: [" + old.quantity + "] to [" + quantity + "]";
            } else if(!locationId.equals(old.locationId)) {
                InventoryLocation oldLocation = old.getLocation();
                if(!oldLocation.getEntityId().equals(location.getEntityId())) {
                    error = "Location changed: [" + oldLocation.toDisplay() + "] to [" + location.toDisplay() + "]";
                }
            }
        }
        if(error != null) {
            throw new Design_Error(null, this.toDisplay() + " (" + error + ")");
        }
        return old;
    }

    private void validateBin(InventoryItem old) throws Exception {
        if(old == null) {
            old = get(getClass(), getId());
        }
        boolean notAllowed = true;
        if(getLocation() instanceof InventoryBin && isSerialized() && !canBin(location)) { // Binning rejected
            if(old.getLocationId().equals(locationId)) { // Bin not changed, rejection maybe due to change in serviceability
                InventoryLocation from = location;
                location = InventoryStoreBin.getForStore(((InventoryBin) location).getStoreId());
                //noinspection ConstantConditions
                locationId = location.getId();
                new InventoryLedger(this, from, getTransaction());
                notAllowed = false; // Allowing
            }
            if(notAllowed) {
                throw new Invalid_State("Item: " + toDisplay() + ", Not allowed here: " + location.toDisplay());
            }
        }
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        if(!inTransit && !isSerialized() && !quantity.isZero()) {
            Transaction t = getTransaction();
            getLocation(t);
            List<InventoryItem> otherItems = listStock(getPartNumber(), location.infiniteSink() ? "" : serialNumber,
                    location, gRNId, batchTag)
                    .filter(ii -> !ii.getId().equals(getId()) && !ii.quantity.isZero() && ii.ownerId.equals(ownerId))
                    .filter(ii -> !t.isInvolved(ii))
                    .toList();
            if(!otherItems.isEmpty()) {
                hacked();
                illegal = false;
            }
            for(InventoryItem item: otherItems) {
                quantity = quantity.add(item.quantity);
                cost = cost.add(item.cost);
                if(cost.isNegative()) {
                    cost = cost.zero();
                }
                item.illegal = false;
                item.quantity = item.quantity.zero();
                item.cost = item.cost.zero();
                item.save(getTransaction());
            }
        }
    }

    @Override
    public void validateDelete() throws Exception {
        if(!quantity.isZero() && !getLocation().infiniteSource()) {
            throw new Invalid_State("Can not delete item with non-zero quantity - " + this + ", Quantity: " + quantity);
        }
        super.validateDelete();
    }

    public static InventoryItem get(String serialNumber, String partNumber) {
        return get(serialNumber, InventoryItemType.get(partNumber));
    }

    @SuppressWarnings("unchecked")
    public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
        if(partNumber == null) {
            return null;
        }
        return get((Class<T>) partNumber.createItem().getClass(), serialNumber, partNumber, true);
    }

    public static InventoryItem getByPartNumberId(String serialNumber, Id partNumber) {
        return partNumber == null ? null : get(serialNumber, (InventoryItemType)get(partNumber));
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber,
                                                              String partNumber) {
        return get(itemClass, serialNumber, InventoryItemType.get(partNumber));
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber,
                                                  InventoryItemType partNumber) {
        return get(itemClass, serialNumber, partNumber, false);
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber,
                                                              String partNumber, boolean any) {
        return get(itemClass, serialNumber, InventoryItemType.get(partNumber), any);
    }

    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber,
                                                  boolean any) {
        if(partNumber == null) {
            return null;
        }
        String s = "PartNumber=" + partNumber.getId() + " AND SerialNumber";
        serialNumber =  toCode(serialNumber);
        T i = get(itemClass, s + "='" + serialNumber + "'", any);
        if(i != null) {
            return i;
        }
        return list(itemClass, s + " LIKE '"+ serialNumber + "%'", any).single(false);
    }

    public static <T extends InventoryItem> T getByPartNumberId(Class<T> itemClass, String serialNumber,
                                                                Id partNumber) {
        return partNumber == null ? null : get(itemClass, serialNumber, (InventoryItemType)get(partNumber));
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber,
                                                                   InventoryItemType partNumber) {
        return list(itemClass, serialNumber, partNumber, true);
    }

    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber,
                                                                   InventoryItemType partNumber, boolean any) {
        if(partNumber == null) {
            return ObjectIterator.create();
        }
        return list(itemClass, "PartNumber=" + partNumber.getId() + " AND SerialNumber LIKE '" +
                toCode(serialNumber) + "%'", any);
    }

    /**
     * Can this item be stored at the given location?
     * <p>Note: This is the method to be used to check if the item can be binned at a specific location or not.
     * The other method {@link #canStore(InventoryLocation)} may be overridden by inherited classes and may
     * skip fundamental checks.</p>
     *
     * @param location Location.
     * @return True or false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean canBin(InventoryLocation location) {
        return location instanceof InventoryStoreBin || location.canBin(this);
    }

    /**
     * Can this item be stored at the given location?
     *
     * @param location Location.
     * @return True or false.
     */
    protected boolean canStore(InventoryLocation location) {
        return true;
    }

    /**
     * Whether this item is serviceable or not. If the item is not serviceable, it will not be selected for
     * issuing against any requests. It can still be transferred to other locations.
     *
     * @return Default is true. However, a subclass may return false depending on other attribute values.
     */
    public boolean isServiceable() {
        return true;
    }

    /**
     * Is this a "blocked" serial? One or more serial numbers (or a sequence of serial numbers) may be blocked
     * and should not be used any more due to safety reasons. The default implementation of this method checks
     * whether the P/N is blocked or not. If this method is overridden, it must call the super so that the default
     * implementation is still applied.
     *
     * @return True or false.
     */
    public boolean isBlocked() {
        return getPartNumber().isBlocked();
    }

    /**
     * Is this an obsolete part number? If marked as obsolete, it will not be ordered again via purchase orders.
     * The default implementation of this method checks
     * whether the P/N is obsolete or not. If this method is overridden, it must call the super so that the default
     * implementation is still applied.
     *
     * @return True or false.
     */
    public boolean isObsolete() {
        return getPartNumber().isObsolete();
    }

    /**
     * <p>Is this a serialized item?</p>
     * <p>A serialized item has a unique serial number (mostly assigned by the manufacturer itself). The item is
     * always tracked by the serial number in the system.</p>
     *
     * @return True or false.
     */
    public final boolean isSerialized() {
        return getPartNumber().isSerialized();
    }

    /**
     * <p>Is this an expendable item?</p>
     * <p>Items (such as nut, bolt, rivet, etc.) for which (1) no authorized repair procedure exists, and/or
     * (2) the cost of repair would exceed the cost of its replacement. Expendable items are usually considered to be
     * consumed when issued and are not recorded as returnable inventory.</p>
     *
     * @return True or false.
     */
    public final boolean isExpendable() {
        return getPartNumber().isExpendable();
    }

    /**
     * <p>Is this a consumable item?</p>
     * <p>A consumable item (or a consumable) is an item that is once used, cannot be recovered. Once issued from
     * stores, consumables get incorporated into other items and lose their identity. An example of a consumable
     * is paint.</p>
     *
     * @return True or false.
     */
    public final boolean isConsumable() {
        return getPartNumber().isConsumable();
    }

    /**
     * <p>Is this item a tool?</p>
     * <p>A tool is always tracked when issued to a location other than another store.</p>
     *
     * @return True or false.
     */
    public final boolean isTool() {
        return getPartNumber().isTool();
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
     * is a serialized item and its {@link #isRepairable()} returns <code>true</code>.</p>
     *
     * @return True or false.
     */
    public final boolean isRepairAllowed() {
        return isSerialized() && isRepairable();
    }

    public final Class<? extends InventoryItemType> getPartNumberType() {
        return getItemType();
    }

    public final Class<? extends InventoryItemType> getItemType() {
        try {
            //noinspection unchecked
            return (Class<? extends InventoryItemType>) JavaClassLoader.getLogic(getClass().getName() + "Type");
        } catch(Throwable ignored) {
        }
        throw new SORuntimeException("Design error");
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

    /**
     * Check whether the measurement unit of the given quantity compatible for this item or not.
     *
     * @param quantity Quantity to check.
     * @param name Name of the quantity (used to generate a message of the exception).
     * @throws Invalid_State Throws if the measurement unit is not compatible.
     */
    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
        getPartNumber().checkUnit(quantity, name);
    }

    /**
     * Check whether the measurement unit of the given quantity compatible for this item or not.
     *
     * @param quantity Quantity to check.
     * @throws Invalid_State Throws if the measurement unit is not compatible.
     */
    public void checkUnit(Quantity quantity) throws Invalid_State {
        getPartNumber().checkUnit(quantity, null);
    }

    @Override
    public String toString() {
        return toDisp() + ", Quantity: " + quantity;
    }

    @Override
    public String toDisplay() {
        return isSerialized() ? toDisp() : toString();
    }

    private String toDisp() {
        return getPartNumber() + (serialNumber.isEmpty() ? "" :
                (", " + getSerialNumberShortName() + " " + serialNumber));
    }

    /**
     * List stock for a given part number and store.
     *
     * @param partNumber Part number.
     * @return List of items that are in the stock.
     */
    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber) {
        return listStock(partNumber, null, (InventoryStore) null);
    }

    /**
     * List stock for a given part number and store.
     *
     * @param partNumber Part number.
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryStore store) {
        return listStock(partNumber, null, store);
    }

    /**
     * List stock for a given part number and store.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber,
                                                          InventoryStore store) {
        return listStock(InventoryItemType.get(partNumber), serialNumber, store);
    }

    /**
     * List stock for a given part number and store.
     *
     * @param partNumber Part number.
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                        InventoryStore store) {
        return listStock(partNumber, null, store);
    }

    /**
     * List stock for a given part number and store.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @param store Store.
     * @return List of items that are in the stock.
     */
    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                        String serialNumber,
                                                                                        InventoryStore store) {
        return listStock(partNumber, serialNumber, store == null ? null : store.getId(), false,
                false);
    }

    /**
     * List stock for a given part number and location.
     *
     * @param partNumber Part number.
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(String partNumber, InventoryLocation location) {
        return listStock(partNumber, null, location);
    }

    /**
     * List stock for a given part number and location.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber,
                                                          InventoryLocation location) {
        return listStock(InventoryItemType.get(partNumber), serialNumber, location);
    }

    /**
     * List stock for a given part number and location.
     *
     * @param partNumber Part number.
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                        InventoryLocation location) {
        return listStock(partNumber, null, location);
    }

    /**
     * List stock for a given part number and location.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @param location Location.
     * @return List of items that are in the stock.
     */
    public static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                        String serialNumber,
                                                                                        InventoryLocation location) {
        return listStock(partNumber, serialNumber, location.getId(), true, false);
    }

    static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                 String serialNumber,
                                                                                 InventoryLocation location,
                                                                                 Id grnId,
                                                                                 String batchTag) {
        return listStock(partNumber, serialNumber, location.getId(), true, false,
                "GRN=" + grnId + " AND NOT InTransit AND BatchTag='" + batchTag + "'");
    }

    private static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                         String serialNumber, Id id,
                                                                                         boolean location,
                                                                                         boolean everyWhere) {
        return listStock(partNumber, serialNumber, id, location, everyWhere, "(Quantity).Quantity>0");
    }

    private static <T extends InventoryItemType> ObjectIterator<InventoryItem> listStock(T partNumber,
                                                                                         String serialNumber, Id id,
                                                                                         boolean location,
                                                                                         boolean everyWhere,
                                                                                         String extraCondition) {
        StringBuilder condition = new StringBuilder("PartNumber=");
        condition.append(partNumber.getId());
        if(serialNumber != null) {
            serialNumber = toCode(serialNumber);
            condition.append(" AND SerialNumber='").append(serialNumber).append('\'');
        }
        if(!everyWhere) {
            condition.append(" AND ");
            if(Id.isNull(id)) {
                condition.append("Store>0");
            } else {
                condition.append(location ? "Location" : "Store").append('=').append(id);
            }
        }
        condition.append(" AND ").append(extraCondition);
        return list(InventoryItem.class, condition.toString(), "GRN,TranId", true);
    }

    /**
     * List stock for a given part number.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(String partNumber, String serialNumber) {
        return listStock(InventoryItemType.get(partNumber), serialNumber);
    }

    /**
     * List stock for a given part number.
     *
     * @param partNumber Part number.
     * @param serialNumber Serial number.
     * @return List of items that are in the stock.
     */
    public static ObjectIterator<InventoryItem> listStock(InventoryItemType partNumber, String serialNumber) {
        if(partNumber == null) {
            return ObjectIterator.create();
        }
        return listStock(partNumber, serialNumber, null, false, true);
    }

    /**
     * List all the items for the given part number. It will return even the items fitted on assemblies,
     * items sent for repair, etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param partNumber Part number for which the list needs to be obtained.
     * @return List of items.
     */
    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber) {
        return listItems(partNumber, null, false);
    }

    /**
     * List all the items for the given part number. It will return even the items fitted on assemblies,
     * items sent for repair, etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param partNumber Part number for which the list needs to be obtained.
     * @param condition Additional condition if any. Could be null.
     * @return List of items.
     */
    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, String condition) {
        return listItems(partNumber, condition, false);
    }

    /**
     * List all the items for the given part number. It will return even the items fitted on assemblies,
     * items sent for repair, etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param partNumber Part number for which the list needs to be obtained.
     * @param includeZeros Whether to include zero quantity items (in the case of non-serialized items) or not.
     * @return List of items.
     */
    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, boolean includeZeros) {
        return listItems(partNumber, null, includeZeros);
    }

    /**
     * List all the items for the given part number. It will return even the items fitted on assemblies,
     * items sent for repair, etc. However, items that are already scrapped/consumed will not be included.
     *
     * @param partNumber Part number for which the list needs to be obtained.
     * @param condition Additional condition if any. Could be null.
     * @param includeZeros Whether to include zero quantity items (in the case of non-serialized items) or not.
     * @return List of items.
     */
    public static ObjectIterator<InventoryItem> listItems(InventoryItemType partNumber, String condition,
                                                          boolean includeZeros) {
        if(partNumber == null) {
            return ObjectIterator.create();
        }
        String c = "PartNumber=" + partNumber.getId();
        if(!includeZeros) {
            c += " AND (Quantity).Quantity>0";
        }
        if(condition != null && !condition.isEmpty()) {
            c += " AND (" + condition + ")";
        }
        ObjectIterator<InventoryItem> list = list(InventoryItem.class, c, "GRN,TranId", true);
        return list.filter(item -> switch(item.getRealLocation().getType()) {
            case 0, 3, 4, 5, 8, 10, 11, 13, 14, 18 -> true;
            default -> false;
        });
    }

    /**
     * Get the grandparent item on which this item is fitted on.
     *
     * @return Grandparent item if exists.
     */
    public InventoryItem getGrandParentItem() {
        Transaction transaction = getTransaction();
        InventoryItem parent = getParentItem(transaction);
        if(parent == null) {
            return null;
        }
        InventoryItem grandParent = parent.getParentItem(transaction);
        while(grandParent != null) {
            parent = grandParent;
            grandParent = parent.getParentItem(transaction);
        }
        return parent;
    }

    /**
     * Get the parent item on which this item is fitted on.
     *
     * @return Parent item if exists.
     */
    public InventoryItem getParentItem() {
        return getParentItem(getTransaction());
    }

    private InventoryItem getParentItem(Transaction transaction) {
        if(!(getLocation(transaction) instanceof InventoryFitmentPosition)) {
            return null;
        }
        return ((InventoryFitmentPosition)location).getItem(transaction);
    }

    /**
     * Get the parent/grandparents item on which this item is fitted on.
     *
     * @param itemClass Type of parent/grandparent to look for.
     * @param <I> Type of parent class.
     * @return Parent item if exists.
     */
    public <I extends InventoryItem> I getParentItem(Class<I> itemClass) {
        return getParentItem(itemClass, getTransaction());
    }

    private <I extends InventoryItem> I getParentItem(Class<I> itemClass, Transaction transaction) {
        InventoryItem parent = getParentItem(transaction);
        if(parent == null) {
            return null;
        }
        //noinspection unchecked
        return parent.getClass() == itemClass ? (I)parent : parent.getParentItem(itemClass, transaction);
    }

    public final InventoryLocation getPreviousLocation() {
        InventoryLedger move = get(InventoryLedger.class, "Item=" + getId() + " AND LocationTo=" + locationId,
                "Date DESC,TranId DESC");
        return move == null ? null : move.getLocationFrom();
    }

    public final InventoryLocation getPreviousLocation(int stepsBackward) {
        if(stepsBackward < 0) {
            return null;
        }
        if(stepsBackward == 0) {
            return getPreviousLocation();
        }
        InventoryLedger move = list(InventoryLedger.class, "Item=" + getId(), "Date DESC,TranId DESC").
                skip(stepsBackward).findFirst();
        return move == null ? null : move.getLocationFrom();
    }

    public final String getPartNumberName() {
        return getPartNumber().getPartNumberName();
    }

    public final String getPartNumberShortName() {
        return getPartNumber().getPartNumberShortName();
    }

    public final String getSerialNumberName() {
        return getParentItem().getSerialNumberName();
    }

    public final String getSerialNumberShortName() {
        return getPartNumber().getSerialNumberShortName();
    }

    /**
     * List of all fitment positions (includes the full tree) under this item.
     *
     * @return Iterator containing all fitment positions.
     */
    public final ObjectIterator<InventoryFitmentPosition> listFitmentPositions() {
        return listTree(listImmediateFitmentPositions(), InventoryItem::listFitmentPositions,null);
    }

    private static ObjectIterator<InventoryFitmentPosition> listFitmentPositions(InventoryFitmentPosition p) {
        InventoryItem item = p.getFittedItem();
        return item == null ? null : item.listImmediateFitmentPositions();
    }

    /**
     * List the fitment positions under this item.
     *
     * @return Iterator containing all fitment positions (not the children).
     */
    public final ObjectIterator<InventoryFitmentPosition> listImmediateFitmentPositions() {
        Transaction t = getTransaction();
        return getPartNumber().listImmediateAssemblies().map(a -> InventoryFitmentPosition.get(t, this, a));
    }

    /**
     * List of assembly items under this item.
     *
     * @param itemClass Type of assemblies to be retrieved.
     * @param <O> Type of the item class.
     * @return Iterator containing assembly items under this item.
     */
    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass) {
        return listAssemblies(itemClass, null);
    }

    /**
     * List of assembly items under this item.
     *
     * @param itemClass Type of assemblies to be retrieved.
     * @param filter Filter to be applied.
     * @param <O> Type of the item class.
     * @return Iterator containing assembly items under this item.
     */
    public final <O extends InventoryItem> ObjectIterator<O> listAssemblies(Class<O> itemClass, Predicate<O> filter) {
        Transaction t = getTransaction();
        return listTree(i -> ((InventoryItem)i).assemblies(t, itemClass, filter), null);
    }

    /**
     * List of assembly items under this item.
     *
     * @return Iterator containing assembly items under this item.
     */
    public final ObjectIterator<InventoryItem> listAssemblies() {
        return listAssemblies((Predicate<InventoryItem>)null);
    }

    /**
     * List of assembly items under this item.
     *
     * @param filter Filter to be applied.
     * @return Iterator containing assembly items under this item.
     */
    public final ObjectIterator<InventoryItem> listAssemblies(Predicate<InventoryItem> filter) {
        Transaction t = getTransaction();
        return listTree(i -> i.assemblies(t, filter), null);
    }

    private <O extends InventoryItem> ObjectIterator<O> assemblies(Transaction t, Class<O> childClass,
                                                                   Predicate<O> childFilter) {
        ObjectIterator<InventoryItem> items = assemblies(t, null);
        @SuppressWarnings("unchecked") ObjectIterator<O> a = items.
                filter(i -> childClass.isAssignableFrom(i.getClass())).
                map(i -> (O)i);
        return childFilter == null ? a : a.filter(childFilter);
    }

    private ObjectIterator<InventoryItem> assemblies(Transaction t, Predicate<InventoryItem> filter) {
        ObjectIterator<InventoryItem> items =
                list(t, InventoryFitmentPosition.class, "Item=" + getId(), "Assembly.DisplayOrder").
                        map(loc -> loc.getFittedItem(t)).
                        filter(Objects::nonNull);
        return filter == null ? items : items.filter(filter);
    }

    /**
     * Check for vacant fitment positions under this item. (Only mandatory assemblies are counted and
     * accessories are not considered).
     *
     * @return True/false.
     */
    public boolean isAssemblyIncomplete() {
        return mandatoryAssemblyFilled() < mandatoryAssemblyCount(getPartNumber());
    }

    private long mandatoryAssemblyFilled() {
        Transaction t = getTransaction();
        return listFitmentPositions().filter(p -> p.getFittedItem(t) != null).
                map(InventoryFitmentPosition::getAssembly).
                filter(a -> !a.getOptional() && !a.getAccessory() && a.getItemType().isSerialized()).count();
    }

    private static long mandatoryAssemblyCount(InventoryItemType pn) {
        return pn.listAssemblies().filter(a -> !a.getOptional() && !a.getAccessory() && a.getItemType().isSerialized()).
                count();
    }

    /**
     * List the assembly positions where items are missing.
     *
     * @return Assembly positions where items are missing.
     */
    public ObjectIterator<InventoryFitmentPosition> listMissingAssemblies() {
        return listFitmentPositions().filter(p -> p.getFittedItem() == null);
    }

    /**
     * Whether this item was data-picked or not.
     *
     * @return True/false.
     */
    public boolean wasDataPicked() {
        return wasDataPicked(0);
    }

    /**
     * Whether this item was data-picked or not.
     *
     * @param stepsBackward Number of steps backward to go for checking.
     * @return True/false.
     */
    public boolean wasDataPicked(int stepsBackward) {
        InventoryLocation location;
        int n = 0;
        while(stepsBackward >= 0) {
            location = getPreviousLocation(n);
            if(location == null) {
                return false;
            }
            if(location.getType() == 12) {
                return true;
            }
            --stepsBackward;
            ++n;
        }
        return false;
    }

    /**
     * Check if this item came as a part of a bigger assembly when it was purchased. This will return true only
     * if the item is still part of the assembly and not moved to anywhere else, including other fitment locations.
     *
     * @return True/false.
     */
    public boolean cameAsAssemblyPart() {
        InventoryLocation location = getLocation();
        if(location instanceof InventoryFitmentPosition) {
            location = getPreviousLocation();
            if(location == null) {
                return false;
            }
            switch(location.getType()) {
                case 0, 9 -> { // From supplier, rented from
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the assembly level of this item. If this item is fitted as part of an assembly, this method returns the level
     * at which that exists from the top level assembly. If it is not fitted on any assembly, this method returns 0.
     *
     * @return Assembly level.
     */
    public final int getAssemblyLevel() {
        InventoryLocation location = getLocation(getTransaction());
        if(location instanceof InventoryFitmentPosition loc) {
            loc.getAssembly().level = 1 + Objects.requireNonNull(getParentItem(getTransaction())).getAssemblyLevel();
            return loc.getAssembly().level;
        }
        return 0;
    }

    /**
     * Get that description of the status of this item (serviceability, storage condition, etc.)
     * @return Status description.
     */
    public String getStatusDescription() {
        return (isServiceable() ? "S" : "Not s") + "erviceable";
    }

    /**
     * Get the PO through which this item was procured. If this item was repaired by a repair organization later,
     * this information will not be available anymore, and you should see {@link #getRO()}.
     *
     * @return PO if available.
     */
    public final InventoryPO getPO() {
        InventoryGRN grn = getGRN();
        if(grn == null || grn.getType() > 2) {
            return null;
        }
        List<InventoryPO> pos = grn.listMasters(getTransaction(), InventoryPO.class, true).toList();
        if(pos.isEmpty()) {
            return null;
        }
        if(pos.size() == 1) {
            return pos.getFirst();
        }
        return pos.stream().filter(po -> po.existsLinks(getTransaction(), InventoryPOItem.class,
                        "PartNumber=" + partNumberId + " AND SerialNumber='" + serialNumber + "'",true))
                .findAny().orElse(null);
    }

    /**
     * Get the RO through which this item was repaired. If this item was repaired more than once,
     * the latest RO is returned.
     *
     * @return RO if available.
     */
    public final InventoryRO getRO() {
        InventoryGRN grn = getGRN();
        if(grn == null || grn.getType() != 3) {
            return null;
        }
        List<InventoryRO> ros = grn.listMasters(getTransaction(), InventoryRO.class, true).toList();
        if(ros.isEmpty()) {
            return null;
        }
        if(ros.size() == 1) {
            return ros.getFirst();
        }
        return ros.stream().filter(ro -> ro.existsLinks(getTransaction(), InventoryROItem.class,
                        "Item=" + getId(),true))
                .findAny().orElse(null);
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
     * @param grnType GRN Type - 0:Purchase, 1: External Owner, 2: Loaned from, 3: Items Repaired by, 4: Sales Return
     * @return GRN
     * @throws Exception if transaction errors occur.
     */
    public InventoryGRN createGRN(Transaction transaction, InventoryStore store, Date grnDate, int grnNumber,
                                  Date invoiceDate, String invoiceReference, Entity supplier, int grnType)
            throws Exception {
        Currency localCurrency = transaction.getManager().getCurrency();
        InventoryGRN grn = getGRN();
        if(grn != null) {
            if(grn.getType() == grnType) {
                if(!grn.getStoreId().equals(store.getId())) {
                    throw new SOException("GRN already exists - " + grn.toDisplay() + " (In store: "
                            + grn.getStore().toDisplay() + ")");
                }
            } else {
                grn = null;
            }
        }
        boolean changed = false;
        if(grn == null) {
            grn = new InventoryGRN();
            if(cost.getCurrency() == localCurrency) {
                grn.setExchangeRate(new Rate());
                grn.setCurrencyObject(localCurrency);
            } else {
                Rate rate = cost.getBuyingRate(localCurrency);
                grn.setExchangeRate(rate);
                grn.setCurrencyObject(cost.getCurrency());
                cost = cost.multiply(rate, localCurrency);
                changed = true;
            }
        } else if(!Currency.getInstance(grn.getCurrency()).equals(cost.getCurrency())) {
            throw new SOException("GRN currency does not match with the cost of the item");
        }
        grn.no = grnNumber;
        grn.date.setTime(grnDate.getTime());
        grn.setInvoiceNumber(invoiceReference);
        grn.invoiceDate.setTime(invoiceDate.getTime());
        grn.storeId = store.getId();
        grn.supplierId = supplier.getId();
        grn.status = 2;
        grn.type = grnType;
        grn.internal = true;
        grn.save(transaction);
        if(!this.gRNId.equals(grn.getId())) {
            this.gRNId = grn.getId();
            changed = true;
        }
        if(changed) {
            save(transaction);
        }
        return grn;
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
     * Attach the PO for this item. This method is used only for creating POs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param po PO to attach.
     * @throws Exception if transaction errors occur.
     */
    public void attachPO(Transaction transaction, InventoryPO po) throws Exception {
        InventoryGRN grn = getGRN();
        if(grn == null) {
            throw new SOException("No GRN found");
        }
        if(!po.created()) {
            throw new Invalid_State("PO is not newly created");
        }
        InventoryPO dup = grn.listMasters(getTransaction(), InventoryPO.class, true).findFirst();
        if(dup != null) {
            throw new SOException("PO already exists: " + dup.toDisplay());
        }
        po.storeId = grn.getStoreId();
        po.supplierId = grn.getSupplierId();
        po.status = 4;
        po.save(transaction);
        po.addLink(transaction, grn);
        if(grn.getType() != 0) {
            grn.type = 0;
            grn.save(transaction);
        }
    }

    /**
     * Attach the RO to this item. This method is used only for creating ROs for the items that are data-picked.
     *
     * @param transaction Transaction.
     * @param ro RO to attach.
     * @throws Exception if transaction errors occur.
     */
    public void attachRO(Transaction transaction, InventoryRO ro) throws Exception {
        InventoryGRN grn = getGRN();
        if(grn == null) {
            throw new SOException("No GRN found");
        }
        ro.setTransaction(transaction);
        InventoryVirtualLocation from = InventoryVirtualLocation.getForRepairOrganization(grn.getSupplierId());
        if(from == null) {
            from = new InventoryVirtualLocation();
            from.setType(3);
            from.setEntity(grn.getSupplierId());
            from.setName(grn.getSupplier().getName());
            from.save(transaction);
        }
        InventoryRO dup = grn.listMasters(getTransaction(), InventoryRO.class, true).findFirst();
        if(dup != null) {
            throw new SOException("RO already exists: " + dup.toDisplay());
        }
        ro.toLocationId = from.getId();
        ro.fromLocationId = grn.getStore().getStoreBin().getId();
        ro.status = 3;
        ro.save();
        ro.addLink(transaction, grn);
        if(grn.getType() != 3) {
            grn.type = 3;
            grn.save(transaction);
        }
    }

    public void changePartNumber(Transaction transaction, InventoryItemType newPartNumber) throws Exception {
        if(newPartNumber.getClass() != getPartNumber().getClass()) {
            throw new Invalid_State("Type mismatch");
        }
        this.partNumberId = newPartNumber.getId();
        this.partNumber = null;
        save(transaction);
    }

    @Override
    public void migrate(TransactionManager tm, StoredObject migratedInstance) throws Exception {
        throw new Exception("Not allowed");
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
        if(migratedType.created()) {
            throw new Invalid_State("Invalid type");
        }
        if(partNumberId.equals(migratedType.getId())) {
            throw new Invalid_State("Same type");
        }
        if(getPartNumber().getClass().equals(migratedType.getClass())) {
            partNumberId = migratedType.getId();
            tm.transact(this::save);
            return;
        }
        getPartNumber().doMigrate(tm, migratedType, itemConvertor, ObjectIterator.create(this));
    }

    /**
     * Resurrect an item so that the same P/N and S/N can be used again.
     *
     * @param cost Cost.
     * @param location New location.
     */
    public void resurrect(Money cost, InventoryLocation location) {
        if(!isSerialized() || !getLocation().canResurrect()) {
            throw new SORuntimeException("Can't be resurrected");
        }
        illegal = false;
        this.quantity = Count.ONE;
        this.cost = cost;
        setLocation(location);
        illegal = true;
    }
}
