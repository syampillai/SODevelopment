package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public final class DailyStatistics extends Statistics {

    private int day;

    public DailyStatistics() {
    }

    public static void columns(Columns columns) {
        columns.add("Day", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Day", true);
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Column(order = 300)
    public int getDay() {
        return day;
    }

    @Override
    public int getPeriod() {
        return day;
    }

    @Override
    public String getPeriodDetail() {
        return DailyConsumption.periodDetail(getYear(), day);
    }
}
