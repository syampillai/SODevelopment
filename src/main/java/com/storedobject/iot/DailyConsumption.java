package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

public final class DailyConsumption extends Consumption {

    private int day;

    public DailyConsumption() {
    }

    public static void columns(Columns columns) {
        columns.add("Day", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Day", true);
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
        return periodDetail(getYear(), day);
    }

    static String periodDetail(int year, int day) {
        Date date = DateUtility.create(year, 1, 1);
        while (DateUtility.getDayOfYear(date) < day) {
            date = DateUtility.addMonth(date, 1);
            if(DateUtility.getYear(date) > year) {
                date = DateUtility.create(year, 1, 31);
                break;
            }
        }
        while (DateUtility.getDayOfYear(date) > day) {
            date = DateUtility.addDay(date, -1);
        }
        return DateUtility.formatDate(date);
    }
}
