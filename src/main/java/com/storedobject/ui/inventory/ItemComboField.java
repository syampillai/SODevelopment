package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectProvider;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ItemComboField<I extends InventoryItem> extends ObjectComboField<I> implements ItemInput<I> {

    private ObjectProvider<? extends InventoryStore> storeField;
    private ObjectProvider<? extends InventoryLocation> locationField;
    private FilterProvider extraFilterProvider;
    private final InventoryFilterProvider filter = new InventoryFilterProvider();
    private Registration forStore, forLocation;

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
        if(forStore != null) {
            forStore.remove();
            forStore = null;
        }
        this.storeField = storeField;
        newFilter();
        if(storeField instanceof HasValue) {
           forStore = ((HasValue<?, ?>) storeField).addValueChangeListener(e -> applyFilter());
        }
    }

    @Override
    public void setStore(InventoryStore store) {
        this.storeField = store == null ? null : () -> store;
        newFilter();
    }

    @Override
    public void setLocation(ObjectProvider<? extends InventoryLocation> locationField) {
        if(forLocation != null) {
            forLocation.remove();
            forLocation = null;
        }
        this.locationField = locationField;
        newFilter();
        if(locationField instanceof HasValue) {
            forLocation = ((HasValue<?, ?>) locationField).addValueChangeListener(e -> applyFilter());
        }
    }

    @Override
    public void setLocation(InventoryLocation location) {
        this.locationField = location == null ? null : () -> location;
        newFilter();
    }

    @Override
    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
        this.extraFilterProvider = extraFilterProvider;
        newFilter();
    }

    @Override
    public ObjectProvider<? extends InventoryItemType> getPartNumberProvider() {
        return () -> {
            I item = getValue();
            if(item != null) {
                return item.getPartNumber();
            }
            item = get(0);
            return item == null ? null : item.getPartNumber();
        };
    }

    private void newFilter() {
        setFixedFilter(filter, false);
        applyFilter();
    }

    private class InventoryFilterProvider implements FilterProvider {

        @Override
        public String getFilterCondition() {
            String f = null;
            if(extraFilterProvider != null) {
                f = "(" + extraFilterProvider.getFilterCondition() + ")";
            }
            if(locationField != null) {
                return (f == null ? "" : " AND ") + "Location=" + locationField.getObjectId();
            }
            if(storeField != null) {
                return (f == null ? "" : " AND ") + "Store=" + storeField.getObjectId();
            }
            return f;
        }
    }

    @Override
    public void reload() {
        load();
    }
}
