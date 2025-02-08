package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

/**
 * The DailyStatistics class represents daily statistical data and provides methods
 * to define column structure, indices, and retrieve specific daily statistics details.
 * This class extends the Statistics class and focuses on representing statistics
 * for a particular day within a given year.
 * <p>
 * This class is immutable and final, ensuring that its behavior does not change via inheritance.
 * </p>
 *
 * @author Syam
 */
public final class DailyStatistics extends Statistics {

    private int day;

    /**
     * Default constructor for the DailyStatistics class.
     * <p></p>
     * Initializes a new instance of the DailyStatistics class, which represents
     * daily statistical data for a specific day in a given year. The object created
     * is immutable and focuses on handling statistics at the daily level.
     */
    public DailyStatistics() {
    }

    /**
     * Defines the column structure for daily statistics.
     *
     * @param columns the Columns instance where the column definitions are added.
     *                This includes column names and their corresponding data types.
     */
    public static void columns(Columns columns) {
        columns.add("Day", "int");
    }

    /**
     * Configures the indices structure for the DailyStatistics class by adding a
     * compound index on the columns "Unit", "Name", "Year", and "Day".
     *
     * @param indices the Indices instance where the compound index will be added
     */
    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Day", true);
    }

    /**
     * Sets the day value for the daily statistics.
     *
     * @param day the day of the year to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Retrieves the day of the month represented by this instance.
     *
     * @return the day as an integer value, typically ranging from 1 to 31 depending on the month.
     */
    @Column(order = 300)
    public int getDay() {
        return day;
    }

    /**
     * Retrieves the period associated with this instance, represented by the day of the year.
     *
     * @return the day of the year as an integer.
     */
    @Override
    public int getPeriod() {
        return day;
    }

    /**
     * Retrieves detailed period information for a specific day within a given year.
     * This method utilizes the DailyConsumption's periodDetail logic to compute the
     * period-specific detail string, such as a formatted date.
     *
     * @return a string representing the detailed period information for the day within the year.
     */
    @Override
    public String getPeriodDetail() {
        return DailyConsumption.periodDetail(getYear(), day);
    }

    /**
     * Retrieves the previous day's statistics relative to the current instance.
     * If the current day is the first day of the year, it calculates the statistics
     * for the last day of the previous year.
     *
     * @return A {@code DailyStatistics} object representing the statistics for the
     *         previous day.
     */
    @Override
    public DailyStatistics previous() {
        int d = day - 1;
        int y = getYear();
        if(d < 1) {
            d = DateUtility.getDayOfYear(DateUtility.create(y, 12, 31));
            --y;
        }
        return get(DailyStatistics.class, "Year=" + y + " AND Day=" + d + cond());
    }

    /**
     * Retrieves the next DailyStatistics instance for the subsequent day.
     * If the next day's data exists for the current year, it is returned.
     * If not, and the current day is the last day of the year, it attempts
     * to fetch the first day's data for the next year.
     *
     * @return the next DailyStatistics instance if available, or null if no data exists
     *         for the next day in the same year and it is not the end of the year.
     */
    @Override
    public DailyStatistics next() {
        int d = day + 1;
        int y = getYear();
        DailyStatistics ds = get(DailyStatistics.class, "Year=" + y + " AND Day=" + d + cond());
        if(ds != null) {
            return ds;
        }
        if(d < 365) {
            return null;
        }
        ++y;
        return get(DailyStatistics.class, "Year=" + y + " AND Day=1" + cond());
    }
}
