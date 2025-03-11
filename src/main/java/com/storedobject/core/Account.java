package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;

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
    private String name, alternateNumber = "";
    protected String number;

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
        return new String[] { "AccountNumber", "Name" };
    }

    public static String[] browseColumns() {
        return new String[] { "AccountNumber", "Name" };
    }

    public static String[] searchColumns() {
        return new String[] { "Number", "Name" };
    }

    public static String[] protectedColumns() {
        return new String[] { "OpeningBalance", "LocalCurrencyOpeningBalance", "LocalCurrencyBalance", "AccountStatus" };
    }

    public final String getAccountNumber() {
        if(this instanceof AccountTitle) {
            return number.substring(0, number.indexOf("-ALT"));
        }
        return number;
    }

    /**
     * Refreshes the balance and account status from the database.
     */
    public final void refresh() {
        RawSQL sql;
        Transaction t = getTransaction();
        sql = t == null ? new RawSQL() : ((AbstractTransaction) t).getSQL();
        try {
            sql.execute("SELECT Balance,LocalCurrencyBalance,AccountStatus FROM core.Account WHERE Id=" + getId());
            ResultSet rs = sql.getResult();
            balance = Money.create(rs.getObject(1));
            balanceLC = Money.create(rs.getObject(2));
            accountStatus = rs.getInt(3);
        } catch(Exception ignored) {
        } finally {
            sql.close();
        }
    }

    /**
     * Gets the local currency.
     *
     * @return The local currency.
     */
    public final Currency getLocalCurrency() {
        return balanceLC.getCurrency();
    }

    /**
     * Gets the currency.
     *
     * @return The currency.
     */
    public final Currency getCurrency() {
        return openingBalance.getCurrency();
    }

    /**
     * Sets the currency.
     *
     * @param currency The currency.
     */
    public final void setCurrency(Currency currency) {
        if(currency != null && balance == null) {
            openingBalance = new Money(currency);
        }
    }

    /**
     * Sets the currency.
     *
     * @param currency The currency.
     */
    public final void setCurrency(String currency) {
        setCurrency(Money.getCurrency(currency));
    }

    /**
     * Gets the Id of the System Entity.
     *
     * @return The Id of the System Entity
     */
    @SetNotAllowed
    public final Id getSystemEntityId() {
        return systemEntityId;
    }

    public final void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public final void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity.getId());
    }

    public final void setSystemEntity(Id systemEntityId) {
        if(Objects.equals(systemEntityId, this.systemEntityId)) {
            return;
        }
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
    public final SystemEntity getSystemEntity() {
        return get(getTransaction(), SystemEntity.class, systemEntityId);
    }

    final void addBalance(Money balance) {
        this.balance = this.balance.add(balance);
    }

    public final void setBalance(Object balance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.balance = Money.create(balance);
    }

    @SetNotAllowed
    public final Money getBalance() {
        if(this instanceof AccountTitle at) {
            return at.getAccount().balance;
        }
        return balance;
    }

    public final Money getBalance(Date date) {
        if(this instanceof AccountTitle at) {
            return at.getAccount().getBalance(date);
        }
        if(date == null || !date.before(getSystemEntity().getWorkingDate())) {
            return getBalance();
        }
        return createAmount(Balances.getBalance(getId(), date));
    }

    final void addLocalCurrencyBalance(Money localCurrencyBalance) {
        this.balanceLC = this.balanceLC.add(localCurrencyBalance);
    }

    public final void setLocalCurrencyBalance(Object localCurrencyBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.balanceLC = Money.create(localCurrencyBalance);
    }

    @SetNotAllowed
    public final Money getLocalCurrencyBalance() {
        if(this instanceof AccountTitle at) {
            return at.getAccount().balanceLC;
        }
        return balanceLC;
    }

    public final Money getLocalCurrencyBalance(Date date) {
        if(this instanceof AccountTitle at) {
            return at.getAccount().getLocalCurrencyBalance(date);
        }
        if(date == null || !date.before(getSystemEntity().getWorkingDate())) {
            return getLocalCurrencyBalance();
        }
        return createLocalCurrencyAmount(Balances.getBalanceLC(getId(), date));
    }

    public final void setOpeningBalance(Object openingBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.openingBalance = Money.create(openingBalance);
    }

    public final Money getOpeningBalance(Date date) {
        return getBalance(DateUtility.addDay(date, -1));
    }

    @SetNotAllowed
    public final Money getOpeningBalance() {
        if(this instanceof AccountTitle at) {
            return at.getAccount().openingBalance;
        }
        return openingBalance;
    }

    public final void setLocalCurrencyOpeningBalance(Object localCurrencyOpeningBalance) {
        if(!loading()) {
            throw new Set_Not_Allowed("Balance");
        }
        this.openingBalanceLC = Money.create(localCurrencyOpeningBalance);
    }

    public final Money getLocalCurrencyOpeningBalance(Date date) {
        return getLocalCurrencyBalance(DateUtility.addDay(date, -1));
    }

    public final Money getLocalCurrencyOpeningBalance() {
        if(this instanceof AccountTitle at) {
            return at.getAccount().openingBalanceLC;
        }
        return openingBalanceLC;
    }

    public final boolean isLocalCurrency() {
        return balance.getCurrency() == balanceLC.getCurrency();
    }

    public final boolean isForeignCurrency() {
        return !isLocalCurrency();
    }

    @SetNotAllowed
    public final int getAccountStatus() {
        return accountStatus;
    }

    /**
     * Set the account status. If invoked on an active account, this will throw a run-time exception unless
     * it is a virtual instance.
     *
     * @param accountStatus Status to set.
     */
    public final void setAccountStatus(int accountStatus) {
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

    void checkSystemEntity(TransactionManager tm) throws Exception {
        if(Id.isNull(systemEntityId) && inserted() && tm.getEntity() != null) {
            systemEntityId = tm.getEntity().getId();
        } else {
            systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class);
        }
    }

    public final String getStatusDescription() {
        return getStatusDescription(accountStatus);
    }

    public static String getStatusDescription(int accountStatus) {
        String s = null;
        int as = accountStatus >> 1;
        switch (as & 0b11) {
            case 0b00 -> {
                switch (as >> 8) { // We get (10, 9) bits
                    case 0b00 -> s = "Debits & credits allowed";
                    case 0b01 -> s = "Generally debited";
                    case 0b10 -> s = "Generally credited";
                    case 0b11 -> s = "Frozen";
                    default -> s = "Unknown " + (as >> 8) + " / " + accountStatus;
                }
            }
            case 0b01 -> s = "Debits allowed (credit blocked)";
            case 0b10 -> s = "Credits allowed (debit blocked)";
            case 0b11 -> s = "Frozen";
        }
        as >>= 2;
        if((as & 1) == 1) {
            s = "Strict balance control, " + s;
        }
        as >>= 1;
        if((as & 1) == 0) {
            s = "Debit balance type, " + s;
        } else {
            s = "Credit balance type, " + s;
        }
        as >>= 1;
        switch (as & 0b11) {
            case 0b00 -> s = "B/S item, " + s;
            case 0b01 -> s = "P/L item, " + s;
            case 0b10 -> s = "Stock item, " + s;
            case 0b11 -> s = "Contingent item, " + s;
        }
        as >>= 2;
        if((as & 1) == 1) {
            s = "Deep frozen, " + s;
        }
        as >>= 1;
        if((as & 1) == 1) {
            s = "Limit checked, " + s;
        }
        if((accountStatus & 1) == 1) {
            s += ", Closed";
        }
        return s;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        checkSystemEntity(tm);
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
        name = getTitle().trim();
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        number = toCode(number);
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
        if(Id.isNull(chartId)) {
            AccountChart.set(this, tm);
        }
        AccountChart ac = getChart();
        if(ac == null) {
            throw new Invalid_Value("Account Chart");
        }
        if(!ac.getAccountsAllowed()) {
            throw new Invalid_State("Account Chart '" + ac.getName() + "' doesn't allow accounts");
        }
        accountStatus = ac.getStatus() | (accountStatus & 0b111);
        super.validateData(tm);
    }

    /**
     * Validate account status to make sure that every status bit value adheres to the type of account.
     *
     * @throws Exception if status is invalid.
     */
    protected void validateAccountStatus() throws Exception {
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        RawSQL sql = new RawSQL("SELECT 1 FROM core.Ledger WHERE Account=" + getId() + " LIMIT 1");
        try {
            sql.execute();
            if(!sql.eoq()) {
                sql.cancel();
                throw new SOException("Account [" + this + "] can not be deleted - Transactions exist!");
            }
        } finally {
            sql.close();
        }
    }

    @Override
    void savedCore() throws Exception {
        TransactionManager.accounting = true;
        Class<? extends Account> ac = getClass();
        if(ac == Account.class || ac == AccountTitle.class) {
            return;
        }
        if(!exists(AccountChartMap.class, "lower(AccountClassName)='" + ac.getName().toLowerCase() + "'")) {
            AccountChartMap acm = new AccountChartMap();
            acm.setChart(chartId);
            acm.setAccountClassName(ac.getName());
            acm.save(getTransaction());
        }
    }

    public final String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
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

    public final void setChart(BigDecimal chartId) {
        setChart(new Id(chartId));
    }

    public final void setChart(Id chartId) {
        this.chartId = chartId;
        if(!loading()) {
            accountStatus = getChart().getStatus() | (accountStatus & 0b111);
        }
    }

    public final void setChart(AccountChart chart) {
        setChart(chart.getId());
    }

    public final Id getChartId() {
        return chartId;
    }

    public final AccountChart getChart() {
        return get(AccountChart.class, chartId);
    }

    /**
     * Get the title of the account. By default, this will return {@link #getName()}. However, if you return
     * some other value, that will be automatically set as the name of the account when save to the database.
     *
     * @return Title of the account.
     */
    public String getTitle() {
        return name;
    }

    @Override
    public final String toString() {
        String a = getAccountNumber();
        String ss = toSubstring();
        String s = "";
        if(!a.equals(ss.toUpperCase())) {
            s += a + " ";
        }
        Currency c = getCurrency();
        if(c != getLocalCurrency() && !number.endsWith(c.getCurrencyCode())) {
            s += "(" + getCurrency().getCurrencyCode() + ") ";
        }
        return s + ss;
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

    public final void addToOpeningBalance(TransactionManager tm, Money amount, Money localCurrencyAmount)
            throws Exception {
        if(amount.isZero() && localCurrencyAmount.isZero()) {
            return;
        }
        if(getCurrency() != amount.getCurrency() || getLocalCurrency() != localCurrencyAmount.getCurrency()) {
            throw new Invalid_State("Currency mismatch");
        }
        Account oa = OffsetAccount.create(tm, getSystemEntity());
        String update0 = "UPDATE core.Account SET LocalCurrencyBalance=ROW((LocalCurrencyBalance).Amount",
                update1 = ",(Balance).Currency),Balance=ROW((Balance).Amount",
                update2 = ",(Balance).Currency),LocalCurrencyOpeningBalance=ROW((LocalCurrencyOpeningBalance).Amount",
                update3 = ",(Balance).Currency),OpeningBalance=ROW((OpeningBalance).Amount",
                update4 = ",(Balance).Currency) WHERE Id=";
        String v = amount.getValue().toPlainString(), vLC = localCurrencyAmount.getValue().toPlainString();
        String x1 = vLC.startsWith("-") ? vLC : ("+" + vLC);
        String x2 = v.startsWith("-") ? v : ("+" + v);
        String x3 = vLC.startsWith("-") ? ("+" + vLC.substring(1)) : ("-" + vLC);
        tm.transact(t -> {
            save(t);
            oa.save(t);
            RawSQL sql = ((DBTransaction)t).getSQL();
            try {
                sql.executeUpdate(update0 + x1 + update1 + x2 + update2 + x1 + update3 + x2 + update4 + getId());
                sql.executeUpdate(update0 + x3 + update1 + x3 + update2 + x3 + update3 + x3 + update4 + oa.getId());
                sql.executeUpdate("DELETE FROM core.AccountBalance WHERE Account=" + getId());
                sql.executeUpdate("DELETE FROM core.AccountBalance WHERE Account=" + oa.getId());
            } finally {
                sql.close();
            }
        });
        Balances.removeAccount(getId());
        Balances.removeAccount(oa.getId());
        openingBalance = openingBalance.add(amount);
        openingBalanceLC = openingBalanceLC.add(localCurrencyAmount);
        refresh();
    }

    public static <A extends Account> A getByNameOrNumber(SystemEntity systemEntity, Class<A> accountClass,
                                                          String nameOrNumber, boolean any) {
        if(nameOrNumber == null || nameOrNumber.isEmpty()) {
            return null;
        }
        String name = ")='" + nameOrNumber.trim().toLowerCase().replace("'", "''") + "'";
        A account = get(accountClass, (systemEntity == null ? "(" : ("SystemEntity=" + systemEntity.getId()
                + " AND (")) + "lower(Name" + name + " OR lower(Number" + name
                + " OR lower(AlternateNumber" + name + ")", any);
        if(account != null) {
            @SuppressWarnings("unchecked") A at = (A) AccountTitle.list(systemEntity, name)
                    .map(AccountTitle::getAccount)
                    .filter(a -> any ? accountClass.isAssignableFrom(a.getClass()) : accountClass == a.getClass())
                    .single(false);
            if(at == null || at == account) {
                return account;
            }
        }
        return listByNameOrNumber(systemEntity, accountClass, nameOrNumber, any).single(false);
    }

    public static <A extends Account> ObjectIterator<A> listByNameOrNumber(final SystemEntity systemEntity,
                                                                           final Class<A> accountClass,
                                                                           String nameOrNumber,
                                                                           final boolean any) {
        if(nameOrNumber == null || nameOrNumber.isEmpty()) {
            return ObjectIterator.create();
        }
        nameOrNumber = nameOrNumber.toLowerCase().replace("'", "''");
        String name = ") LIKE '" + nameOrNumber + "%'";
        return list(accountClass, (systemEntity == null ? "(" : ("SystemEntity=" + systemEntity.getId()
                + " AND (")) + "lower(Name" + name + " OR lower(Number" + name
                + " OR lower(AlternateNumber" + name + ")", any)
                .add(AccountTitle.list(systemEntity, nameOrNumber, accountClass, any));
    }

    public static Account get(SystemEntity systemEntity, String name) {
        return getByNameOrNumber(systemEntity, Account.class, name, true);
    }

    public static ObjectIterator<? extends Account> list(SystemEntity systemEntity, String name) {
        return listByNameOrNumber(systemEntity, Account.class, name, true);
    }

    public static Account getFor(String number) {
        return get(Account.class, "lower(number)='" + toCode(number).toLowerCase() + "'", true);
    }

    public Ledger getLedger(DatePeriod period) {
        return new AccountLedger(period);
    }

    public Ledger getLedger(Date from, Date to) {
        return new AccountLedger(DatePeriod.create(from, to));
    }

    public String actionPrefixForUI() {
        return "AC";
    }

    class AccountLedger implements Ledger {

        static final String ENTRY =
                "SELECT TranId,Object,EntrySerial,Date,Amount,LocalCurrencyAmount,Narration,Type,ValueDate FROM core.Ledger WHERE Account=";
        private final DatePeriod datePeriod;
        private final RawSQL sql;
        private Money opBalance, opBalanceLC, runningBal, runningBalLC;

        private AccountLedger(DatePeriod datePeriod) {
            this.datePeriod = datePeriod;
            sql = new RawSQL();
            try {
                init();
                sql.setSQL(ENTRY + Account.this.getId());
                sql.addSQL(" AND Date BETWEEN '" + Database.format(datePeriod.getFrom()) + "' AND '"
                        + Database.format(datePeriod.getTo()));
                sql.addSQL("' ORDER BY Account,Date,TranId,EntrySerial");
                sql.execute();
            } catch(Exception e) {
                throw new SORuntimeException(e);
            }
        }

        private void init() {
            Date date = datePeriod.getFrom();
            opBalance = Account.this.getOpeningBalance(date);
            opBalanceLC = Account.this.getLocalCurrencyOpeningBalance(date);
            runningBal = opBalance;
            runningBalLC = opBalanceLC;
        }

        @Override
        public Money getOpeningBalance() {
            return opBalance;
        }

        @Override
        public Money getLocalCurrencyOpeningBalance() {
            return opBalanceLC;
        }

        @Override
        public Account getAccount() {
            return Account.this;
        }

        @Override
        public DatePeriod getPeriod() {
            return datePeriod;
        }

        @Override
        public void close() {
            sql.close();
        }

        @Override
        public LedgerEntry next() {
            if(sql.eoq()) {
                throw new NoSuchElementException();
            }
            LedgerEntry entry = new LedgerEntry();
            try {
                entry.setRow(Account.this, sql.getResult(), runningBal, runningBalLC);
                runningBal = entry.balance;
                runningBalLC = entry.localCurrencyBalance;
                sql.skip();
            } catch(Exception e) {
                throw new NoSuchElementException("Can't set values");
            }
            return entry;
        }

        @Override
        public boolean hasNext() {
            if(sql.eoq()) {
                sql.close();
                return false;
            }
            return true;
        }
    }

    static class LedgerEntry implements com.storedobject.core.LedgerEntry {

        Money amount, localCurrencyAmount, balance, localCurrencyBalance;
        java.sql.Date date, valueDate;
        private String narration;
        private BigInteger voucher, ledgerTran, type;
        private int entrySerial;

        /**
         * Constructor.
         */
        LedgerEntry() {
        }

        void init(java.sql.Date date, Money balance, Money localCurrencyBalance) {
            this.balance = balance;
            this.localCurrencyBalance = localCurrencyBalance;
            this.date = date;
        }

        void setRow(Account account, ResultSet rs, Money opBalance, Money opBalanceLC) throws Exception {
            setRow(account, rs);
            this.balance = opBalance.add(amount);
            this.localCurrencyBalance = opBalanceLC.add(localCurrencyAmount);
        }

        void setRow(Account account, ResultSet rs) throws Exception {
            ledgerTran = rs.getBigDecimal(1).toBigInteger();
            voucher = rs.getBigDecimal(2).toBigInteger();
            entrySerial = rs.getInt(3);
            date = rs.getDate(4);
            BigDecimal a = rs.getBigDecimal(5), la = rs.getBigDecimal(6);
            amount = new Money(a, account.getCurrency());
            localCurrencyAmount = new Money(la, account.getLocalCurrency());
            narration = rs.getString(7);
            type = rs.getBigDecimal(8).toBigInteger();
            valueDate = rs.getDate(9);
        }

        @Override
        public Date getDate() {
            return date;
        }

        @Override
        public Date getValueDate() {
            return valueDate;
        }

        @Override
        public Money getAmount() {
            return amount;
        }

        @Override
        public Money getLocalCurrencyAmount() {
            return localCurrencyAmount;
        }

        @Override
        public Money getBalance() {
            return balance;
        }

        @Override
        public Money getLocalCurrencyBalance() {
            return localCurrencyBalance;
        }

        @Override
        public String getParticulars() {
            return narration;
        }

        @Override
        public int getEntrySerial() {
            return entrySerial;
        }

        @Override
        public Id getLedgerTran() {
            return new Id(ledgerTran);
        }

        @Override
        public JournalVoucher getVoucher() {
            return get(JournalVoucher.class, new Id(voucher), true);
        }

        @Override
        public List<JournalVoucher> getVouchers() {
            return vouchers(StoredObject.get(JournalVoucher.class, new Id(voucher), true));
        }

        static List<JournalVoucher> vouchers(JournalVoucher voucher) {
            List<JournalVoucher> vouchers = new ArrayList<>();
            if(voucher == null) {
                return vouchers;
            }
            vouchers.add(voucher);
            RawSQL sql = new RawSQL();
            try {
                sql.execute("SELECT DISTINCT Object FROM core.Ledger WHERE TranId=" + voucher.getTransactionId() +
                        " AND Object<>" + voucher.getId());
                ResultSet rs = sql.getResult();
                while(!sql.eoq()) {
                    vouchers.add(StoredObject.get(JournalVoucher.class, new Id(rs.getBigDecimal(1)), true));
                    sql.skip();
                }
            } catch (Exception ignored) {
            } finally {
                sql.close();
            }
            return vouchers;
        }

        @Override
        public String toString() {
            return toDisplay();
        }

        @Override
        public String getType() {
            return type.equals(BigInteger.ZERO) ? null : get(TransactionType.class, new Id(type)).getShortName();
        }
    }

    private static final long BALANCE_LIFE = 300000; // 5 Minutes
    private static final int BALANCE_COUNT = 10000;
    private static final Balances balances = new Balances();

    static class Balances extends HashMap<Id, DatedBalance> {

        private Balances() {
        }

        public static BigDecimal getBalance(Id id, Date date) {
            return get(id, date).balance;
        }

        public static BigDecimal getBalanceLC(Id id, Date date) {
            return get(id, date).balanceLC;
        }

        private static Balance get(Id id, Date date) {
            Balance b;
            boolean clean = false;
            synchronized (balances) {
                DatedBalance db = balances.get(id);
                if (db == null) {
                    db = new DatedBalance();
                    balances.put(id, db);
                    clean = balances.size() > BALANCE_COUNT;
                }
                b = db.get(id, date);
            }
            if(clean) {
                BalanceCacheCleaner.clean();
            }
            return b;
        }

        private boolean kill(long age) {
            age = System.currentTimeMillis() - age;
            boolean killed = false, killedNow = true;
            while (killedNow) {
                killedNow = false;
                for (Id id: keySet()) {
                    if(get(id).time <= age) {
                        killedNow = true;
                        remove(id);
                        break;
                    }
                }
                if(!killed) {
                    killed = killedNow;
                }
            }
            return killed;
        }

        public static void removeAccount(Id id) {
            synchronized (balances) {
                balances.remove(id);
            }
        }
    }

    private static class DatedBalance extends HashMap<Date, Balance> {

        private long time = System.currentTimeMillis();

        public Balance get(Id id, Date date) {
            time = System.currentTimeMillis();
            Balance b = get(date);
            if(b == null) {
                b = build(id, date);
                put(date, b);
            }
            return b;
        }

        private Balance build(Id id, Date date) {
            RawSQL sql = new RawSQL();
            try {
                String query = "SELECT Balance,LocalCurrencyBalance FROM core.AccountBalance WHERE Account=" + id
                        + " AND Date=";
                ResultSet rs;
                sql.execute(query + d(date));
                if(!sql.eoq()) {
                    rs = sql.getResult();
                    return new Balance(rs.getBigDecimal(1), rs.getBigDecimal(2));
                }
                String sum = "SELECT SUM(Amount),SUM(LocalCurrencyAmount) FROM core.Ledger WHERE Account=" + id
                        + " AND Date";
                Date d;
                BigDecimal b, bLC, tb, tbLC;
                sql.execute("SELECT Min(Date) FROM core.AccountBalance WHERE Account=" + id + " AND Date>"
                        + d(date));
                rs = sql.getResult();
                d = rs.getDate(1);
                if(!rs.wasNull()) {
                    sql.execute(query + d(d));
                    rs = sql.getResult();
                    b = rs.getBigDecimal(1);
                    bLC = rs.getBigDecimal(2);
                    sql.execute(sum + ">" + d(date) + " AND Date<=" + d(d));
                    rs = sql.getResult();
                    tb = rs.getBigDecimal(1);
                    if(!rs.wasNull()) {
                        tbLC = rs.getBigDecimal(2);
                        b = b.subtract(tb);
                        bLC = bLC.subtract(tbLC);
                    }
                    return insert(sql, b, bLC, id, date);
                }
                sql.execute("SELECT Max(Date) FROM core.AccountBalance WHERE Account=" + id + " AND Date<"
                        + d(date));
                rs = sql.getResult();
                d = rs.getDate(1);
                if(!rs.wasNull()) {
                    sql.execute(query + d(d));
                    rs = sql.getResult();
                    b = rs.getBigDecimal(1);
                    bLC = rs.getBigDecimal(2);
                    sql.execute(sum + "<=" + d(date) + " AND Date>" + d(d));
                    //noinspection DuplicatedCode
                    rs = sql.getResult();
                    tb = rs.getBigDecimal(1);
                    if(!rs.wasNull()) {
                        tbLC = rs.getBigDecimal(2);
                        b = b.add(tb);
                        bLC = bLC.add(tbLC);
                    }
                    return insert(sql, b, bLC, id, date);
                }
                sql.execute("SELECT (OpeningBalance).Amount,(LocalCurrencyOpeningBalance).Amount FROM core.Account WHERE Id="
                        + id);
                rs = sql.getResult();
                b = rs.getBigDecimal(1);
                bLC = rs.getBigDecimal(2);
                sql.execute(sum + "<=" + d(date));
                //noinspection DuplicatedCode
                rs = sql.getResult();
                tb = rs.getBigDecimal(1);
                if(!rs.wasNull()) {
                    tbLC = rs.getBigDecimal(2);
                    b = b.add(tb);
                    bLC = bLC.add(tbLC);
                }
                return insert(sql, b, bLC, id, date);
            } catch (Exception e) {
                ApplicationServer.log(e);
                throw new SORuntimeException("Unable to get dated balances");
            } finally {
                sql.close();
            }
        }

        private static String d(Date date) {
            return "'" + Database.format(date) + "'";
        }

        private static Balance insert(RawSQL sql, BigDecimal b, BigDecimal bLC, Id id, Date date) {
            sql.executeUpdate("INSERT INTO core.AccountBalance(Account,Date,Balance,LocalCurrencyBalance) VALUES("
                    + id + "," + d(date) + "," + b.toPlainString() + ","
                    + bLC.toPlainString() + ")");
            return new Balance(b, bLC);
        }
    }

    private record Balance(BigDecimal balance, BigDecimal balanceLC) {}

    private static class BalanceCacheCleaner implements Runnable {

        private static boolean cleaning = false;

        private synchronized static void clean() {
            if(cleaning) {
                return;
            }
            cleaning = true;
            Thread.ofVirtual().start(new BalanceCacheCleaner());
        }

        @Override
        public void run() {
            synchronized (balances) {
                runClean();
                cleaning = false;
            }
        }

        private void runClean() {
            long age = BALANCE_LIFE;
            while (true) {
                if(balances.kill(age) && balances.size() < BALANCE_COUNT) {
                    return;
                }
                if((age = age >> 1) == 0) {
                    return;
                }
            }
        }
    }
}
