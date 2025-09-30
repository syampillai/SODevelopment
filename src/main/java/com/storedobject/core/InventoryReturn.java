package com.storedobject.core;

import com.storedobject.core.annotation.Column;

/**
 * Class that represents a type of inventory transfer that is returned later.
 *
 * @author Syam
 */
public abstract class InventoryReturn extends InventoryTransfer implements TradeType {

    private boolean approvalRequired = true;

    public InventoryReturn() {
    }

    public static void columns(Columns columns) {
        columns.add("ApprovalRequired", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("FromLocation,Status", "Status IN (1,2)");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "ApprovalRequired",
        };
    }

    @Override
    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    @Column(required = false)
    @Override
    public boolean getApprovalRequired() {
        return approvalRequired;
    }

    /**
     * Close (only if all the items are returned via some means).
     *
     * @param tm Transaction manager.
     */
    public void close(TransactionManager tm) throws Exception {
        close(tm, false);
    }

    /**
     * Close (only if all the items are returned via some means).
     *
     * @param transaction Transaction.
     */
    public void close(Transaction transaction) throws Exception {
        close(transaction, false);
    }

    /**
     * Close (only if all the items are returned via some means).
     *
     * @param tm Transaction manager.
     * @param manually Manually or not (Status will be set to "Returned" or "Closed" accordingly).
     */
    public void close(TransactionManager tm, boolean manually) throws Exception {
        tm.transact(t -> close(t, manually));
    }

    /**
     * Close (only if all the items are returned via some means).
     *
     * @param transaction Transaction.
     * @param manually Manually or not (Status will be set to "Returned" or "Closed" accordingly).
     */
    public void close(Transaction transaction, boolean manually) throws Exception {
        InventoryItem ii = listLinks(InventoryReturnItem.class, true)
                .map(InventoryTransferItem::getItem)
                .filter(i -> i.getLocationId().equals(getToLocationId())).findFirst();
        if(ii != null) {
            throw new Invalid_State("Item '" + ii.toDisplay() + "' is still located at the other location.");
        }
        status = manually ? 3 : 4; // Returned / Closed
        save(transaction);
    }

    @Override
    public String getActionDescription(ActionType actionType) {
        return switch (actionType) {
            case VERB_PRESENT -> "Send";
            case VERB_PAST, VERB_PAST_PARTICIPLE -> "Sent";
            default -> super.getActionDescription(actionType);
        };
    }

    @Override
    public final int getType() {
        return 1001;
    }
}
