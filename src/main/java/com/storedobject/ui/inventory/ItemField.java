package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public class ItemField<I extends InventoryItem> extends ObjectGetField<I> implements ItemInput<I> {

    public ItemField(Class<I> objectClass) {
        this(null, objectClass);
    }

    public ItemField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    public ItemField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public ItemField(String label, Class<I> objectClass, boolean allowAny) {
        super(objectClass, allowAny);
    }

    public void fixPartNumber(InventoryItemType partNumber) {
    }

    public void setPartNumber(InventoryItemType partNumber) {
    }

    public InventoryItemType getPartNumber() {
        return new InventoryItemType();
    }

    public Id getPartNumberId() {
        return new Id();
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

    @Override
    protected void doSearch() {
    }
}
