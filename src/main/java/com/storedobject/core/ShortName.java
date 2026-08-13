package com.storedobject.core;

import com.storedobject.core.annotation.*;

public abstract class ShortName extends Name implements HasShortName {

    protected String shortName;

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

    @Column(order = 50)
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

    @Override
    public String toString() {
        return shortName;
    }

    public static <SN extends ShortName> SN getFor(Class<SN> snClass, String name) {
        if(StringUtility.isWhite(name)) return null;
        name = name.toLowerCase().trim();
        var _instance1 = get(snClass, "lower(ShortName)='" + name + "'", false);
        var _instance2 = get(snClass, "lower(Name)='" + name + "'", false);
        if (_instance1 != null && _instance2 == null) return _instance1;
        return (_instance2 != null && _instance1 == null) ? _instance2 : null;
    }
}
