package com.storedobject.core;

import com.storedobject.core.annotation.*;

public abstract class ShortName extends Name implements HasShortName {

    private String shortName;

    public ShortName() {
    }

    public static void columns(Columns columns) {
        columns.add("ShortName", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(ShortName),T_Family", true);
    }

    public static int hints() {
        return ObjectHint.SMALL;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    @Column(order = 200)
    public String getShortName() {
        return shortName;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(isCode()) {
            shortName = toCode(shortName);
        }
        if (StringUtility.isWhite(shortName)) {
            throw new Invalid_Value("Short Name");
        }
        checkForDuplicate("ShortName");
        super.validateData(tm);
    }

    protected boolean isCode() {
        return false;
    }
}
