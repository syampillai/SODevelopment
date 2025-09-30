package com.storedobject.core;

import com.storedobject.core.annotation.*;

import java.math.BigDecimal;

/**
 * Fixed asset location represents a location where fixed assets are located.
 *
 * @author Syam
 */
public abstract class FixedAssetLocation extends InventoryLocation implements OfEntity {

    private Id systemEntityId;

    public FixedAssetLocation() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity");
    }

    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 100, caption = "Of")
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return get(SystemEntity.class, systemEntityId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            systemEntityId = check(tm, systemEntityId);
        }
        super.validateData(tm);
    }

    public final int getType() {
        return 20;
    }
}
