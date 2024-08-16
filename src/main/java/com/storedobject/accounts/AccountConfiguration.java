package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to configure accounts for posting transactions such as purchase and sales invoices.
 *
 * @author Syam
 */
public final class AccountConfiguration extends StoredObject implements OfEntity {

    private static final Map<String, AccountConfiguration> AC = new HashMap<>();
    private static final String[] categoryValues =
            new String[] {
                    "Supplier", "Customer",
            };
    private static final String[] allowBitValues =
            new String[] {
                    "Inherited", "Cash", "Card", "Digital",
            };
    private Id systemEntityId;
    private int category = 0;
    private int type;
    private Id accountId;
    private String entityAccountClassName = EntityAccount.class.getName();
    private Class<? extends EntityAccount> entityAccountClass;
    private int allow = 0;
    private String name;

    /**
     * This class represents an Account Configuration.
     * It provides a constructor to create an instance of AccountConfiguration.
     */
    public AccountConfiguration() {
    }

    /**
     * Adds columns to the given Columns object.
     *
     * @param columns The Columns object to add the columns to.
     */
    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Name", "text");
        columns.add("Category", "int");
        columns.add("Type", "int");
        columns.add("Account", "id");
        columns.add("EntityAccountClassName", "text");
        columns.add("Allow", "int");
    }

    /**
     * Adds indices to the given Indices object.
     *
     * @param indices the Indices object to add indices to
     */
    public static void indices(Indices indices) {
        indices.add("SystemEntity, Category, Type", true);
    }

    /**
     * Retrieves the unique condition used for querying the system.
     * The condition is formed by concatenating the system entity ID, category, and type.
     *
     * @return The unique condition.
     */
    @Override
    public String getUniqueCondition() {
        return "SystemEntity="
                + systemEntityId
                + " AND "
                + "Category="
                + category
                + " AND "
                + "Type="
                + type;
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
     * Sets the name for this object.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the AccountConfiguration.
     *
     * @return The name of the AccountConfiguration as a string.
     */
    @Column(order = 150)
    public String getName() {
        return name;
    }

    /**
     * Sets the category for the object.
     *
     * @param category the new category to be set
     * @throws Set_Not_Allowed if setting the category is not allowed
     */
    public void setCategory(int category) {
        if (!loading()) {
            throw new Set_Not_Allowed("Category");
        }
        this.category = category;
    }

    /**
     * Returns the category of the item.
     *
     * @return the category of the item as an integer.
     */
    @SetNotAllowed
    @Column(order = 200)
    public int getCategory() {
        return category;
    }

    /**
     * Retrieves the list of category values.
     *
     * @return an array of strings representing the category values.
     */
    public static String[] getCategoryValues() {
        return categoryValues;
    }

    /**
     * Returns the category value corresponding to the given input value.
     *
     * @param value the input value for which the category value is required
     * @return the category value corresponding to the given input value
     */
    public static String getCategoryValue(int value) {
        String[] s = getCategoryValues();
        return s[value % s.length];
    }

    /**
     * Returns the category value.
     *
     * @return the category value as a string.
     */
    public String getCategoryValue() {
        return getCategoryValue(category);
    }

    /**
     * Sets the type of the object.
     *
     * @param type the new type to be set
     * @throws Set_Not_Allowed if loading() is false, indicating that setting the type is not allowed
     */
    public void setType(int type) {
        if (!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        this.type = type;
    }

    /**
     * Returns the type of the object.
     *
     * @return the type of the object.
     */
    @SetNotAllowed
    @Column(required = false, order = 300)
    public int getType() {
        return type;
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
    @Column(style = "(any)", order = 400, caption = "Purchase/Sale Account")
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

    /**
     * Sets the entity account class name.
     *
     * @param entityAccountClassName the fully qualified class name
     */
    public void setEntityAccountClassName(String entityAccountClassName) {
        this.entityAccountClassName = entityAccountClassName;
    }

    /**
     * Retrieves the entity account class name.
     *
     * @return the entity account class name.
     */
    @Column(order = 500)
    public String getEntityAccountClassName() {
        return entityAccountClassName;
    }

    public void setAllow(int allow) {
        this.allow = allow;
    }

    @Column(order = 600, required = false)
    public int getAllow() {
        return allow;
    }

    public static String[] getAllowBitValues() {
        return allowBitValues;
    }

    public static String getAllowValue(int value) {
        String[] s = getAllowBitValues();
        return StringUtility.bitsValue(value, s);
    }

    public String getAllowValue() {
        return getAllowValue(allow);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        checkForDuplicate("SystemEntity", "Category", "Type");
        Account a = getAccount();
        if(a == null) {
            throw new Invalid_Value("Account");
        }
        checkGLAccount(a);
        if(!a.getSystemEntityId().equals(getSystemEntityId())) {
            throw new Invalid_State("Account does not belong to this organization");
        }
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        super.validateData(tm);
    }

    private void checkGLAccount(Account a) throws Invalid_State {
        if(Financial.isSpecial(a) || a instanceof EntityAccount) {
            throw new Invalid_State(a + " - Not a suitable Account");
        }
        if(!Financial.hasStrictBalanceControl(a)) {
            throw new Invalid_State(a + " - A/c doesn't have strict balance control");
        }
        int cat = Financial.getCategory(a);
        switch (cat) {
            case 0, 1-> {
            }
            default -> throw new Invalid_State(a + " - Doesn't belong to B/S or P/L");
        }
        if(cat == 0) { // B/S - it should be an asset a/c
            if(Financial.isAsset(a)) {
                return;
            }
            throw new Invalid_State(a + " - Not an asset a/c");
        }
        switch(category) {
            case 0 -> { // Supplier
                if(Financial.getTransactionType(a) == 1) {
                    return;
                }
            }
            case 1 -> { // Customer
                if(Financial.getTransactionType(a) == 2) {
                    return;
                }
            }
        }
        switch(category) {
            case 0 -> throw new Invalid_State("Not a purchase account");
            case 1 -> throw new Invalid_State("Not a sales account");
        }
    }

    @Override
    public void saved() throws Exception {
        AC.remove(systemEntityId + "/" + category + "/" + type);
    }

    public Class<? extends EntityAccount> getEntityAccountClass() {
        if(entityAccountClass == null) {
            try {
                Class<?> eac = JavaClassLoader.getLogic(entityAccountClassName);
                if(eac != null && EntityAccount.class.isAssignableFrom(eac)) {
                    //noinspection unchecked
                    entityAccountClass = (Class<? extends EntityAccount>) eac;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return entityAccountClass;
    }

    /**
     * Retrieves the AccountConfiguration for a given SystemEntity, category, and type.
     *
     * @param systemEntityId The Id of the System Entity.
     * @param category     The category of the AccountConfiguration.
     * @param type         The type of the AccountConfiguration.
     * @return The AccountConfiguration object corresponding to the provided SystemEntity, category, and type.
     */
    public static AccountConfiguration getFor(Id systemEntityId, int category, int type) {
        AccountConfiguration ac = AC.get(systemEntityId + "/" + category + "/" + type);
        if(ac == null) {
            ac = get(AccountConfiguration.class, "SystemEntity=" + systemEntityId + " AND Category="
                    + category + " AND Type=" + type);
            if(ac != null) {
                AC.put(systemEntityId + "/" + category + "/" + type, ac);
            }
        }
        return ac;
    }

    @Override
    public String toString() {
        return name;
    }
}
