package com.storedobject.core;

import java.math.BigDecimal;

/**
 * Represents all types of virtual locations for inventory items. These are auto-created by the platform as
 * and when needed.
 *
 * @author Syam
 */
public final class InventoryVirtualLocation extends InventoryLocation {

    public InventoryVirtualLocation() {
    }

    public static void columns(Columns columns) {
    }

    public void setEntity(Id entityId) {
    }

    public void setEntity(BigDecimal idValue) {
    }

    public void setEntity(Entity entity) {
    }

    public Id getEntityId() {
        return new Id();
    }

    public Entity getEntity() {
        return new Entity();
    }

    public int getType() {
        return 0;
    }

    public void setType(int type) {
    }
}