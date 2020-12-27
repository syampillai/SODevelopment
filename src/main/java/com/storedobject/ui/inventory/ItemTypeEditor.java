package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.ObjectEditor;

public class ItemTypeEditor<T extends InventoryItemType> extends ObjectEditor<T> {

    public ItemTypeEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public ItemTypeEditor(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public ItemTypeEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public ItemTypeEditor(String className) throws Exception {
        super(className);
    }
}
