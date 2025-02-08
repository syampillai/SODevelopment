package com.storedobject.iot;

import com.storedobject.core.*;

/**
 * Represents the yearly resource consumption details.
 * This class extends the base Consumption class to provide
 * functionality specific to yearly periods.
 * <br/>
 * The class includes methods to handle column definitions,
 * index creation, and to get information about the current,
 * previous, and next periods.
 *
 * @author Syam
 */
public final class YearlyConsumption extends Consumption {

    /**
     * Constructs a new instance of the YearlyConsumption class.
     * This constructor initializes the instance and prepares it for use
     * in representing and managing yearly resource consumption details.
     */
    public YearlyConsumption() {
    }

    /**
     * Configures the column definitions for the yearly consumption data.
     *
     * @param columns an instance of the Columns class used to define the structure
     *                and attributes of the columns associated with yearly resource consumption.
     */
    public static void columns(Columns columns) {
    }

    /**
     * Configures indices for the yearly consumption data.
     *
     * @param indices the indices object used to define and manage database indices
     */
    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year", true);
    }

    /**
     * Retrieves the period for the yearly consumption, represented as an integer year value.
     *
     * @return the year associated with the consumption record.
     */
    @Override
    public int getPeriod() {
        return getYear();
    }

    /**
     * Provides a detailed description of the period by returning
     * the year associated with this consumption record, prefixed by "Year ".
     * <br/>
     * Example output: "Year 2023".
     *
     * @return a string representing the period detail, which includes the year prefixed by "Year ".
     */
    @Override
    public String getPeriodDetail() {
        return "Year " + getYear();
    }

    /**
     * Retrieves the YearlyConsumption instance for the previous year.
     * It calculates the year by subtracting 1 from the current year
     * and constructs a query condition using the item and resource identifiers.
     *
     * @return the YearlyConsumption instance for the previous year if available.
     */
    @Override
    public YearlyConsumption previous() {
        return get(YearlyConsumption.class, "Year=" + (getYear() - 1) + cond());
    }

    /**
     * Retrieves the next yearly consumption record if it exists.
     * The method compares the current year of the consumption record
     * with the system's current year and, if applicable, fetches the
     * record for the following year based on the condition constructed.
     *
     * @return the next YearlyConsumption instance if the current year is less than the system's current year,
     *         or null if there is no next record.
     */
    @Override
    public YearlyConsumption next() {
        if( getYear() < DateUtility.getYear()) {
            return get(YearlyConsumption.class, "Year=" + (getYear() + 1) + cond());
        }
        return null;
    }
}
