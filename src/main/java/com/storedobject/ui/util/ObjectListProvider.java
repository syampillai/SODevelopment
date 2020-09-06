package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectListProvider<T extends StoredObject, F> extends CallbackDataProvider<T, F> implements ObjectDataProvider<T, F> {

    public ObjectListProvider(Class<T> objectClass) {
        this();
    }

    public ObjectListProvider(List<T> list) {
        this();
    }

    private ObjectListProvider() {
        //noinspection ConstantConditions
        super(null, null);
    }

    @Override
    public Object getId(T item) {
        return null;
    }

    @Override
    public boolean isAllowAny() {
        return false;
    }

    public void setAllowAny(boolean allowAny) {
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
    public void filter(Predicate<T> filter) {
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public final void load(String condition, String orderBy) {
    }

    @Override
    public final void load(int linkType, StoredObject master, String condition, String orderBy) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return false;
    }

    public boolean contains(T item) {
        return false;
    }

    @Override
    public void filterChanged() {
        refreshAll();
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
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

    public final boolean add(T item) {
        return false;
    }

    public boolean add(int index, T item) {
        return true;
    }

    public boolean update(T item) {
        return true;
    }

    @Override
    public void edited(T item) {
    }

    public boolean delete(T item) {
        return false;
    }

    public boolean delete(int index) {
        return false;
    }

    public void reloadAll() {
    }

    public void reload(T item) {
    }

    public void clear() {
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return null;
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public boolean isInMemory() {
        return true;
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
    public int getObjectCount() {
        return 0;
    }

    public int size() {
        return 0;
    }

    public Stream<T> streamAll() {
        return null;
    }

    public Stream<T> streamFiltered() {
        return null;
    }

    @Override
    public void added(T item) {
    }

    @Override
    public void deleted(T item) {
    }

    @Override
    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        return null;
    }

    @Override
    public final AutoCloseable getResource() {
        return null;
    }
}