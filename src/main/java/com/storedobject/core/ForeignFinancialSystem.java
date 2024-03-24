package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public final class ForeignFinancialSystem extends Name {

    private boolean active = true;

    public ForeignFinancialSystem() {
    }

    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 300)
    public boolean getActive() {
        return active;
    }

    public static ForeignFinancialSystem get(String name) {
        return StoredObjectUtility.get(ForeignFinancialSystem.class, "Name", name);
    }

    public static ObjectIterator < ForeignFinancialSystem > list(String name) {
        return StoredObjectUtility.list(ForeignFinancialSystem.class, "Name", name);
    }
}