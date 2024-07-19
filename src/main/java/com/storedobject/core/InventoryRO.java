package com.storedobject.core;

public final class InventoryRO extends InventoryTransfer {

    public InventoryRO() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    public boolean getApprovalRequired() {
        return Math.random() > 0.5;
    }

    public Entity getRepairEntity() {
        return new Entity();
    }


    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param tm Transaction manager.
     */
    public void close(TransactionManager tm) throws Exception {
        tm.transact(this::close);
    }

    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param transaction Transaction.
     */
    public void close(Transaction transaction) throws Exception {
        InventoryItem ii = listLinks(InventoryROItem.class)
                .map(InventoryTransferItem::getItem)
                .filter(i -> i.getLocationId().equals(getToLocationId())).findFirst();
        if(ii != null) {
            throw new Invalid_State("Item '" + ii.toDisplay() + "' is still located at the repair location.");
        }
        status = 3;
        save(transaction);
    }
}
