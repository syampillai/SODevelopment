package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Currency;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * This class represents an Account. Account has a status ({@link #getAccountStatus()}) which is a bit pattern with
 * following values:
 * <pre>
 *    0: [Account level] 0 = Active, 1 = Closed
 *  2,1: [Account level, Overrides 10,9] 00 = Bits 10,9 applicable, 01 = Debits allowed (credit blocked),
 *                                       10 = Credits allowed (debits blocked), 11 = Frozen
 *    3: [Chart level] 0 = Balance control is not strict, 1 = Strict balance control
 *    4: [Chart level] (Strictly applied if bit 3 = 1) 0 = Debit balance, 1 = Credit balance
 *  6,5: [Chart level] 00 = BS item, 01 = PL item, 10 = Stock item, 11 = Contingent.
 *    7: [Chart level] 0 = Normal, 1 = Deep frozen (No transactions, no way to override).
 *    8: [Chart level] 0 = No limit check, 1 = Limit check
 * 10,9: [Chart level] 00 = Debit and credit trans allowed, 01 = Generally debited, 10 = Generally credited, 11 = Frozen
 * </pre>
 */
public class Account extends StoredObject implements OfEntity, HasName {

    private Id chartId, systemEntityId;
    private Money openingBalance, openingBalanceLC, balance, balanceLC;
    private int accountStatus;
    private String name, number, alternateNumber = "";

    /**
     * Constructs an Account in local currency.
     *
     * @param chartId Chart Id.
     */
    public Account(Id chartId) {
        this(chartId, null, null);
    }

    /**
     * Constructs an Account.
     *
     * @param chartId Chart Id.
     * @param currency The accounting currency.
     */
    public Account(Id chartId, String currency) {
        this(chartId, currency, null);
    }

    /**
     * Constructs an Account in local currency.
     *
     * @param chartId Chart Id.
     * @param systemEntityId Id of the System Entity where this account is opened.
     */
    public Account(Id chartId, Id systemEntityId) {
        this(chartId, null, systemEntityId);
    }

    /**
     * Constructs an Account.
     *
     * @param chartId Chart Id.
     * @param systemEntityId Id of the System Entity where this account is opened.
     * @param currency Account's currency.
     */
    public Account(Id chartId, String currency, Id systemEntityId) {
        this.systemEntityId = systemEntityId;
        this.chartId = chartId;
        setCurrency(currency);
        accountStatus = 0;
    }

    /**
     * Constructs a local currency Account.
     */
    public Account() {
        this(null, null, null);
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Chart", "id");
        columns.add("Number", "text");
        columns.add("AlternateNumber", "text");
        columns.add("Name", "text");
        columns.add("OpeningBalance", "amount");
        columns.add("LocalCurrencyOpeningBalance", "amount");
        columns.add("Balance", "amount");
        columns.add("LocalCurrencyBalance", "amount");
        columns.add("AccountStatus", "int");
    }

    public static void readOnlyColumns(ColumnNames columnNames) {
        columnNames.add("SystemEntity");
        columnNames.add("OpeningBalance");
        columnNames.add("LocalCurrencyOpeningBalance");
        columnNames.add("Balance");
        columnNames.add("LocalCurrencyBalance");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity,Id", false);
        indices.add("lower(Name)", false);
        indices.add("lower(Number)", true);
        indices.add("lower(AlternateNumber)", "AlternateNumber<>''",true);
        indices.add("Chart,SystemEntity", false);
    }

    public static String[] displayColumns() {
        return new String[] { "Number", "Name" };
    }

    public static String[] browseColumns() {
        return new String[] { "Number", "Name", "Balance" };
    }

    public static String[] protectedColumns() {
        return new String[] { "SystemEntity", "OpeningBalance", "LocalCurrencyOpeningBalance",
                "LocalCurrencyBalance", "AccountStatus" };
    }

    /**
     * Refreshes the balance and account status from the database.
     */
    public final void refresh() {
    }

    /**
     * Gets the local currency.
     *
     * @return The local currency.
     */
    public Currency getLocalCurrency() {
        return balanceLC.getCurrency();
    }

    /**
     * Gets the currency.
     *
     * @return The currency.
     */
    public Currency getCurrency() {
        return balance.getCurrency();
    }

    /**
     * Sets the currency.
     *
     * @param currency The currency.
     */
    public void setCurrency(Currency currency) {
        if(currency != null && balance == null) {
            openingBalance = new Money(currency);
        }
    }

    /**
     * Sets the currency.
     *
     * @param currency The currency.
     */
    public void setCurrency(String currency) {
        setCurrency(Money.getCurrency(currency));
    }

    /**
     * Gets the Id of the System Entity.
     *
     * @return The Id of the System Entity
     */
    @SetNotAllowed
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity.getId());
    }

    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    /**
     * Gets the System Entity.
     *
     * @return The System Entity
     */
    public SystemEntity getSystemEntity() {
        return get(getTransaction(), SystemEntity.class, systemEntityId);
    }

    final void addBalance(Money balance) {
        this.balance = this.balance.add(balance);
    }

    public void setBalance(Object balance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.balance = Money.create(balance);
    }

    @SetNotAllowed
    public Money getBalance() {
        return balance;
    }

    public Money getBalance(Date date) {
        return getBalance();
    }

    final void addLocalCurrencyBalance(Money localCurrencyBalance) {
        this.balanceLC = this.balanceLC.add(localCurrencyBalance);
    }

    public void setLocalCurrencyBalance(Object localCurrencyBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.balanceLC = Money.create(localCurrencyBalance);
    }

    @SetNotAllowed
    public Money getLocalCurrencyBalance() {
        return balanceLC;
    }

    public Money getLocalCurrencyBalance(Date date) {
        return getLocalCurrencyBalance();
    }

    public void setOpeningBalance(Object openingBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.openingBalance = Money.create(openingBalance);
    }

    @SetNotAllowed
    public Money getOpeningBalance() {
        return openingBalance;
    }

    public void setLocalCurrencyOpeningBalance(Object localCurrencyOpeningBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.openingBalanceLC = Money.create(localCurrencyOpeningBalance);
    }

    public Money getLocalCurrencyOpeningBalance() {
        return openingBalanceLC;
    }

    public boolean isLocalCurrency() {
        return balance.getCurrency() == balanceLC.getCurrency();
    }

    public boolean isForeignCurrency() {
        return !isLocalCurrency();
    }

    @SetNotAllowed
    public int getAccountStatus() {
        return accountStatus;
    }

    /**
     * Set the account status. If invoked on an active account, this will throw a run-time exception unless
     * it is a virtual instance.
     *
     * @param accountStatus Status to set.
     */
    public void setAccountStatus(int accountStatus) {
        boolean v = isVirtual();
        if(!loading() && !v) {
            throw new Set_Not_Allowed("Status");
        }
        if(v) {
            accountStatus &= 7; // Only 3 bits can be set.
        }
        this.accountStatus = accountStatus;
    }

    public final void close(Transaction transaction) throws Exception {
        if(isClosed()) {
            return;
        }
        accountStatus |= 1;
        save(transaction);
    }

    public final void reopen(Transaction transaction) throws Exception {
        if(!isClosed()) {
            return;
        }
        accountStatus &= ~1;
        save(transaction);
    }

    public final void allow(Transaction transaction, boolean debit, boolean credit) throws Exception {
        int s = 0;
        if(!credit) {
            s = 1;
        }
        if(!debit) {
            s |= 0b10;
        }
        accountStatus = accountStatus | (s << 1);
        save(transaction);
    }

    public final boolean isClosed() {
        return (accountStatus & 1) == 1;
    }

    private boolean isNonZero(Money m) {
        return m != null && !m.isZero();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(Id.isNull(systemEntityId) && inserted() && tm.getEntity() != null) {
            systemEntityId = tm.getEntity().getId();
        } else {
            systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class);
        }
        if(inserted()) {
            SystemEntity se = getSystemEntity();
            balanceLC = openingBalanceLC = new Money(se.getCurrency());
            if(openingBalance == null) {
                openingBalance = Objects.requireNonNullElseGet(balance, () -> openingBalanceLC);
            }
            if(balance == null) {
                balance = openingBalance;
            }
            if(openingBalance.getCurrency() != balance.getCurrency()) {
                throw new Invalid_State("Currency mismatch - " + openingBalance.getCurrency().getCurrencyCode()
                        + " <> " + balance.getCurrency().getCurrencyCode());
            }
            if(isNonZero(balance) || isNonZero(balanceLC) || isNonZero(openingBalance) || isNonZero(openingBalanceLC)) {
                throw new Invalid_State("Balance must be zero");
            }
        }
        name = getTitle();
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        number = toCode(number).replace("/", "");
        if(StringUtility.isWhite(number)) {
            throw new Invalid_Value("Number");
        }
        Account a = get(Account.class, "Number='" + number + "' AND Id<>" + getId(), true);
        if(a != null) {
            throw new Invalid_State("Duplicate Number - " + number);
        }
        alternateNumber = toCode(alternateNumber);
        if(!alternateNumber.isEmpty()) {
            a = get(Account.class, "AlternateNumber='" + alternateNumber + "' AND Id<>" + getId(), true);
            if(a != null) {
                throw new Invalid_State("Duplicate Alternate Number - " + alternateNumber);
            }
        }
        AccountChart ac = getChart();
        if(ac == null) {
            throw new Invalid_Value("Account Chart");
        }
        if(!ac.getAccountsAllowed()) {
            throw new Invalid_State("Account Chart '" + ac.getName() + "' doesn't allow accounts");
        }
        validateChart();
        accountStatus = ac.getStatus() | (accountStatus & 0x07);
        super.validateData(tm);
    }

    private void validateChart() throws Exception {
        if(getClass() == Account.class) {
            return;
        }
        try (Query q = query(AccountChartMap.class, "Chart", "lower(AccountClassName)='"
                + getClass().getName().toLowerCase() + "' AND Chart=" + chartId)) {
            if (q.hasNext()) {
                return;
            }
        }
        String m = getClass().getName();
        throw new Invalid_State("Account Chart not Mapped for '"
                + StringUtility.makeLabel(m.substring(m.lastIndexOf('.') + 1)) + "'");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SetNotAllowed
    @Column(style = "(code)")
    public final String getNumber() {
        return number;
    }

    public final void setNumber(String number) {
        if(!loading()) {
            throw new Set_Not_Allowed("Account Number");
        }
        this.number = number;
    }

    @Column(required = false, style = "(code)")
    public String getAlternateNumber() {
        return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber;
    }

    public void setChart(BigDecimal chartId) {
        setChart(new Id(chartId));
    }

    public void setChart(Id chartId) {
        this.chartId = chartId;
        if(!loading()) {
            accountStatus = getChart().getStatus() | (accountStatus & 0x07);
        }
    }

    public void setChart(AccountChart chart) {
        setChart(chart.getId());
    }

    public Id getChartId() {
        return chartId;
    }

    public AccountChart getChart() {
        if(chartId == null) {
            try (Query q = query(AccountChartMap.class, "Chart", "lower(AccountClassName)='"
                    + getClass().getName().toLowerCase() + "'")) {
                for (ResultSet rs : q) {
                    chartId = new Id(rs.getBigDecimal(1));
                    break;
                }
            } catch (Exception ignored) {
            }
        }
        return get(AccountChart.class, chartId);
    }

    public String getTitle() {
        return name;
    }

    @Override
    public final String toString() {
        return number + " (" + getCurrency().getCurrencyCode() + ") " + toSubstring();
    }

    @Override
    public String toDisplay() {
        return toString();
    }

    public String toSubstring() {
        return name;
    }

    public final Money createAmount(BigDecimal amount) {
        return new Money(amount, balance.getCurrency());
    }

    public final Money createLocalCurrencyAmount(BigDecimal amount) {
        return new Money(amount, balanceLC.getCurrency());
    }

    public final void setOpeningBalance(TransactionManager tm, Money amount) throws Exception {
        setOpeningBalance(tm, amount, amount);
    }

    public final void setOpeningBalance(TransactionManager tm, Money amount, Money localCurrencyAmount) throws Exception {
        if(getCurrency() != amount.getCurrency() || getLocalCurrency() != localCurrencyAmount.getCurrency()) {
            throw new Invalid_State("Currency mismatch");
        }
        refresh();
        addToOpeningBalance(tm, amount.subtract(getOpeningBalance()),
                localCurrencyAmount.subtract(getLocalCurrencyOpeningBalance()));
    }

    public final void addToOpeningBalance(TransactionManager tm, Money amount) throws Exception {
        addToOpeningBalance(tm, amount, amount);
    }

    public final void addToOpeningBalance(TransactionManager tm, Money amount, Money localCurrencyAmount) throws Exception {
        if(new Random().nextBoolean()) {
            throw new Exception();
        }
    }

    public static <A extends Account> A getByNameOrNumber(SystemEntity systemEntity, Class<A> accountClass,
                                                          String nameOrNumber, boolean any) {
        if(nameOrNumber == null || nameOrNumber.isEmpty()) {
            return null;
        }
        String name = ")='" + nameOrNumber.trim().toLowerCase().replace("'", "''") + "'";
        A account = get(accountClass, (systemEntity == null ? "(" : ("SystemEntity=" + systemEntity.getId()
                + " AND (")) + "lower(Name" + name + " OR lower(Number" + name
                + " OR lower(AlternateNumber" + nameOrNumber + ")", any);
        return account == null ? listByNameOrNumber(systemEntity, accountClass, nameOrNumber, any)
                .single(false) : account;
    }

    public static <A extends Account> ObjectIterator<A> listByNameOrNumber(final SystemEntity systemEntity,
                                                                           final Class<A> accountClass,
                                                                           String nameOrNumber, final boolean any) {
        if(nameOrNumber == null || nameOrNumber.isEmpty()) {
            return ObjectIterator.create();
        }
        nameOrNumber = nameOrNumber.toLowerCase().replace("'", "''");
        nameOrNumber = ") LIKE '" + nameOrNumber + "%'";
        return list(accountClass, (systemEntity == null ? "(" : ("SystemEntity=" + systemEntity.getId()
                + " AND (")) + "lower(Name" + nameOrNumber + " OR lower(Number" + nameOrNumber
                + " OR lower(AlternateNumber" + nameOrNumber + ")", any);
    }

    public static Account get(SystemEntity systemEntity, String name) {
        return getByNameOrNumber(systemEntity, Account.class, name, true);
    }

    public static ObjectIterator<? extends Account> list(SystemEntity systemEntity, String name) {
        return listByNameOrNumber(systemEntity, Account.class, name, true);
    }

    public static Account getFor(String number) {
        return get(Account.class, "lower(number)='" + toCode(number) + "'", true);
    }

    public Ledger getLedger(DatePeriod period) {
        return new Ledger() {
            @Override
            public Money getBalance() {
                return null;
            }

            @Override
            public Money getLocalCurrencyBalance() {
                return null;
            }

            @Override
            public java.sql.Date getDate() {
                return null;
            }

            @Override
            public Account getAccount() {
                return null;
            }

            @Override
            public DatePeriod getPeriod() {
                return null;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public LedgerEntry next() {
                return null;
            }
        };
    }
}
