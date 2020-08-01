package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryStore;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public class ItemField<I extends InventoryItem> extends ObjectGetField<I> {

    public ItemField(Class<I> objectClass) {
        this(null, objectClass);
    }

    public ItemField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    public ItemField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public ItemField(Class<I> objectClass, boolean allowAny, boolean addAllowed) {
        this(null, objectClass, allowAny, addAllowed);
    }

    public ItemField(String label, Class<I> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny, false);
    }

    public ItemField(String label, Class<I> objectClass, boolean allowAny, boolean addAllowed) {
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
