package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all types of virtual locations for inventory items. These are auto-created by the platform as
 * and when needed.
 * 
 * @author Syam
 */
public final class InventoryVirtualLocation extends InventoryLocation implements HasChildren {

    static final Map<Id, InventoryVirtualLocation> cache = new HashMap<>();
    private static final String[] statusValues =
            new String[] {
                    "Active", "Inactive",
            };
    private static InventoryVirtualLocation recycle;
    private int type = 1;
    private Id entityId;
    private String code = "";
    private int status = 0;

    public InventoryVirtualLocation() {
    }

    public static void columns(Columns columns) {
        columns.add("Entity", "Id");
        columns.add("Type", "int");
        columns.add("Code", "text");
        columns.add("Status", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Entity, Type, lower(Name)", true);
        indices.add("Entity, Type, Code", "Code<>''", true);
    }

    public static String[] browseColumns() {
        return new String[] { "Name", "Type", "Entity", "Status" };
    }

    public static String[] protectedColumns() {
        return new String[] { "Code", "Status" };
    }

    public static String[] links() {
        return new String[] {
                "Work Centers|com.storedobject.core.InventoryVirtualLocation|||0",
        };
    }

    public void setEntity(Id entityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Entity");
        }
        this.entityId = entityId;
    }

    public void setEntity(BigDecimal idValue) {
        setEntity(new Id(idValue));
    }

    public void setEntity(Entity entity) {
        setEntity(entity == null ? null : entity.getId());
    }

    @Override
    @SetNotAllowed
    @Column(order = 200)
    public Id getEntityId() {
        return entityId;
    }

    public Entity getEntity() {
        return get(Entity.class, entityId);
    }

    @SetNotAllowed
    @Override
    @Column(order = 300)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        if(!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        this.type = type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(order = 400, required = false, style = "(code)")
    public String getCode() {
        return code;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(order = 500, required = false)
    public int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return getStatusValue(status);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        code = toCode(code);
        switch(type) {
            case 1, 2 -> {
                if(isSO()) {
                    setName("Cash " + (type == 1 ? "Supplier" : "Customer"));
                }
            }
        }
        checkForDuplicate("Entity", "Type", "Name");
        if(!code.isEmpty()) {
            checkForDuplicate("Entity", "Type", "Code");
        }
        switch(type) {
            // Store
            // Production unit
            // Assembly
            // Fixed asset
            case 0, 4, 14, 20 ->
                    throw new Invalid_State("Type not allowed: " + getTypeValue());
        }
        if(type == 15) { // Recycle - Could be any System Entity
            if(Id.isNull(entityId)) {
                entityId = tm.getEntity().getEntityId();
            }
        }
        entityId = tm.checkType(this, entityId, Entity.class, false);
        switch (type) {
            // Recycle
            case 15 -> {
            }
            // Supplier
            // Consumer
            // Repair org.
            // Rented out
            // Rented from
            // External owner
            // External consumption
            case 1, 2, 3, 8, 9, 17, 21 -> {
                if (isSO()) {
                    throw new Invalid_State("For location type '" + getTypeValue() + "', entity '" +
                            getEntity() + "' is not allowed");
                }
            }
            default -> { // Internal to org.
                if (!isSO()) {
                    throw new Invalid_State("For location type '" + getTypeValue() + "', external entity '" +
                            getEntity() + "' is not allowed");
                }
            }
        }
        if(inserted()) {
            switch(type) {
                // Supplier
                // Consumer
                // Repair org
                // Scrap
                // Inventory shortage
                // Rented out
                // Rented from
                // Initial inventory
                // Service/subscription
                // External owner
                case 1, 2, 3, 6, 7, 8, 9, 12, 13, 17 -> {
                    if(exists(InventoryVirtualLocation.class, "Entity=" + entityId + " AND Type=" + type)) {
                        throw new Invalid_State("Duplicate entry");
                    }
                }
                // Thrash
                case 15 -> {
                    if(getRecycleLocation() != null) {
                        throw new Invalid_State("Duplicate thrash location");
                    }
                }
            }
        }
        super.validateData(tm);
    }

    private boolean isSO() {
        return list(SystemEntity.class, "Entity=" + entityId).findFirst() != null;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        if(recycle != null && this.getId().equals(recycle.getId())) {
            recycle = get(InventoryVirtualLocation.class, recycle.getId());
        }
        synchronized (cache) {
            cache.remove(this.getId());
        }
    }

    @Override
    public String toString() {
        String name = getName();
        String type = getTypeValue();
        if(name.toLowerCase().contains(type.toLowerCase())) {
            return name;
        }
        return name + " (" + type + ")";
    }

    @Override
    public String toDisplay() {
        return toString();
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

    public static InventoryVirtualLocation getConsumptionLocation(Entity entity) {
        return getFor(entity.getId(), 21);
    }

    public static InventoryVirtualLocation getRecycleLocation() {
        if(recycle == null) {
            recycle = get(InventoryVirtualLocation.class, "Type=15");
        }
        return recycle;
    }

    static InventoryVirtualLocation createRecycleLocation() {
        if(recycle == null) {
            recycle = get(InventoryVirtualLocation.class, "Type=15");
        }
        if(recycle != null) {
            throw new SORuntimeException("State error - Thrash");
        }
        InventoryVirtualLocation loc = new InventoryVirtualLocation();
        loc.type = 15; // Recycle
        loc.setName("Thrash");
        return loc;
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        super.validateChildAttach(child, linkType);
        if(!(child instanceof InventoryVirtualLocation loc)) {
            return;
        }
        if(type != 10 || loc.getType() != 10) {
            throw new Invalid_State("Only 'Service Units / Work Centers' can be added and " +
                    "can be added under other 'Service Units / Work Centers' only.");
        }
    }

    @Override
    public boolean isActive() {
        return status == 0;
    }
}
