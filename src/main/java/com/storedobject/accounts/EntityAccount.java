package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * EntityAccount is a subclass of com.storedobject.core.Account that represents an account associated with an entity.
 *
 * @author Syam
 */
public class EntityAccount extends Account {

    private Id entityId;
    private AccountEntity<?> entity;

    /**
     * Creates a new instance of EntityAccount.
     */
    public EntityAccount() {
    }

    /**
     * Adds a new column with the specified name and type to the provided Columns object.
     *
     * @param columns The Columns object to add the column to.
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
        indices.add("Entity");
    }

    /**
     * Sets the entity id.
     *
     * @param entityId The id of the entity to set.
     * @throws Set_Not_Allowed If the method is called while loading.
     */
    public void setEntity(Id entityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Entity");
        }
        this.entity = null;
        this.entityId = entityId;
    }

    /**
     * Sets the entity object with the provided id value.
     *
     * @param idValue the ID value of the entity
     */
    public void setEntity(BigDecimal idValue) {
        setEntity(new Id(idValue));
    }

    /**
     * Sets the associated entity for the EntityAccount.
     * If the given entity is null, sets the entity ID to null.
     *
     * @param entity The AccountEntity to associate with the EntityAccount.
     */
    public void setEntity(AccountEntity<?> entity) {
        setEntity(entity == null ? null : entity.getId());
    }

    /**
     * Returns the entity Id.
     *
     * @return The entity Id.
     */
    @SetNotAllowed
    @Column(style = "(any)", order = 100)
    public Id getEntityId() {
        return entityId;
    }

    /**
     * Retrieves the associated entity for this EntityAccount.
     *
     * @return The AccountEntity associated with this EntityAccount. If the entity has not been fetched yet, it will be fetched and cached before returning.
     */
    public AccountEntity<?> getEntity() {
        if (this.entity == null) {
            this.entity = getRelated(AccountEntity.class, entityId, true);
        }
        return this.entity;
    }

    /**
     * Retrieves an instance of {@link EntityAccount} based on the given {@link SystemEntity} and name.
     *
     * @param systemEntity The system entity associated with the account.
     * @param name         The name of the account.
     * @return An instance of {@link EntityAccount} matching the given system entity and name.
     */
    public static EntityAccount get(SystemEntity systemEntity, String name) {
        return EntityAccount.getByNameOrNumber(systemEntity, EntityAccount.class, name, true);
    }

    /**
     * Returns an iterator of EntityAccount objects that match the specified systemEntity and name.
     *
     * @param systemEntity The SystemEntity object to filter the accounts by.
     * @param name The name of the accounts to retrieve.
     * @return An iterator of EntityAccount objects that match the specified systemEntity and name.
     */
    public static ObjectIterator<? extends EntityAccount> list(SystemEntity systemEntity, String name) {
        return EntityAccount.listByNameOrNumber(systemEntity, EntityAccount.class, name, true);
    }

    /**
     * Retrieves the name of the entity associated with this account.
     *
     * @return The name of the entity as a string.
     */
    @Override
    public final String getName() {
        return this instanceof InstantaneousAccount ? super.getName() : getEntity().getName();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        entityId = tm.checkTypeAny(this, entityId, AccountEntity.class, false);
        if(!(this instanceof InstantaneousAccount) && getEntity() != null) {
            setName(entity.getName());
        }
        super.validateData(tm);
        if(entity != null) {
            boolean sameParty = getEntityId().equals(entity.getId());
            if((this instanceof InstantaneousAccount && !sameParty) || (!(this instanceof InstantaneousAccount) && sameParty)) {
                throw new Invalid_Value("Entity");
            }
        }
    }

    @Override
    protected void validateAccountStatus() throws Exception {
        if(Financial.getCategory(this) != 0) { // Not a balance-sheet a/c
            throw new Invalid_State("Not a balance-sheet account");
        }
    }

    /**
     * Determines whether the entity account is associated with a personal party.
     *
     * @return true if the entity account is associated with a personal party, false otherwise
     */
    public final boolean isPersonal() {
        return getEntity().isPersonal();
    }

    /**
     * Determines whether the account entity associated with this EntityAccount object is a business entity.
     *
     * @return {@code true} if the account entity is a business entity, {@code false} otherwise.
     */
    public final boolean isBusiness() {
        return getEntity().isBusiness();
    }

    /**
     * Creates an EntityAccount for the provided {@link AccountEntity}.
     *
     * @param tm The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @return The created EntityAccount object.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity) {
        return createFor(tm, accountEntity, EntityAccount.class);
    }

    /**
     * Creates an EntityAccount for the provided {@link AccountEntity}, category and type of invoice.
     * <p>Note: Category is typically 0 for suppliers and 1 for customers.</p>
     *
     * @param tm The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @return The created EntityAccount object.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity, int category,
                                          int type) {
        AccountConfiguration ac = AccountConfiguration.getFor(tm.getEntity().getEntityId(), category, type);
        if(ac == null) {
            return null;
        }
        return createFor(tm, accountEntity, ac.getEntityAccountClass());
    }

    /**
     * Creates an EntityAccount for the provided {@link AccountEntity} and class of {@link EntityAccount}.
     *
     * @param tm The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @param accountClass Class of the {@link EntityAccount}.
     * @return The created EntityAccount object.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity,
                                                          Class<? extends EntityAccount> accountClass) {
        if(tm.getEntity().getEntityId().equals(accountEntity.getId())) {
            accountClass = InstantaneousAccount.class;
        }
        EntityAccount ea = list(accountClass, "Entity=" + accountEntity.getId(), true).findFirst();
        if(ea != null) {
            return ea;
        }
        try {
            ea = accountClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            return null;
        }
        String chartName;
        if(accountClass == InstantaneousAccount.class) {
            chartName = "Cash";
        } else {
            chartName = "Suppliers/Customers";
            if(accountClass != EntityAccount.class) {
                chartName += " (" + StringUtility.makeLabel(accountClass) + ")";
            }
        }
        AccountChart.set(ea, chartName, tm);
        ea.setEntity(accountEntity);
        ea.setSystemEntity(tm.getEntity());
        try {
            tm.transact(ea::save);
        } catch (Exception e) {
            return null;
        }
        return ea;
    }
}
