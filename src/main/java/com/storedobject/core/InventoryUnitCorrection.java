package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

public class InventoryUnitCorrection extends StoredObject {

    private Id itemId;
    private Quantity previousUnit = Quantity.create(Quantity.class);
    private Quantity correctedUnit = Quantity.create(Quantity.class);
    boolean internal;

    public InventoryUnitCorrection() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("PreviousUnit", "quantity");
        columns.add("CorrectedUnit", "quantity");
    }

    public void setItem(Id itemId) {
        if (!loading() && !Id.equals(this.getItemId(), itemId)) {
            throw new Set_Not_Allowed("Item");
        }
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItemType item) {
        setItem(item == null ? null : item.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 100)
    public Id getItemId() {
        return itemId;
    }

    public InventoryItemType getItem() {
        return getRelated(InventoryItemType.class, itemId, true);
    }

    public void setPreviousUnit(Quantity previousUnit) {
        if (!loading()) {
            throw new Set_Not_Allowed("Previous Unit");
        }
        this.previousUnit = previousUnit;
    }

    public void setPreviousUnit(Object value) {
        setPreviousUnit(Quantity.create(value));
    }

    @SetNotAllowed
    @Column(order = 200)
    public Quantity getPreviousUnit() {
        return previousUnit;
    }

    public void setCorrectedUnit(Quantity correctedUnit) {
        if (!loading()) {
            throw new Set_Not_Allowed("Corrected Unit");
        }
        this.correctedUnit = correctedUnit;
    }

    public void setCorrectedUnit(Object value) {
        setCorrectedUnit(Quantity.create(value));
    }

    @SetNotAllowed
    @Column(order = 300)
    public Quantity getCorrectedUnit() {
        return correctedUnit;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!internal) {
            throw new SOException("Not internal");
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItemType.class, false);
        super.validateData(tm);
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        internal = false;
    }
}
