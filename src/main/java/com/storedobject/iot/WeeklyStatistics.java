package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public final class WeeklyStatistics extends Statistics {

    private int week;

    public WeeklyStatistics() {
    }

    public static void columns(Columns columns) {
        columns.add("Week", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Week", true);
    }

    public void setWeek(int week) {
        this.week = week;
    }

    @Column(order = 300)
    public int getWeek() {
        return week;
    }

    @Override
    public int getPeriod() {
        return week;
    }

    @Override
    public String getPeriodDetail() {
        return DailyConsumption.periodDetail(getYear(), week);
    }
}
