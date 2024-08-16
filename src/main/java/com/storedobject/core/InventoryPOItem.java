package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.List;

public class InventoryPOItem extends StoredObject implements Detail {

    private final static String[] typeValues = {
            "", "APN", "Excess"
    };
    private Id partNumberId;
    private InventoryItemType partNumber;
    private String serialNumber = "";
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money unitPrice = new Money();
    Quantity received = Quantity.create(Quantity.class);
    private int type = 0;
    boolean internal = false;

    public InventoryPOItem() {
    }

    public static void columns(Columns columns) {
        columns.add("PartNumber", "id");
        columns.add("SerialNumber", "text");
        columns.add("Quantity", "quantity");
        columns.add("UnitPrice", "money");
        columns.add("Received", "quantity");
        columns.add("Type", "int");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Received",
                "Type",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "PartNumber.Name AS Item",
                "PartNumber.PartNumber AS Part Number",
                "SerialNumber",
                "Quantity",
                "UnitPrice",
                "Received",
                "Type",
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

    public void setQuantity(Quantity quantity) {
        if(!loading() && (!received.isZero() || quantity.isZero())) {
            throw new Set_Not_Allowed("Quantity can't be changed");
        }
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Column(order = 300)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setUnitPrice(Object moneyValue) {
        setUnitPrice(Money.create(moneyValue));
    }

    @Column(order = 400, required = false)
    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setReceived(Quantity quantity) {
        if(!loading()) {
            throw new Set_Not_Allowed("Received");
        }
        this.received = quantity;
    }

    public void setReceived(Object value) {
        setReceived(Quantity.create(value));
    }

    @SetNotAllowed
    @Column(order = 500)
    public Quantity getReceived() {
        return received;
    }

    public static String[] getTypeValues() {
        return typeValues;
    }

    public void setType(int type) {
        if(!loading() && !internal) {
            throw new Set_Not_Allowed("Type");
        }
        if(type < 0 || type >= typeValues.length) {
            throw new SORuntimeException();
        }
        this.type = type;
    }

    @SetNotAllowed
    @Column(order = 600)
    public int getType() {
        return type;
    }

    public String getTypeValue() {
        return typeValues[type];
    }

    public Quantity getBalance() {
        return quantity.subtract(received);
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        partNumber = null;
        internal = false;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            return;
        }
        serialNumber = toCode(serialNumber);
        partNumberId = tm.checkTypeAny(this, partNumberId, InventoryItemType.class, false);
        InventoryItemType pn = getPartNumber();
        if(pn.isBlocked()) {
            throw new Invalid_State("Blocked Item: " + pn.toDisplay());
        }
        if(pn.isObsolete()) {
            throw new Invalid_State("Obsolete Item: " + pn.toDisplay());
        }
        Quantity uom = pn.getUnitOfMeasurement();
        quantity.canConvert(uom);
        if(pn.isSerialized()) {
            if(quantity.isZero()) {
                quantity = Count.ONE;
            } else {
                if(!serialNumber.isEmpty() && quantity.isGreaterThan(Count.ONE)) {
                    throw new Invalid_State("Quantity can not be " + quantity + " because 'serial number' is specified");
                }
            }
        }
        if(received.isZero()) {
            received = uom;
        } else {
            received.canConvert(uom);
        }
        if(received.isGreaterThan(quantity)) {
            throw new Invalid_State("Received quantity (" + received + ") can't be more than ordered quantity ("
                    + quantity + ")");
        }
        if(unitPrice.isNegative()) {
            throw new Invalid_State("Unit price can't be " + unitPrice);
        }
        super.validateData(tm);
    }

    @Override
    public String getUniqueCondition() {
        return partNumberId + "_" + serialNumber + "_" + type;
    }

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return InventoryPO.class.isAssignableFrom(masterClass) &&
                (masterClass.getName() + "Item").equals(getClass().getName());
    }

    @Override
    public String toString() {
        String s = getPartNumber().toDisplay();
        if(type > 0) {
            s += " (" + getTypeValue() + ")";
        }
        if(!serialNumber.isEmpty()) {
            s += ", " + partNumber.getSerialNumberShortName() + ": " + serialNumber;
        }
        return s + ", Quantity: " + quantity + ", Received: " + received;
    }

