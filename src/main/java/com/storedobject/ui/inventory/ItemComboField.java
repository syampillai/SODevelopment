package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectProvider;

import java.util.List;

public class ItemComboField<I extends InventoryItem> extends ObjectComboField<I> implements ItemInput<I> {

    public ItemComboField(Class<I> objectClass) {
        super(objectClass);
    }

    public ItemComboField(Class<I> objectClass, boolean any) {
        super(objectClass, any);
    }

    public ItemComboField(Class<I> objectClass, String condition) {
        super(objectClass, condition);
    }

    public ItemComboField(Class<I> objectClass, String condition, boolean any) {
        super(objectClass, condition, any);
    }

    public ItemComboField(Class<I> objectClass, String condition, String orderBy) {
        super(objectClass, condition, orderBy);
    }

    public ItemComboField(Class<I> objectClass, String condition, String orderBy, boolean any) {
        super(objectClass, condition, orderBy, any);
    }

    public ItemComboField(String label, Class<I> objectClass) {
        super(label, objectClass);
    }

    public ItemComboField(String label, Class<I> objectClass, boolean any) {
        super(label, objectClass, any);
    }

    public ItemComboField(String label, Class<I> objectClass, String condition) {
        super(label, objectClass, condition);
    }

    public ItemComboField(String label, Class<I> objectClass, String condition, boolean any) {
        super(label, objectClass, condition, any);
    }

    public ItemComboField(String label, Class<I> objectClass, String condition, String orderBy) {
        super(label, objectClass, condition, orderBy);
    }

    public ItemComboField(String label, Class<I> objectClass, String condition, String orderBy, boolean any) {
        super(label, objectClass, condition, orderBy, any);
    }

    public ItemComboField(List<I> list) {
        super(list);
    }

    public ItemComboField(String label, List<I> list) {
        super(label, list);
    }

    @Override
    public void setStore(ObjectProvider<? extends InventoryStore> storeField) {
    }

    @Override
    public void setStore(InventoryStore store) {
    }

    @Override
    public void setLocation(ObjectProvider<? extends InventoryLocation> locationField) {
    }

    @Override
    public void setLocation(InventoryLocation location) {
    }

    @Override
    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
    }
}
