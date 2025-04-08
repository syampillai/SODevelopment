package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;

/**
 * The HourlyStatistics class extends the Statistics class and adds functionality specific to hourly data.
 * It represents statistical information associated with a particular hour of the year.
 * This class provides methods to set, retrieve, and manipulate the hour-related data and period-specific details.
 *
 * @author Syam
 */
public final class HourlyStatistics extends Statistics<PeriodType> {

    private int hour;

    /**
     * Constructs a new instance of HourlyStatistics.
     * This constructor initializes an object that represents
     * statistical data corresponding to a specific hour of the year.
     */
    public HourlyStatistics() {
    }

    /**
     * Configures the columns by adding a column with the name "Hour" and type "int".
     *
     * @param columns the Columns object used to define and store column information
     */
    public static void columns(Columns columns) {
        columns.add("Hour", "int");
    }

    /**
     * Configures the given Indices object by adding an index with the specified keys.
     *
     * @param indices the Indices object to which the index will be added
     */
    public static void indices(Indices indices) {
        indices.add("Unit,Name,Year,Hour", true);
    }

    /**
     * Sets the hour for the HourlyStatistics object.
     *
     * @param hour the hour to be set, typically represented as an integer ranging from 0 to 23,
     *             where 0 represents midnight and 23 represents 11 PM.
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Retrieves the hour associated with this HourlyStatistics instance.
     *
     * @return the hour value represented as an integer, corresponding to the specific hour of the year.
     */
    @Column(order = 300)
    public int getHour() {
        return hour;
    }

    /**
     * Retrieves the period associated with this instance, which corresponds to the hour value.
     *
     * @return the hour of the year as an integer, representing the period.
     */
    @Override
    public int getPeriod() {
        return hour;
    }

    /**
     * Retrieves the detailed representation of the period associated with this instance.
     * For the HourlyStatistics class, this method provides a formatted string representing the hour-specific period details.
     *
     * @return a string representing the formatted period details, including the hour component.
     */
    @Override
    public String getPeriodDetail() {
        return DateUtility.formatWithTimeHHMM(new Date(DateUtility.create(getYear(), 1, 1).getTime()
                + (hour - 1) * 3600000L));
    }

    /**
     * Retrieves the HourlyStatistics instance corresponding to the previous hour.
     * If the current hour is the first hour of the year (hour 0 of day 1), this method
     * calculates the statistics for the last hour of the previous year.
     *
     * @return the HourlyStatistics object representing the previous hour's statistics.
     */
    @Override
    public HourlyStatistics previous() {
        int h = getHour() - 1;
        int y = getYear();
        if(h < 0) {
            h = DateUtility.getHourOfYear(new Date(DateUtility.create(y, 1, 1).getTime() - 60000L));
            --y;
        }
        return get(HourlyStatistics.class, "Year=" + y + " AND Hour=" + h + cond());
    }

    /**
     * Retrieves the next HourlyStatistics instance based on the current hour and year.
     * The method increments the current hour, retrieves the corresponding data, and
     * if no data exists for the incremented hour within the same year, it progresses
     * to the next year starting from hour 1. If no further data is available, it returns null.
     *
     * @return the next HourlyStatistics instance if available; otherwise, null.
     */
    @Override
    public HourlyStatistics next() {
        int h = getHour() + 1;
        int y = getYear();
        HourlyStatistics hs = get(HourlyStatistics.class, "Year=" + y + " AND Hour=" + h + cond());
        if(hs != null) {
            return hs;
        }
        if(h <= 8748) {
            return null;
        }
        ++y;
        return get(HourlyStatistics.class, "Year=" + y + " AND Hour=1" + cond());
    }

    /**
     * Retrieves the type of period for the current statistics instance.
     * For the HourlyStatistics class, this method always returns PeriodType.HOURLY,
     * indicating hourly granularity for statistical data.
     *
     * @return the period type, which is always PeriodType.HOURLY for this implementation.
     */
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.HOURLY;
    }
}
