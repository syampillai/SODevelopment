package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

/**
 * Business entity - Used by {@link EntityAccount}.
 *
 * <p>Note (1): Contact grouping code ({@link #getContactGroupingCode()}) for this class is 10192.</p>
 * <p>Note (2): You typically extend the {@link EntityAccount} to customize party-related attributes.</p>
 *
 * @author Syam
 */
public final class BusinessEntity extends AccountEntity<Entity> {

    private Id entityId;

    /**
     * This class represents a business entity.
     *
     * <p>Note: Contact grouping code for this class is 10192.</p>
     */
    public BusinessEntity() {
    }

    /**
     * Adds columns to the given Columns object.
     *
     * @param columns The Columns object to which the columns will be added
     */
    public static void columns(Columns columns) {
        columns.add("Entity", "id");
    }

    /**
     * Adds the specified column list to the indices.
     *
     * @param indices The Indices object to add the column list to.
     */
    public static void indices(Indices indices) {
        indices.add("Entity", true);
    }

    /**
     * Sets the party ID for the current entity.
     *
     * @param id The ID of the party to be set.
     */
    @Override
    protected void setPartyId(Id id) {
        entityId = id;
    }

    /**
     * Retrieves the party ID.
     *
     * @return The party ID.
     */
    @Override
    protected Id getPartyId() {
        return entityId;
    }

    /**
     * Sets the entity of the party.
     *
     * @param idValue the value of the entity ID
     */
    public void setEntity(BigDecimal idValue) {
        setParty(new Id(idValue));
    }

    /**
     * Sets the entity for this object.
     *
     * @param personId the identifier of the person entity to be set
     */
    public void setEntity(Id personId) {
        setParty(personId);
    }

    /**
     * Sets the entity for this object.
     *
     * @param person The entity to set. (null is allowed)
     */
    public void setEntity(Entity person) {
        setParty(person);
    }

    /**
     * Retrieves the entity associated with this account entity.
     * If the entity is not already initialized, it retrieves the related instance from the database using the entity class and entity id.
     * If the instance is old, it might return an old instance.
     *
     * @return The entity associated with this account entity, or null if it is not available for the given parameters.
     */
    public Entity getEntity() {
        return getParty();
    }

    /**
     * Retrieves the ID of the business entity.
     *
     * @return The ID of the business entity.
     */
    @SetNotAllowed
    @Column(order = 100, caption = "Business Entity")
    public Id getEntityId() {
        return entityId;
    }

    /**
     * Returns the class representing the party associated with this entity.
     *
     * @return The class representing the party.
     */
    @Override
    protected Class<Entity> getPartyClass() {
        return Entity.class;
    }

    /**
     * Retrieves the name of the business entity associated with this account entity.
     *
     * @return The name of the business entity
     */
    @Override
    public String getName() {
        return getParty().getName();
    }

    /**
     * Returns the contact grouping code for the given class.
     *
     * @return The contact grouping code. This value is always 10192.
     */
    @Override
    public int getContactGroupingCode() {
        return 10192;
    }

    public static BusinessEntity getFor(Id entityId) {
        return get(BusinessEntity.class, "Entity=" + entityId);
    }
}
