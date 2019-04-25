package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryTransferReceiptItem extends InventoryReceiptDocumentItem {

	public InventoryTransferReceiptItem() {
	}
	
    public static void columns(Columns columns) {
    }

    public void setRequest(Id requestId) {
    }

    public void setRequest(BigDecimal idValue) {
    }

    public void setRequest(InventoryTransferRequestItem request) {
    }

    public Id getRequestId() {
        return null;
    }

    public InventoryTransferRequestItem getRequest() {
        return null;
    }
}
