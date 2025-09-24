package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;

public abstract class InventoryReturnItem extends InventoryTransferItem {

    private Id originalItemId = Id.ZERO;

    public InventoryReturnItem() {
    }

    public static void columns(Columns columns) {
        columns.add("OriginalItem", "id");
    }

    public static String[] protectedColumns() {
        return new String[] { "OriginalItem" };
    }

    public void setOriginalItem(Id itemId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Original Item");
        }
        originalItemId = Id.isNull(itemId) ? super.itemId : itemId;
    }

    public void setOriginalItem(BigDecimal idValue) {
        setOriginalItem(new Id(idValue));
    }

    public void setOriginalItem(InventoryItem item) {
        setOriginalItem(item == null ? null : item.getId());
    }

    @Column(style = "(any)", order = 1000)
    public Id getOriginalItemId() {
        return Id.isNull(originalItemId) ? itemId : originalItemId;
    }

    public InventoryItem getOriginalItem() {
        if(Id.isNull(originalItemId) || originalItemId.equals(itemId)) {
            return getItem();
        }
        return get(getTransaction(), InventoryItem.class, originalItemId, true);
    }

    public abstract String getItemType();

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            return;
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, false);
        if(Id.isNull(originalItemId)) {
            originalItemId = itemId;
        } else if(!originalItemId.equals(itemId)) {
            originalItemId = tm.checkTypeAny(this, originalItemId, InventoryItem.class, false);
        }
        InventoryItem item = getItem();
        if(!internal) {
            switch(item.getLocation().getType()) {
                case 0, 4, 5, 9, 10, 11, 17 -> {
                }
                default -> {
                    boolean check = !created();
                    if(check) {
                        InventoryReturnItem old = get(getClass(), getId());
                        check = !old.getItemId().equals(itemId);
                    }
                    if(check) {
                        throw new Invalid_State("Item: " + item.toDisplay()
                                + ". The current location of this item is: " + item.getLocation().toDisplay());
                    }
                }
            }
        }
        if(item.isBlocked()) {
            throw new Invalid_State("Blocked Item: " + item.toDisplay());
        }
        super.validateData(tm);
    }

    @Override
    public String toString() {
        return getItem().toDisplay();
    }

    public void setReturningItem(InventoryItem item) throws SOException {
        if(created()) {
            throw new Invalid_State("Can't do this on unsaved item");
        }
        if(item == null) {
            throw new SOException("Item not specified");
        }
        if(item.isBlocked()) {
            throw new SOException("Blocked Item: " + item.toDisplay());
        }
        InventoryItem originalItem = getOriginalItem();
        if(!originalItem.getPartNumberId().equals(item.getPartNumberId()) && !item.getPartNumber().isAPN(originalItem)) {
            throw new SOException(item.toDisplay() + " is not an APN of " + originalItem.toDisplay());
        }
        setItem(item.getId());
    }
}
