package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryTransferRequest extends InventoryIssueDocument {

	public InventoryTransferRequest() {
	}

    public static void columns(Columns columns) {
    }
    
	@Override
	public Entity getIssuedTo() {
		return null;
	}
	
    public void setReceipt(Id receiptId) {
    }

    public void setReceipt(BigDecimal idValue) {
    }

    public void setReceipt(InventoryTransferReceipt receipt) {
    }

    public Id getReceiptId() {
        return null;
    }

    public InventoryTransferReceipt getReceipt() {
        return null;
    }
	
    public Id getStoreId() {
        return null;
    }
	
    public void setRequestedBy(Id requestedById) {
    }

    public void setRequestedBy(BigDecimal idValue) {
    }

    public void setRequestedBy(InventoryStore requestedBy) {
    }

    public Id getRequestedById() {
        return null;
    }

    public InventoryStore getRequestedBy() {
        return null;
    }
}