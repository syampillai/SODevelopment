package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryTransferReceipt extends InventoryReceiptDocument {

	public InventoryTransferReceipt() {
	}
	
    public static void columns(Columns columns) {
    }

	@Override
	public Entity getReceivedFrom() {
		return null;
	}
	
    public void setRequest(Id requestId) {
    }

    public void setRequest(BigDecimal idValue) {
    }

    public void setRequest(InventoryTransferRequest request) {
    }

    public Id getRequestId() {
		return null;
    }

    public InventoryTransferRequest getRequest() {
		return null;
    }

    public Id getStoreId() {
		return null;
    }
	
    public void setSentBy(Id sentById) {
    }

    public void setSentBy(BigDecimal idValue) {
    }

    public void setSentBy(InventoryStore sentBy) {
    }

    public Id getSentById() {
		return null;
    }

    public InventoryStore getSentBy() {
        return null;
    }
}
