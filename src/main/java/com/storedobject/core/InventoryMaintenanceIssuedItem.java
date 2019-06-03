package com.storedobject.core;

public class InventoryMaintenanceIssuedItem extends InventoryIssueInformation {

    public InventoryMaintenanceIssuedItem() {
    }

    public static void columns(Columns columns) {
    }

    public static String[] protectedColumns() {
        return new String[] { "IssuedTo", };
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return null;
    }

    public void setQuantityRemaining(Quantity quantityRemaining) {
    }

    public void setQuantityRemaining(Object value) {
    }

    public Quantity getQuantityRemaining() {
        return null;
    }
    
    public void setDocument(InventoryIssueDocument document, InventoryIssue issue, InventoryIssueItem issueItem) {
    }
}
