package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a type of tax that is associated with a specific region and tax computation method.
 * It facilitates the management of tax types, including their applicability period,
 * related regions and tax methods, and display order.
 *
 * @author Syam
 */
public final class TaxType extends Name implements DisplayOrder {

    private static final Map<Id, TaxType> cache = new HashMap<>();
    private Id regionId, taxMethodId = Id.ZERO;
    private int displayOrder;
    private final Date applicableFrom = DateUtility.create(2000, 1, 1),
            applicableTo = DateUtility.create(2999, 12, 31);

    /**
     * Default constructor for the TaxType class.
     * Initializes a new instance of the TaxType object with default values.
     */
    public TaxType() {
    }

    /**
     * Configures the column definitions needed for the TaxType entity.
     *
     * @param columns the Columns object used to define and add column properties
     *                such as name and type. The following columns will be added:
     *                - Region: Identifies the region, type "id".
     *                - TaxMethod: Specifies the tax method, type "id".
     *                - DisplayOrder: Determines the display order, type "int".
     *                - ApplicableFrom: Specifies the start date of applicability, type "date".
     *                - ApplicableTo: Specifies the end date of applicability, type "date".
     */
    public static void columns(Columns columns) {
        columns.add("Region", "id");
        columns.add("TaxMethod", "id");
        columns.add("DisplayOrder", "int");
        columns.add("ApplicableFrom", "date");
        columns.add("ApplicableTo", "date");
    }

    /**
     * Configures the indices for a database table by adding the "Region" column to the list of indices.
     *
     * @param indices the Indices object to which the "Region" column will be added
     */
    public static void indices(Indices indices) {
        indices.add("Region");
    }

    /**
     * Provides the list of column names used for browsing tax types.
     *
     * @return An array of strings representing the column names: "Region AS Applicable to",
     *         "TaxMethod AS Method of Computation", and "ApplicablePeriod".
     */
    public static String[] browseColumns() {
        return new String[] { "Region AS Applicable to", "Name", "TaxMethod AS Method of Computation", "ApplicablePeriod" };
    }

    /**
     * Retrieves a TaxType object based on the provided name.
     *
     * @param name the name of the TaxType to retrieve
     * @return the TaxType object that matches the specified name, or null if no match is found
     */
    public static TaxType get(String name) {
        return StoredObjectUtility.get(TaxType.class, "Name", name, false);
    }

    /**
     * Retrieves an iterator over TaxType objects filtered by the specified name.
     *
     * @param name the name to filter the TaxType objects
     * @return an iterator of TaxType objects matching the specified name
     */
    public static ObjectIterator<TaxType> list(String name) {
        return StoredObjectUtility.list(TaxType.class, "Name", name, false);
    }

    /**
     * Returns the hints associated with the TaxType class. The hints indicate specific characteristics
     * such as the size or nature of the objects handled by this class.
     *
     * @return An integer value that represents a combination of {@code ObjectHint.SMALL} and
     * {@code ObjectHint.SMALL_LIST}, indicating that the TaxType objects are small and support small lists.
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the region for the current TaxType instance.
     * Ensures that the region can only be set when the loading condition is met,
     * and the new region ID differs from the existing one. Otherwise, an exception
     * is thrown.
     *
     * @param regionId The ID of the region to be set.
     *                  Must be non-null and distinct from the current region ID
     *                  unless the loading condition allows changes.
     * @throws Set_Not_Allowed if changing the region is not allowed due to
     *                         current state or other conditional restrictions.
     */
    public void setRegion(Id regionId) {
        if (!loading() && !Id.equals(this.getRegionId(), regionId)) {
            throw new Set_Not_Allowed("Region");
        }
        this.regionId = regionId;
    }

    /**
     * Sets the region for this TaxType instance using the specified region identifier value.
     * This method converts the given BigDecimal value into an {@code Id} instance and
     * delegates the region setting to another {@code setRegion} method.
     *
     * @param idValue The BigDecimal value representing the identifier for the region to be set.
     */
    public void setRegion(BigDecimal idValue) {
        setRegion(new Id(idValue));
    }

    /**
     * Sets the tax region for the current TaxType instance.
     * If the provided TaxRegion object is null, the region is set to null. Otherwise,
     * the region ID from the provided TaxRegion object is extracted and assigned.
     *
     * @param region The TaxRegion object to set. Can be null.
     */
    public void setRegion(TaxRegion region) {
        setRegion(region == null ? null : region.getId());
    }

    /**
     * Retrieves the identifier of the region to which this tax type is applicable.
     *
     * @return The identifier of the applicable region.
     */
    @SetNotAllowed
    @Column(order = 200, caption = "Applicable to")
    public Id getRegionId() {
        return regionId;
    }

    /**
     * Retrieves the tax region associated with this tax type.
     *
     * @return The {@link TaxRegion} object corresponding to the region identifier
     *         of this tax type, or null if no corresponding region is found.
     */
    public TaxRegion getRegion() {
        return TaxRegion.getFor(regionId);
    }

