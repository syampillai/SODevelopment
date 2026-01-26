package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * Represents a monetary tax amount computed for a give tax type and rate. When taxes are computed, for each calculation,
 * the resulting tax amount is stored in this class.
 *
 * @author Syam
 */
public final class Tax extends StoredObject {

    private Id typeId;
    private Percentage rate = Quantity.create(Percentage.class);
    private Money tax = new Money();
    int status = 0;
    boolean internal = false;

    /**
     * Default constructor for the Tax class.
     * Initializes a new instance of the Tax object with no specific parameters.
     */
    public Tax() {
    }

    /**
     * Configures the columns for the provided {@code Columns} instance by adding specific column definitions.
     *
     * @param columns the {@code Columns} instance to which the columns should be added
     */
    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("Rate", "percentage");
        columns.add("Tax", "money");
    }

    /**
     * Sets the type for this Tax object using the specified type ID.
     * Throws a {@code Set_Not_Allowed} exception if the type cannot be changed.
     * Updates the tax rate based on the specified type ID.
     *
     * @param typeId The {@code Id} representing the type to set for this Tax object.
     *               If the type ID is {@code null}, the rate is not updated.
     * @throws Set_Not_Allowed if the type is not allowed to be set in the current state.
     */
    public void setType(Id typeId) {
        if (!loading() && !Id.equals(this.typeId, typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
        if(!Id.isNull(typeId)) {
            rate = TaxRate.getRate(DateUtility.today(), typeId);
        }
    }

    /**
     * Sets the type of the tax entity using the given id value.
     *
     * @param idValue The BigDecimal value representing the identifier of the type to be set.
     */
    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    /**
     * Sets the tax type by extracting and setting the ID from the provided TaxType object.
     *
     * @param type The TaxType object whose ID is to be set. If the provided TaxType is null,
     *             a null value will be set.
     */
    public void setType(TaxType type) {
        setType(type == null ? null : type.getId());
    }

    /**
     * Retrieves the identifier representing the type.
     *
     * @return The identifier of the type as an {@link Id} object.
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getTypeId() {
        return typeId;
    }

    /**
     * Retrieves the tax type associated with the current tax instance.
     *
     * @return The tax type determined by the internal type identifier.
     */
    public TaxType getType() {
        return TaxType.getFor(typeId);
    }

    /**
     * Sets the rate for this instance.
     *
     * @param rate The percentage rate to be set. It represents a value of type {@code Percentage}.
     */
    public void setRate(Percentage rate) {
        this.rate = rate;
    }

    /**
     * Sets the rate value by converting the provided object into a {@link Percentage} instance.
     *
     * @param value The value to be converted to a {@link Percentage} and set as the rate.
     */
    public void setRate(Object value) {
        setRate(Percentage.create(value, Percentage.class));
    }

    /**
     * Retrieves the rate as a percentage value.
     *
     * @return The rate associated with this object, represented as a Percentage instance.
     */
    @Column(order = 200)
    public Percentage getRate() {
        return rate;
    }

    /**
     * Sets the tax value for the current Tax object.
     *
     * @param tax the monetary amount representing the tax to be set
     */
    public void setTax(Money tax) {
        this.tax = tax;
    }

    /**
     * Sets the tax value using the provided amount. The value is converted to a {@code Money}
     * instance using the {@code Money.create} method before storing it.
     *
     * @param moneyValue The monetary value to set as tax. It can be any object convertible to {@code Money}.
     */
    public void setTax(Object moneyValue) {
        setTax(Money.create(moneyValue));
    }

    /**
     * Retrieves the tax amount associated with this entity.
     *
     * @return the tax amount as a Money object
     */
    @Column(order = 200)
    public Money getTax() {
        return tax;
    }

    /**
     * Validates the tax data by ensuring all required fields, including the tax type, are properly set.
     * If the tax type is null, an exception is thrown with the description of the missing value.
     * Additionally, invokes the superclass's validation logic.
     *
     * @param tm The {@code TransactionManager} instance to manage this validation process.
     * @throws Invalid_Value If the tax type is not set.
     * @throws Exception If the superclass validation or other validation logic encounters an issue.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(getType() == null) {
            throw new Invalid_Value("Tax Type");
        }
        super.validateData(tm);
    }

    /**
     * Validates the object to ensure it is in an acceptable state before operations such as saving to the database are performed.
     * This method is overridden to enforce specific internal validation logic.
     * It performs the following checks:
     * 1. Verifies whether the object is marked as internal. If it is not internal, an {@link Invalid_State} exception is thrown
     *    with an error message indicating illegal access.
     * 2. Invokes the superclass's `validate` method to ensure further state or business logic validation is executed.
     * Note: Failing to call the superclass's `validate` method may result in a design error.
     *
     * @throws Invalid_State if the object is accessed illegally when not marked as internal.
     * @throws Exception if any other conditions in the superclass validation fail.
     */
    @Override
    public void validate() throws Exception {
        if(!internal) {
            throw new Invalid_State("Illegal access");
        }
        super.validate();
    }

    /**
     * This method represents an overridden operation of a core save behavior specific to the Tax entity.
     * It is invoked after the core saving logic is executed.
     * Upon invocation, this method modifies the internal state of the object by setting the `internal`
     * field to `false`. This action may signify that the object is no longer in an "internal" status
     * following the completion of the save operation.
     *
     * @throws Exception If an error occurs during the execution of the method.
     */
    @Override
    void savedCore() throws Exception {
        internal = false;
    }

    /**
     * Returns a string representation of the Tax object, combining its label and tax value.
     *
     * @return A string composed of the label and the tax value, separated by a space.
     */
    @Override
    public String toString() {
        return getLabel() + " " + tax;
    }

    /**
     * Generates and returns a label that combines the name of the tax type
     * with the tax rate.
     *
     * @return A string representing the label in the format "<TaxTypeName> @<Rate>".
     */
    public String getLabel() {
        return getType().getName() + " @" + rate;
    }

    /**
     * Retrieves the region information of the associated tax type.
     *
     * @return The {@link TaxRegion} corresponding to the tax type of this tax.
     */
    public TaxRegion getRegion() {
        return getType().getRegion();
    }

    /**
     * Status - 0: Normal / No change, 1: Newly computed, 2: Recomputed, 3: Region changed (to deleted), 4: No more applicable (to delete)
     * @return Status value
     */
    public int getStatus() {
        return status;
    }
}
