package com.storedobject.ui;

import com.storedobject.common.ResourceOwner;
import com.storedobject.core.ObjectList;
import com.storedobject.core.ObjectForest;
import com.storedobject.core.*;
import com.storedobject.ui.util.ChildVisitor;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.function.SerializablePredicate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectForestProvider<T extends StoredObject> extends AbstractTreeProvider<Object, T>
        implements ObjectLoader<T>, ObjectChangedListener<T>, AutoCloseable, ResourceOwner, FilterMethods<T>,
        ChildVisitor<T, Object> {

    private final ObjectLoadFilter<T> systemFilter = new ObjectLoadFilter<>(), fixedFilter = new ObjectLoadFilter<>();
    private LoadCallBack loadCallBack;
    private final ObjectForest<T> forest;

    public ObjectForestProvider(ObjectForest<T> forest) {
        super(forest.getObjectClass());
        this.forest = forest;
    }

    public ObjectForest<T> getForest() {
        return forest;
    }

    @Override
    public List<T> getRoots() {
        return forest.getRoots();
    }

    @Override
    public List<T> listRoots() {
        return getRoots();
    }

    public Stream<Object> streamChildren(Object parent) {
        return forest.stream(parent, 0, Integer.MAX_VALUE);
    }

    @Override
    public Filtered<T> getData() {
        return forest;
    }

    ObjectLoadFilter<T> getSystemFilter() {
        return systemFilter;
    }

    @Override
    public ObjectLoadFilter<T> getFixedFilter() {
        return fixedFilter;
    }

    @Override
    public Object getId(Object item) {
        return item instanceof HasId hi ? hi.getId() : item;
    }

    public boolean contains(T item) {
        return getRoots().contains(item);
    }

    @Override
    void saveFilter(Predicate<Object> filter) {
        if(filter == null) {
            ObjectLoader.super.setViewFilter(null, false);
        } else {
            ObjectLoader.super.setViewFilter(new WrappedPredicate<>(filter), false);
        }
    }

    @Override
    Predicate<Object> retrieveFilter() {
        Predicate<T> predicate = getLoadFilter().getLoadingPredicate();
        return predicate == null ? null : ((WrappedPredicate<T>)predicate).predicate;
    }

    private record WrappedPredicate<T>(Predicate<Object> predicate) implements Predicate<T> {

        @Override
        public boolean test(T item) {
            return predicate.test(item);
        }
    }

    @Override
    public String getEffectiveCondition(String condition) {
        return systemFilter.getFilter(fixedFilter.getFilter(condition));
    }

    @Override
    public void load(String condition, String orderBy, boolean any) {
        if(loadCallBack == null) {
            loadInt(condition, orderBy, any);
        } else {
            loadCallBack.load(() -> loadInt(condition, orderBy, any));
        }
    }

    private void loadInt(String condition, String orderBy, boolean any) {
        setOrderBy(orderBy, false);
        setMaster(null, false);
        forest.load(getEffectiveCondition(condition), orderBy, any);
        refreshAll();
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy, boolean any) {
        if(loadCallBack == null) {
            loadInt(linkType, master, condition, orderBy, any);
        } else {
            loadCallBack.load(() -> loadInt(linkType, master, condition, orderBy, any));
        }
    }

    private void loadInt(int linkType, StoredObject master, String condition, String orderBy, boolean any) {
        setOrderBy(orderBy, false);
        setMaster(null, false);
        setLinkType(linkType, false);
        forest.load(linkType, master, getEffectiveCondition(condition), orderBy, any);
        refreshAll();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        if(loadCallBack == null) {
            loadInt(objects);
        } else {
            loadCallBack.load(() -> loadInt(objects));
        }
    }

    private void loadInt(ObjectIterator<T> objects) {
        forest.load(objects);
        refreshAll();
    }

    @Override
    public void applyFilterPredicate() {
        getData().filter(getLoadFilter().getViewFilter());
        refreshAll();
    }

    @Override
    public T get(int index) {
        return getRoots().get(index);
    }

    @Override
    public int indexOf(T object) {
        return getRoots().indexOf(object);
    }

    @Override
    public Stream<T> streamAll() {
        return forest.streamAll(0, forest.sizeAll());
    }

    @Override
    public Stream<T> streamFiltered() {
        return forest.stream(0, forest.size());
    }

    @Override
    public int getCacheLevel() {
        List<T> roots = getRoots();
        if(roots instanceof ObjectList) {
            return ((ObjectList<T>)roots).getCacheLevel();
        }
        return 100;
    }

    @Override
    public void clear() {
        forest.close();
        refreshAll();
    }

    @Override
    public void refreshAll() {
        forest.refresh();
        super.refreshAll();
    }

    @Override
    public void refreshItem(Object item) {
        forest.refresh(item);
        super.refreshItem(item);
    }

    @Override
    public void refreshItem(Object item, boolean refreshChildren) {
        forest.refresh(item, refreshChildren);
        super.refreshItem(item, refreshChildren);
    }

    @Override
    public void inserted(T object) {
        reload();
    }

    @Override
    public void updated(T object) {
        forest.refresh(object);
        refreshItem(object);
    }

    @Override
    public void deleted(T object) {
        reload();
    }

    @Override
    public AutoCloseable getResource() {
        return new Cleaner(forest);
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return forest.getLoadFilter();
    }

    void setLoadCallBack(LoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    private record Cleaner(ObjectForest<?> cache) implements AutoCloseable {

        @Override
        public void close() {
            if(cache != null) {
                cache.close();
            }
        }
    }

    protected interface LoadCallBack {
        void load(Runnable loadFunction);
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, SerializablePredicate<Object>> query) {
        return forest.size(query.getParent(), query.getOffset(), query.getOffset() + query.getLimit());
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, SerializablePredicate<Object>> query) {
        return forest.stream(query.getParent(), query.getOffset(), query.getOffset() + query.getLimit());
    }

    @Override
    public boolean hasChildren(Object parent) {
        return forest.size(parent, 0, Integer.MAX_VALUE) > 0;
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        forest.setLoadFilter(loadFilter);
    }

    @Override
    public int size() {
        return forest.size();
    }
}
