package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class AccountLimit extends StoredObject {

    private Id accountId;
    private Money limitAmount = new Money();
    private final Date validFrom = DateUtility.today();
    private final Date validTo = DateUtility.today();
    private int temporaryExtension;

    public AccountLimit() {
    }

    public static void columns(Columns columns) {
        columns.add("Account", "id");
        columns.add("LimitAmount", "money");
        columns.add("ValidFrom", "date");
        columns.add("ValidTo", "date");
        columns.add("TemporaryExtension", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Account, ValidFrom", true);
    }

    public String getUniqueCondition() {
        return "Account="
                + getAccountId()
                + " AND "
                + "ValidFrom='"
                + Database.format(getValidFrom())
                + "'";
    }

    public static String[] protectedColumns() {
        return new String[] {
                "TemporaryExtension",
        };
    }

    public void setAccount(Id accountId) {
        if (!loading() && !Id.equals(this.getAccountId(), accountId)) {
            throw new Set_Not_Allowed("Account");
        }
        this.accountId = accountId;
    }

    public void setAccount(BigDecimal idValue) {
        setAccount(new Id(idValue));
    }

    public void setAccount(Account account) {
        setAccount(account == null ? null : account.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 100)
    public Id getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return getRelated(Account.class, accountId, true);
    }

    public void setLimitAmount(Money limitAmount) {
        this.limitAmount = limitAmount;
    }

    public void setLimitAmount(Object moneyValue) {
        setLimitAmount(Money.create(moneyValue));
    }

    @Column(order = 200)
    public Money getLimitAmount() {
        return limitAmount;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom.setTime(validFrom.getTime());
    }

    @Column(order = 300)
    public Date getValidFrom() {
        return new Date(validFrom.getTime());
    }

    public void setValidTo(Date validTo) {
        this.validTo.setTime(validTo.getTime());
    }

    @Column(order = 400)
    public Date getValidTo() {
        return new Date(validTo.getTime());
    }

    public void setTemporaryExtension(int temporaryExtension) {
        this.temporaryExtension = temporaryExtension;
    }

    @Column(required = false, caption = "Temporary Extension (Days)", order = 500)
    public int getTemporaryExtension() {
        return temporaryExtension;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        accountId = tm.checkTypeAny(this, accountId, Account.class, false);
        if (Utility.isEmpty(validFrom)) {
            throw new Invalid_Value("Valid from");
        }
        if (Utility.isEmpty(validTo)) {
            throw new Invalid_Value("Valid to");
        }
        if(limitAmount.getCurrency() != getAccount().getCurrency()) {
            throw new Invalid_State("Limit currency mismatch");
        }
        super.validateData(tm);
    }

    public void extendBy(int days) {
        Date d = validTo;
        d = DateUtility.addDay(d, -temporaryExtension + days);
        validTo.setTime(d.getTime());
        temporaryExtension = 0;
    }

    public void extendTo(Date toDate) {
        validTo.setTime(toDate.getTime());
        temporaryExtension = 0;
    }

    public void extendTemporarilyBy(int days) {
        extendBy(days);
        temporaryExtension = days;
    }

    public void extendTemporarilyTo(Date toDate) {
        int days = temporaryExtension + DateUtility.getPeriodInDays(validTo, toDate);
        extendTo(toDate);
        temporaryExtension = days;
    }

    public static AccountLimit getApplicable(Account account) {
        return getApplicable(account.getId());
    }

    public static AccountLimit getApplicable(Id accountId) {
        String today = "'" + Database.format(DateUtility.today()) + "'";
        return StoredObject.list(AccountLimit.class, "Account=" + accountId + " AND ValidFrom<="
                + today + " AND ValidTo>=" + today, "ValidFrom DESC").limit(1).findFirst();
    }
}
