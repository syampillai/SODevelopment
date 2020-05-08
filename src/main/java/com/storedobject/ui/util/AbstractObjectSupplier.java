package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.CallbackDataProvider;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class AbstractObjectSupplier<T extends StoredObject, M> extends CallbackDataProvider<M, String> implements AbstractObjectDataProvider<T, M> {

    ObjectsCached<T, M> supplier;

    AbstractObjectSupplier(ObjectsCached<T, M> supplier, boolean load) {
        super(supplier.new Fetcher(), supplier.new Counter(), AbstractObjectSupplier::id);
        this.supplier = supplier;
    }

    private static Object id(Object item) {
        return item instanceof StoredObject ? ((StoredObject) item).getId() : item;
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
        supplier.unload();
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
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
    public void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return null;
    }

    /**
     * Set a programmatic filter.
     *
     * @param filter Filter
     */
    @Override
    public void filter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    /**
     * Get the DB filter.
     *
     * @param create Create a blank one if not exists
     * @return Current filter.
     */
    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void setFilter(String filter) {
    }

    @Override
    public void setFilter(FilterProvider filter) {
    }

    @Override
    public void filterChanged() {
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    @Override
    public void refreshAll() {
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public int indexOf(T object) {
        return -1;
    }

    @Override
    public T getItem(int index) {
        return null;
    }

    @Override
    public int getObjectCount() {
        return 0;
    }

    @Override
    public Stream<T> streamAll() {
        return null;
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return true;
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
    public final AutoCloseable getResource() {
        return null;
    }
}