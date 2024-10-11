package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

public final class WeeklyConsumption extends Consumption {

    private int week;

    public WeeklyConsumption() {
    }

    public static void columns(Columns columns) {
        columns.add("Week", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Week", true);
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
        return periodDetail(getYear(), week);
    }

    static String periodDetail(int y, int week) {
        Date date = DateUtility.create(y, 1, 1);
        while (DateUtility.getWeekOfYear(date) < week) {
            date = DateUtility.addMonth(date, 1);
            if(DateUtility.getYear(date) > y) {
                date = DateUtility.create(y, 31, 1);
                break;
            }
        }
        while (DateUtility.getWeekOfYear(date) > week) {
            date = DateUtility.addDay(date, -1);
        }
        while (DateUtility.getWeekOfYear(date) == week) {
            date = DateUtility.addDay(date, 1);
        }
        date = DateUtility.addDay(date, -1);
        Date date2 = date;
        while (DateUtility.getWeekOfYear(date) == week) {
            date = DateUtility.addDay(date, -1);
        }
        date = DateUtility.addDay(date, 1);
        return DateUtility.formatDate(date) + " - " + DateUtility.formatDate(date2);
    }
}
