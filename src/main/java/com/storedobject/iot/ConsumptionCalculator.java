package com.storedobject.iot;

import com.storedobject.core.Id;

/**
 * A functional interface for calculating consumption values over a specified time period.
 * The calculation can be customized using various factory methods.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ConsumptionCalculator {

    /**
     * Computes a value based on the specified resource, unit identifier, and time range.
     * The calculation logic can be implemented using the specific details provided.
     *
     * @param resource the resource identifier used in the computation
     * @param unitId the unique identifier for the unit on which computation is performed
     * @param from the starting timestamp for the computation period
     * @param to the ending timestamp for the computation period
     * @return a Double representing the calculated value, or null if the computation fails or is not applicable
     */
    Double compute(int resource, Id unitId, long from, long to);

    /**
     * Creates a ConsumptionCalculator instance to compute the difference in values
     * of a specific variable for a given data class between the specified time range.
     *
     * @param dataClass the class type of the data from which values are retrieved
     * @param variable the name of the variable to compute the difference for
     * @return a ConsumptionCalculator instance configured for the specified data class and variable
     */
    static ConsumptionCalculator create(Class<? extends Data> dataClass, String variable) {
        return (resource, unitId, from, to) -> Data.getValueDifference(dataClass, unitId, variable, from, to);
    }

    /**
     * Creates a ConsumptionCalculator instance with a specified multiplier to adjust the computed value.
     * The created calculator computes the difference of a given variable for the specified data class,
     * then multiplies the result by the provided multiplier.
     *
     * @param dataClass the class type extending Data, representing the data source for the computation
     * @param variable the name of the variable to be used in the computation
     * @param multiplier the factor by which the computed difference is multiplied
     * @return a ConsumptionCalculator instance configured with the specified parameters
     */
    static ConsumptionCalculator create(Class<? extends Data> dataClass, String variable, double multiplier) {
        return (resource, unitId, from, to) -> {
            Double c = Data.getValueDifference(dataClass, unitId, variable, from, to);
            return c == null ? null : c * multiplier;
        };
    }

    /**
     * Creates a ConsumptionCalculator instance to compute the difference in values of a specific
     * variable for a given data class between the specified time range. The calculation is adjusted
     * by a multiplier and handles potential negative values by applying an optional meter reset.
     *
     * @param dataClass the class type extending Data, representing the data source for the computation
     * @param variable the name of the variable to compute the difference for
     * @param multiplier the factor by which the computed difference is multiplied
     * @param meterReset the value used to reset a negative difference to a positive range
     * @return a ConsumptionCalculator instance configured for the specified data class, variable,
     *         multiplier, and meter reset logic
     */
    static ConsumptionCalculator create(Class<? extends Data> dataClass, String variable, double multiplier, double meterReset) {
        return (resource, unitId, from, to) -> {
            Double c = Data.getValueIncrease(dataClass, unitId, variable, from, to, meterReset);
            return c == null ? null : c * multiplier;
        };
    }

    /**
     * Creates a ConsumptionCalculator instance that computes consumption based on state changes
     * of the specified variable within the provided data class. The calculation can be customized
     * using the boolean condition and multiplier.
     *
     * @param dataClass the class type of the data being used for computation
     * @param variable the name of the variable used for state change computation
     * @param toTrue the condition for computing state changes; if true, only changes to "true"
     *               are considered
     * @param multiplier the value by which the computed state changes will be multiplied
     * @return a ConsumptionCalculator instance that computes values based on state changes in the data
     */
    static ConsumptionCalculator create(Class<? extends Data> dataClass, String variable, boolean toTrue, double multiplier) {
        return (resource, unitId, from, to) -> {
            Integer c = Data.getStateChanged(dataClass, unitId, variable, from, to, toTrue);
            return c == null ? null : c * multiplier;
        };
    }
}
