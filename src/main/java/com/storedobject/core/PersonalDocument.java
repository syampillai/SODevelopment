package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class PersonalDocument extends Document {

    private Id ownerId;

    public PersonalDocument() {
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

    public void setOwner(Person owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    @SetNotAllowed
    @Column(order = 400)
    @Override
    public Id getOwnerId() {
        return ownerId;
    }

    public Person getOwner() {
        return getRelated(Person.class, ownerId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        ownerId = tm.checkType(this, ownerId, Person.class, false);
        super.validateData(tm);
    }
}
