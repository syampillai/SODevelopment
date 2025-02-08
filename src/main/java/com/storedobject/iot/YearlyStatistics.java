package com.storedobject.iot;

import com.storedobject.core.*;

/**
 * Represents statistics that are aggregated on a yearly basis.
 * Inherits functionality from the base Statistics class and adds methods
 * specific to yearly data representation.
 *
 * @author Syam
 */
public final class YearlyStatistics extends Statistics {

    /**
     * Initializes a new instance of the YearlyStatistics class.
     * This constructor sets up the necessary properties for
     * handling statistics aggregated on a yearly basis.
     */
    public YearlyStatistics() {
    }

    /**
     * Sets or processes the provided columns for a specific operation or configuration.
     *
     * @param columns the columns object that contains the data structure or metadata to be used
     */
    public static void columns(Columns columns) {
    }

    /**
     * Configures the provided Indices object by adding an index definition
     * with "Unit,Name,Year" as the index key and a flag indicating whether
     * the index is unique.
     *
     * @param indices the Indices object to which the index definition is added
     */
    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year", true);
    }

    /**
     * Retrieves the period value for this instance, which corresponds to the year.
     *
     * @return the year value as an integer.
     */
    @Override
    public int getPeriod() {
        return getYear();
    }

    /**
     * Generates a detailed string representation of the period for this instance.
     * Specifically, it retrieves the year and formats it like "Year 1998".
     *
     * @return a string representation of the period detail, formatted like "Year 1998".
     */
    @Override
    public String getPeriodDetail() {
        return "Year " + getYear();
    }

    /**
     * Retrieves the YearlyStatistics object for the previous year relative to the
     * current instance's year value. This method constructs a query condition to
     * fetch the previous year's statistics while including additional filtering
     * conditions based on the current instance's context.
     *
     * @return a YearlyStatistics object representing the statistics for the previous year,
     *         or null if no data exists for the calculated year with the specified conditions.
     */
    @Override
    public YearlyStatistics previous() {
        return get(YearlyStatistics.class, "Year=" + (getYear() - 1) + cond());
    }

    /**
     * Retrieves the next instance of YearlyStatistics corresponding to the subsequent year,
     * if the current year is less than the present year. Constructs a query condition
     * to fetch the relevant data.
     *
     * @return the next YearlyStatistics instance if available; otherwise, returns null
     *         if the current year is the same as or greater than the present year.
     */
    @Override
    public YearlyStatistics next() {
        if( getYear() < DateUtility.getYear()) {
            return get(YearlyStatistics.class, "Year=" + (getYear() + 1) + cond());
        }
        return null;
    }
}
