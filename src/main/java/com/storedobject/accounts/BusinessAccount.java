package com.storedobject.accounts;

import com.storedobject.core.Account;
import com.storedobject.core.Entity;
import com.storedobject.core.Id;
import com.storedobject.core.annotation.SetNotAllowed;

/**
 * Represents a business account.
 *
 * @author Syam
 */
public class BusinessAccount extends Account {

    private final Id entityId;

    /**
     * Constructs a Business Account.
     *
     * @param entityId Id of the entity owning this account.
     * @param systemEntityId Id of the System Entity where this account is opened.
     * @param currencyId Id of the account currency.
     */
    public BusinessAccount(Id entityId, Id systemEntityId, Id currencyId) {
        super(systemEntityId, null, currencyId);
        this.entityId = entityId;
    }

    /**
     * Constructs a Business Account.
     *
     * @param entityId of the entity owning this account.
     * @param currencyId Id of the account currency.
     */
    public BusinessAccount(Id entityId, Id currencyId) {
        this(entityId, null, currencyId);
    }

    /**
     * Constructs a Business Account.
     *
     * @param entityId of the entity owning this account.
     */
    public BusinessAccount(Id entityId) {
        this(entityId, null, null);
    }

    /**
     * Constructs a local currency Account.
     */
    public BusinessAccount() {
        this(null, null, null);
    }

    /**
     * Gets the Id of the entity.
     *
     * @return The Id of the entity
     */
    @SetNotAllowed
    public Id getEntityId() {
        return entityId;
    }

    /**
     * Gets the entity.
     *
     * @return The entity.
     */
    public Entity getEntity() {
        return get(getTransaction(), Entity.class, entityId);
    }
}