    /**
     * Sets the tax method for this instance. If the operation is not allowed due to the current
     * state or a change in value, an exception is thrown.
     *
     * @param taxMethodId The identifier of the tax method to be set.
     * @throws Set_Not_Allowed if the operation is not allowed.
     */
    public void setTaxMethod(Id taxMethodId) {
        if (!loading() && !Id.equals(this.getTaxMethodId(), taxMethodId)) {
            throw new Set_Not_Allowed("Tax Method");
        }
        this.taxMethodId = taxMethodId;
    }

    /**
     * Sets the tax method using a BigDecimal value to identify the tax method.
     *
     * @param idValue the BigDecimal value representing the identifier of the tax method
     */
    public void setTaxMethod(BigDecimal idValue) {
        setTaxMethod(new Id(idValue));
    }

    /**
     * Sets the tax method for this instance. If the provided tax method is null,
     * it sets the tax method ID to null; otherwise, it uses the ID of the provided tax method.
     *
     * @param taxMethod The tax method to set. Can be null if no tax method is assigned.
     */
    public void setTaxMethod(TaxMethod taxMethod) {
        setTaxMethod(taxMethod == null ? null : taxMethod.getId());
    }

    /**
     * Retrieves the identifier for the tax computation method.
     *
     * @return an {@code Id} representing the tax method identifier.
     */
    @SetNotAllowed
    @Column(order = 400, caption = "Method of Computation", style = "(any)")
    public Id getTaxMethodId() {
        return taxMethodId;
    }

    /**
     * Retrieves the tax method associated with the current tax type.
     *
     * @return The tax method corresponding to the value of the taxMethodId field, resolved through TaxMethod.
     */
    public TaxMethod getTaxMethod() {
        return TaxMethod.getFor(taxMethodId);
    }

    /**
     * Sets the display order of the tax type.
     *
     * @param displayOrder The integer value representing the order in which the tax type
     *                     should be displayed. Lower values indicate higher priority in
     *                     the display sequence.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Retrieves the display order of the item or entity.
     *
     * @return the display order value as an integer, used to determine the item's position or sequence.
     */
    @Column(order = 400, required = false)
    public int getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Sets the starting date from which the tax type is applicable.
     *
     * @param applicableFrom The date indicating the start of applicability.
     */
    public void setApplicableFrom(Date applicableFrom) {
        this.applicableFrom.setTime(applicableFrom.getTime());
    }

    /**
     * Retrieves the date from which this TaxType is applicable.
     *
     * @return A Date object representing the start date of applicability for this TaxType.
     */
    public Date getApplicableFrom() {
        return DateUtility.create(applicableFrom);
    }

    /**
     * Sets the "applicable to" date for this tax type.
     *
     * @param applicableTo the date until which this tax type is applicable
     */
    public void setApplicableTo(Date applicableTo) {
        this.applicableTo.setTime(applicableTo.getTime());
    }

    /**
     * Retrieves the date applicable to the current context.
     *
     * @return A Date object representing the applicable date, created using the specified utility method.
     */
    public Date getApplicableTo() {
        return DateUtility.create(applicableTo);
    }

    /**
     * Retrieves the applicable period defined by the start and end dates.
     *
     * @return a DatePeriod object representing the period between the applicable start and end dates.
     */
    public DatePeriod getApplicablePeriod() {
        return new DatePeriod(applicableFrom, applicableTo);
    }

    /**
     * Validates the data of the current object by ensuring that required fields are properly set
     * and meet the necessary conditions. Performs checks and transformations using the provided
     * TransactionManager to maintain data integrity.
     *
     * @param tm The TransactionManager instance used to perform checks, transactions, and other
     *           operations necessary for validation.
     * @throws Exception If validation fails due to errors such as missing required fields,
     *                   invalid data, or logical inconsistencies (e.g., applicableFrom is after
     *                   applicableTo).
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        regionId = tm.checkType(this, regionId, TaxRegion.class, false);
        if(getTaxMethod() == null) {
            TaxMethod m = get(TaxMethod.class);
            if(m == null) {
                m = new TaxMethod();
                tm.transact(m::save);
            }
            taxMethodId = m.getId();
        }
        taxMethodId = tm.checkTypeAny(this, taxMethodId, TaxMethod.class, false);
        if(applicableFrom.after(applicableTo)) {
            throw new Invalid_Value("Applicable Period");
        }
        super.validateData(tm);
    }

    /**
     * Removes the current object's identifier from the cache.
     * This method is invoked to ensure that the cache stays up-to-date
     * after the object has been successfully saved.
     *
     * @throws Exception if an error occurs during the removal process.
     */
    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    /**
     * Retrieves a TaxType object for the specified Id. If the TaxType is not present in the cache,
     * it retrieves it from the datastore and updates the cache.
     *
     * @param id the unique identifier for the TaxType to retrieve
     * @return the TaxType associated with the provided Id, or null if no such TaxType exists
     */
    public static TaxType getFor(Id id) {
        TaxType tt = cache.get(id);
        if(tt == null) {
            tt = get(TaxType.class, id);
            if(tt != null) {
                cache.put(id, tt);
            }
        }
        return tt;
    }
}
