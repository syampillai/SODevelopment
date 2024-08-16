package com.storedobject.core;

import com.storedobject.core.annotation.*;

public abstract class AbstractTradeType extends Name implements TradeType {

    private int type;

    public AbstractTradeType() {}

    public static void columns(Columns columns) {
        columns.add("Type", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Type", true);
    }

    @Override
    public String getUniqueCondition() {
        return "Type=" + getType();
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    @Column(required = false, order = 200)
    public int getType() {
        return type;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        checkForDuplicate("Type");
        super.validateData(tm);
    }
}
