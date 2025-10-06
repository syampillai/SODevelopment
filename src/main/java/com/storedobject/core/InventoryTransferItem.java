package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public abstract class InventoryTransferItem extends StoredObject implements Detail, HasInventoryItem {

    Id itemId;
    private InventoryItem item;
    private Quantity quantity = Quantity.create(Quantity.class);
    private int amendment = 0;
    boolean internal;

    public InventoryTransferItem() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Quantity", "quantity");
        columns.add("Amendment", "int");
    }

    public static String[] protectedColumns() {
        return new String[] { "Amendment" };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Item.PartNumber.Name AS Item",
                "Item.PartNumber.PartNumber AS Part Number",
                "Item.SerialNumberDisplay AS Serial/Batch Number",
                "Quantity",
        };
    }

    public void setItem(Id itemId) {
        this.itemId = itemId;
        item = null;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItem item) {
        setItem(item == null ? null : item.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getItemId() {
        return itemId;
    }

    @Override
    public InventoryItem getItem() {
        if(item == null) {
            item = get(getTransaction(), InventoryItem.class, itemId, true);
        }
        return item;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Override
    @Column(order = 200, required = false)
    public Quantity getQuantity() {
        return quantity;
    }

    public final void setAmendment(int amendment) {
        if (!loading()) {
            throw new Set_Not_Allowed("Amendment");
        }
        this.amendment = amendment;
    }

    @SetNotAllowed
    @Column(order = 1000)
    public final int getAmendment() {
        return amendment;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            super.validateData(tm);
            return;
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, false);
        if(getItem().isSerialized()) {
            quantity = Count.ONE;
        } else if(quantity.isZero()) {
            quantity = item.getQuantity();
        }
        MeasurementUnit mu = quantity.getUnit();
        if(mu.obsolete) {
            throw new Invalid_State("Obsolete unit used: " + quantity);
        }
        if(quantity.isZero()) {
            throw new Invalid_State("Quantity of the item can't be zero");
        }
        quantity.canConvert(item.getPartNumber().getUnitOfMeasurement());
        if(quantity.isGreaterThan(item.getQuantity())) {
            throw new Invalid_State("Can not transfer " + quantity + ", only " + item.getQuantity() + " is available");
        }
        super.validateData(tm);
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        InventoryTransfer mr = getMaster(InventoryTransfer.class, true);
        if(mr != null && mr.getStatus() == 1) {
            throw new Invalid_State("Item is already sent. Please ask someone at '" +
                    mr.getToLocation().toDisplay() + "' to receive it first");
        }
    }

    @Override
    void savedCore() throws Exception {
        internal = false;
        super.savedCore();
    }

    @Override
    public final Id getUniqueId() {
        return itemId;
    }

    @Override
    public final boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return getClass().getName().equals(masterClass.getName() + "Item");
    }

    /**
     * Move the item to the specified location.
     * <p>Note: The default implementation just moves the item vy invoking
     * {@link InventoryTransaction#moveTo(InventoryItem, Quantity, String, InventoryLocation)} method.</p>
     * @param transaction Transaction to be used.
     * @param item Item to be moved (This is the same as the {@link #getItem()}, but its transit flag is already set.
     * @param toEntity Target entity.
     * @param toLocation Target location.
     */
    protected void  move(InventoryTransaction transaction, InventoryItem item, InventoryLocation toLocation, Entity toEntity) {
        transaction.moveTo(item, quantity, null, toLocation);
    }
}
