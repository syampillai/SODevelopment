package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AbstractObjectSupplier<T extends StoredObject, M, F> extends CallbackDataProvider<M, F> implements AbstractObjectDataProvider<T, M, F> {

    private final List<ObjectDataLoadedListener> dataLoadedListeners = new ArrayList<>();
    ObjectsCached<T, M, F> supplier;

    AbstractObjectSupplier(ObjectsCached<T, M, F> supplier, boolean load) {
        super(supplier.new Fetcher(), supplier.new Counter(), AbstractObjectSupplier::id);
        this.supplier = supplier;
        this.supplier.init = load;
        this.supplier.setDataLoadedListener(() -> this.dataLoadedListeners.forEach(ObjectDataLoadedListener::dataLoaded));
    }

    @Override
    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    private static Object id(Object item) {
        return item instanceof StoredObject ? ((StoredObject) item).getId() : item;
    }

    @Override
    public boolean isAllowAny() {
        return supplier.any;
    }

    @Override
    public Class<T> getObjectClass() {
        return supplier.objectClass;
    }

    @Override
    public void close() {
        supplier.unload();
    }

    @Override
    public void setViewFilter(ViewFilter<T> viewFilter) {
        supplier.viewFilter =viewFilter;
    }

    @Override
    public ViewFilter<T> getViewFilter() {
        return supplier.viewFilter;
    }

    @Override
    public void filterView(String filters) {
        supplier.viewFilterString = filters;
        super.refreshAll();
    }

    @Override
    public boolean isFullyLoaded() {
        return supplier.fullyLoaded;
    }

    @Override
    public void load(String condition, String orderBy) {
        if(isFullyLoaded() && supplier.nullCond(condition) && !orderChanged(orderBy)) {
            return;
        }
        load(0, null, condition, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        supplier.init = true;
        supplier.master = master;
        supplier.linkType = linkType;
        supplier.condition = condition;
        supplier.orderBy = orderBy;
        if(master != null) {
            supplier.unload();
        } else {
            supplier.reload();
        }
        super.refreshAll();
    }

    @Override
    public void load(Stream<T> objects) {
        if(objects == null) {
            return;
        }
        supplier.init = true;
        supplier.load(objects);
        super.refreshAll();
    }

    @Override
    public void clear(boolean refresh) {
        supplier.unload();
        if(refresh) {
            super.refreshAll();
        }
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
        supplier.setLoadFilter(filter);
        super.refreshAll();
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return supplier.getLoadFilter();
    }

    /**
     * Set a programmatic filter.
     *
     * @param filter Filter
     */
    @Override
    public void filter(Predicate<T> filter) {
        if(supplier.filter(filter)) {
            super.refreshAll();
        }
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return supplier.filter;
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        supplier.filterDB = filter;
        filterChanged();
    }

    /**
     * Get the DB filter.
     *
     * @param create Create a blank one if not exists
     * @return Current filter.
     */
    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        if(supplier.filterDB == null) {
            if(!create) {
                return null;
            }
            supplier.filterDB = new ObjectSearchFilter();
        }
        return supplier.filterDB;
    }

    @Override
    public void setFilter(String filter) {
        if(supplier.filterDB == null) {
            if(filter == null || filter.isEmpty()) {
                return;
            }
            supplier.filterDB = new ObjectSearchFilter();
        }
        if(Objects.equals(filter, supplier.filterDB.getCondition())) {
            return;
        }
        supplier.filterDB.setCondition(filter);
        filterChanged();
    }

    @Override
    public void filterChanged() {
        supplier.unload();
        supplier.init = true;
        super.refreshAll();
    }

    @Override
    public void refreshAll() {
        if(supplier.cache != null) {
            supplier.cache.refresh();
        }
        super.refreshAll();
    }

    @Override
    public boolean isFullyCached() {
        return supplier.cache != null && (supplier.cache.getCacheSize() == 0 || supplier.cache.size() <= supplier.cache.getCacheSize());
    }

    @Override
    public int indexOf(T object) {
        if(object == null || supplier.cache == null || !supplier.objectClass.isAssignableFrom(object.getClass())) {
            return -1;
        }
        return supplier.cache.indexOf(object);
    }

    private boolean orderChanged(String orderBy) {
        return !Objects.equals(orderBy, supplier.orderBy);
    }

    @Override
    public T getItem(int index) {
        return supplier.getItem(index);
    }

    @Override
    public int getObjectCount() {
        return supplier.size();
    }

    @Override
    public Stream<T> streamAll() {
        return supplier.cache == null ? Stream.of() : supplier.cache.list().stream();
    }

    @Override
    public boolean validateFilterCondition(T value) {
        if(value == null) {
            return true;
        }
        Predicate<T> predicate = getFilterPredicate();
        if(predicate != null && !predicate.test(value)) {
            return false;
        }
        predicate = getLoadFilter();
        if(predicate != null && !predicate.test(value)) {
            return false;
        }
        ObjectSearchFilter filter = getFilter(false);
        if(filter == null) {
            return true;
        }
        String c = filter.getFilter(null);
        if(c == null || c.isEmpty()) {
            return true;
        }
        return StoredObject.exists(value.getClass(), "T.Id=" + value.getId() + " AND (" + c + ")");
    }

    @Override
    public void added(T item) {
        supplier.added(item);
        super.refreshAll();
    }

    @Override
    public void deleted(T item) {
        supplier.deleted(item);
        super.refreshAll();
    }

    @Override
    public void edited(T item) {
        System.err.println("TODO: " + getClass());
    }

    @Override
    public final AutoCloseable getResource() {
        return new Cleaner(supplier);
    }

    private static class Cleaner implements AutoCloseable {

        private final ObjectsCached<?, ?, ?> supplier;

        private Cleaner(ObjectsCached<?, ?, ?> supplier) {
            this.supplier = supplier;
        }

        @Override
        public void close() {
            supplier.unload();
        }
    }
}
