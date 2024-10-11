package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public final class MonthlyStatistics extends Statistics {

    private int month;

    public MonthlyStatistics() {
    }

    public static void columns(Columns columns) {
        columns.add("Month", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Month", true);
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Column(order = 300)
    public int getMonth() {
        return month;
    }

    @Override
    public int getPeriod() {
        return month;
    }

    @Override
    public String getPeriodDetail() {
        return DateUtility.getMonthNames()[month - 1] + " " + getYear();
    }
}
