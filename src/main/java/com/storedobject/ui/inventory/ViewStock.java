package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectGrid;
import com.storedobject.vaadin.CloseableView;

public class ViewStock extends ObjectGrid<InventoryItem> implements CloseableView {

    public ViewStock() {
        this(null, null);
    }

    public ViewStock(String caption) {
        this(caption, null);
    }

    public ViewStock(InventoryItemType partNumber) {
        this(null, partNumber);
    }

    public ViewStock(boolean canEdit) {
        this(null, null, canEdit);
    }

    public ViewStock(String caption, boolean canEdit) {
        this(caption, null, canEdit);
    }

    public ViewStock(InventoryItemType partNumber, boolean canEdit) {
        this(null, partNumber, canEdit);
    }

    public ViewStock(String caption, InventoryItemType partNumber) {
        this(caption, partNumber, false);
    }

    public ViewStock(String caption, InventoryItemType partNumber, boolean canEdit) {
        super(InventoryItem.class, StringList.create("Quantity", "SerialNumber", "InTransit", "Location"), true);
    }

    protected void customizeEditor(ObjectEditor<? extends InventoryItem> editor) {
    }
}
