package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryTransferRequest;

public class TransferRequestEditor extends IssueDocumentEditor<InventoryTransferRequest> {

    public TransferRequestEditor() {
        this(0);
    }

    public TransferRequestEditor(int actions) {
        this(actions, null);
    }

    public TransferRequestEditor(int actions, String caption) {
        super(InventoryTransferRequest.class, actions, caption);
    }
}
