package com.storedobject.core;

import com.storedobject.core.annotation.*;

public class ShortName extends Name {

    private String shortName;

    public ShortName() {
    }

    public static void columns(Columns columns) {
        columns.add("ShortName", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(ShortName),T_Family", true);
    }

    public static ShortName get(String name) {
        return StoredObjectUtility.get(ShortName.class, "Name", name, false);
    }

    public static ObjectIterator<ShortName> list(String name) {
        return StoredObjectUtility.list(ShortName.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

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
