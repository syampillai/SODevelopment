package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.AbstractObjectForest;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectForestViewerSupplier<T extends StoredObject, F> implements AbstractObjectDataProvider<T, Object, F>, AbstractObjectForestSupplier<T, F> {

    private final ObjectForestSupplier<T, F> supplier;

    public ObjectForestViewerSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        supplier = new ObjectForestSupplier<>(objectClass, condition, orderBy, any);
    }

    public ObjectForestViewerSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition,
                                      String orderBy, boolean any) {
        supplier = new ObjectForestSupplier<>(linkType, master, objectClass, condition, orderBy, any);
        load(ObjectIterator.create());
    }

    @Override
    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        return supplier.addObjectDataLoadedListener(listener);
    }

    @Override
    public final AbstractObjectForest.Customizer getCustomizer() {
        return supplier.getCustomizer();
    }

    @Override
    public void setListLinks(ListLinks listLinks) {
        supplier.setListLinks(listLinks);
    }

    @Override
    public void close() {
        supplier.close();
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
    public boolean isAllowAny() {
        return supplier.isAllowAny();
    }

    @Override
    public Class<T> getObjectClass() {
        return supplier.getObjectClass();
    }

    @Override
    public void load(String filterClause, String orderBy) {
        supplier.load(filterClause, orderBy);
    }

    @Override
    public void load(int linkType, StoredObject master, String filterClause, String orderBy) {
        supplier.load(linkType, master, filterClause, orderBy);
    }

    @Override
    public void load(Stream<T> objects) {
        supplier.load(objects);
    }

    @Override
    public boolean isFullyLoaded() {
        return supplier.isFullyLoaded();
    }

    @Override
    public void setFilter(FilterProvider filterProvider) {
        supplier.setFilter(filterProvider);
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        supplier.setFilter(filter);
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return supplier.getFilter(create);
    }

    @Override
    public void filter(Predicate<T> filter) {
        supplier.filter(filter);
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return supplier.getFilterPredicate();
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
        supplier.setLoadFilter(filter);
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return supplier.getLoadFilter();
    }

    @Override
    public void setFilter(String extraFilterClause) {
        supplier.setFilter(extraFilterClause);
    }

    @Override
    public Stream<T> streamAll() {
        return supplier.streamAll();
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return supplier.validateFilterCondition(value);
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return supplier.getFilter();
    }

    @Override
    public int indexOf(T object) {
        return supplier.indexOf(object);
    }

    @Override
    public List<T> listRoots() {
        return supplier.listRoots();
    }

    @Override
    public T getItem(int index) {
        return supplier.getItem(index);
    }

    @Override
    public boolean isFullyCached() {
        return supplier.isFullyCached();
    }

    @Override
    public void filterChanged() {
        supplier.filterChanged();
    }

    @Override
    public int getObjectCount() {
        return supplier.getObjectCount();
    }

    @Override
    public void added(T item) {
        supplier.added(item);
    }

    @Override
    public void edited(T item) {
        supplier.edited(item);
    }

    @Override
    public void deleted(T item) {
        supplier.deleted(item);
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, F> hierarchicalQuery) {
        Object p = hierarchicalQuery.getParent();
        if(p instanceof ObjectForestSupplier.LinkObject) {
            p = ((ObjectForestSupplier.LinkObject) p).getObject();
        }
        if(p instanceof StoredObject) {
            return (int) fetchChildren(hierarchicalQuery).count();
        }
        return supplier.getChildCount(hierarchicalQuery);
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, F> hierarchicalQuery) {
        Object p = hierarchicalQuery.getParent();
        if(p instanceof ObjectForestSupplier.LinkObject) {
            p = ((ObjectForestSupplier.LinkObject) p).getObject();
        }
        if(p instanceof StoredObject) {
            int offset = hierarchicalQuery.getOffset(), limit = hierarchicalQuery.getLimit(), count = 0;
            HierarchicalQuery<Object, F> q = new HierarchicalQuery<>(0, Integer.MAX_VALUE, null, null, null, p);
            List<ObjectForestSupplier.LinkNode> linkNodes = supplier.fetchChildren(q).map(c -> (ObjectForestSupplier.LinkNode)c).collect(Collectors.toList());
            Stream<Object> items = null;
            List<ObjectForestSupplier.LinkObject> links;
            for(ObjectForestSupplier.LinkNode linkNode: linkNodes) {
                if(linkNode.size(q) == 0) {
                    continue;
                }
                links = linkNode.links();
                if(items == null) {
                    items = links.stream().map(o -> o);
                } else {
                    items = Stream.concat(items, links.stream());
                }
                count += links.size();
                if(count >= (offset + limit)) {
                    break;
                }
            }
            return items == null ? Stream.of() : items.skip(offset).limit(limit);
        }
        return supplier.fetchChildren(hierarchicalQuery);
    }

    @Override
    public boolean hasChildren(Object p) {
        if(p instanceof ObjectForestSupplier.LinkObject) {
            p = ((ObjectForestSupplier.LinkObject) p).getObject();
        }
        if(p instanceof StoredObject) {
            HierarchicalQuery<Object, F> q = new HierarchicalQuery<>(0, Integer.MAX_VALUE, null, null, null, p);
            return supplier.fetchChildren(q).map(c -> (ObjectForestSupplier.LinkNode)c).anyMatch(linkNode -> !linkNode.isEmpty());
        }
        return supplier.hasChildren(p);
    }

    @Override
    public boolean isInMemory() {
        return supplier.isInMemory();
    }

    @Override
    public void refreshItem(Object o) {
        supplier.refreshItem(o);
    }

    @Override
    public void refreshAll() {
        supplier.refreshAll();
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<Object> dataProviderListener) {
        return supplier.addDataProviderListener(dataProviderListener);
    }

    @Override
    public final AutoCloseable getResource() {
        return new Cleaner(supplier);
    }

    private record Cleaner(ObjectForestSupplier<?, ?> supplier) implements AutoCloseable {

        @Override
        public void close() {
            supplier.close();
        }
    }
}
