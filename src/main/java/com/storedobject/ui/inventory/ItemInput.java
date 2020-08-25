package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.ui.ObjectInput;

public interface ItemInput<T extends InventoryItem> extends ObjectInput<T> {

    void setStore(ObjectProvider<? extends InventoryStore> storeField);
    void setStore(InventoryStore store);
    void setLocation(ObjectProvider<? extends InventoryLocation> locationField);
    void setLocation(InventoryLocation location);
    void setExtraFilterProvider(FilterProvider extraFilterProvider);

    static <I extends InventoryItem> ItemInput<I> create(Class<I> objectClass) {
        return create(null, objectClass, false);
    }

    static <I extends InventoryItem> ItemInput<I> create(String label, Class<I> objectClass) {
        return create(label, objectClass, false);
    }

    static <I extends InventoryItem> ItemInput<I> create(Class<I> objectClass, boolean allowAny) {
        return create(null, objectClass, allowAny);
    }

    static <I extends InventoryItem> ItemInput<I> create(String label, Class<I> objectClass, boolean allowAny) {
        if((StoredObjectUtility.hints(objectClass) & 2) == 2 && StoredObjectUtility.howBig(objectClass, allowAny) < 16) {
            return new ItemComboField<>(label, objectClass, allowAny);
        }
        return new ItemField<>(label, objectClass, allowAny);
    }
}