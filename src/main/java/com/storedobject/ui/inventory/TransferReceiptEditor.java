package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryTransferReceipt;

public class TransferReceiptEditor extends ReceiptDocumentEditor<InventoryTransferReceipt> {

    public TransferReceiptEditor() {
        this(0);
    }

    public TransferReceiptEditor(int actions) {
        this(actions, null);
    }

    public TransferReceiptEditor(int actions, String caption) {
        super(InventoryTransferReceipt.class, actions, caption);
    }
}
