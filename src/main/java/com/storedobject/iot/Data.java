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
                + " AND CollectedAt BETWEEN " + (collectedAt - ts) + " AND " + (collectedAt + ts))) {
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
        Duration timeSpan = Duration.ofMillis((to - from) >> 1);
        Value v1 = Data.getValueAt(dataClass, unitId, variable, from, timeSpan);
        if(v1 == null) {
            return null;
        }
        Value v2 = Data.getValueAt(dataClass, unitId, variable, to, timeSpan);
        if(v2 == null) {
            return null;
        }
        return (v2.value - v1.value) * (to - from) / (v2.time - v1.time);
    }

    /**
     * Structure to hold time and value.
     * @param time Time.
     * @param value Value.
     * @author Syam
     */
    public record Value(long time, double value) {}
}