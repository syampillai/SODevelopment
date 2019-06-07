package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryReceiptDocument;
import com.storedobject.ui.ObjectEditor;

public class ReceiptDocumentEditor <T extends InventoryReceiptDocument> extends ObjectEditor<T> {

    public ReceiptDocumentEditor(Class<T> documentClass) {
        this(documentClass, 0);
    }

    public ReceiptDocumentEditor(Class<T> documentClass, int actions) {
        this(documentClass, actions, null);
    }

    public ReceiptDocumentEditor(Class<T> documentClass, int actions, String caption) {
        super(documentClass, actions, caption);
    }
}
