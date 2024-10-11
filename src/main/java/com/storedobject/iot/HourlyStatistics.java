package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

public final class HourlyStatistics extends Statistics {

    private int hour;

    public HourlyStatistics() {
    }

    public static void columns(Columns columns) {
        columns.add("Hour", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Hour", true);
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Column(order = 300)
    public int getHour() {
        return hour;
    }

    @Override
    public int getPeriod() {
        return hour;
    }

    @Override
    public String getPeriodDetail() {
        return DateUtility.formatWithTimeHHMM(new Date(DateUtility.create(getYear(), 1, 1).getTime()
                + (hour - 1) * 3600000L));
    }
}
