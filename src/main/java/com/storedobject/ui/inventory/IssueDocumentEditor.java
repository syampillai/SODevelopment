package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryIssueDocument;
import com.storedobject.ui.ObjectEditor;

public class IssueDocumentEditor <T extends InventoryIssueDocument> extends ObjectEditor<T> {

    public IssueDocumentEditor(Class<T> documentClass) {
        this(documentClass, 0);
    }

    public IssueDocumentEditor(Class<T> documentClass, int actions) {
        this(documentClass, actions, null);
    }

    public IssueDocumentEditor(Class<T> documentClass, int actions, String caption) {
        super(documentClass, actions, caption);
    }
}
