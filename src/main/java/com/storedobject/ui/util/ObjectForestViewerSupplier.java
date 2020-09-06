package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectForestViewerSupplier<T extends StoredObject, F> implements AbstractObjectDataProvider<T, Object, F>, AbstractObjectForestSupplier<T, F> {

    public ObjectForestViewerSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
    }

    public ObjectForestViewerSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
    }

    @Override
    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        return () -> {};
    }

    @Override
    public List<T> listRoots() {
        return null;
    }

    @Override
    public void setListLinks(ListLinks listLinks) {
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    @Override
    public Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public void setViewFilter(ViewFilter<T> viewFilter) {
    }

    @Override
    public ViewFilter<T> getViewFilter() {
        return null;
    }

    @Override
    public void filterView(String filters) {

    }

    @Override
    public Stream<T> streamAll() {
        return null;
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return false;
    }

    @Override
    public int indexOf(T object) {
        return 0;
    }

    @Override
    public T getItem(int index) {
        return null;
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public void load(String condition, String orderBy) {
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
    }

    @Override
    public void load(ObjectIterator<T> objects) {
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public void filter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return null;
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void filterChanged() {
    }

    @Override
    public void added(T item) {
    }

    @Override
    public void edited(T item) {
    }

    @Override
    public void deleted(T item) {
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, F> hierarchicalQuery) {
        return 0;
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, F> hierarchicalQuery) {
        return Stream.of();
    }

    @Override
    public boolean hasChildren(Object o) {
        return false;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public void refreshItem(Object o) {
    }

    @Override
    public void refreshAll() {

    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<Object> dataProviderListener) {
        return null;
    }

    @Override
    public final AutoCloseable getResource() {
        return null;
    }
}
