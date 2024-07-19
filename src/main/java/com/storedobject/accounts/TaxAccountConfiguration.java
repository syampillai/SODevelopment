package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * This class is used to configure tax accounts.
 *
 * @author Syam
 */
public final class TaxAccountConfiguration extends StoredObject implements OfEntity {

    private Id systemEntityId;
    private Id typeId;
    private Id accountId;

    /**
     * This class represents a Tax Account Configuration.
     * It provides a constructor to create an instance of TaxAccountConfiguration.
     */
    public TaxAccountConfiguration() {
    }

    /**
     * Adds columns to the given Columns object.
     *
     * @param columns The Columns object to add the columns to.
     */
    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Type", "id");
        columns.add("Account", "id");
    }

    /**
     * Adds indices to the given Indices object.
     *
     * @param indices the Indices object to add indices to
     */
    public static void indices(Indices indices) {
        indices.add("SystemEntity, Type", true);
    }

    /**
     * Retrieves the unique condition used for querying the system.
     * The condition is formed by concatenating the system entity ID and type.
     *
     * @return The unique condition.
     */
    @Override
    public String getUniqueCondition() {
        return "SystemEntity="
                + systemEntityId
                + " AND Type="
                + typeId;
    }

    /**
     * Provides hints about objects.
     * <p>
     * This method returns a combination of hints representing certain characteristics of objects.
     * The possible hints are defined in the {@link ObjectHint} class.
     * </p>
     *
     * @return An integer value that represents a combination of hints.
     *         The hints can be extracted using binary AND operations.
     * @see ObjectHint
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the system entity Id for this object.
     *
     * @param systemEntityId the Id of the system entity to set
     * @throws Set_Not_Allowed if the method is called when the object is not in a loading state
     *                         or if the provided system entity Id is different from the current one
     */
    public void setSystemEntity(Id systemEntityId) {
        if (!loading() && !Id.equals(this.getSystemEntityId(), systemEntityId)) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    /**
     * Sets the system entity using the provided id value.
     *
     * @param idValue the id value of the system entity
     */
    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    /**
     * Sets the system entity for this AccountConfiguration.
     *
     * @param systemEntity The system entity to set. If null, the system entity will be set to null.
     */
    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    /**
     * Retrieves the system entity ID.
     *
     * @return the system entity ID
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    /**
     * Retrieves the system entity associated with this AccountConfiguration.
     *
     * @return The SystemEntity object associated with this AccountConfiguration.
     */
    public SystemEntity getSystemEntity() {
        return getRelated(SystemEntity.class, systemEntityId);
    }

    /**
     * Sets the type of tax.
     *
     * @param typeId the Id representing the desired type
     * @throws Set_Not_Allowed if the method is called outside the loading state
     *                         and the new typeId is different from the current typeId
     */
    public void setType(Id typeId) {
        if (!loading() && !Id.equals(this.typeId, typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
    }

    /**
     * Sets the type of tax.
     *
     * @param idValue the BigDecimal value of the id to set as the new type
     */
    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    /**
     * Sets the type of tax.
     *
     * @param type the TaxType to set.
     */
    public void setType(TaxType type) {
        setType(type == null ? null : type.getId());
    }

    /**
     * Retrieves the type ID of the tax type.
     *
     * @return the type ID of the tax type
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getTypeId() {
        return typeId;
    }

    /**
     * Returns the TaxType associated with the current instance.
     *
     * @return the TaxType object for the current instance.
     */
    public TaxType getType() {
        return TaxType.getFor(typeId);
    }

    /**
     * Sets the account ID for the current object.
     *
     * @param accountId the unique identifier of the account
     */
    public void setAccount(Id accountId) {
        this.accountId = accountId;
    }

    /**
     * Sets the account for this object using the specified id value.
     *
     * @param idValue the value for the account id
     */
    public void setAccount(BigDecimal idValue) {
        setAccount(new Id(idValue));
    }

    /**
     * Sets the account for this AccountConfiguration.
     *
     * @param account The account to set. If null, the account ID will be set to null.
     */
    public void setAccount(Account account) {
        setAccount(account == null ? null : account.getId());
    }

    /**
     * Returns the account Id.
     *
     * @return the account Id
     */
    @Column(style = "(any)", order = 400)
    public Id getAccountId() {
        return accountId;
    }

    /**
     * Retrieves the Account related to the current AccountConfiguration.
     *
     * @return The related Account object.
     */
    public Account getAccount() {
        return getRelated(Account.class, accountId, true);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        Account a = getAccount();
        if(a == null) {
            throw new Invalid_Value("Account");
        }
        if(Financial.isSpecial(a) || a instanceof EntityAccount || a.isForeignCurrency()
                || Financial.getCategory(a) != 0 || Financial.getBalanceType(a) != 1
                || Financial.getTransactionType(a) != 2) {
            throw new Invalid_State("Account is not a tax account");
        }
        if(!systemEntityId.equals(a.getSystemEntityId())) {
            throw new Invalid_State("Account does not belong to this organization");
        }
        super.validateData(tm);
    }
}
