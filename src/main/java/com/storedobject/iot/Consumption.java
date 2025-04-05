package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * Represents consumption data associated with a specific resource, item, and a time period.
 * This is an abstract class that provides a framework for subclasses to define consumption records
 * over different periods, such as hourly, daily, weekly, monthly, or yearly.
 * <p>
 * The class enforces restrictions on setting certain attributes while not loading, and ensures data
 * consistency and correctness through validations and relationships with related entities.
 * Consumption values can also be updated and adjusted incrementally.
 * </p>
 *
 * @author Syam
 */
public abstract class Consumption extends StoredObject implements DBTransaction.NoHistory {

    private Id resourceId, itemId;
    private double consumption = 0;
    private int year;

    /**
     * Default constructor for the Consumption class.
     * Initializes an instance of the Consumption object.
     */
    public Consumption() {
    }

    /**
     * Configures the specified {@link Columns} instance by adding column definitions
     * representing the fields of the Consumption class.
     *
     * @param columns The {@link Columns} instance to which column definitions are added.
     *                <pre>
     *                - Adds a column named "Item" of type "id" to represent an identifier for items.
     *                - Adds a column named "Resource" of type "id" to represent an identifier for resources.
     *                - Adds a column named "Year" of type "int" to represent the year field.
     *                - Adds a column named "Consumption" of type "double precision" to represent the consumption field.
     *                </pre>
     */
    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Resource", "id");
        columns.add("Year", "int");
        columns.add("Consumption", "double precision");
    }

    /**
     * Sets the item identifier for the current object if allowed.
     *
     * @param itemId The identifier of the item to set.
     * @throws Set_Not_Allowed if the itemId cannot be set due to current conditions.
     */
    public void setItem(Id itemId) {
        if (!loading() && !Id.equals(this.getItemId(), itemId)) {
            throw new Set_Not_Allowed("Item");
        }
        this.itemId = itemId;
    }

    /**
     * Sets the item identifier for the current object using the given BigDecimal value.
     * The BigDecimal value is used to create a new ID, which is then set as the item identifier.
     *
     * @param idValue A BigDecimal value representing the identifier of the item to be set.
     */
    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    /**
     * Sets the item for this consumption instance using an AbstractUnit.
     * If the provided AbstractUnit is null, the item ID is also set to null.
     * Otherwise, the item ID is set to the ID of the provided AbstractUnit.
     *
     * @param item AbstractUnit instance whose ID will be set as the item ID,
     *             or null to clear the item ID.
     */
    public void setItem(AbstractUnit item) {
        setItem(item == null ? null : item.getId());
    }

    /**
     * Retrieves the unique identifier associated with the item in the consumption record.
     *
     * @return The identifier of the item as an {@code Id}.
     */
    @SetNotAllowed
    @Column(style = "(any)", order = 50)
    public Id getItemId() {
        return itemId;
    }

    /**
     * Retrieves the associated item as an {@link AbstractUnit}.
     *
     * @return The associated {@link AbstractUnit} instance based on the item ID.
     */
    public AbstractUnit getItem() {
        return getRelated(AbstractUnit.class, itemId, true);
    }

    /**
     * Sets the resource by its identifier. This method ensures that the resource identifier is only
     * set under valid conditions. It throws an exception if the resource identifier is not permitted
     * to be updated under the current state of the object.
     *
     * @param resourceId The identifier of the resource to be set.
     * @throws Set_Not_Allowed If the resource identifier cannot be updated.
     */
    public void setResource(Id resourceId) {
        if (!loading() && !Id.equals(this.getResourceId(), resourceId)) {
            throw new Set_Not_Allowed("Resource");
        }
        this.resourceId = resourceId;
    }

    /**
     * Sets the resource of the object using the specified BigDecimal value.
     * The BigDecimal value is converted to an Id instance before setting.
     *
     * @param idValue the BigDecimal value representing the ID of the resource to set
     */
    public void setResource(BigDecimal idValue) {
        setResource(new Id(idValue));
    }

    /**
     * Sets the resource for this consumption. If the input resource is null, the resource ID is set to null.
     *
     * @param resource The resource to be associated with this consumption object.
     *                 If this is null, the resource ID will be set to null.
     */
    public void setResource(Resource resource) {
        setResource(resource == null ? null : resource.getId());
    }

    /**
     * Retrieves the resource identifier associated with this consumption entity.
     *
     * @return The identifier of the resource as an {@code Id}.
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getResourceId() {
        return resourceId;
    }

    /**
     * Retrieves the {@link Resource} associated with the current object.
     *
     * @return The associated Resource instance.
     */
    public Resource getResource() {
        return getRelated(com.storedobject.iot.Resource.class, resourceId);
    }

    /**
     * Sets the year for the consumption record.
     *
     * @param year The year value to set.
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Retrieves the year associated with the consumption record.
     *
     * @return the year as an integer.
     */
    @Column(order = 200)
    public int getYear() {
        return year;
    }

    /**
     * Sets the consumption value for this instance.
     *
     * @param consumption The consumption value to set, represented as a double.
     */
    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    /**
     * Retrieves the consumption value.
     *
     * @return the consumption as a double value.
     */
    @Column(required = false, order = 2000)
    public double getConsumption() {
        return consumption;
    }

    /**
     * Adds the specified consumption value to the existing consumption.
     * If the resulting consumption is less than a small positive threshold (0.000000001),
     * it resets the consumption to 0 to avoid very small residual values.
     *
     * @param consumption the consumption value to add; can be positive or negative
     */
    void addConsumption(double consumption) {
        this.consumption += consumption;
        if (this.consumption < 0.000000001) {
            this.consumption = 0;
        }
    }

    /**
     * Validates the data associated with the consumption record by ensuring that the specified
     * item and resource are of the correct types and are properly set. Additionally, it invokes
     * the superclass implementation for further validation.
     *
     * @param tm The {@code TransactionManager} instance that facilitates the verification of
     *           object types and enforces any validation rules for the transaction.
     * @throws Exception If any validation rule fails or the data is invalid.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            itemId = tm.checkTypeAny(this, itemId, AbstractUnit.class, false);
            if (getItem() instanceof SuperUnit) {
                throw new Invalid_State("Item can not be a super-unit");
            }
            resourceId = tm.checkType(this, resourceId, Resource.class, false);
        }
        super.validateData(tm);
    }

    /**
     * Retrieves the period name of the consumption instance.
     * If the instance is of type {@link DailyConsumption}, the method returns "Day".
     * For other subclasses, it derives the name from the class name,
     * removing anything after and including "ly" in the class name.
     *
     * @return The name of the period associated with the consumption instance.
     *         For DailyConsumption, it returns "Day". For other types, it derives the period name dynamically.
     */
    public String getPeriodName() {
        if(this instanceof DailyConsumption) {
            return "Day";
        }
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        return s.substring(0, s.indexOf("ly"));
    }

    /**
     * Abstract method to retrieve the period associated with the consumption record.
     * This is intended to be implemented by subclasses to define specific logic
     * for determining the period.
     *
     * @return The period as an integer, representing a time frame or interval
     *         linked to the consumption data.
     */
    public abstract int getPeriod();

    /**
     * Provides detailed information about the period associated with the consumption instance.
     *
     * @return A string representing the detailed period information.
     */
    public abstract String getPeriodDetail();

    /**
     * Converts the consumption record into a displayable string representation.
     *
     * @return A string representation of the consumption record in the format:
     *         "Resource (Item) = Consumption (Year: [Yearly or Detailed Period Info])".
     */
    @Override
    public String toDisplay() {
        return getResource().toDisplay() + " (" + getItem().toDisplay() + ") = " + consumption + " (Year: " + year
                + (this instanceof YearlyConsumption ? "" : (", " + getPeriodName() + ": " + getPeriod())) + ")";
    }

    /**
     * Retrieves the previous instance of the Consumption object, typically representing
     * the data for the preceding time period associated with the current object.
     *
     * @return The previous Consumption object, or null if there is no prior consumption data.
     */
    public abstract Consumption previous();

    /**
     * Retrieves the next Consumption object in a sequence, if available.
     * This method is abstract and must be implemented by subclasses to define
     * the logic for fetching the next Consumption instance.
     *
     * @return the next Consumption object, or null if no further object exists in the sequence.
     */
    public abstract Consumption next();

    /**
     * Constructs a conditional string for querying or filtering data based on item and resource identifiers.
     *
     * @return A string containing SQL-like conditions in the format " AND Item={itemId} AND Resource={resourceId}",
     *         where {itemId} and {resourceId} represent the respective identifiers.
     */
    String cond() {
        return " AND Item=" + itemId + " AND Resource=" + resourceId;
    }
}