    public void setAPN(Transaction transaction, InventoryItemType apn, Quantity quantity) throws  Exception {
        InventoryPO po = getMaster(InventoryPO.class, true);
        if(po == null || !isDetailOf(po.getClass())) {
            throw new Invalid_State("PO not found for " + toDisplay());
        }
        if(po.isClosed()) {
            throw new Invalid_State("Closed PO found for " + toDisplay());
        }
        InventoryItemType pn = getPartNumber();
        if(!pn.listAPNs().contains(apn)) {
            throw new Invalid_Value(apn + " is not an APN of " + pn);
        }
        Quantity b = getBalance();
        if(!b.isPositive()) {
            throw new Invalid_State("Already received: " + this);
        }
        quantity.canConvert(b);
        if(quantity.isGreaterThan(b)) {
            throw new Invalid_State("For " + pn.toDisplay() + ", " + quantity + " > " + b);
        }
        received = received.add(quantity);
        internal = true;
        save(transaction);
        InventoryPOItem i = getClass().getConstructor().newInstance();
        i.internal = true;
        i.setPartNumber(apn);
        i.received = quantity.zero();
        i.setQuantity(quantity);
        i.setUnitPrice(unitPrice);
        i.setType(1);
        i.save(transaction);
        po.addLink(transaction, i);
    }

    public void correctUnitPrice(DBTransaction transaction, Money unitPrice) throws Exception {
        internal = true;
        boolean simple = received.isZero();
        if(received.isZero()) {
            setUnitPrice(unitPrice);
            save(transaction);
            return;
        }
        List<InventoryGRN> grnList = null;
        if(!simple) {
            grnList = getMaster(InventoryPO.class, true).listLinks(InventoryGRN.class).toList();
            grnList.removeIf(g -> g.listLinks(InventoryGRNItem.class, "PartNumber=" + getPartNumberId())
                    .findFirst() == null);
            if(grnList.isEmpty()) {
                simple = true;
            }
        }
        if(simple) {
            setUnitPrice(unitPrice);
            save(transaction);
            return;
        }
        Money incUC = unitPrice.subtract(this.unitPrice), m;
        boolean itemFound;
        setUnitPrice(unitPrice);
        save(transaction);
        List<InventoryGRNItem> gis;
        for(InventoryGRN g: grnList) {
            gis = g.listLinks(InventoryGRNItem.class, "PartNumber=" + getPartNumberId()).toList();
            for(InventoryGRNItem gi: gis) {
                gi.internal = true;
                gi.setUnitCost(unitPrice);
                gi.save(transaction);
                itemFound = false;
                for(InventoryItem ii: list(InventoryItem.class, "PartNumber=" + gi.getPartNumberId()
                        + " AND GRN=" + getId())) {
                    if(ii.getQuantity().isZero()) {
                        continue;
                    }
                    if(!itemFound) {
                        itemFound = ii.getId().equals(gi.getItemId());
                    }
                    if(ii.getQuantity().isGreaterThanOrEqual(gi.getQuantity())) {
                        m = ii.getCost().add(incUC.multiply(gi.getQuantity()));
                    } else {
                        m = ii.getCost()
                                .add(incUC.multiply(ii.getQuantity().convert(gi.getQuantity().getUnit())));
                    }
                    transaction.updateSQL("UPDATE core.InventoryItem SET Cost=" + m.getStorableValue()
                            + " WHERE Id=" + ii.getId());
                    for(InventoryLedger movement : list(InventoryLedger.class, "Item=" + ii.getId()
                            + " AND Date>='" + Database.format(g.date) + "'", "Item,Date")) {
                        movement.increaseCost(transaction, incUC, gi.getQuantity());
                    }
                }
                if(!itemFound) {
                    InventoryItem ii = gi.getItem();
                    if(ii != null && !ii.getQuantity().isZero()) {
                        if(ii.getQuantity().isGreaterThanOrEqual(gi.getQuantity())) {
                            m = ii.getCost().add(incUC.multiply(gi.getQuantity()));
                        } else {
                            m = ii.getCost()
                                    .add(incUC.multiply(ii.getQuantity().convert(gi.getQuantity().getUnit())));
                        }
                        transaction.updateSQL("UPDATE core.InventoryItem SET Cost=" + m.getStorableValue()
                                + " WHERE Id=" + ii.getId());
                    }
                    for(InventoryLedger movement : list(InventoryLedger.class, "Item=" + gi.getItemId()
                            + " AND Date>='" + Database.format(g.date) + "'", "Item,Date")) {
                        movement.increaseCost(transaction, incUC, gi.getQuantity());
                    }
                }
            }
        }
    }

    /**
     * Check whether this item can be received now or not. If this method returns <code>false</code>, no GRN entries
     * will be generated for this item.
     *
     * @return True/false.
     */
    public boolean canReceive() {
        return true;
    }
}
