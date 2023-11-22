package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

public class BusinessEntity extends AccountEntity<Entity> {

    private Id entityId;

    public BusinessEntity() {
    }

    public static void columns(Columns columns) {
        columns.add("Entity", "id");
    }

    @Override
    protected void setPartyId(Id id) {
        entityId = id;
    }

    @Override
    protected Id getPartyId() {
        return entityId;
    }

    public void setEntity(BigDecimal idValue) {
        setParty(new Id(idValue));
    }

    public void setEntity(Id personId) {
        setParty(personId);
    }

    public void setEntity(Entity person) {
        setParty(person);
    }

    public Entity getEntity() {
        return getParty();
    }

    @SetNotAllowed
    @Column(order = 100, caption = "Business Entity")
    public Id getEntityId() {
        return entityId;
    }

    @Override
    protected final Class<Entity> getPartyClass() {
        return Entity.class;
    }

    @Override
    public final String getName() {
        return getParty().getName();
    }
}
