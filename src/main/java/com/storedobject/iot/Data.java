package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;

/**
 * Base class that represents data values that belong to a {@link Unit}.
 * It generally contains only "boolean" and "double" values.
 * When transacted singly, it doesn't maintain any history or audit trail.
 * This is not used for any direct data entry and is mostly created or updated by
 * IoT data capture processes. The {@link #directUpdate(TransactionManager)} method can be used to directly update
 * the data in the case of existing instances without even creating new transactions.
 *
 * @author Syam
 */
public abstract class Data extends StoredObject implements DBTransaction.NoHistory {

    private long collectedAt = -5364662400000L;
    private Id unitId;

    /**
     * Attributes.
     *
     * @param columns Attributes.
     */
    public static void columns(Columns columns) {
        columns.add("CollectedAt", "bigint");
        columns.add("Unit", "id");
    }

    /**
     * Indices.
     *
     * @param indices Index definitions.
     */
    public static void indices(Indices indices) {
        indices.add("CollectedAt", false);
    }

    /**
     * Mandatory attribute - data collection time (GMT)
     *
     * @param collectedAt Data collection time.
     */
    public final void setCollectedAt(long collectedAt) {
        this.collectedAt = collectedAt;
    }

    /**
     * Get the time at which this data is collected (GMT).
     *
     * @return Data collection time.
     */
    public final long getCollectedAt() {
        return collectedAt;
    }

    /**
     * Get the time at which this data is collected (GMT) as a timestamp.
     *
     * @return Data collection time as a timestamp.
     */
    public final Timestamp getTimestamp() {
        return new Timestamp(collectedAt);
    }

    /**
     * Set the unit {@link Id} of this IoT object. Typically, every IoT object instance belongs to some unit
     * (plant or equipment being monitored). There must be a "data class" for every such unit and the {@link Id} of
     * that unit must be set here. An IoT object itself may be independent of any unit (for example,
     * generic values like temperature/humidity of an environment etc.) and in such cases, a virtual unit (for example,
     * Environment) must be defined.
     *
     * @param unitId Id of the unit.
     */
    public void setUnit(Id unitId) {
        if (!loading() && !Id.equals(this.unitId, unitId)) {
            throw new Set_Not_Allowed("Unit");
        }
        this.unitId = unitId;
    }

    /**
     * Set the unit {@link Id} of this IoT object. Typically, every IoT object instance belongs to some unit
     * (plant or equipment being monitored). There must be a "data class" for every such unit and the {@link Id} of
     * that unit must be set here. An IoT object itself may be independent of any unit (for example,
     * generic values like temperature/humidity of an environment etc.) and in such cases, a virtual unit (for example,
     * Environment) must be defined.
     *
     * @param idValue Id of the unit.
     */
    public final void setUnit(BigDecimal idValue) {
        setUnit(new Id(idValue));
    }

    /**
     * Set the unit of this IoT object. Typically, every IoT object instance belongs to some unit
     * (plant or equipment being monitored). There must be a "data class" for every such unit and
     * that unit must be set here. An IoT object itself may be independent of any unit (for example,
     * generic values like temperature/humidity of an environment etc.) and in such cases, a virtual unit (for example,
     * Environment) must be defined.
     *
     * @param unit Unit.
     */
    public final void setUnit(Unit unit) {
        setUnit(unit == null ? null : unit.getId());
    }

    /**
     * Get the {@link Id} of the unit this IoT object belongs to.
     *
     * @return Id of the unit.
     */
    @SetNotAllowed
    @Column(order = 100, style = "(any)")
    public final Id getUnitId() {
        return unitId;
    }

    /**
     * Get the unit to which this data belong to. See {@link #setUnit(Unit)}.
     *
     * @return Unit.
     */
    public final Unit getUnit() {
        return getRelated(Unit.class, unitId, true);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        if(collectedAt < 0) {
            throw new Invalid_Value("Data Collection Time");
        }
        unitId = tm.checkTypeAny(this, unitId, Unit.class, false);
    }

    /**
     * Get the latest data record collected.
     *
     * @param objectClass IOT object class.
     * @param <IOT> Type of object class.
     * @param unitId {@link Id} of the unit (could be null).
     * @return Latest data record or null if not available.
     */
    public static <IOT extends Data> IOT getLatest(Class<IOT> objectClass, Id unitId) {
        Query q = query(objectClass, "Max(Id)", Id.isNull(unitId) ? null : ("Unit=" + unitId));
        BigDecimal id = null;
        try {
            ResultSet rs = q.getResultSet();
            if(!rs.wasNull()) {
                id = rs.getBigDecimal(1);
            }
        } catch(SQLException throwable) {
            return null;
        } finally {
            q.close();
        }
        return id == null ? null : get(objectClass, new Id(id));
    }

    /**
     * Get the Unit's class for this data instance.
     *
     * @return Unit class.
     */
    public Class<? extends Unit> getUnitClass() {
        return getUnitClass(getClass());
    }

