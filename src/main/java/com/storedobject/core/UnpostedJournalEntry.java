package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class UnpostedJournalEntry extends StoredObject implements Detail {

    private int displayOrder;
    private Id accountId;
    private Money amount = new Money();
    private Money localCurrencyAmount = new Money();
    private int entrySerial;
    private Id typeId = Id.ZERO;
    private String particulars;
    private Date valueDate;
    boolean internal = false;
    private String extraData;

    public UnpostedJournalEntry() {
    }

    public static void columns(Columns columns) {
        columns.add("DisplayOrder", "int");
        columns.add("Account", "id");
        columns.add("Amount", "money");
        columns.add("LocalCurrencyAmount", "money");
        columns.add("EntrySerial", "int");
        columns.add("Type", "id");
        columns.add("Particulars", "text");
        columns.add("ValueDate", "date");
        columns.add("ExtraData", "text");
    }

    public void setDisplayOrder(int displayOrder) {
        if (!loading()) {
            throw new Set_Not_Allowed("Display Order");
        }
        this.displayOrder = displayOrder;
    }

    @SetNotAllowed
    @Column(order = 100)
    public int getDisplayOrder() {
        return displayOrder;
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
    @Column(style = "(any)", order = 200)
    public Id getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return getRelated(Account.class, accountId, true);
    }

    public void setAmount(Money amount) {
        if (!loading()) {
            throw new Set_Not_Allowed("Amount");
        }
        this.amount = amount;
    }

    public void setAmount(Object moneyValue) {
        setAmount(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(order = 300)
    public Money getAmount() {
        return amount;
    }

    public void setLocalCurrencyAmount(Money localCurrencyAmount) {
        if (!loading()) {
            throw new Set_Not_Allowed("Local Currency Amount");
        }
        this.localCurrencyAmount = localCurrencyAmount;
    }

    public void setLocalCurrencyAmount(Object moneyValue) {
        setLocalCurrencyAmount(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(order = 400)
    public Money getLocalCurrencyAmount() {
        return localCurrencyAmount;
    }

    public void setEntrySerial(int entrySerial) {
        if (!loading()) {
            throw new Set_Not_Allowed("Entry Serial");
        }
        this.entrySerial = entrySerial;
    }

    @SetNotAllowed
    @Column(order = 500)
    public int getEntrySerial() {
        return entrySerial;
    }

    public void setType(Id typeId) {
        if (!loading() && !Id.equals(this.getTypeId(), typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
    }

    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public void setType(TransactionType type) {
        setType(type == null ? null : type.getId());
    }

    @SetNotAllowed
    @Column(required = false, order = 600)
    public Id getTypeId() {
        return typeId;
    }

    public TransactionType getType() {
        return getRelated(TransactionType.class, typeId);
    }

    public void setParticulars(String particulars) {
        if (!loading()) {
            throw new Set_Not_Allowed("Particulars");
        }
        this.particulars = particulars;
    }

    @SetNotAllowed
    @Column(order = 700)
    public String getParticulars() {
        return particulars;
    }

    public void setValueDate(Date valueDate) {
        if (!loading()) {
            throw new Set_Not_Allowed("Value Date");
        }
        this.valueDate = valueDate == null ? null : new Date(valueDate.getTime());
    }

    @SetNotAllowed
    @Column(order = 800)
    public Date getValueDate() {
        return new Date(valueDate.getTime());
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Column(order = 900, required = false)
    public String getExtraData() {
        return extraData;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            if (!internal) {
                throw new Invalid_State("Illegal");
            }
            accountId = tm.checkTypeAny(this, accountId, Account.class, false);
            typeId = tm.checkType(this, typeId, TransactionType.class, true);
            if (StringUtility.isWhite(particulars)) {
                throw new Invalid_Value("Particulars");
            }
            if (valueDate == null) {
                throw new Invalid_Value("Value Date");
            }
        }
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == UnpostedJournal.class;
    }
}
