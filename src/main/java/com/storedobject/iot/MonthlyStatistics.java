package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

/**
 * Represents a statistical record for a specific month. This class extends the
 * base {@code Statistics} class and provides additional functionality to
 * handle and retrieve details specific to monthly data.
 * <p>
 * This class includes methods to define table columns and indices for database
 * operations, along with getters and setters to manage and retrieve the month
 * corresponding to the statistical data.
 * </p>
 *
 * @author Syam
 */
public final class MonthlyStatistics extends Statistics {

    private int month;

    /**
     * Constructs a new instance of {@code MonthlyStatistics}.
     * <p></p>
     * This default constructor initializes the {@code MonthlyStatistics} object,
     * which represents statistical data for a specific month. The instance can
     * be further configured by setting the month and other properties as required.
     */
    public MonthlyStatistics() {
    }

    /**
     * Defines a table column with the name "Month" and the data type "int".
     *
     * @param columns an instance of the Columns class used to specify column definitions
     */
    public static void columns(Columns columns) {
        columns.add("Month", "int");
    }

    /**
     * Adds database indices based on the specified column names and index type.
     * This ensures that a unique compound index is created using the provided
     * column names to optimize query performance.
     *
     * @param indices The {@code Indices} object used to define new database indices.
     */
    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Month", true);
    }

    /**
     * Sets the month value for this statistical record.
     *
     * @param month the month to set, represented as an integer (e.g., 1 for January, 12 for December)
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Retrieves the month associated with this statistical record.
     *
     * @return the month as an integer, where 1 represents January and 12 represents December.
     */
    @Column(order = 300)
    public int getMonth() {
        return month;
    }

    /**
     * Retrieves the period associated with the monthly statistics.
     * This method overrides the base class implementation to return the
     * specific month value represented as an integer.
     *
     * @return the period as an integer, where the period corresponds to the
     *         month represented in the statistical record (e.g., 1 for January,
     *         2 for February, etc.).
     */
    @Override
    public int getPeriod() {
        return month;
    }

    /**
     * Retrieves a detailed representation of the period in the format "Month Year".
     * The method combines the name of the month (derived from a utility class)
     * and the year associated with the instance.
     *
     * @return a string representing the period detail in the format "Month Year".
     */
    @Override
    public String getPeriodDetail() {
        return DateUtility.getMonthNames()[month - 1] + " " + getYear();
    }

    /**
     * Retrieves the {@code MonthlyStatistics} instance for the previous month.
     * If the current month is January, it will transition to December of the previous year.
     *
     * @return a {@code MonthlyStatistics} instance representing the statistics of the previous month.
     */
    @Override
    public MonthlyStatistics previous() {
        int m = month - 1;
        int y = getYear();
        if (m < 1) {
            m = 12;
            y--;
        }
        return get(MonthlyStatistics.class, "Year=" + y + " AND Month=" + m  + cond());
    }

    /**
     * Retrieves the next instance of {@code MonthlyStatistics}, representing the subsequent month
     * based on the current instance's month and year. If the month is December (12), the year is
     * incremented, and the month is reset to January (1).
     *
     * @return the next {@code MonthlyStatistics} object corresponding to the following month.
     */
    @Override
    public MonthlyStatistics next() {
        int m = month + 1;
        int y = getYear();
        if (m > 12) {
            m = 1;
            y++;
        }
        return get(MonthlyStatistics.class, "Year=" + y + " AND Month=" + m + cond());
    }
}
