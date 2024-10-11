package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.util.Date;
import java.util.List;

public final class Resource extends Name {

    private int code; // 1: Electricity, 2: Water
    private String measurementUnit;

    public Resource() {
    }

    public static void columns(Columns columns) {
        columns.add("Code", "int");
        columns.add("MeasurementUnit", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Code", true);
    }

    public String getUniqueCondition() {
        return "Code=" + getCode();
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static Resource get(String name) {
        return StoredObjectUtility.get(Resource.class, "Name", name, false);
    }

    public static ObjectIterator<Resource> list(String name) {
        return StoredObjectUtility.list(Resource.class, "Name", name, false);
    }

    public void setCode(int code) {
        if (!loading()) {
            throw new Set_Not_Allowed("Code");
        }
        this.code = code;
    }

    @SetNotAllowed
    @Column(order = 200)
    public int getCode() {
        return code;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @Column(order = 300)
    public String getMeasurementUnit() {
        return measurementUnit;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        checkForDuplicate("Code");
        if(StringUtility.isWhite(measurementUnit)) {
            throw new Invalid_Value("Measurement Unit");
        }
        super.validateData(tm);
    }

    private <T extends Consumption> T getConsumption(Id item, Class<T> cClass, String condition) {
        return consumption(item, cClass, condition).single(false);
    }

    private <T extends Consumption> List<T> listConsumption(Id item, Class<T> cClass, String condition) {
        return consumption(item, cClass, condition).toList();
    }

    private <T extends Consumption> ObjectIterator<T> consumption(Id item, Class<T> cClass, String condition) {
        return list(cClass, condition + " AND Resource=" + getId() + " AND Item=" + item);
    }

    public List<YearlyConsumption> listYearlyConsumption(Id item, int yearFrom, int yearTo) {
        String c;
        if(yearFrom == yearTo) {
            c = "Year=" + yearFrom;
        } else {
            c = "Year BETWEEN " + yearFrom + " AND " + yearTo;
        }
        return listConsumption(item, YearlyConsumption.class, c);
    }

    public YearlyConsumption getYearlyConsumption(Id item, int year) {
        return getConsumption(item, YearlyConsumption.class, "Year=" + year);
    }

    public <D extends Date> YearlyConsumption getYearlyConsumption(Id item, D date) {
        return getYearlyConsumption(item, DateUtility.getYear(date));
    }

    <D extends Date> YearlyConsumption createYearlyConsumption(Id item, D date) {
        int y = DateUtility.getYear(date);
        YearlyConsumption c = getYearlyConsumption(item, y);
        if(c == null) {
            c = new YearlyConsumption();
            c.setResource(this);
            c.setItem(item);
            c.setYear(y);
            c.makeVirtual();
        }
        return c;
    }

    public List<MonthlyConsumption> listMonthlyConsumption(Id item, int year, int monthFrom, int monthTo) {
        if(monthFrom == monthTo) {
            return listConsumption(item, MonthlyConsumption.class, "Year=" + year + " AND Month=" + monthFrom);
        }
        if(monthFrom < monthTo) {
            return listConsumption(item, MonthlyConsumption.class, "Year=" + year + " AND Month BETWEEN "
                    + monthFrom + " AND " + monthTo);
        }
        List<MonthlyConsumption> con = listMonthlyConsumption(item, year, monthFrom, 12);
        con.addAll(listMonthlyConsumption(item, year + 1, 1, monthTo));
        return con;
    }

    public MonthlyConsumption getMonthlyConsumption(Id item, int year, int month) {
        return getConsumption(item, MonthlyConsumption.class, "Year=" + year + " AND Month=" + month);
    }

    public <D extends Date> MonthlyConsumption getMonthlyConsumption(Id item, D date) {
        return getMonthlyConsumption(item, DateUtility.getYear(date), DateUtility.getMonth(date));
    }

    <D extends Date> MonthlyConsumption createMonthlyConsumption(Id item, D date) {
        int y = DateUtility.getYear(date), m = DateUtility.getMonth(date);
        MonthlyConsumption c = getMonthlyConsumption(item, y, m);
        if(c == null) {
            c = new MonthlyConsumption();
            c.setResource(this);
            c.setItem(item);
            c.setYear(y);
            c.setMonth(m);
            c.makeVirtual();
        }
        return c;
    }

    public List<WeeklyConsumption> listWeeklyConsumption(Id item, int year, int weekFrom, int weekTo) {
        if(weekFrom == weekTo) {
            return listConsumption(item, WeeklyConsumption.class, "Year=" + year + " AND Week=" + weekFrom);
        }
        if(weekFrom < weekTo) {
            return listConsumption(item, WeeklyConsumption.class, "Year=" + year + " AND Week BETWEEN "
                    + weekFrom + " AND " + weekTo);
        }
        List<WeeklyConsumption> con = listWeeklyConsumption(item, year, weekFrom, 53);
        con.addAll(listWeeklyConsumption(item, year + 1, 1, weekTo));
        return con;
    }

    public WeeklyConsumption getWeeklyConsumption(Id item, int year, int week) {
        return getConsumption(item, WeeklyConsumption.class, "Year=" + year + " AND Week=" + week);
    }

    public <D extends Date> WeeklyConsumption getWeeklyConsumption(Id item, D date) {
        return getWeeklyConsumption(item, DateUtility.getYear(date), DateUtility.getWeekOfYear(date));
    }

    <D extends Date> WeeklyConsumption createWeeklyConsumption(Id item, D date) {
        int y = DateUtility.getYear(date), w = DateUtility.getWeekOfYear(date);
        WeeklyConsumption c = getWeeklyConsumption(item, y, w);
        if(c == null) {
            c = new WeeklyConsumption();
            c.setResource(this);
            c.setItem(item);
            c.setYear(y);
            c.setWeek(w);
            c.makeVirtual();
        }
        return c;
    }

    public List<HourlyConsumption> listHourlyConsumption(Id item, int year, int hourFrom, int hourTo) {
        if(hourFrom == hourTo) {
            return listConsumption(item, HourlyConsumption.class, "Year=" + year + " AND Hour=" + hourFrom);
        }
        if(hourFrom < hourTo) {
            return listConsumption(item, HourlyConsumption.class, "Year=" + year + " AND Hour BETWEEN "
                    + hourFrom + " AND " + hourTo);
        }
        List<HourlyConsumption> con = listHourlyConsumption(item, year, hourFrom, 53);
        con.addAll(listHourlyConsumption(item, year + 1, 1, hourTo));
        return con;
    }

    public HourlyConsumption getHourlyConsumption(Id item, int year, int hour) {
        return getConsumption(item, HourlyConsumption.class, "Year=" + year + " AND Hour=" + hour);
    }

    public <D extends Date> HourlyConsumption getHourlyConsumption(Id item, D date) {
        return getHourlyConsumption(item, DateUtility.getYear(date), DateUtility.getHourOfYear(date));
    }

    <D extends Date> HourlyConsumption createHourlyConsumption(Id item, D date) {
        int y = DateUtility.getYear(date), h = DateUtility.getHourOfYear(date);
        HourlyConsumption c = getHourlyConsumption(item, y, h);
        if(c == null) {
            c = new HourlyConsumption();
            c.setResource(this);
            c.setItem(item);
            c.setYear(y);
            c.setHour(h);
            c.makeVirtual();
        }
        return c;
    }

    public List<DailyConsumption> listDailyConsumption(Id item, int year, int dayFrom, int dayTo) {
        if(dayFrom == dayTo) {
            return listConsumption(item, DailyConsumption.class, "Year=" + year + " AND Day=" + dayFrom);
        }
        if(dayFrom < dayTo) {
            return listConsumption(item, DailyConsumption.class, "Year=" + year + " AND Day BETWEEN "
                    + dayFrom + " AND " + dayTo);
        }
        List<DailyConsumption> con = listDailyConsumption(item, year, dayFrom, 366);
        con.addAll(listDailyConsumption(item, year + 1, 1, dayTo));
        return con;
    }

    public DailyConsumption getDailyConsumption(Id item, int year, int day) {
        return getConsumption(item, DailyConsumption.class, "Year=" + year + " AND Day=" + day);
    }

    public <D extends Date> DailyConsumption getDailyConsumption(Id item, D date) {
        return getDailyConsumption(item, DateUtility.getYear(date), DateUtility.getDayOfYear(date));
    }

    <D extends Date> DailyConsumption createDailyConsumption(Id item, D date) {
        int y = DateUtility.getYear(date), d = DateUtility.getDayOfYear(date);
        DailyConsumption c = getDailyConsumption(item, y, d);
        if(c == null) {
            c = new DailyConsumption();
            c.setResource(this);
            c.setItem(item);
            c.setYear(y);
            c.setDay(d);
            c.makeVirtual();
        }
        return c;
    }
}
