package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

/**
 * Represents statistical data pertaining to units and periods.
 * This class provides methods for calculating and updating statistics,
 * including minimum, maximum, mean, standard deviation, and count for a dataset.
 * It is designed to handle various periods such as hourly, daily, weekly, monthly, and yearly.
 * <p>
 * Concrete subclasses must define the specific period-related methods and data fields.
 * </p>
 *
 * @author Syam
 */
public abstract class Statistics extends StoredObject implements DBTransaction.NoHistory {

    private String name;
    private Id unitId;
    private int year;
    private int count = 0;
    double min = Double.MAX_VALUE, max = Double.MIN_VALUE, mean, sD;

    /**
     * Default constructor for the Statistics class.
     * Initializes a Statistics object with default values. This serves as the
     * default instantiation mechanism for creating a basic Statistics instance
     * without setting specific attributes during initialization.
     */
    public Statistics() {
    }

    /**
     * Configures the column definitions for a given Columns object. This method
     * defines various statistical columns with their respective names and data types.
     *
     * @param columns The Columns object to which the column definitions are added. It must
     *                not be null and should be capable of accepting column definitions.
     */
    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Unit", "id");
        columns.add("Year", "int");
        columns.add("Count", "int");
        columns.add("Min", "double precision");
        columns.add("Max", "double precision");
        columns.add("Mean", "double precision");
        columns.add("SD", "double precision");
    }

    /**
     * Sets the unit associated with the Statistics object.
     * This method modifies the unitId field if the object is not in a loading state
     * and the provided unitId differs from the current unitId. If these conditions are not met,
     * it throws a Set_Not_Allowed exception.
     *
     * @param unitId The unique identifier of the unit to associate with the Statistics object.
     *               This identifier must be valid and not violate the conditions for modification.
     * @throws Set_Not_Allowed if the object is not in a loading state or the provided unitId
     *                          tries to alter the current unitId while modification is restricted.
     */
    public void setUnit(Id unitId) {
        if (!loading() && !Id.equals(this.getUnitId(), unitId)) {
            throw new Set_Not_Allowed("Unit");
        }
        this.unitId = unitId;
    }

    /**
     * Sets the unit using the provided BigDecimal value.
     * The BigDecimal value is used to create an {@code Id} object,
     * which is then set as the unit.
     *
     * @param idValue The BigDecimal value representing the ID of the unit.
     */
    public void setUnit(BigDecimal idValue) {
        setUnit(new Id(idValue));
    }

    /**
     * Assigns the given Unit to this Statistics instance by extracting its identifier.
     *
     * @param unit The Unit object to set for this Statistics instance. If null, the unit will be set to null.
     */
    public void setUnit(Unit unit) {
        setUnit(unit == null ? null : unit.getId());
    }

    /**
     * Retrieves the unique identifier for the unit associated with the statistics.
     *
     * @return the unit's unique identifier as an {@code Id}.
     */
    @SetNotAllowed
    @Column(style = "(any)", order = 50)
    public Id getUnitId() {
        return unitId;
    }

    /**
     * Retrieves the unit associated with this instance based on the {@code unitId}.
     *
     * @return The associated {@code Unit} object, or {@code null} if no unit is found.
     */
    public Unit getUnit() {
        return getRelated(Unit.class, unitId, true);
    }

    /**
     * Sets the name of the statistics instance.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name associated with this instance.
     *
     * @return The name as a String.
     */
    @Column(order = 100)
    public String getName() {
        return name;
    }

    /**
     * Sets the year for this statistics object.
     *
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Retrieves the year associated with this instance.
     *
     * @return the year value as an integer.
     */
    @Column(order = 200)
    public int getYear() {
        return year;
    }

    /**
     * Sets the count value for the Statistics object.
     *
     * @param count The count value to be set.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Retrieves the count associated with the statistics.
     *
     * @return the count value as an integer
     */
    @Column(order = 3000)
    public int getCount() {
        return count;
    }

    /**
     * Sets the minimum value for the statistical data field.
     *
     * @param min The minimum value to be set.
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Retrieves the minimum value from the statistical data.
     *
     * @return the minimum value as a double.
     */
    @Column(order = 3100)
    public double getMin() {
        return min;
    }

    /**
     * Sets the maximum value for this Statistics object.
     *
     * @param max The maximum value to be set.
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Retrieves the maximum value among the recorded statistics.
     *
     * @return the maximum value as a double
     */
    @Column(order = 3200)
    public double getMax() {
        return max;
    }

    /**
     * Sets the mean value for this instance.
     *
     * @param mean The mean value to set.
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * Retrieves the mean value of the statistical data.
     *
     * @return The mean value as a double.
     */
    @Column(order = 3300)
    public double getMean() {
        return mean;
    }

    /**
     * Sets the standard deviation (SD) value for this instance.
     *
     * @param sd The standard deviation value to set.
     */
    public void setSD(double sd) {
        this.sD = sd;
    }

    /**
     * Retrieves the standard deviation (SD) for the associated statistical data.
     *
     * @return The standard deviation value as a double.
     */
    @Column(order = 3400, caption = "SD")
    public double getSD() {
        return sD;
    }

    /**
     * Validates the data of the current object.
     * <p></p>
     * This method ensures that the required fields have valid values and performs additional
     * checks using the provided transaction manager.
     *
     * @param tm The TransactionManager object used for data validation and type checking.
     * @throws Exception If any validation criteria are not met, such as missing or invalid
     *                   values, or if an error occurs during type checking.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        unitId = tm.checkTypeAny(this, unitId, AbstractUnit.class, false);
        super.validateData(tm);
    }

    /**
     * Retrieves the period name associated with the instance of the class.
     * If the instance belongs to the {@code DailyStatistics} class, the period name returned will be "Day".
     * For other subclasses derived from the {@code Statistics} class, this method infers the period name
     * based on the class name by trimming certain suffixes.
     *
     * @return A string representing the period name derived from the class type.
     */
    public String getPeriodName() {
        if(this instanceof DailyStatistics) {
            return "Day";
        }
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        return s.substring(0, s.indexOf("ly"));
    }

    /**
     * Retrieves the period value associated with the statistics object.
     *
     * @return the period value as an integer
     */
    public abstract int getPeriod();

    /**
     * Retrieves the detailed description of the period associated with this statistics object.
     *
     * @return A String representing the specific details of the period.
     */
    public abstract String getPeriodDetail();

    /**
     * Converts the statistics object into a formatted display string including its name,
     * unit, count, minimum value, maximum value, mean value, standard deviation, and year.
     * If not a yearly statistic, also includes period name and period value.
     *
     * @return A human-readable string format of the statistics object with detailed attributes.
     */
    @Override
    public String toDisplay() {
        return StringUtility.makeLabel(name) + " (" + getUnit().toDisplay() + ") Count = " + count
                + ", Min = " + min + ", Max = " + max + ", Mean = " + mean + ", SD = " + sD
                + " (Year: " + year
                + (this instanceof YearlyStatistics ? "" : (", " + getPeriodName() + ": " + getPeriod())) + ")";
    }

    /**
     * Calculates a statistical value based on the count and other statistical measures.
     * The value returned depends on the number of elements defined by the "count" field:
     * - If count is 0, it returns 0.
     * - If count is 1, it returns the mean value.
     * - Otherwise, it returns the squared standard deviation (sD * sD) multiplied by the count.
     *
     * @return A double representing the computed statistical value based on conditions of the count field.
     */
    private double ss() {
        return switch (count) {
            case 0 -> 0;
            case 1 -> mean;
            default -> sD * sD * count;
        };
    }

    /**
     * Adds a value to the statistics and updates the minimum, maximum, mean,
     * standard deviation, and the count of values accordingly.
     *
     * @param value The value to be added to the statistical data.
     */
    public void add(double value) {
        if(count == 0) {
            min = value;
            max = value;
            mean = value;
            sD = 0;
            ++count;
            return;
        }
        if(value < min) {
            min = value;
        }
        if(value > max) {
            max = value;
        }
        double ss = ss() + ((value - mean) * (value - mean));
        mean = ((mean * count) + value) / (count + 1);
        ++count;
        sD = Math.sqrt((ss / count));
    }

    /**
     * Adds the statistical data from another `Statistics` object to this one.
     *
     * @param another Another `Statistics` object whose data will be added
     *                to this object. If the passed `Statistics` object is
     *                null or has no data (count is 0), the method returns
     *                without making any changes.
     */
    public void add(Statistics another) {
        if(another == null || another.count == 0) {
            return;
        }
        if(count == 0) {
            count = another.count;
            min = another.min;
            max = another.max;
            mean = another.mean;
            sD = another.sD;
            name = another.name;
            unitId = another.unitId;
            return;
        }
        if(another.count == 1) {
            add(another.min);
            name = another.name;
            unitId = another.unitId;
            return;
        }
        if(another.min < min) {
            min = another.min;
        }
        if(another.max > max) {
            max = another.max;
        }
        double ss = ss() + another.ss();
        ss  += ((another.sD * another.sD) + (another.mean * another.mean)) * another.count;
        mean = ((mean * count) + (another.mean * another.count));
        count += another.count;
        mean /= count;
        sD = Math.sqrt((ss / count));
    }

    /**
     * Retrieves the previous instance of statistics in a sequence or ordered collection.
     *
     * @return A statistics object representing the previous instance in the sequence.
     * */
    public abstract Statistics previous();

    /**
     * Retrieves the next instance of statistics in a sequence or ordered collection.
     *
     * @return A statistics object representing the next instance in the sequence.
     */
    public abstract Statistics next();

    /**
     * Constructs a query condition string incorporating the name and unit ID.
     * This method combines instance-specific `name` and `unitId` values into an SQL-style
     * condition used for filtering data in queries.
     *
     * @return A string representing the SQL-like condition in the format:
     *         "AND Name='[name]' AND Unit=[unitId]". The returned string includes the
     *         current values of the `name` and `unitId` fields.
     */
    String cond() {
        return " AND Name='" + name + "' AND Unit=" + unitId;
    }
}