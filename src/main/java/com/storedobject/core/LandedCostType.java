package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.util.HashMap;
import java.util.Map;

public final class LandedCostType extends StoredObject {

    private static final Map<Id, LandedCostType> cache = new HashMap<>();
    private String name;
    private int displayOrder;
    private boolean deduct, partOfInvoice, tax, inactive;

    public LandedCostType() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("DisplayOrder", "int");
        columns.add("Deduct", "boolean");
        columns.add("PartOfInvoice", "boolean");
        columns.add("Tax", "boolean");
        columns.add("Inactive", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static LandedCostType get(String name) {
        return StoredObjectUtility.get(LandedCostType.class, "Name", name, false);
    }

    public static ObjectIterator<LandedCostType> list(String name) {
        return StoredObjectUtility.list(LandedCostType.class, "Name", name, false);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Column(required = false, order = 200)
    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDeduct(boolean deduct) {
        if(!loading()) {
            throw new Set_Not_Allowed("Deduct");
        }
        this.deduct = deduct;
    }

    @SetNotAllowed
    @Column(order = 300)
    public boolean getDeduct() {
        return deduct;
    }

    public void setPartOfInvoice(boolean partOfInvoice) {
        this.partOfInvoice = partOfInvoice;
    }

    @SetNotAllowed
    @Column(order = 400)
    public boolean getPartOfInvoice() {
        return partOfInvoice;
    }

    public void setTax(boolean tax) {
        this.tax = tax;
    }

    @Column(order = 500)
    public boolean getTax() {
        return tax;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    @Column(order = 600)
    public boolean getInactive() {
        return inactive;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        if(tax) {
            partOfInvoice = true;
        }
        super.validateData(tm);
    }

    @Override
    void savedCore() throws Exception {
        cache.remove(getId());
    }

    public static LandedCostType getFor(Id typeId) {
        LandedCostType lct = cache.get(typeId);
        if(lct == null) {
            lct = get(LandedCostType.class, typeId);
            if(lct != null) {
                cache.put(typeId, lct);
            }
        }
        return lct;
    }
}
