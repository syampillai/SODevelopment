package com.storedobject.iot;

public record Period(PeriodType periodType, int year, int period) {

    public static Period year(int year) {
        return new Period(PeriodType.YEARLY, year, 0);
    }

    public static Period month(int year, int month) {
        return new Period(PeriodType.MONTHLY, year, month);
    }

    public static Period day(int year, int month) {
        return new Period(PeriodType.DAILY, year, month);
    }

    public static Period hour(int year, int month, int day, int hour) {
        return new Period(PeriodType.HOURLY, year, day);
    }

    public static Period week(int year, int week) {
        return new Period(PeriodType.WEEKLY, year, week);
    }
}