    /**
     * Get the Unit's class for the given data class.
     *
     * @param dataClass Data class.
     * @return Unit class.
     */
    public static Class<? extends Unit> getUnitClass(Class<? extends Data> dataClass) {
        String ucName = dataClass.getName();
        ucName = ucName.substring(0, ucName.indexOf("Data"));
        try {
            Class<?> c = JavaClassLoader.getLogic(ucName);
            if(Unit.class.isAssignableFrom(c)) {
                //noinspection unchecked
                return (Class<? extends Unit>) c;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    /**
     * Get the data value collected at the given instance.
     *
     * @param dataClass Data class.
     * @param unitId Unit Id.
     * @param collectedAt Time of collection.
     * @param timeSpan Time span. (Collected at +/- this time span is searched).
     * @param <D> Data type.
     * @return Data value if found.
     */
    public static <D extends Data> Value getValueAt(Class<D> dataClass, Id unitId, String variable, long collectedAt,
                                                     Duration timeSpan) {
        Double data = null;
        long ts = timeSpan == null ? 0 : timeSpan.toMillis(), at, pat = 0;
        try (Query list = query(dataClass, "CollectedAt," + variable, "Unit=" + unitId
                + " AND CollectedAt BETWEEN " + (collectedAt - ts) + " AND " + (collectedAt + ts), "CollectedAt")) {
            for(ResultSet d: list) {
                try {
                    at = d.getLong(1);
                    if (at == collectedAt) {
                        return new Value(collectedAt, d.getDouble(2));
                    }
                    if (data == null || Math.abs(pat - collectedAt) < Math.abs(at - collectedAt)) {
                        pat = at;
                        data = d.getDouble(2);
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return data == null ? null : new Value(pat, data);
    }

    /**
     * Get the difference in data value collected at 2 different time instances.
     *
     * @param dataClass Data class.
     * @param unitId Unit Id.
     * @param from Time from.
     * @param to Time to.
     * @param <D> Data type.
     * @return Data value if found.
     */
    public static <D extends Data> Double getValueDifference(Class<D> dataClass, Id unitId, String variable,
                                                             long from, long to) {
        Value[] v = values(dataClass, unitId, variable, from, to);
        if(v == null) {
            return null;
        }
        Value v1 = v[0], v2 = v[1];
        return (v2.value - v1.value) * (to - from) / (v2.time - v1.time);
    }

    /**
     * Get the increase in data value collected at 2 different time instances.
     *
     * @param dataClass Data class.
     * @param unitId Unit Id.
     * @param from Time from.
     * @param to Time to.
     * @param meterReset Value at which the Meter resets.
     * @param <D> Data type.
     * @return Data value if found.
     */
    public static <D extends Data> Double getValueIncrease(Class<D> dataClass, Id unitId, String variable,
                                                             long from, long to, double meterReset) {
        Value[] v = values(dataClass, unitId, variable, from, to);
        if(v == null) {
            return null;
        }
        Value v1 = v[0], v2 = v[1];
        if(v2.value < v1.value) {
            v2 = new Value(v2.time, v2.value + meterReset);
        }
        return (v2.value - v1.value) * (to - from) / (v2.time - v1.time);
    }

    private static <D extends Data> Value[] values(Class<D> dataClass, Id unitId, String variable,
                           long from, long to) {
        Duration timeSpan = Duration.ofMillis((to - from) >> 1);
        Value v1 = Data.getValueAt(dataClass, unitId, variable, from, timeSpan);
        if(v1 == null) {
            return null;
        }
        Value v2 = Data.getValueAt(dataClass, unitId, variable, to, timeSpan);
        if(v2 == null || v1.time == v2.time) {
            return null;
        }
        return new Value[] { v1, v2 };
    }

    /**
     * Calculates the difference in the values of a specified variable for a given data class
     * and unit within a specified time period.
     *
     * @param <D>         The type of the data class, which must extend the {@code Data} class.
     * @param dataClass   The data class containing the variable whose value difference is to be calculated.
     * @param unitId      The identifier of the unit for which the value difference is being calculated.
     * @param variable    The name of the variable whose value difference is to be calculated.
     * @param periodType  The type of the time period (e.g., HOURLY, DAILY) for which the value difference is calculated.
     * @param periodCount The number of periods to look back from the current time.
     * @return The calculated value difference of the variable over the specified period, or {@code null} if the calculation fails.
     */
    public static <D extends Data> Double getValueDifference(Class<D> dataClass, Id unitId, String variable,
                                                             PeriodType periodType, int periodCount) {
        long to = System.currentTimeMillis();
        long from = periodType.time(to, -periodCount);
        return getValueDifference(dataClass, unitId, variable, from, to);
    }

    /**
     * Calculates the increase in the value of a specified variable for a given data class
     * and unit within a specified time period.
     *
     * @param <D>         The type of the data class, which must extend the {@code Data} class.
     * @param dataClass   The data class containing the variable whose value difference is to be calculated.
     * @param unitId      The identifier of the unit for which the value difference is being calculated.
     * @param variable    The name of the variable whose value difference is to be calculated.
     * @param periodType  The type of the time period (e.g., HOURLY, DAILY) for which the value difference is calculated.
     * @param periodCount The number of periods to look back from the current time.
     * @param meterReset Value at which the Meter resets.
     * @return The calculated value difference of the variable over the specified period, or {@code null} if the calculation fails.
     */
    public static <D extends Data> Double getValueIncrease(Class<D> dataClass, Id unitId, String variable,
                                                             PeriodType periodType, int periodCount, double meterReset) {
        long to = System.currentTimeMillis();
        long from = periodType.time(to, -periodCount);
        return getValueIncrease(dataClass, unitId, variable, from, to, meterReset);
    }

    /**
     * Determines the number of state changes for a specified variable of a given data class
     * for a specific unit within a specified time range. A state change is counted when
     * the variable transitions to the specified target state.
     *
     * @param <D>       Type of the data class that extends {@code Data}.
     * @param dataClass The data class in which the state changes are being queried.
     * @param unitId    The identifier of the unit for which the state changes are being tracked.
     * @param variable  The variable name to monitor for state changes.
     * @param from      The starting time (exclusive) of the time range in milliseconds.
     * @param to        The ending time (inclusive) of the time range in milliseconds.
     * @param toTrue    Target state to track changes towards. If {@code true}, counts transitions
     *                  to {@code true}. If {@code false}, counts transitions to {@code false}.
     * @return The number of state changes to the target state between the specified time range,
     *         or {@code null} if an error occurs during computation.
     */
    public static <D extends Data> Integer getStateChanged(Class<D> dataClass, Id unitId, String variable,
                                                           long from, long to, boolean toTrue) {
        int changed = -1;
        boolean previous = toTrue, current;
        // Note: If the state was changed exactly at "from", it will be ignored because it was counted previously.
        try (Query list = query(dataClass, variable, "Unit=" + unitId
                + " AND CollectedAt BETWEEN " + (from + 1) + " AND " + to, "CollectedAt")) {
            for(ResultSet d: list) {
                try {
                    current = d.getBoolean(1);
                    if(current != previous && current == toTrue) {
                        changed++;
                    }
                    previous = current;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        if(changed < 0) {
            return null;
        }
        return ++changed;
    }

    /**
     * Determines the state changes of a specific variable within a given period of time.
     *
     * @param dataClass the class type of the data
     * @param unitId the unique identifier of the unit
     * @param variable the name of the variable to monitor for state changes
     * @param periodType the type of time period to consider (e.g., hours, days)
     * @param periodCount the number of periods to look back from the current time
     * @param toTrue boolean indicating if the state change to be counted is towards true
     * @return the count of state changes for the specified variable within the given period
     */
    public static <D extends Data> Integer getStateChanged(Class<D> dataClass, Id unitId, String variable,
                                                           PeriodType periodType, int periodCount, boolean toTrue) {
        long to = System.currentTimeMillis();
        long from = periodType.time(to, -periodCount);
        return getStateChanged(dataClass, unitId, variable, from, to, toTrue);
    }

    /**
     * Computes the number of occurrences of a specific variable with a specified value
     * for a given data class and unit within a defined time range.
     *
     * @param <D>       The type of data class, which must extend the {@code Data} class.
     * @param dataClass The data class in which the count is being queried.
     * @param unitId    The identifier of the unit whose data is being queried.
     * @param variable  The name of the variable to query for.
     * @param value     The value of the variable to count occurrences of.
     * @param from      The start time (inclusive) of the time range in milliseconds.
     * @param to        The end time (inclusive) of the time range in milliseconds.
     * @return The number of occurrences of the specified value for the variable
     *         within the specified time range.
     */
    public static <D extends Data> int getValueCount(Class<D> dataClass, Id unitId, String variable, int value,
                                                     long from, long to) {
        return count(dataClass, "Unit=" + unitId + " AND CollectedAt BETWEEN " + from + " AND " + to + " AND "
                + variable + "=" + value);
    }

    /**
     * Calculates the number of occurrences of a specific variable with a given value
     * for a specified data class and unit within a defined time period.
     *
     * @param <D>         The type of the data class, which must extend the {@code Data} class.
     * @param dataClass   The data class in which the count is being queried.
     * @param unitId      The identifier of the unit whose data is being queried.
     * @param variable    The name of the variable to query for.
     * @param value       The value of the variable to count occurrences of.
     * @param periodType  The type of the time period (e.g., HOURLY, DAILY) for the query.
     * @param periodCount The number*/
    public static <D extends Data> int getValueCount(Class<D> dataClass, Id unitId, String variable, int value,
                                                     PeriodType periodType, int periodCount) {
        long to = System.currentTimeMillis();
        long from = periodType.time(to, -periodCount);
        return getValueCount(dataClass, unitId, variable, value, from, to);
    }

    /**
     * Structure to hold time and value.
     * @param time Time.
     * @param value Value.
     * @author Syam
     */
    public record Value(long time, double value) {}
}
