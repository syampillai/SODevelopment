package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

public final class LandedCostType extends StoredObject {

    private String name;
    private int displayOrder;
    private boolean deduct;

    public LandedCostType() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("DisplayOrder", "int");
        columns.add("Deduct", "boolean");
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

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        super.validateData(tm);
    }
}
