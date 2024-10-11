package com.storedobject.iot;

import com.storedobject.core.*;

public final class YearlyConsumption extends Consumption {

    public YearlyConsumption() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year", true);
    }

    @Override
    public int getPeriod() {
        return getYear();
    }

    @Override
    public String getPeriodDetail() {
        return "Year " + getYear();
    }
}
