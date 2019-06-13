package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryStore;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public class InventoryItemField<I extends InventoryItem> extends ObjectGetField<I> {

    public InventoryItemField(Class<I> objectClass) {
        this(null, objectClass);
    }

    public InventoryItemField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    public InventoryItemField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public InventoryItemField(Class<I> objectClass, boolean allowAny, boolean addAllowed) {
        this(null, objectClass, allowAny, addAllowed);
    }

    public InventoryItemField(String label, Class<I> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny, false);
    }

    public InventoryItemField(String label, Class<I> objectClass, boolean allowAny, boolean addAllowed) {
        super(objectClass, allowAny);
    }

    @Override
    protected ButtonLayout initComponent() {
        return null;
    }

    @Override
    protected void setPresentationValue(I value) {
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
    }

    @Override
    public void setDisplayDetail(Consumer<I> displayDetail) {
    }

    @Override
    public void setPrefixFieldControl(boolean searchFieldControl) {
    }

    public void setStoreField(ObjectProvider<? extends InventoryStore> storeField) {
    }

    public ObjectProvider<? extends InventoryStore> getStoreField() {
        return null;
    }

    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
    }

    public FilterProvider getExtraFilterProvider() {
        return null;
    }

    @Override
    protected void doSearch() {
    }
}
