package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public final class InventoryRO extends InventoryTransfer implements TradeType {

    private boolean approvalRequired = true;

    public InventoryRO() {
    }

    public static void columns(Columns columns) {
        columns.add("ApprovalRequired", "boolean");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "ApprovalRequired",
        };
    }

    public static void indices(Indices indices) {
        indices.add("FromLocation,Status", "Status IN (1,2)");
    }

    public static String[] links() {
        return new String[]{
                "Items|com.storedobject.core.InventoryROItem|||0",
        };
    }

    @Override
    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    @Column(order = 800, required = false)
    @Override
    public boolean getApprovalRequired() {
        return approvalRequired;
    }

    public Entity getRepairEntity() {
        return get(Entity.class, getToLocation().getEntityId());
    }

    @Override
    public String getActionDescription(ActionType actionType) {
        return switch (actionType) {
            case NOUN -> "Send for repair";
            case VERB_PRESENT -> "Send";
            case VERB_PAST, VERB_PAST_PARTICIPLE -> "Sent";
        };
    }

    @Override
    public int getToLocationType() {
        return 3;
    }

    @Override
    public String getToLocationName() {
        return "Repair/Maintenance Organization";
    }

    @Override
    public String getStatusValue() {
        if(getStatus() == 0) {
            return approvalRequired ? getStatusValue(0) : "Approved";
        }
        return super.getStatusValue();
    }

    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param tm Transaction manager.
     */
    public void close(TransactionManager tm) throws Exception {
        close(tm, false);
    }

    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param transaction Transaction.
     */
    public void close(Transaction transaction) throws Exception {
        close(transaction, false);
    }

    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param tm Transaction manager.
     * @param manually Manually or not (Status will be set to "Returned" or "Closed" accordingly).
     */
    public void close(TransactionManager tm, boolean manually) throws Exception {
        tm.transact(t -> close(t, manually));
    }

    /**
     * Close the RO (only if all the items are returned via some means).
     *
     * @param transaction Transaction.
     * @param manually Manually or not (Status will be set to "Returned" or "Closed" accordingly).
     */
    public void close(Transaction transaction, boolean manually) throws Exception {
        InventoryItem ii = listLinks(InventoryROItem.class)
                .map(InventoryTransferItem::getItem)
                .filter(i -> i.getLocationId().equals(getToLocationId())).findFirst();
        if(ii != null) {
            throw new Invalid_State("Item '" + ii.toDisplay() + "' is still located at the repair location.");
        }
        status = manually ? 3 : 4; // Returned / Closed
        save(transaction);
    }

    public static String actionPrefixForUI() {
        return "RO";
    }

    @Override
    public int getType() {
        return 1001;
    }
}