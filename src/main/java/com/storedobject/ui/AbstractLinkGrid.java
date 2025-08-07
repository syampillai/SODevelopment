package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.inventory.ItemContextMenu;
import com.storedobject.ui.inventory.ItemTypeContextMenu;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.Grid;

public abstract class AbstractLinkGrid<T extends StoredObject> extends EditableObjectGrid<T> implements LinkGrid<T> {

    final StoredObjectUtility.Link<T> link;
    final ObjectLinkField<T> linkField;

    public AbstractLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns, boolean any, boolean createContextMenu) {
        super(linkField.getObjectClass(), columns, any);
        this.linkField = linkField;
        this.link = linkField.getLink();
        init(createContextMenu);
    }

    protected AbstractLinkGrid(ObjectLinkField<T> linkField, Filtered<T> list, Iterable<String> columns, boolean createContextMenu) {
        super(linkField.getObjectClass(), list, columns);
        this.linkField = linkField;
        this.link = linkField.getLink();
        init(createContextMenu);
    }

    private void init(boolean createContextMenu) {
        if(createContextMenu) {
            if(HasInventoryItem.class.isAssignableFrom(getObjectClass())) {
                @SuppressWarnings("unchecked") Grid<? extends HasInventoryItem> g = (Grid<? extends HasInventoryItem>) this;
                new ItemContextMenu<>(g).setHideGRNDetails(getObjectClass() == InventoryGRNItem.class);
            } else if(HasInventoryItemType.class.isAssignableFrom(getObjectClass())) {
                @SuppressWarnings("unchecked") Grid<? extends HasInventoryItemType> g = (Grid<? extends HasInventoryItemType>) this;
                new ItemTypeContextMenu<>(g);
            }
        }
        setAllRowsVisible(true);
    }

    @Override
    protected void reloadedAllNow() {
        super.reloadedAllNow();
        if(size() > 450) {
            int ps = (size() + 50) / 10;
            if(getPageSize() < ps) {
                setPageSize(ps);
            }
        }
    }

    @Override
    public final ObjectEditor<T> createObjectEditor() {
        return this instanceof ReferenceLinkGrid<T> ? null : LinkGrid.super.createObjectEditor();
    }

    @Override
    public ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    @Override
    public final int getType() {
        return link.getType();
    }

    @Override
    public final String getName() {
        return link.getName();
    }

    @Override
    public final ObjectLinkField<T> getField() {
        return linkField;
    }

    @Override
    public final boolean isDetail() {
        return this instanceof DetailLinkGrid<T>;
    }

    @Override
    public final void setMaster(StoredObject master, boolean load) {
        super.setMaster(master, load);
    }

    @Override
    public final StoredObject getMaster() {
        return super.getMaster();
    }

    @Override
    public final boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void reload() {
        throw new SORuntimeException();
    }

    @Override
    public final T getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public final T getSelected() {
        return super.getSelected();
    }

    @Override
    public final View createView() {
        return null;
    }

    @Override
    public final View getView() {
        return null;
    }

    @Override
    public final View getView(boolean create) {
        return null;
    }

    @Override
    public void applyFilter() {
        link.setCondition(getEffectiveCondition(getFilterCondition()));
        link.setLoadPredicate(getLoadFilter().getLoadingPredicate());
        super.applyFilter();
    }

    @Override
    public void setOrderBy(String orderBy, boolean load) {
        link.setOrderBy(getOrderBy());
        super.setOrderBy(orderBy, load);
    }
}
