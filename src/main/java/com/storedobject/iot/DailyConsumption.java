package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

/**
 * Represents daily consumption data tied to a specific item, resource, year, and day.
 * This class extends the `Consumption` class to provide additional functionality
 * specific to daily-level details.
 *
 * @author Syam
 */
public final class DailyConsumption extends Consumption<PeriodType> {

    private int day;

    /**
     * Initializes a new instance of the DailyConsumption class.
     * This constructor creates an empty DailyConsumption object with default values.
     */
    public DailyConsumption() {
    }

    /**
     * Adds a column definition to the provided {@code Columns} object.
     * The column defines the "Day" field as an integer type.
     *
     * @param columns the {@code Columns} object to which the column definition is added
     */
    public static void columns(Columns columns) {
        columns.add("Day", "int");
    }

    /**
     * Adds an index definition to the specified {@code indices} object.
     * The index is defined on the combination of fields: "Item, Resource, Year, Day".
     *
     * @param indices the {@code Indices} object to which the index definition is added
     */
    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Day", true);
    }

    /**
     * Sets the day of the year for this instance of DailyConsumption.
     *
     * @param day the day of the year to set, represented as an integer (1-365 or 1-366 in leap years)
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Retrieves the day associated with this daily consumption instance.
     *
     * @return the day of the year as an integer
     */
    @Column(order = 300)
    public int getDay() {
        return day;
    }

    /**
     * Retrieves the period value associated with the daily consumption instance.
     * The period represents the specific day number within a year.
     *
     * @return the day number corresponding to this daily consumption period
     */
    @Override
    public int getPeriod() {
        return day;
    }

    /**
     * Retrieves the detailed representation of the period for the given year and day.
     * This method utilizes the `periodDetail` helper method to calculate and format the date
     * corresponding to the specified year and day.
     *
     * @return a string representing the detailed period in a formatted date string.
     */
    @Override
    public String getPeriodDetail() {
        return periodDetail(getYear(), day);
    }

    /**
     * Computes and returns the formatted date string corresponding to a given `year`
     * and `day` (day of the year). The method determines the month and day by
     * navigating forward and backward within the year and formats the resultant date.
     *
     * @param year the year for which the detail is calculated
     * @param day the day of the year for which the corresponding date is determined
     * @return the formatted date string representing the month and day within the given year
     */
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

    /**
     * Retrieves the previous day's {@code DailyConsumption} record for the same item, resource, and year
     * relative to the current instance. If the current day is the first day of the year, the method
     * returns the last day of the previous year.
     *
     * @return the {@code DailyConsumption} instance representing the previous day, or {@code null}
     *         if no such record exists.
     */
    @Override
    public DailyConsumption previous() {
        int d = day - 1;
        int y = getYear();
        if(d < 1) {
            d = DateUtility.getDayOfYear(DateUtility.create(y, 12, 31));
            --y;
        }
        return get(DailyConsumption.class, "Year=" + y + " AND Day=" + d + cond());
    }

    /**
     * Retrieves the next `DailyConsumption` record based on the current day and year.
     * If the next day's record does not exist but the year has not ended, returns null.
     * If the next day's record does not exist because the year has ended, retrieves
     * the first day's record of the next year, if available.
     *
     * @return The next `DailyConsumption` instance if it exists, null otherwise.
     */
    @Override
    public DailyConsumption next() {
        int d = day + 1;
        int y = getYear();
        DailyConsumption dc = get(DailyConsumption.class, "Year=" + y + " AND Day=" + d + cond());
        if(dc != null) {
            return dc;
        }
        if(d < 365) {
            return null;
        }
        ++y;
        return get(DailyConsumption.class, "Year=" + y + " AND Day=1" + cond());
    }

    /**
     * Retrieves the period type for this consumption instance.
     * This method identifies the frequency or granularity of the
     * consumption data being represented.
     *
     * @return the {@code PeriodType} associated with this instance, which is {@code DAILY}.
     */
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.DAILY;
    }
}
