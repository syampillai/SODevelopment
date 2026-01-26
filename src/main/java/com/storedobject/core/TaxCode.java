package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Tax code and region of the {@link SystemEntity}. For other entities, tax codes and regions must be specified in
 * the respective {@link com.storedobject.accounts.AccountEntity} class.
 *
 * @author Syam
 */
public class TaxCode extends StoredObject {

    private static final Map<Id, TaxCode> cache = new HashMap<>();
    private Id organizationId;
    private String taxCode;
    private Id taxRegionId;

    /**
     * Default constructor for the TaxCode class.
     * Initializes a new instance of the TaxCode object with no parameters.
     */
    public TaxCode() {}

    /**
     * Configures the specified Columns object by adding predefined column names and types.
     *
     * @param columns the Columns object to configure by adding column definitions
     */
    public static void columns(Columns columns) {
        columns.add("Organization", "id");
        columns.add("TaxCode", "text");
        columns.add("TaxRegion", "id");
    }

    /**
     * Configures the specified Indices object by adding a predefined index for "Organization".
     * The index is marked as unique.
     *
     * @param indices the Indices object to configure by adding the "Organization" index
     */
    public static void indices(Indices indices) {
        indices.add("Organization", true);
    }

    /**
     * Provides hints for the associated object, combining predefined constants
     * to denote object characteristics such as "small" or "small list".
     *
     * @return A bitwise combination of object hints, specifically `ObjectHint.SMALL`
     *         and `ObjectHint.SMALL_LIST`.
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the organization for this TaxCode.
     * This method validates the input and throws an exception if the operation is not allowed
     * when the object is not in a loading state and the specified organization ID differs
     * from the current one.
     *
     * @param organizationId the ID of the organization to be associated with this TaxCode
     * @throws Set_Not_Allowed if the object is not in a loading state, and an attempt is made to change the organization
     */
    public void setOrganization(Id organizationId) {
        if (!loading() && !Id.equals(this.getOrganizationId(), organizationId)) {
            throw new Set_Not_Allowed("Organization");
        }
        this.organizationId = organizationId;
    }

    /**
     * Sets the organization associated with this instance using the provided BigDecimal identifier.
     * This method internally converts the BigDecimal value into an {@code Id} object and delegates to
     * {@link #setOrganization(Id)}.
     *
     * @param idValue the identifier of the organization as a {@code BigDecimal}
     *                value to assign to this instance
     */
    public void setOrganization(BigDecimal idValue) {
        setOrganization(new Id(idValue));
    }

    /**
     * Sets the organization associated with this TaxCode using a {@link SystemEntity}.
     * If the provided {@link SystemEntity} is null, the organization will be cleared.
     *
     * @param organization the {@link SystemEntity} representing the organization to set,
     *                     or null to clear the organization.
     */
    public void setOrganization(SystemEntity organization) {
        setOrganization(organization == null ? null : organization.getId());
    }

    /**
     * Retrieves the unique identifier of the organization.
     *
     * @return the unique identifier of the organization as an {@code Id}.
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getOrganizationId() {
        return organizationId;
    }

    /**
     * Retrieves the {@link SystemEntity} instance associated with the organization.
     *
     * @return the related organization as a {@link SystemEntity} instance, or null if no organization is associated.
     */
    public SystemEntity getOrganization() {
        return getRelated(SystemEntity.class, organizationId);
    }

    /**
     * Sets the tax code for the entity.
     *
     * @param taxCode the tax code to be assigned to the entity
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * Retrieves the tax code associated with this entity.
     *
     * @return The tax code as a string. Returns null if no tax code is set.
     */
    @Column(required = false, order = 200, style = "(code)")
    public String getTaxCode() {
        return taxCode;
    }

    /**
     * Sets the tax region associated with this object.
     *
     * @param taxRegionId the identifier of the tax region to be set
     */
    public void setTaxRegion(Id taxRegionId) {
        this.taxRegionId = taxRegionId;
    }

    /**
     * Sets the tax region using the provided identifier value.
     *
     * @param idValue the identifier value for the tax region
     */
    public void setTaxRegion(BigDecimal idValue) {
        setTaxRegion(new Id(idValue));
    }

    /**
     * Sets the tax region by assigning the corresponding region ID.
     * If the provided TaxRegion object is null, the region ID is set to null.
     *
     * @param taxRegion the TaxRegion object representing the tax region to be set
     */
    public void setTaxRegion(TaxRegion taxRegion) {
        setTaxRegion(taxRegion == null ? null : taxRegion.getId());
    }

    /**
     * Retrieves the tax region identifier.
     *
     * @return the unique identifier associated with the tax region.
     */
    @Column(order = 300)
    public Id getTaxRegionId() {
        return taxRegionId;
    }

    /**
     * Retrieves the TaxRegion associated with the current instance.
     *
     * @return the TaxRegion object corresponding to the provided taxRegionId.
     */
    public TaxRegion getTaxRegion() {
        return getRelated(TaxRegion.class, taxRegionId);
    }

    /**
     * Validates the data consistency and integrity for the current instance.
     * This method ensures that the organizationId and taxRegionId are properly
     * typed and associated with their respective system entities.
     *
     * @param tm the transaction manager used to validate and check entity types
     * @throws Exception if validation fails or any errors occur during the process
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        organizationId = tm.checkType(this, organizationId, SystemEntity.class, false);
        taxRegionId = tm.checkType(this, taxRegionId, TaxRegion.class, false);
        taxCode = toCode(taxCode);
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        cache.remove(organizationId);
    }

    /**
     * Retrieves the {@link TaxCode} associated with the specified {@link SystemEntity}.
     *
     * @param systemEntity the {@link SystemEntity} for which the corresponding {@link TaxCode} is to be retrieved
     * @return the {@link TaxCode} associated with the provided {@link SystemEntity}
     */
    public static TaxCode getFor(SystemEntity systemEntity) {
        Id id = systemEntity.getId();
        TaxCode tc = cache.get(id);
        if(tc == null) {
            tc = get(TaxCode.class, "Organization=" + id);
            if(tc == null) {
                throw new SORuntimeException("Unable to find the tax region of " + systemEntity.toDisplay());
            } else {
                cache.put(id, tc);
            }
        }
        return tc;
    }
}
