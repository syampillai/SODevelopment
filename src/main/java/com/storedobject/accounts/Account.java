package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class Account extends com.storedobject.core.Account {

    private Id entityId;
    private AccountEntity<?> entity;

    public Account() {
    }

    public static void columns(Columns columns) {
        columns.add("Entity", "id");
    }

    public void setEntity(Id entityId) {
        this.entity = null;
        this.entityId = entityId;
    }

    public void setEntity(BigDecimal idValue) {
        setEntity(new Id(idValue));
    }

    public void setEntity(AccountEntity<?> entity) {
        setEntity(entity == null ? null : entity.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getEntityId() {
        return entityId;
    }

    public AccountEntity<?> getEntity() {
        if (this.entity == null) {
            this.entity = getRelated(AccountEntity.class, entityId, true);
        }
        return this.entity;
    }

    public static Account get(SystemEntity systemEntity, String name) {
        return Account.getByNameOrNumber(systemEntity, Account.class, name, true);
    }

    public static ObjectIterator<? extends Account> list(SystemEntity systemEntity, String name) {
        return Account.listByNameOrNumber(systemEntity, Account.class, name, true);
    }

    @Override
    public final String getName() {
        return getEntity().getName();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        entityId = tm.checkTypeAny(this, entityId, AccountEntity.class, false);
        if(getEntity() != null) {
            setName(getEntity().getName());
        }
        super.validateData(tm);
    }

    public final boolean isPersonal() {
        return getEntity().isPersonal();
    }

    public final boolean isBusiness() {
        return getEntity().isBusiness();
    }
}
