package com.storedobject.core;

public abstract class InventoryReturnItem extends InventoryTransferItem {

    public InventoryReturnItem() {
    }

    public static void columns(Columns columns) {
    }

    public abstract String getItemType();

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(deleted()) {
            return;
        }
        itemId = tm.checkTypeAny(this, itemId, InventoryItem.class, false);
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

    @Override
    protected abstract void  move(InventoryTransaction transaction, InventoryItem item, InventoryLocation toLocation, Entity toEntity);
}
