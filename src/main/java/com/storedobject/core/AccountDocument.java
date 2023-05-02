package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class AccountDocument extends Document {

    private Id ownerId;

    public AccountDocument() {
    }

    public static void columns(Columns columns) {
        columns.add("Owner", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Owner");
    }

    @Override
    public void setOwner(Id ownerId) {
        if (!loading() && !Id.equals(this.getOwnerId(), ownerId)) {
            throw new Set_Not_Allowed("Owner");
        }
        this.ownerId = ownerId;
    }

    public void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    public void setOwner(Account owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 400)
    @Override
    public Id getOwnerId() {
        return ownerId;
    }

    @Override
    public Account getOwner() {
        return getRelated(Account.class, ownerId, true);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        ownerId = tm.checkTypeAny(this, ownerId, Account.class, false);
        super.validateData(tm);
    }
}
