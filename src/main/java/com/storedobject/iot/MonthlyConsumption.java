package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

/**
 * Represents a monthly consumption entity that extends the base Consumption class.
 * This class provides functionality to handle data specific to a single month's consumption,
 * including retrieving, navigating, and setting details about the monthly period.
 *
 * @author Syam
 */
public final class MonthlyConsumption extends Consumption<PeriodType> {

    private int month;

    /**
     * Default constructor for the MonthlyConsumption class.
     * Initializes a new instance of the MonthlyConsumption entity, which is used
     * to represent and manage the consumption details for a specific month.
     */
    public MonthlyConsumption() {
    }

    /**
     * Configures the columns for the data representation of the entity.
     * Adds a column labeled "Month" of type "int".
     *
     * @param columns the Columns object used to define and manage the data columns
     */
    public static void columns(Columns columns) {
        columns.add("Month", "int");
    }

    /**
     * Configures the indices for the MonthlyConsumption class.
     * This method adds a composite index consisting of the fields "Item", "Resource", "Year", and "Month".
     * The index is marked as unique.
     *
     * @param indices the Indices object to which the index configuration will be added
     */
    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Month", true);
    }

    /**
     * Sets the month value for the current instance.
     *
     * @param month the month value to set, represented as an integer (1 for January up to 12 for December)
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Retrieves the value of the month corresponding to this instance of MonthlyConsumption.
     *
     * @return the month as an integer, where 1 represents January and 12 represents December.
     */
    @Column(order = 300)
    public int getMonth() {
        return month;
    }

    /**
     * Retrieves the period associated with the monthly consumption.
     * The period is represented as an integer value corresponding to the month.
     *
     * @return the month of the period as an integer, where 1 represents January and 12 represents December.
     */
    @Override
    public int getPeriod() {
        return month;
    }

    /**
     * Retrieves the detailed representation of the consumption period in the format of
     * "Month Year".
     * <p></p>
     * The month is derived from the internal state of the class and represented as the
     * corresponding month name (e.g., "January", "February"). The year is retrieved
     * using the {@link #getYear()} method.
     *
     * @return a string representing the period detail, combining the name of the month and the year.
     */
    @Override
    public String getPeriodDetail() {
        return DateUtility.getMonthNames()[month - 1] + " " + getYear();
    }

    /**
     * Retrieves the MonthlyConsumption instance representing the previous month relative
     * to the current instance. If the current month is January, it shifts to December
     * of the previous year.
     *
     * @return the MonthlyConsumption instance for the previous month.
     */
    @Override
    public MonthlyConsumption previous() {
        int m = month - 1;
        int y = getYear();
        if (m < 1) {
            m = 12;
            y--;
        }
        return get(MonthlyConsumption.class, "Year=" + y + " AND Month=" + m  + cond());
    }

    /**
     * Retrieves the next {@code MonthlyConsumption} object, advancing to the subsequent month.
     * If the current month is December, the method transitions to January of the next year.
     *
     * @return the {@code MonthlyConsumption} object for the next month, based on the current month and year.
     */
    @Override
    public MonthlyConsumption next() {
        int m = month + 1;
        int y = getYear();
        if (m > 12) {
            m = 1;
            y++;
        }
        return get(MonthlyConsumption.class, "Year=" + y + " AND Month=" + m + cond());
    }

    @Override
    public PeriodType getPeriodType() {
        return PeriodType.MONTHLY;
    }
}
