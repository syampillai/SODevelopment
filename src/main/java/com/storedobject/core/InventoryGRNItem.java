package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.List;

/**
 * Items in a GRN. See {@link InventoryGRN}.
 *
 * @author Syam
 */
public final class InventoryGRNItem extends StoredObject implements Detail, HasInventoryItem {

    private Id partNumberId;
    private InventoryItemType partNumber;
    private String serialNumber;
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money unitCost = new Money(), tax;
    private Id binId = Id.ZERO;
    private InventoryBin bin;
    private Id itemId = Id.ZERO;
    private InventoryItem item;
    boolean inspected = false;
    boolean internal = false;

    public InventoryGRNItem() {
    }

    public static void columns(Columns columns) {
        columns.add("PartNumber", "id");
        columns.add("SerialNumber", "text");
        columns.add("Quantity", "quantity");
        columns.add("UnitCost", "money");
        columns.add("Bin", "id");
        columns.add("Item", "id");
        columns.add("Inspected", "boolean");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Item",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "PartNumber.Name AS Item", "PartNumber.PartNumber AS Part Number",
                "SerialNumberDisplay AS Serial/Batch Number", "Inspected", "Quantity", "UnitCost", "Bin"
        };
    }

    public void setPartNumber(Id partNumberId) {
        if(partNumber != null && (Id.isNull(partNumberId) || !partNumberId.equals(this.partNumberId))) {
            partNumber = null;
        }
        this.partNumberId = partNumberId;
    }

    public void setPartNumber(BigDecimal idValue) {
        setPartNumber(new Id(idValue));
    }

    public void setPartNumber(InventoryItemType partNumber) {
        setPartNumber(partNumber == null ? null : partNumber.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getPartNumberId() {
        return partNumberId;
    }

    public InventoryItemType getPartNumber() {
        if(partNumber == null) {
            partNumber = get(InventoryItemType.class, partNumberId, true);
        }
        return partNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(order = 200, required = false)
    public String getSerialNumber() {
        return serialNumber;
    }

    public String getSerialNumberDisplay() {
        if(getItem() != null) {
            return item.getSerialNumberDisplay();
        }
        if(serialNumber == null || serialNumber.isBlank()) {
            return "";
        }
        return getPartNumber().getSerialNumberShortName() + ": " + serialNumber;
    }

    public void setQuantity(Quantity quantity) {
        if(!loading()) {
            throw new Set_Not_Allowed("Quantity");
        }
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @SetNotAllowed
    @Column(order = 300)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setUnitCost(Money unitCost) {
        this.unitCost = unitCost;
    }

    public void setUnitCost(Object moneyValue) {
        setUnitCost(Money.create(moneyValue));
    }

    @Column(order = 400, required = false)
    public Money getUnitCost() {
        return unitCost;
    }

    public void setBin(Id binId) {
        if(bin != null && (Id.isNull(binId) || !binId.equals(this.binId))) {
            bin = null;
        }
        this.binId = binId;
    }

    public void setBin(BigDecimal idValue) {
        setBin(new Id(idValue));
    }

    public void setBin(InventoryBin bin) {
        setBin(bin == null ? null : bin.getId());
    }

    @Column(style = "(any)", required = false, order = 600)
    public Id getBinId() {
        return binId;
    }

    public InventoryBin getBin() {
        if(bin == null && !Id.isNull(binId)) {
            bin = get(InventoryBin.class, binId, true);
        }
        return bin;
    }

    public void setItem(Id itemId) {
        if(itemId == null) {
            inspected = false;
        }
        if(item != null && (Id.isNull(itemId) || !itemId.equals(this.itemId))) {
            item = null;
        }
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItem item) {
        setItem(item == null ? null : item.getId());
    }

    @Column(style = "(any)", required = false, order = 700)
    public Id getItemId() {
        return itemId;
    }

    public InventoryItem getItem() {
        if(item == null && !Id.isNull(itemId)) {
            item = get(getTransaction(), InventoryItem.class, itemId, true);
        }
        return item;
    }

    public void setInspected(boolean inspected) {
        if(!loading()) {
            throw new Set_Not_Allowed("Inspection status");
        }
        this.inspected = inspected;
    }

    @SetNotAllowed
    @Column(order = 800)
    public boolean getInspected() {
        return inspected || getItem() != null;
    }

    /**
     * Get cost of this item (excluding item-tax if any).
     *
     * @return Cost excluding item-tax if any.
     */
    public Money getCost() {
        return unitCost.multiply(quantity);
    }

    /**
     * Gte the tax amount for this entry.
     *
     * @return Tax amount.
     */
    public Money getTax() {
        if(tax == null) {
            tax = new Money();
            for (Tax t : listLinks(Tax.class)) {
                tax = tax.add(t.getTax());
            }
        }
        return tax;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        partNumber = null;
        bin = null;
        item = null;
        internal = false;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            return;
        }
        if(unitCost.getCurrency() != tm.getCurrency()) {
            throw new Invalid_State("Currency mismatch");
        }
        if(internal) {
            return;
        }
        serialNumber = toCode(serialNumber);
        partNumberId = tm.checkTypeAny(this, partNumberId, InventoryItemType.class, false);
        InventoryItemType pn = getPartNumber();
        if(pn.isSerialized() && !quantity.equals(Count.ONE)) {
            if(quantity.isZero()) {
                quantity = Count.ONE;
            } else {
                throw new Invalid_State("Quantity can not be " + quantity);
            }
        } else {
            quantity.canConvert(pn.getUnitOfMeasurement(), "Item " + pn.toDisplay() + ".");
        }
        MeasurementUnit mu = quantity.getUnit();
        if(mu.obsolete) {
            throw new Invalid_State("Obsolete unit used: " + quantity);
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, true);
        binId = tm.checkTypeAny(this, binId, InventoryBin.class, true);
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(deleted()) {
            return;
        }
        if(getItem() != null) {
            if(!item.getPartNumberId().equals(partNumberId)) {
                item.delete(getTransaction());
                itemId = Id.ZERO;
                inspected = false;
            } else {
                if(!internal && !item.getQuantity().equals(quantity)) {
                    throw new Invalid_State("Quantity mismatch " + quantity + " ≠ " + item.getQuantity()
                            + " for item " + item.toDisplay());
                }
            }
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(internal) {
            return;
        }
        InventoryGRN grn = getMaster(getTransaction(), InventoryGRN.class);
        if(grn == null) {
            return;
        }
        switch(grn.getStatus()) {
            case 0: // Initiated only, anything is allowed
                if(getItem() != null) {
                    if(item.getLocation().infiniteSource()) {
                        item.delete(getTransaction()); // Remove the item that will be dangling otherwise
                    }
                }
                return;
            case 1: // Processed
                throw new Invalid_State("Can't delete item that is already being processed");
            case 2: // Closed, deletion allowed for purging
        }
    }

    public void inspect(Transaction transaction) throws Exception {
        if(Id.isNull(itemId)) {
            throw new Invalid_State("Item not specified: " + toDisplay());
        }
        internal = true;
        inspected = true;
        save(transaction);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryGRN.class == masterClass;
    }

    @Override
    public String toString() {
        String s = getPartNumber().toDisplay();
        if(!serialNumber.isEmpty()) {
            s += ", " + partNumber.getSerialNumberShortName() + ": " + serialNumber;
        }
        return s;
    }

    public void splitQuantity(Transaction transaction, Quantity quantityToSplit) throws Exception {
        if(getPartNumber().isSerialized()) {
            throw new Invalid_State("Not allowed");
        }
        if(quantityToSplit.isZero() || quantityToSplit.isGreaterThan(quantity)
                || !quantity.isConvertible(quantityToSplit)) {
            throw new Invalid_Value("Quantity");
        }
        if(quantity.equals(quantityToSplit)) {
            if(quantity.getUnit().equals(quantityToSplit.getUnit())) {
                return;
            }
            quantity = quantityToSplit;
            save(transaction);
            return;
        }
        InventoryGRNItem newGrnItem = new InventoryGRNItem();
        newGrnItem.setPartNumber(partNumberId);
        newGrnItem.quantity = quantityToSplit;
        newGrnItem.setUnitCost(unitCost);
        quantity = quantity.subtract(quantityToSplit);
        save(transaction);
        newGrnItem.save(transaction);
        getMaster(InventoryGRN.class).addLink(transaction, newGrnItem);
    }

    /**
     * Update various attribute values.
     *
     * @param tm Transaction manager.
     * @param newQuantity New quantity (Could be null if change not required).
     * @param newUnitCost New unit cost (Could be null if change not required).
     * @param newSerialNumber New serial number (Could be null if change not required).
     * @throws Exception If error occurs while updating the value.
     * @return True if the values are updated.
     */
    public boolean updateValues(TransactionManager tm, Quantity newQuantity, Money newUnitCost, String newSerialNumber)
            throws Exception {
        boolean update = updateUnitCost(newUnitCost) || updateSN(newSerialNumber);
        if(newQuantity == null || quantity.equals(newQuantity)) {
            if(update) {
                internal = true;
                tm.transact(this::save);
            }
            return update;
        }
        if(getItem() != null) {
            throw new Invalid_State("Can't change quantity, the item was already created");
        }
        if(newQuantity.isZero()) {
            newQuantity = quantity.zero();
        } else {
            newQuantity.canConvert(quantity);
        }
        PO po = po();
        if(po.po == null || po.item == null) {
            throw new Invalid_State("Can't determine the corresponding PO");
        }
        if(po.po.isClosed()) {
            throw new Invalid_State("PO is already closed");
        }
        Quantity increase = newQuantity.subtract(quantity);
        if(po.item.getReceived().subtract(quantity).add(newQuantity).isGreaterThan(po.item.getQuantity())) {
            throw new Invalid_State("Quantity can't be more than "
                    + po.item.getQuantity().subtract(po.item.getReceived()).add(quantity));
        }
        Transaction t = null;
        try {
            t = tm.createTransaction();
            internal = true;
            po.item.internal = true;
            po.item.received = po.item.received.add(increase);
            po.item.save(t);
            po.po.setInternalStatusTo2(); // Partially received
            po.po.save(t);
            quantity = newQuantity;
            if(newQuantity.isZero()) {
                delete(t);
            } else {
                save(t);
            }
            t.commit();
        } catch(Exception error) {
            if(t != null) {
                t.rollback();
            }
            throw error;
        }
        return true;
    }

    private boolean updateUnitCost(Money newUnitCost) throws Exception {
        if(newUnitCost == null || unitCost.equals(newUnitCost)) {
            return false;
        }
        if(getItem() != null) {
            throw new Invalid_State("Can't change unit cost, the item was already created");
        }
        setUnitCost(newUnitCost);
        return true;
    }

    private boolean updateSN(String serialNumber) throws Exception {
        if(serialNumber == null) {
            return false;
        }
        if(getItem() != null) {
            throw new Invalid_State("Can't change S/N, the item was already created");
        }
        serialNumber = toCode(serialNumber);
        if(serialNumber.isBlank() || this.serialNumber.equals(serialNumber)) {
            return false;
        }
        InventoryItem ii = InventoryItem.get(serialNumber, getPartNumber());
        if(ii != null && ii.getSerialNumber().equals(serialNumber)) {
            throw new Invalid_State("An item with this serial number already exists - " + ii.toDisplay());
        }
        this.serialNumber = serialNumber;
        return true;
    }

    public InventoryPOItem getPOItem() {
        return po().item;
    }

    private PO po() {
        InventoryGRN grn = getMaster(InventoryGRN.class);
        List<InventoryPO> pos = grn.listMasters(InventoryPO.class, true).toList();
        InventoryPOItem poItem;
        if(pos.isEmpty()) {
            return new PO(grn, null, null);
        }
        InventoryPO po;
        if(pos.size() == 1) {
            po = pos.getFirst();
        } else {
            po = null;
            InventoryPOItem item = null, second;
            for(InventoryPO p: pos) {
                second = p.listItems().filter(i -> i.getPartNumberId().equals(partNumberId)).findFirst();
                if(second == null) {
                    continue;
                }
                if(item == null) {
                    item = second;
                    po = p;
                } else {
                    return new PO(grn, null, null);
                }
            }
            return new PO(grn, po, item);
        }
        List<InventoryPOItem> poItems = po.listItems()
                .filter(i -> i.getPartNumberId().equals(partNumberId) && i.received.isPositive()).toList();
        poItem = poItems.stream()
                .filter(i -> i.getSerialNumber().equals(serialNumber) && i.received.equals(quantity))
                .findAny().orElse(null);
        if(poItem == null) {
            poItem = poItems.stream()
                    .filter(i -> i.getSerialNumber().equals(serialNumber) && i.received.isGreaterThan(quantity))
                    .findAny().orElse(null);
        }
        if(poItem == null) {
            poItem = poItems.stream()
                    .filter(i -> i.received.equals(quantity)).findAny().orElse(null);
        }
        if(poItem == null) {
            poItem = poItems.stream()
                    .filter(i -> i.received.isGreaterThan(quantity)).findAny().orElse(null);
        }
        return new PO(grn, po, poItem);
    }

    private record PO(InventoryGRN grn, InventoryPO po, InventoryPOItem item) {}

    public List<Tax> computeTax(Date date, TaxRegion region, Currency localCurrency) {
        return region.computeTax(date, this, getItem(), quantity, unitCost, localCurrency);
    }

    @Override
    public InventoryItemType getInventoryItemType() {
        return getPartNumber();
    }
}
