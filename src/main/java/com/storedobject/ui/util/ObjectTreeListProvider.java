package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectTreeBuilder;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.stream.Stream;

public class ObjectTreeListProvider<T extends StoredObject> extends AbstractBackEndHierarchicalDataProvider<T, String> {

    public ObjectTreeListProvider(List<T> roots, ObjectTreeBuilder objectTreeBuilder) {
    }

    public ObjectTreeListProvider(Stream<T> roots, ObjectTreeBuilder objectTreeBuilder) {
    }

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        return () -> {};
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    public ObjectTreeBuilder getTreeBuilder() {
        return null;
    }

    public List<T> listRoots() {
        return null;
    }

    @Override
    protected Stream<T> fetchChildrenFromBackEnd(HierarchicalQuery<T, String> query) {
        return null;
    }

    @Override
    public int getChildCount(HierarchicalQuery<T, String> query) {
        return 0;
    }

    @Override
    public boolean hasChildren(T parent) {
        return false;
    }
    public T getItem(int index) {
        return null;
    }

    public int getObjectCount() {
        return 0;
    }
}
