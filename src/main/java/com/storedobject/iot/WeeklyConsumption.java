package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

/**
 * The WeeklyConsumption class extends the Consumption class to represent
 * consumption data on a weekly basis. This class includes methods for managing
 * and retrieving weekly consumption data and calculating period details.
 * <br/>
 * It defines the specific week within which consumption data is associated
 * and provides utility methods to navigate to the previous and next weekly
 * consumption records. Additionally, it provides functionality to format and
 * display period details in a human-readable format.
 */
public final class WeeklyConsumption extends Consumption {

    private int week;

    /**
     * Default constructor for the WeeklyConsumption class.
     * Initializes a new instance of the WeeklyConsumption class,
     * which represents weekly consumption data. This class is
     * used to manage, calculate, and navigate consumption records
     * for specific weeks within a year.
     */
    public WeeklyConsumption() {
    }

    /**
     * Adds a column definition for the "Week" property to the specified Columns instance.
     *
     * @param columns the Columns instance to which the "Week" column definition will be added
     */
    public static void columns(Columns columns) {
        columns.add("Week", "int");
    }

    /**
     * Configures the indices for the WeeklyConsumption dataset.
     *
     * @param indices the Indices object to which the index "Item,Resource,Year,Week"
     *                is added with a unique constraint.
     */
    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Week", true);
    }

    /**
     * Sets the week for the WeeklyConsumption instance.
     *
     * @param week the week number to be set, typically representing a specific
     *             week of the year. Valid values depend on the year's configuration,
     *             typically ranging from 1 to 52 or 53 for some years.
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * Retrieves the specific week associated with the weekly consumption data.
     *
     * @return an integer representing the week of the year.
     */
    @Column(order = 300)
    public int getWeek() {
        return week;
    }

    /**
     * Retrieves the period of the weekly consumption, represented by the week number.
     *
     * @return the week number corresponding to the period of the consumption.
     */
    @Override
    public int getPeriod() {
        return week;
    }

    /**
     * Retrieves the formatted period detail for the weekly consumption.
     * The period detail represents the start and end dates of the given week within the year.
     *
     * @return a string representing the date range, formatted as "start_date - end_date".
     */
    @Override
    public String getPeriodDetail() {
        return periodDetail(getYear(), week);
    }

    /**
     * Calculates the date range (start and end dates) for a specified week
     * in a given year and returns it as a formatted string.
     *
     * @param y the year for which the period detail is to be calculated
     * @param week the week number for which the period detail is to be calculated
     * @return a string representing the date range for the specified week in
     *         the format "startDate - endDate"
     */
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

    /**
     * Retrieves the WeeklyConsumption object representing the previous week
     * relative to the current instance. If the current week is the first week
     * of the year, it adjusts to return the last week of the previous year.
     *
     * @return a WeeklyConsumption object for the previous week, constructed
     *         based on the adjusted year and week values.
     */
    @Override
    public WeeklyConsumption previous() {
        int w = week - 1;
        int y = getYear();
        if(w == 0) {
            --y;
            w = 52;
        }
        return get(WeeklyConsumption.class, "Year=" + y + " AND Week=" + w + cond());
    }

    /**
     * Navigates to the next weekly consumption record based on the current week and year.
     * If the current week is the last week of the year (week 52 or 53), this method rolls over
     * to week 1 of the next year.
     *
     * @return the next WeeklyConsumption object corresponding to the subsequent week
     *         and possibly the next year if the week exceeds 52 or 53.
     */
    @Override
    public WeeklyConsumption next() {
        int w = week + 1;
        int y = getYear();
        if(w == 53) {
            ++y;
            w = 1;
        }
        return get(WeeklyConsumption.class, "Year=" + y + " AND Week=" + w + cond());
    }
}
