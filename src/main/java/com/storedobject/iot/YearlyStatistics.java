package com.storedobject.iot;

import com.storedobject.core.*;

public final class YearlyStatistics extends Statistics {

    public YearlyStatistics() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year", true);
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
