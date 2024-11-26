package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class MaterialRequestItem extends StoredObject implements Detail, HasInventoryItemType {

    private Id partNumberId;
    private InventoryItemType partNumber;
    private Quantity requested = Quantity.create(Quantity.class);
    private Quantity issued = Quantity.create(Quantity.class);

    public MaterialRequestItem() {
    }

    public static void columns(Columns columns) {
        columns.add("PartNumber", "id");
        columns.add("Requested", "quantity");
        columns.add("Issued", "quantity");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Issued",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "PartNumber.Name AS Item",
                "PartNumber.PartNumber AS Part Number",
                "Requested",
                "Issued",
        };
    }

    public void setPartNumber(Id partNumberId) {
        this.partNumberId = partNumberId;
        partNumber = null;
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

    public void setRequested(Quantity requested) {
        this.requested = requested;
    }

    public void setRequested(Object value) {
        setRequested(Quantity.create(value));
    }

    @Column(order = 200)
    public Quantity getRequested() {
        return requested;
    }

    public void setIssued(Quantity issued) {
        this.issued = issued;
    }

    public void setIssued(Object value) {
        setIssued(Quantity.create(value));
    }

    @Column(required = false, order = 300)
    public Quantity getIssued() {
        return issued;
    }

    public Quantity getBalance() {
        return requested.subtract(issued);
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        partNumber = null;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        partNumberId = tm.checkTypeAny(this, partNumberId, InventoryItemType.class, false);
        getPartNumber().checkUnit(requested, "requested quantity");
        if(issued.isZero()) {
            issued = partNumber.getUnitOfMeasurement();
        } else {
            partNumber.checkUnit(issued, "issued quantity");
        }
        if(issued.isGreaterThan(requested)) {
            throw new Invalid_State("Issued quantity can't be more that the requested quantity");
        }
        super.validateData(tm);
    }

    @Override
    public void validateInsert() throws Exception {
        super.validateInsert();
        checkReqQty();
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        checkReqQty();
    }

    private void checkReqQty() throws Invalid_State {
        if(requested.isZero()) {
            throw new Invalid_State("Requested quantity can't be zero");
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(requested.isZero() && issued.isZero()) {
            return;
        }
        throw new SOException("Delete not allowed");
    }

    @Override
    public Id getUniqueId() {
        return partNumberId;
    }

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return MaterialRequest.class.isAssignableFrom(masterClass)
                && (masterClass.getName() + "Item").equals(getClass().getName());
    }

    public void reduceRequestedQuantity(Transaction transaction, Quantity reduceBy) throws Exception {
        reduceRequestedQuantity(transaction, reduceBy, true);
    }

    void reduceRequestedQuantity(Transaction transaction, Quantity reduceBy, boolean checkStatus) throws Exception {
        if(checkStatus) {
            getMaster(MaterialRequest.class, true).checkStatus();
        }
        Quantity b = getBalance();
        if(reduceBy.isGreaterThan(b)) {
            throw new SOException("Balance to be issued is only " + b);
        }
        requested = requested.subtract(reduceBy);
        if(reduceBy.equals(b)) {
            delete(transaction);
        } else {
            save(transaction);
        }
    }

    @Override
    public final InventoryItemType getInventoryItemType() {
        return getPartNumber();
    }
}
