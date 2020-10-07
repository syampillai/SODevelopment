package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectGrid;
import com.storedobject.vaadin.CloseableView;

public class LocateItem extends ObjectGrid<InventoryItem> implements CloseableView {

    public LocateItem() {
        this((String)null);
    }

    public LocateItem(String caption) {
        this(caption, null, null, false);
    }

    public LocateItem(InventoryItemType partNumber) {
        this(null, partNumber);
    }

    public LocateItem(boolean canEdit) {
        this(null, null, null, canEdit);
    }

    public LocateItem(String caption, boolean canEdit) {
        this(caption, null, null, canEdit);
    }

    public LocateItem(InventoryItemType partNumber, boolean canEdit) {
        this(null, partNumber, canEdit);
    }

    public LocateItem(String caption, InventoryItemType partNumber) {
        this(caption, partNumber, false);
    }

    public LocateItem(String caption, InventoryItemType partNumber, boolean canEdit) {
        this(caption, partNumber, null, canEdit);
    }

    public LocateItem(Class<? extends InventoryItem> itemClass) {
        this(null, itemClass, false);
    }

    public LocateItem(String caption, Class<? extends InventoryItem> itemClass) {
        this(caption, null, itemClass, false);
    }

    public LocateItem(String caption, Class<? extends InventoryItem> itemClass, boolean canEdit) {
        this(caption, null, itemClass, canEdit);
    }

    private LocateItem(String caption, InventoryItemType partNumber, Class<? extends InventoryItem> itemClass, boolean canEdit) {
        super(InventoryItem.class, StringList.create("Quantity", "SerialNumber", "InTransit", "Location"), true);
    }

    protected void customizeEditor(ObjectEditor<? extends InventoryItem> editor) {
    }
}
