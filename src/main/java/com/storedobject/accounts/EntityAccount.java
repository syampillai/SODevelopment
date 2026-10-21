package com.storedobject.accounts;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

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
        if (!loading()) {
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
     * @param name         The name of the accounts to retrieve.
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
    public final String getTitle() {
        return this instanceof InstantaneousAccount ? super.getName() : getEntity().getName();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        entityId = tm.checkTypeAny(this, entityId, AccountEntity.class, false);
        if (!(this instanceof InstantaneousAccount) && getEntity() != null) {
            setName(entity.getName());
        }
        super.validateData(tm);
        if (entity != null) {
            boolean sameParty = tm.getEntity().getId().equals(entity.getPartyId());
            if ((this instanceof InstantaneousAccount && !sameParty) || (!(this instanceof InstantaneousAccount) && sameParty)) {
                throw new Invalid_Value("Entity");
            }
        }
    }

    @Override
    protected void validateAccountStatus() throws Exception {
        if (Financial.getCategory(this) != 0) { // Not a balance-sheet a/c
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
     * @param tm            The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @return The created EntityAccount object.
     * @throws Exception if any error occurs.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity) throws Exception {
        return createFor(tm, accountEntity, EntityAccount.class);
    }

    /**
     * Creates an EntityAccount for the provided {@link AccountEntity}, category, and type of invoice.
     * <p>Note: Category is typically 0 for suppliers and 1 for customers.</p>
     *
     * @param tm            The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @param category Category.
     * @param type Type.
     * @return The created EntityAccount object.
     * @throws Exception if any error occurs.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity, int category,
                                          int type) throws Exception {
        AccountConfiguration ac = AccountConfiguration.getFor(tm.getEntity().getEntityId(), category, type);
        if (ac == null) {
            return null;
        }
        return createFor(tm, accountEntity, ac.getEntityAccountClass());
    }


    /**
     * Creates an EntityAccount for the provided {@link AccountEntity} and class of {@link EntityAccount}.
     *
     * @param tm            The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @param accountClass  Class of the {@link EntityAccount}.
     * @return The created EntityAccount object.
     * @throws Exception if any error occurs.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity,
                                          Class<? extends EntityAccount> accountClass) throws Exception {
        return createFor(tm, accountEntity, tm.getCurrency(), accountClass);
    }

    /**
     * Creates an EntityAccount for the provided {@link AccountEntity} and class of {@link EntityAccount}.
     *
     * @param tm The TransactionManager to be used for database transactions.
     * @param accountEntity The {@link AccountEntity} for which the {@link EntityAccount} needs to be created.
     * @param currency Currency of the account.
     * @param accountClass Class of the {@link EntityAccount}.
     * @return The created EntityAccount object.
     * @throws Exception if any error occurs.
     */
    public static EntityAccount createFor(TransactionManager tm, AccountEntity<?> accountEntity, Currency currency,
                                                          Class<? extends EntityAccount> accountClass) throws Exception {
        if(tm.getEntity().getEntityId().equals(accountEntity.getId())
                && !InstantaneousAccount.class.isAssignableFrom(accountClass)) {
            throw new Invalid_State("Invalid account class");
        }
        EntityAccount ea = list(accountClass, "Entity=" + accountEntity.getId(), true)
                .filter(a -> a.getCurrency() == currency)
                .findFirst();
        if(ea != null) {
            return ea;
        }
        try {
            ea = accountClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            return null;
        }
        String chartName;
        if(InstantaneousAccount.class.isAssignableFrom(accountClass)) {
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
        String an = (accountEntity.isPersonal() ? "P" : "B") + accountEntity.getId();
        if(tm.getCurrency() != currency) {
            an += currency.getCurrencyCode();
        }
        ea.setNumber(an);
        ea.setCurrency(currency);
        tm.transact(ea::save);
        return ea;
    }

    private static Date[] bucketDates(int bucketDays) {
        if(bucketDays <= 0) {
            bucketDays = 30;
        }
        Date[] dates = new Date[4];
        Date d = DateUtility.today();
        dates[3] = d;
        dates[2] = DateUtility.addDay(d, -bucketDays);
        dates[1] = DateUtility.addDay(d, -(bucketDays << 1));
        dates[0] = DateUtility.addDay(d, -bucketDays * 3);
        return dates;
    }

    private Date[] bucketDates(Date[] dates) {
        if(this instanceof InstantaneousAccount) {
            throw new SORuntimeException("Ageing analysis is not applicable for instantaneous accounts");
        }
        if(dates == null || dates.length == 0) {
            dates = bucketDates(0);
        }
        Arrays.sort(dates);
        return dates;
    }

    private Ledger ledger(Date[] dates, Money[] balances) {
        Ledger ledger = getLedger(dates[0], dates[dates.length - 1]);
        Money m = ledger.getOpeningBalance();
        balances[0] = m;
        m = m.zero();
        for(int i = 1; i < dates.length; i++) {
            balances[i] = m;
        }
        return ledger;
    }

    /**
     * Retrieves the ageing balance of accounts based on the specified bucket interval in the past days.
     *
     * @param bucketDays the number of days per bucket to categorize the ageing balance
     * @return an array of Money objects representing the ageing balance for each bucket
     */
    public Money[] getAgeingBalances(int bucketDays) {
        return getAgeingBalances(bucketDates(bucketDays));
    }

    /**
     * Calculates and returns the ageing balance for an account based on the provided array of dates.
     * The ageing balance represents the balance of a financial account divided across time periods
     * as defined by the input dates. If the account is of type InstantaneousAccount, this method will
     * throw an exception as ageing balance is not applicable.
     *
     * @param dates an array of dates used as cut-off points for dividing the account balance
     *              into ageing buckets. Dates should be in descending order (most recent first).
     *              If null or empty, default bucket dates will be used.
     * @return an array of Money objects representing the balance for each ageing period defined
     *         by the input dates. The size of the array matches the length of the provided dates array.
     *         If no transactions are present, the balances will correspond to zero or the account's
     *         opening balance conditions.
     */
    public Money[] getAgeingBalances(Date[] dates) {
        dates = bucketDates(dates);
        int i;
        Money[] balances = new Money[dates.length];
        Money m = getBalance();
        if(!m.isDebit()) {
            balances[dates.length - 1] = m;
            m = m.zero();
            for(i = 0; i < (dates.length - 1); i++) {
                balances[i] = m;
            }
            return balances;
        }
        Ledger ledger = ledger(dates, balances);
        Date d;
        for(LedgerEntry le: ledger) {
            d = le.getDate();
            m = le.getAmount();
            if(m.isDebit()) {
                processLE(dates, balances, m, d);
            } else {
                balances[0] = balances[0].add(m);
            }
        }
        m = m.zero();
        for(i = 0; i < dates.length; i++) {
            if(m.isCredit()) {
                balances[i] = balances[i].add(m);
            }
            if(!balances[i].isDebit()) {
                m = balances[i];
                balances[i] = m.zero();
            } else {
                m = m.zero();
            }
        }
        return balances;
    }

    /**
     * Calculates and retrieves an array of ageing payments based on the specified bucket duration.
     *
     * @param bucketDays the number of days to determine the ageing bucket for categorizing payments
     * @return an array of {@code Money} objects representing the ageing payments for the specified bucket duration
     */
    public Money[] getAgeingPayments(int bucketDays) {
        return getAgeingPayments(bucketDates(bucketDays));
    }

    /**
     * Calculates the ageing payments for a given array of dates. The method processes ledger entries
     * and assigns payment balances to appropriate date buckets based on their credit or debit status.
     *
     * @param dates an array of {@code Date} objects representing the bucket dates for payments; these dates
     *              should be ordered in ascending order of time.
     * @return an array of {@code Money} objects representing the payment balances corresponding to each
     *         date bucket. The index of the array corresponds to the date ranges defined by the input dates.
     */
    public Money[] getAgeingPayments(Date[] dates) {
        dates = bucketDates(dates);
        int i;
        Money[] balances = new Money[dates.length];
        Money m = getBalance();
        if(!m.isCredit()) {
            balances[dates.length - 1] = m;
            m = m.zero();
            for(i = 0; i < (dates.length - 1); i++) {
                balances[i] = m;
            }
            return balances;
        }
        Ledger ledger = ledger(dates, balances);
        Date d;
        for(LedgerEntry le: ledger) {
            d = le.getDate();
            m = le.getAmount();
            if(m.isCredit()) {
                processLE(dates, balances, m, d);
            } else {
                balances[0] = balances[0].add(m);
            }
        }
        m = m.zero();
        for(i = 0; i < dates.length; i++) {
            if(m.isDebit()) {
                balances[i] = balances[i].add(m);
            }
            if(!balances[i].isCredit()) {
                m = balances[i];
                balances[i] = m.zero();
            } else {
                m = m.zero();
            }
        }
        return balances;
    }

    private void processLE(Date[] dates, Money[] balances, Money m, Date d) {
        int i;
        boolean added = false;
        for(i = 1; i < dates.length; i++) {
            if(d.before(dates[i])) {
                balances[i] = balances[i].add(m);
                added = true;
                break;
            }
        }
        if(!added) {
            balances[dates.length - 1] = balances[dates.length - 1].add(m);
        }
    }

    /**
     * Determines whether the account is identified as a customer account.
     *
     * @return {@code true} if the account is neither an instance of {@link InstantaneousAccount}
     *         nor identified as a supplier by the {@code isSupplier} method, {@code false} otherwise.
     */
    public boolean isCustomer() {
        return !(this instanceof InstantaneousAccount) && Financial.isAsset(this);
    }

    /**
     * Determines whether the account is identified as a supplier account.
     *
     * @return {@code true} if the account is not an instance of {@link InstantaneousAccount}
     *         and is identified as a liability by the {@link Financial#isLiability} method,
     *         {@code false} otherwise.
     */
    public boolean isSupplier() {
        return !(this instanceof InstantaneousAccount) && Financial.isLiability(this);
    }
}
