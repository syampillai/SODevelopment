package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An item can be an assembly, composed of one or more subitems, and each subitem
 * can also be an assembly.</p>
 * <p>This class represents the definition of an assembly node.</p>
 *
 * @author Syam
 */
public final class InventoryAssembly extends StoredObject implements HasInventoryItemType {

    private String position;
    private Id itemTypeId, parentItemTypeId;
    private InventoryItemType itemType;
    private Quantity quantity = Quantity.create(Quantity.class);
    private boolean accessory, optional;
    private int displayOrder;
    int level = 0;

    public InventoryAssembly() {
    }

    public static void columns(Columns columns) {
        columns.add("Position", "text");
        columns.add("ItemType", "id");
        columns.add("ParentItemType", "id");
        columns.add("Quantity", "quantity");
        columns.add("Accessory", "boolean");
        columns.add("Optional", "boolean");
        columns.add("DisplayOrder", "int");
    }

    public static void indices(Indices indices) {
        indices.add("ParentItemType,ItemType,lower(Position)", true);
        indices.add("ItemType");
    }

    public static String[] searchColumns() {
        return new String[] {
                "ItemType.PartNumber AS P/N",
                "ItemType.Name AS Item Name",
        };
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(required = false, order = 200, caption = "Position/Label")
    public String getPosition() {
        return position;
    }

    public void setItemType(Id itemTypeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Item Type");
        }
        this.itemTypeId = itemTypeId;
    }

    public void setItemType(BigDecimal idValue) {
        setItemType(new Id(idValue));
    }

    public void setItemType(InventoryItemType itemType) {
        setItemType(itemType == null ? null : itemType.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 300)
    public Id getItemTypeId() {
        return itemTypeId;
    }

    public InventoryItemType getItemType() {
        if(itemType == null) {
            itemType = get(InventoryItemType.class, itemTypeId, true);
        }
        return itemType;
    }

    public void setParentItemType(Id parentItemTypeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Parent Item Type");
        }
        this.parentItemTypeId = parentItemTypeId;
    }

    public void setParentItemType(BigDecimal idValue) {
        setParentItemType(new Id(idValue));
    }

    public void setParentItemType(InventoryItemType itemType) {
        setParentItemType(itemType == null ? null : itemType.getId());
    }

    @SetNotAllowed
    @Column(order = 100, style = "(any)", caption = "Next Hierarchy of")
    public Id getParentItemTypeId() {
        return parentItemTypeId;
    }

