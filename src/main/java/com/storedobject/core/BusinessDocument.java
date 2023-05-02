package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class BusinessDocument extends Document {

    private Id ownerId;

    public BusinessDocument() {
    }

    public static void columns(Columns columns) {
        columns.add("Owner", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Owner");
    }

    public void setOwner(Id ownerId) {
        if (!loading() && !Id.equals(this.getOwnerId(), ownerId)) {
            throw new Set_Not_Allowed("Owner");
        }
        this.ownerId = ownerId;
    }

    public void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    public void setOwner(Entity owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    @SetNotAllowed
    @Column(order = 400)
    public Id getOwnerId() {
        return ownerId;
    }

    public Entity getOwner() {
        return getRelated(Entity.class, ownerId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        ownerId = tm.checkType(this, ownerId, Entity.class, false);
        super.validateData(tm);
    }
}
