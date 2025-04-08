package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

/**
 * The WeeklyStatistics class extends the Statistics class and provides
 * functionality for managing and retrieving weekly statistical data.
 * This class includes methods to define database columns and indices,
 * as well as methods to get and set the week value of the statistics.
 * Additionally, it overrides methods to provide details about the
 * specific period (week) being represented.
 *
 * @author Syam
 */
public final class WeeklyStatistics extends Statistics<PeriodType> {

    private int week;

    /**
     * Default constructor for the WeeklyStatistics class.
     * Initializes an instance of the WeeklyStatistics class,
     * which is a specialized subclass of Statistics designed
     * for handling statistical data on a weekly basis.
     */
    public WeeklyStatistics() {
    }

    /**
     * Adds a column definition for the "Week" attribute of type "int"
     * to the provided Columns object.
     *
     * @param columns the Columns object to which the "Week" column should be added
     */
    public static void columns(Columns columns) {
        columns.add("Week", "int");
    }

    /**
     * Configures the database indices for the WeeklyStatistics table.
     *
     * @param indices The {@code Indices} object used to define the indices for the table.
     *                This method adds an index on the columns "Unit, Name, Year, Week"
     *                with a specification to enforce uniqueness.
     */
    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Week", true);
    }

    /**
     * Sets the week value for the statistics.
     *
     * @param week the week number to set, representing the statistical period
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * Retrieves the week value for the current instance of WeeklyStatistics.
     *
     * @return the week value as an integer.
     */
    @Column(order = 300)
    public int getWeek() {
        return week;
    }

    /**
     * Gets the period represented by the current instance of WeeklyStatistics.
     * In this implementation, the period corresponds to the specific week value.
     *
     * @return the week value representing the current period.
     */
    @Override
    public int getPeriod() {
        return week;
    }

    /**
     * Retrieves a detailed representation of the specific period (week)
     * for this instance, based on the year and week values.
     *
     * @return a string representing the detailed period, formatted as defined
     *         by the DailyConsumption.periodDetail method.
     */
    @Override
    public String getPeriodDetail() {
        return DailyConsumption.periodDetail(getYear(), week);
    }

    /**
     * Retrieves the WeeklyStatistics for the previous week relative to the current instance.
     * If the current week is the first week of the year (week 1), this method adjusts
     * the year and week values to point to the last week (week 52) of the previous year.
     *
     * @return the WeeklyStatistics object representing the previous week's data.
     */
    @Override
    public WeeklyStatistics previous() {
        int w = week - 1;
        int y = getYear();
        if(w == 0) {
            --y;
            w = 52;
        }
        return get(WeeklyStatistics.class, "Year=" + y + " AND Week=" + w + cond());
    }

    /**
     * Retrieves the next week's statistics based on the current instance.
     * If the current week is the last week of the year (week 53), the method
     * transitions to the first week of the next year.
     *
     * @return an instance of {@code WeeklyStatistics} representing the statistics
     *         for the next week.
     */
    @Override
    public WeeklyStatistics next() {
        int w = week + 1;
        int y = getYear();
        if(w == 53) {
            ++y;
            w = 1;
        }
        return get(WeeklyStatistics.class, "Year=" + y + " AND Week=" + w + cond());
    }

    /**
     * Retrieves the type of period associated with this instance of WeeklyStatistics.
     * Specifically, this method indicates that the statistical data is organized
     * and represented on a weekly basis.
     *
     * @return the PeriodType for this instance, which is {@code PeriodType.WEEKLY}.
     */
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.WEEKLY;
    }
}
