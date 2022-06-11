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

    private static InventoryVirtualLocation getFor(Id entityId, int type) {
        return get(InventoryVirtualLocation.class, "Entity=" + entityId + " AND Type=" + type);
    }

    public static InventoryVirtualLocation getForExternalOwner(Id ownerId) {
        return getFor(ownerId, 17);
    }

    public static InventoryVirtualLocation getForLoanFrom(Id lenderId) {
        return getFor(lenderId, 9);
    }

    public static InventoryVirtualLocation getForLoanTo(Id loaneeId) {
        return getFor(loaneeId, 8);
    }

    public static InventoryVirtualLocation getForSupplier(Id supplierId) {
        return getFor(supplierId, 1);
    }

    public static InventoryVirtualLocation getForRepairOrganization(Id supplierId) {
        return getFor(supplierId, 3);
    }

    public static InventoryVirtualLocation getForConsumer(Id consumerId) {
        return getFor(consumerId, 2);
    }

    public static InventoryVirtualLocation getScrapLocation(SystemEntity systemEntity) {
        return getFor(systemEntity.getEntityId(), 6);
    }

    public static InventoryVirtualLocation getShortageLocation(SystemEntity systemEntity) {
        return getFor(systemEntity.getEntityId(), 7);
    }

    public static InventoryVirtualLocation getConsumptionLocation(SystemEntity systemEntity) {
        return getFor(systemEntity.getEntityId(), 16);
    }

    public static InventoryVirtualLocation getRecycleLocation() {
        return new InventoryVirtualLocation();
    }
}