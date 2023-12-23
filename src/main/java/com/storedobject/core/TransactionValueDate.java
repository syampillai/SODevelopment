package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class TransactionValueDate extends StoredObject {

    private DecimalNumber ledgerTran = DecimalNumber.zero(0);
    private Id voucherId;
    private int entrySerial;
    private final Date valueDate = DateUtility.today();
    private final Date newValueDate = DateUtility.today();
    private Id accountId;

    public TransactionValueDate() {
    }

    public static void columns(Columns columns) {
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
            throw new Set_Not_Allowed("Illegal call");
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
            throw new Set_Not_Allowed("Illegal call");
        }
        this.entrySerial = entrySerial;
    }

    @SetNotAllowed
    @Column(order = 300)
    public int getEntrySerial() {
        return entrySerial;
    }

    public void setNewValueDate(Date date) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Illegal call");
        }
        this.newValueDate.setTime(date.getTime());
    }

    @SetNotAllowed
    @Column(order = 500)
    public Date getNewValueDate() {
        return new Date(newValueDate.getTime());
    }

    public void setValueDate(Date valueDate) {
        this.valueDate.setTime(valueDate.getTime());
    }

    @Column(order = 400)
    public Date getValueDate() {
        return new Date(valueDate.getTime());
    }

    public void setAccount(Id accountId) {
        if (notLoading() && !Id.equals(this.getAccountId(), accountId)) {
            throw new Set_Not_Allowed("Illegal call");
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

    public static void changeTo(Date newValueDate, Id accountId, Date transactionDate, Money amount,
                                TransactionManager tm) throws Exception {
        changeTo(newValueDate, accountId, transactionDate, amount, tm, null);
    }

    public static void changeTo(Date newValueDate, Id accountId, Date transactionDate, Money amount, Id voucherId,
                                TransactionManager tm) throws Exception {
        changeTo(newValueDate, accountId, transactionDate, amount, tm, "Voucher=" + voucherId);
    }

    public static void changeTo(Date newValueDate, Id accountId, Date transactionDate, Money amount, Id voucherId,
                                int entrySerial, TransactionManager tm) throws Exception {
        changeTo(newValueDate, accountId, transactionDate, amount, tm, "Voucher=" + voucherId
                + " AND EntrySerial=" + entrySerial);
    }

    public static void changeTo(Date newValueDate, Id accountId, Date transactionDate, Money amount,
                                int entrySerial, TransactionManager tm) throws Exception {
        changeTo(newValueDate, accountId, transactionDate, amount, tm, "EntrySerial=" + entrySerial);
    }

    private static void changeTo(Date newValueDate, Id accountId, Date transactionDate, Money amount,
                                  TransactionManager tm, String condition) throws Exception {
        if(newValueDate == null || accountId == null || transactionDate == null || amount == null || tm == null ||
                (condition != null && condition.isEmpty())) {
            throw new Exception();
        }
    }
}