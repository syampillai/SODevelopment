package com.storedobject.core;

import com.storedobject.common.HasName;
import com.storedobject.core.annotation.Column;

public abstract class Name extends StoredObject implements HasName {

    protected String name;

    public Name() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name),T_Family", true);
    }

    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "' AND T_Family=" + family();
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(order = 100)
    public String getName() {
        return name;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        checkForDuplicate("Name");
        super.validateData(tm);
    }
}