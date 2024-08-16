package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a "fitment position" (a position where an item can be fitted) of an assembled item.
 *
 * @author Syam
 */
public final class InventoryFitmentPosition extends InventoryLocation {

    private Id itemId;
    private Id assemblyId;
    private InventoryItem item;
    private InventoryAssembly assembly;

    /**
     * Constructor.
     */
    public InventoryFitmentPosition() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Assembly", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Item,Assembly", true);
        indices.add("Assembly");
    }

    public static String[] searchColumns() {
        return new String[] { "Name", "Item.PartNumber.PartNumber AS Item's P/N"};
    }

    public static String[] browseColumns() {
        return new String[] {
                "Item AS Assembly of",
                "Assembly.Position",
                "Assembly.Quantity",
                "Assembly.Accessory",
                "Assembly.Optional",
                "Assembly.ItemType AS Fitment Type",
                "FittedItem"
        };
    }

    @Override
    public void setName(String name) {
        if(!loading()) {
            throw new Set_Not_Allowed("Name");
        }
        super.setName(name);
    }

    @SetNotAllowed
    @Override
    public String getName() {
        return super.getName();
    }

    public void setItem(Id itemId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Item");
        }
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItem item) {
        setItem(item == null ? null : item.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)")
    public Id getItemId() {
        return itemId;
    }

    public InventoryItem getItem() {
        return getItem(null);
    }

    InventoryItem getItem(Transaction transaction) {
        if(item == null) {
            item = get(transaction, InventoryItem.class, itemId, true);
        }
        return item;
    }

    @SetNotAllowed
    public Id getAssemblyId() {
        return assemblyId;
    }

    public InventoryAssembly getAssembly() {
        if(assembly == null) {
            assembly = getRelated(InventoryAssembly.class, assemblyId);
        }
        return assembly;
    }

    public void setAssembly(Id assemblyId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Assembly");
        }
        this.assemblyId = assemblyId;
    }

    public void setAssembly(BigDecimal idValue) {
        setAssembly(new Id(idValue));
    }

    public void setAssembly(InventoryAssembly assembly) {
        setAssembly(assembly == null ? null : assembly.getId());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            return;
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, false);
        assemblyId = tm.checkType(this, assemblyId, InventoryAssembly.class);
        if(!getAssembly().getParentItemTypeId().equals(getItem().getPartNumberId())) {
            throw new Invalid_State("Item-assembly mismatch");
        }
        String name = assembly.getPosition();
        super.setName(name.isBlank() ? "*" : name);
        super.validateData(tm);
        checkForDuplicate("Item", "Assembly");
    }

    @SuppressWarnings("unused")
    private boolean dataCorrection(TransactionControl tc) {
        Id itemPN = getItem().getPartNumberId();
        if(getAssembly().getParentItemTypeId().equals(itemPN)) {
            return true;
        }
        InventoryItem fi = getFittedItem(tc.useTransaction());
        if(fi == null) {
            return tc.delete(this);
        }
        // Can we update assembly? ok
        boolean ok = true;
        for(InventoryFitmentPosition f: list(InventoryFitmentPosition.class, "Assembly=" + assemblyId)) {
            if(f.getItem().getPartNumberId().equals(itemPN)) {
                continue;
            }
            ok = false;
        }
        if(ok) {
            //assembly.parentItemTypeId = itemPN;
            return tc.save(assembly);
        }
        // Collect all matching assemblies
        List<InventoryAssembly> as = list(InventoryAssembly.class,
                "ParentItemType=" + itemPN + " AND Position='"+ assembly.getPosition() + "'").toList();
        // Check if we have a fully matching one
        for(InventoryAssembly a: as) {
            if(a.getItemTypeId().equals(fi.getPartNumberId())) { // Matching with currently fitted item
                assembly = null;
                assemblyId = a.getId();
                break;
            }
        }
        if(assembly != null) {
            // Check if we have an APN matching one
            for(InventoryAssembly a : as) {
                if(a.getItemType().listAPNs().stream().anyMatch(apn -> apn.getId().equals(fi.getPartNumberId()))) {
                    // Fitted item is an APN of the assembly
                    assembly = null;
                    assemblyId = a.getId();
                    break;
                }
            }
        }
        if(assembly == null) {
            // Duplicate?
            InventoryFitmentPosition duplicate = get(InventoryFitmentPosition.class,
                    "Item=" + itemId + " AND Assembly=" + assemblyId);
            if(duplicate != null) {
                if(duplicate.getFittedItem() == null) {
                    return tc.delete(duplicate);
                } else {
                    tc.setError(new SOException("Duplicate position: " + duplicate.toDisplay()));
                    return false;
                }
            }
            return tc.save(this);
        }
        // Create new assembly
        assembly.makeNew();
        //assembly.parentItemTypeId = itemPN;
        if(!tc.save(assembly)) {
            return false;
        }
        assemblyId = assembly.getId();
        assembly = null;
        try {
            setTransaction(tc.getTransaction());
        } catch(Exception e) {
            tc.setError(e);
            tc.rollback();
            return false;
        }
        return tc.save(this);
    }

    @Override
    public int getType() {
        return 14;
    }

    @Override
    public Id getEntityId() {
        return Id.ZERO;
    }

    @Override
    boolean checkStorage(InventoryItemType partNumber) {
        return canFit(partNumber);
    }

    /**
     * Is the given part number compatible for this position?
     *
     * @param partNumber Part number to check.
     * @return True or false.
     */
    public boolean canFit(InventoryItemType partNumber) {
        return getAssembly().canFit(partNumber);
    }

    /**
     * Is the given item compatible for this position?
     *
     * @param item Item to check.
     * @return True or false.
     */
    public boolean canFit(InventoryItem item) {
        return item != null && getAssembly().canFit(item.getPartNumberId());
    }

    /**
     * Is the given part number {@link Id} compatible for this position?
     *
     * @param partNumberId {@link Id} of the part number to check.
     * @return True or false.
     */
    public boolean canFit(Id partNumberId) {
        return getAssembly().canFit(partNumberId);
    }

    @Override
    public String toDisplay() {
        return toDisplay(true);
    }

    public String toDisplay(boolean includeFittedItem) {
        InventoryItem item = getItem();
        String it = item.toDisplay();
        InventoryAssembly ia = getAssembly();
        String position = getPosition();
        if(!position.isEmpty() && !position.startsWith("-") && !"*".equals(position)) {
            it = "(" + position + ") " + it;
        }
        if(!includeFittedItem) {
            return it;
        }
        it += " ⇐ ";
        InventoryItem fittedItem = getFittedItem();
        if(fittedItem == null) {
            if(ia == null) {
                it += "(ERROR)";
            } else {
                it += ia.getItemType().toDisplay();
            }
            if(item.isSerialized() || ia == null) {
                return it;
            }
            return it + " (" + ia.getQuantity() + ")";
        }
        return it + fittedItem.toDisplay();
    }

    public String getPosition() {
        InventoryAssembly ia = getAssembly();
        if(ia == null) {
            return "ERROR";
        }
        String p = ia.getPosition();
        if(!"-".equals(p)) {
            return p;
        }
        InventoryLocation loc = getItem().getLocation();
        return loc instanceof InventoryFitmentPosition ? ((InventoryFitmentPosition) loc).getPosition() : p;
    }

    /**
     * Get the item that is fitted at this position.
     *
     * @return The item that is fitted at this position. Null is returned if nothing is fitted there.
     */
    public InventoryItem getFittedItem() {
        return getFittedItem(null);
    }

    /**
     * Get the item that is fitted at this position (checks within a transaction).
     *
     * @param transaction Transaction.
     * @return The item that is fitted at this position. Null is returned if nothing is fitted there.
     */
    public InventoryItem getFittedItem(Transaction transaction) {
        return get(transaction, InventoryItem.class, "Location=" + getId(), true);
    }

    /**
     * Get a fitment position for the given item and assembly.
     *
     * @param item Item.
     * @param assembly Assembly.
     * @return Fitment position.
     */
    public static InventoryFitmentPosition get(InventoryItem item, InventoryAssembly assembly) {
        return getInt(null, item, assembly);
    }

    private static InventoryFitmentPosition getInt(Transaction t, InventoryItem item, InventoryAssembly assembly) {
        InventoryFitmentPosition pos = get(t, InventoryFitmentPosition.class,
                "Item=" + item.getId() + " AND Assembly=" + assembly.getId());
        if(pos != null) {
            pos.item = item;
            pos.assembly = assembly;
        }
        return pos;
    }

    /**
     * Get a fitment position for the given item and assembly. If the position doesn't exist, it will be created
     * if the transaction is non-null, otherwise, a virtual instance will be created. Also, if the transaction is
     * non-null and the creation fails, transaction will be rolled back and a virtual instance will be returned.
     *
     * @param transaction Transaction
     * @param item Item.
     * @param assembly Assembly.
     * @return Fitment position.
     */
    public static InventoryFitmentPosition get(Transaction transaction, InventoryItem item, InventoryAssembly assembly) {
        InventoryFitmentPosition pos = getInt(transaction, item, assembly);
        if(pos == null) {
            pos = new InventoryFitmentPosition();
            pos.itemId = item.getId();
            pos.item = item;
            pos.assemblyId = assembly.getId();
            pos.assembly = assembly;
            if(transaction == null) {
                pos.makeVirtual();
            } else {
                try {
                    pos.save(transaction);
                } catch(Exception e) {
                    transaction.rollback();
                    return get(null, item, assembly);
                }
            }
        }
        return pos;
    }

    /**
     * Get the level number for this assembly. Assembly level may not be determined correctly if no item is fitted
     * here.
     *
     * @return Level number in the hierarchy.
     */
    public int getLevel() {
       int level = getAssembly().level;
       if(level == 0) {
           InventoryItem item = getFittedItem(getTransaction());
           if(item != null) {
               level = item.getAssemblyLevel();
           }
       }
       return level;
    }

    /**
     * List of assemblies defined under this fitment position. The assemblies under the assembly involved in
     * this fitment position are listed. However, if this position is fitted with an APN, the immediate
     * assemblies under that APN's assembly are listed.
     *
     * @return List of assemblies.
     */
    public ObjectIterator<InventoryAssembly> listImmediateAssemblies() {
        InventoryItem item = getFittedItem();
        if(item == null) {
            return getAssembly().listImmediateAssemblies();
        }
        return item.getPartNumber().listImmediateAssemblies();
    }
}
