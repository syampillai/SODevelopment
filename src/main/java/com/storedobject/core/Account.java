package com.storedobject.core;

import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * This class represents an Account. Account has a status ({@link #getAccountStatus()}) which is a bit pattern with following values:<pre>
 *    0: [Account level] 0 = Active, 1 = Closed
 *  2,1: [Account level, Overrides 10,9] 00 = Bits 10,9 applicable, 01 = Debits allowed (credit blocked), 10 = Credits allowed (debits blocked), 11 = Frozen
 *    3: [Chart level] 0 = Balance control is not strict, 1 = Strict balance control
 *    4: [Chart level] (Strictly applied if bit 3 = 1) 0 = Debit balance, 1 = Credit balance
 *  6,5: [Chart level] 00 = BS item, 01 = PL item, 10 = Stock item, 11 = Contingent.
 *    7: [Chart level] 0 = Normal, 1 = Deep frozen (No transactions, no way to override).
 *    8: [Chart level] 0 = No limit check, 1 = Limit check
 * 10,9: [Chart level] 00 = Debit and credit trans allowed, 01 = Generally debited, 10 = Generally credited, 11 = Frozen
 * </pre>
 */
public class Account extends StoredObject implements OfEntity {

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
    }

    /**
     * Constructs a local currency Account.
     */
    public Account() {
        this(null, null, null);
    }

    public static void columns(Columns columns) {
    }

    /**
     * Refreshes the balance and accountStatus from the database.
     */
    public final void refresh() {
    }

    /**
     * Gets the local currency.
     *
     * @return The local currency.
     */
    public Currency getLocalCurrency() {
        return Money.getDefaultCurrency();
    }

    /**
     * Gets the currency.
     *
     * @return The currency.
     */
    public Currency getCurrency() {
        return Money.getDefaultCurrency();
    }

    /**
     * Sets the currency.
     *
     * @param currency The currency.
     */
    public void setCurrency(Currency currency) {
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
        return new Id();
    }

    // For internal use only.
    public void setSystemEntity(BigDecimal idValue) {
    }

    /**
     * Gets the System Entity.
     *
     * @return The System Entity
     */
    public SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public void setBalance(Object balance) {
    }

    public Money getBalance() {
        return new Money();
    }

    public Money getBalance(Date date) {
        return new Money();
    }

    public void setLocalCurrencyBalance(Object localCurrencyBalance) {
    }

    public Money getLocalCurrencyBalance() {
        return new Money();
    }

    public Money getLocalCurrencyBalance(Date date) {
        return new Money();
    }

    public void setOpeningBalance(Object openingBalance) {
    }

    public Money getOpeningBalance() {
        return new Money();
    }

    public void setLocalCurrencyOpeningBalance(Object localCurrencyOpeningBalance) {
    }

    public Money getLocalCurrencyOpeningBalance() {
        return new Money();
    }

    public boolean isLocalCurrency() {
        return true;
    }

    public boolean isForeignCurrency() {
        return !isLocalCurrency();
    }

    public int getAccountStatus() {
        return 0;
    }

    public void setAccountStatus(int accountStatus) {
    }

    public void close() throws Exception {
    }

    public void reopen() throws Exception {
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
    }

    public final String getNumber() {
        return "";
    }

    public final void setNumber(String number) {
    }

    public void setChart(BigDecimal chartId) {
        setChart(new Id(chartId));
    }

    public void setChart(Id chartId) {
    }

    public Id getChartId() {
        return new Id();
    }

    public AccountChart getChart() {
        return new AccountChart();
    }

    public String getTitle() {
        return "";
    }

    @Override
    public final String toString() {
        return "";
    }

    public String toSubstring() {
        return "";
    }

    public Money createAmount(BigDecimal amount) {
        return new Money();
    }

    public Money createLocalCurrencyAmount(BigDecimal amount) {
        return new Money();
    }

    public static <A extends Account> A getByNumber(SystemEntity systemEntity, Class<A> accountClass, String number) {
        return get(accountClass);
    }

    public static <A extends Account> A getByNumber(SystemEntity systemEntity, Class<A> accountClass, String number, boolean any) {
        return get(accountClass);
    }

    public static <A extends Account> ObjectIterator<A> listByNumber(Class<A> accountClass, String number) {
        return listByNumber(accountClass, number, false);
    }

    public static <A extends Account> ObjectIterator<A> listByNumber(final Class<A> accountClass, String number, final boolean any) {
        return list(accountClass);
    }

    public static <A extends Account> ObjectIterator<A> listByNumber(SystemEntity systemEntity, Class<A> accountClass, String number) {
        return list(accountClass);
    }

    public static <A extends Account> ObjectIterator<A> listByNumber(final SystemEntity systemEntity,
                                                                     final Class<A> accountClass, String number, final boolean any) {
        return list(accountClass);
    }

    public static <A extends Account> A getByName(SystemEntity systemEntity, Class<A> accountClass, String name) {
        return get(accountClass);
    }

    public static <A extends Account> A getByName(SystemEntity systemEntity, Class<A> accountClass, String name, boolean any) {
        return get(accountClass);
    }

    public static <A extends Account> ObjectIterator<A> listByName(SystemEntity systemEntity, Class<A> accountClass, String name) {
        return list(accountClass);
    }

    public static <A extends Account> ObjectIterator<A> listByName(final SystemEntity systemEntity,
                                                                   final Class<A> accountClass, String name, final boolean any) {
        return list(accountClass);
    }

    public static Account get(SystemEntity systemEntity, String name) {
        return new Account();
    }

    public static ObjectIterator<? extends Account> list(SystemEntity systemEntity, String name) {
        return list(Account.class);
    }
}
