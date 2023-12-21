package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class TransactionValueDate extends StoredObject {

    private DecimalNumber ledgerTran = DecimalNumber.zero(0);
    private Id voucherId;
    private int entrySerial;
    private final Date date = DateUtility.today();
    private final Date valueDate = DateUtility.today();
    private Id accountId;
    private Money amount = new Money();
    private Money localCurrencyAmount = new Money();

    public TransactionValueDate() {
    }

    public static void columns(Columns columns) {
        columns.add("LedgerTran", "numeric(30,0)");
        columns.add("Voucher", "id");
        columns.add("EntrySerial", "int");
        columns.add("Date", "date");
        columns.add("ValueDate", "date");
        columns.add("Account", "id");
        columns.add("Amount", "money");
        columns.add("LocalCurrencyAmount", "money");
    }

    public static void indices(Indices indices) {
        indices.add("LedgerTran, Voucher, EntrySerial", true);
        indices.add("Account, Date");
        indices.add("Account, ValueDate");
    }

    public String getUniqueCondition() {
        return "LedgerTran=" + ledgerTran.getStorableValue() + " AND Voucher=" + getVoucherId() + " AND EntrySerial="
                + entrySerial;
    }

    private boolean notLoading() {
        if(getId() == null) {
            return true;
        }
        if(isVirtual()) {
            return true;
        }
        return !loading();
    }

    public void setLedgerTran(DecimalNumber ledgerTran) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Ledger Tran");
        }
        this.ledgerTran = new DecimalNumber(ledgerTran.getValue(), 0);
    }

    public void setLedgerTran(Object value) {
        setLedgerTran(DecimalNumber.create(value, 0));
    }

    @SetNotAllowed
    @Column(style = "(d:30,0)", order = 100)
    public DecimalNumber getLedgerTran() {
        return ledgerTran;
    }

    public void setVoucher(Id voucherId) {
        if (notLoading() && !Id.equals(this.getVoucherId(), voucherId)) {
            throw new Set_Not_Allowed("Voucher");
        }
        this.voucherId = voucherId;
    }

    public void setVoucher(BigDecimal idValue) {
        setVoucher(new Id(idValue));
    }

    public void setVoucher(JournalVoucher voucher) {
        setVoucher(voucher == null ? null : voucher.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 200)
    public Id getVoucherId() {
        return voucherId;
    }

    public JournalVoucher getVoucher() {
        return getRelated(JournalVoucher.class, voucherId, true);
    }

    public void setEntrySerial(int entrySerial) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Entry Serial");
        }
        this.entrySerial = entrySerial;
    }

    @SetNotAllowed
    @Column(order = 300)
    public int getEntrySerial() {
        return entrySerial;
    }

    public void setDate(Date date) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Date");
        }
        this.date.setTime(date.getTime());
    }

    @SetNotAllowed
    @Column(order = 400)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setValueDate(Date valueDate) {
        this.valueDate.setTime(valueDate.getTime());
    }

    @Column(order = 500)
    public Date getValueDate() {
        return new Date(valueDate.getTime());
    }

    public void setAccount(Id accountId) {
        if (notLoading() && !Id.equals(this.getAccountId(), accountId)) {
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
    @Column(style = "(any)", order = 600)
    public Id getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return getRelated(Account.class, accountId, true);
    }

    public void setAmount(Money amount) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Amount");
        }
        this.amount = amount;
    }

    public void setAmount(Object moneyValue) {
        setAmount(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(order = 700)
    public Money getAmount() {
        return amount;
    }

    public void setLocalCurrencyAmount(Money localCurrencyAmount) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Local Currency Amount");
        }
        this.localCurrencyAmount = localCurrencyAmount;
    }

    public void setLocalCurrencyAmount(Object moneyValue) {
        setLocalCurrencyAmount(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(order = 800)
    public Money getLocalCurrencyAmount() {
        return localCurrencyAmount;
    }
}
