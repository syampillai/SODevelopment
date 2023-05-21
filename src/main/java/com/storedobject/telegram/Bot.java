package com.storedobject.telegram;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public final class Bot extends StoredObject {

    private String name;
    private String key;

    public Bot() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Key", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name), T_Family", true);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setKey(String key) {
        if (!loading()) {
            throw new Set_Not_Allowed("Key");
        }
        this.key = key;
    }

    @SetNotAllowed
    @Column(style = "(secret)", order = 200)
    public String getKey() {
        return key;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        if (StringUtility.isWhite(key)) {
            throw new Invalid_Value("Key");
        }
        super.validateData(tm);
    }
}