    public InventoryItemType getParentItemType() {
        return get(InventoryItemType.class, parentItemTypeId, true);
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Column(order = 400)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setAccessory(boolean accessory) {
        this.accessory = accessory;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Column(order = 500)
    public boolean getAccessory() {
        return accessory;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Column(order = 600, caption = "Optional/Add-on")
    public boolean getOptional() {
        return optional;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Column(order = 700, required = false)
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(position == null) {
            position = "";
        } else {
            position = position.trim();
        }
        parentItemTypeId = tm.checkTypeAny(this, parentItemTypeId, InventoryItemType.class, false);
        itemTypeId = tm.checkTypeAny(this, itemTypeId, InventoryItemType.class, false);
        if(getItemType().isSerialized()) {
            if(StringUtility.isWhite(position)) {
                throw new Invalid_Value("Position can not be empty");
            }
            quantity = Count.ONE;
        }
        if(quantity.isZero()) {
            if(!itemType.isSerialized()) {
                throw new Invalid_Value("Quantity can not be zero");
            }
        }
        quantity.canConvert(itemType.getUnit());
        checkForDuplicate("ParentItemType", "ItemType", "Position");
        if(inserted() || created()) {
            checkCirc(parentItemTypeId, itemTypeId);
        }
        super.validateData(tm);
    }

    private static void checkCirc(Id pid, Id cid) throws Invalid_State {
        if(pid.equals(cid)) {
            throw new Invalid_State("Circular reference under: " + get(InventoryItemType.class, pid, true));
        }
        List<InventoryAssembly> ias = new ArrayList<>();
        list(InventoryAssembly.class, "ParentItemType=" + cid).collectAll(ias);
        int i = 0;
        InventoryAssembly ia;
        while(!ias.isEmpty()) {
            ia = ias.get(i);
            if(ia.getItemTypeId().equals(pid)) {
                throw new Invalid_State("Circular reference");
            }
            checkCirc(pid, ia.itemTypeId);
            InventoryAssembly finalIa = ia;
            ias.removeIf(a -> a.getItemTypeId().equals(finalIa.getItemTypeId()));
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        List<InventoryFitmentPosition> positions = new ArrayList<>();
        list(getTransaction(), InventoryFitmentPosition.class, "Assembly=" + getId(), true).
                collectAll(positions);
        for(InventoryFitmentPosition p: positions) {
            if(exists(InventoryItem.class, "Location=" + p.getId(), true)) {
                throw new Invalid_State("Assembly definition is in use (by " + positions.getFirst().toDisplay() + ")");
            }
        }
        for(InventoryFitmentPosition p: positions) {
            p.delete(getTransaction());
        }
    }

    @Override
    public String toString() {
        InventoryItemType iType = getParentItemType();
        String it = iType == null ? "[Invalid Parent Item Type]" : iType.toDisplay();
        if(!position.isEmpty() && !"-".equals(position) && !"*".equals(position)) {
            it = "(" + position + ") " + it;
        }
        iType = getItemType();
        it += " ⇐ " + (iType == null ? "[Invalid Item Type]" : iType.toDisplay());
        if(iType != null && iType.isSerialized()) {
            return it;
        }
        return it + " (" + quantity + ")";
    }

    public static InventoryAssembly get(String pn) {
        InventoryItemType iit = InventoryItemType.get(pn);
        if(iit == null) {
            return null;
        }
        return list(InventoryAssembly.class, "ItemType=" + iit.getId()).single(false);
    }

    public static ObjectIterator<InventoryAssembly> list(String pn) {
        if(pn == null || pn.isEmpty()) {
            return ObjectIterator.create();
        }
        return InventoryItemType.list(pn).
                expand(iit -> list(InventoryAssembly.class, "ItemType=" + iit.getId()));
    }

    /**
     * Is the given part number compatible for this assembly position?
     *
     * @param partNumber Part number to check.
     * @return True or false.
     */
    public boolean canFit(InventoryItemType partNumber) {
        if(partNumber == null) {
            return false;
        }
        return canFit(partNumber.getId());
    }

    /**
     * Is the given part number {@link Id} compatible for this assembly position?
     *
     * @param partNumberId {@link Id} of the part number to check.
     * @return True or false.
     */
    public boolean canFit(Id partNumberId) {
        if(Id.isNull(partNumberId)) {
            return false;
        }
        if(partNumberId.equals(itemTypeId)) {
            return true;
        }
        InventoryItemType iit = getItemType();
        return iit != null && iit.listAPNs().stream().anyMatch(it -> it.getId().equals(partNumberId));
    }

    /**
     * List of assemblies defined under this assembly.
     * This instance is not included.
     *
     * @return List of assemblies.
     */
    public ObjectIterator<InventoryAssembly> listImmediateAssemblies() {
        return list(InventoryAssembly.class,
                "ParentItemType=" + itemTypeId, "ParentItemType,DisplayOrder").
                filter(ia -> {
                    ia.level = level + 1;
                    return true;
                });
    }

    /**
     * List of all assemblies (includes full-tree) defined under this assembly.
     * This instance is not included.
     *
     * @return List of assemblies.
     */
    public ObjectIterator<InventoryAssembly> listAssemblies() {
        return listTree(InventoryAssembly::listImmediateAssemblies);
    }

    /**
     * Get the level number for this assembly. (Level numbers will be correctly set only if assemblies are
     * retrieved via {@link #listAssemblies()} or {@link #listImmediateAssemblies()} methods).
     *
     * @return Level number in the hierarchy.
     */
    public int getLevel() {
        return level;
    }

    @Override
    public InventoryItemType getInventoryItemType() {
        return getItemType();
    }
}
