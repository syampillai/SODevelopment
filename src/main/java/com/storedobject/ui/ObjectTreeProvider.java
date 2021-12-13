package com.storedobject.ui;

import com.storedobject.common.ResourceOwner;
import com.storedobject.core.ObjectList;
import com.storedobject.core.ObjectTree;
import com.storedobject.core.*;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.function.SerializablePredicate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectTreeProvider<T extends StoredObject> extends AbstractTreeProvider<T, T>
        implements ObjectLoader<T>, ObjectChangedListener<T>, AutoCloseable, ResourceOwner, FilterMethods<T> {

    private final ObjectLoadFilter<T> systemFilter = new ObjectLoadFilter<>(), fixedFilter = new ObjectLoadFilter<>();
    private LoadCallBack loadCallBack;
    private final ObjectTree<T> tree;

    public ObjectTreeProvider(ObjectTree<T> tree) {
        super(tree.getObjectClass());
        this.tree = tree;
    }

    public ObjectTree<T> getTree() {
        return tree;
    }

    @Override
    public List<T> getRoots() {
        return tree.getRoots();
    }

    @Override
    public Filtered<T> getData() {
        return tree;
    }

    ObjectLoadFilter<T> getSystemFilter() {
        return systemFilter;
    }

    @Override
    public ObjectLoadFilter<T> getFixedFilter() {
        return fixedFilter;
    }

    @Override
    public Id getId(T item) {
        return item.getId();
    }

    public boolean contains(T item) {
        return tree.getRoots().contains(item);
    }

    @Override
    void saveFilter(Predicate<T> filter) {
        ObjectLoader.super.setViewFilter(filter);
    }

    @Override
    Predicate<T> retrieveFilter() {
        return tree.getLoadFilter().getLoadingPredicate();
    }

    private String cond(String cond) {
        return systemFilter.getFilter(fixedFilter.getFilter(cond));
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
        tree.load(cond(condition), orderBy, any);
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
        setMaster(master, false);
        setLinkType(linkType, false);
        tree.load(linkType, master, cond(condition), orderBy, any);
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
        tree.load(objects);
        refreshAll();
    }

    @Override
    public int getObjectCount() {
        return getRoots().size();
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
        return tree.streamAll(0, tree.sizeAll());
    }

    @Override
    public Stream<T> streamFiltered() {
        return tree.stream(0, tree.size());
    }

    @Override
    public int getCacheLevel() {
        List<T> roots = getRoots();
        if(roots instanceof ObjectList) {
            return ((ObjectList<T>)roots).getCacheLevel();
        }
        return 0;
    }

    @Override
    public void refreshAll() {
        tree.refresh();
        super.refreshAll();
    }

    @Override
    public void refreshItem(T item) {
        tree.refresh(item);
        super.refreshItem(item);
    }

    @Override
    public void refreshItem(T item, boolean refreshChildren) {
        tree.refreshItem(item, refreshChildren);
        super.refreshItem(item, refreshChildren);
    }

    @Override
    public void inserted(T object) {
        load();
    }

    @Override
    public void updated(T object) {
        tree.refresh(object);
        refreshItem(object);
    }

    @Override
    public void deleted(T object) {
        load();
    }

    @Override
    public AutoCloseable getResource() {
        return new Cleaner(tree);
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return tree.getLoadFilter();
    }

    void setLoadCallBack(LoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    private record Cleaner(ObjectTree<?> cache) implements AutoCloseable {

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

    /**
     * Visit the children of the parent item.
     *
     * @param parent Parent item.
     * @param consumer Consumer to consume the visit purpose.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void visitChildren(T parent, Consumer<T> consumer, boolean includeGrandChildren) {
        tree.stream(parent, 0, Integer.MAX_VALUE).forEach(c -> {
            consumer.accept(c);
            if(includeGrandChildren) {
                visitChildren(parent, consumer, true);
            }
        });
    }

    @Override
    public int getChildCount(HierarchicalQuery<T, SerializablePredicate<T>> query) {
        Predicate<T> filter = getViewFilter().getPredicate(tokens, query.getFilter().orElse(null));
        if(filter == null) {
            return tree.size(query.getParent(), query.getOffset(), query.getOffset() + query.getLimit());
        }
        return (int) tree.stream(query.getParent(), 0, Integer.MAX_VALUE).filter(filter)
                .skip(query.getOffset()).limit(query.getLimit()).count();
    }

    @Override
    public Stream<T> fetchChildren(HierarchicalQuery<T, SerializablePredicate<T>> query) {
        Predicate<T> filter = getViewFilter().getPredicate(tokens, query.getFilter().orElse(null));
        tree.order(query.getSortingComparator().orElse(null));
        if(filter == null) {
            return tree.stream(query.getParent(), query.getOffset(), query.getOffset() + query.getLimit());
        }
        return tree.stream(query.getParent(), 0, Integer.MAX_VALUE).filter(filter)
                .skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public boolean hasChildren(T parent) {
        return tree.size(parent, 0, Integer.MAX_VALUE) > 0;
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        tree.setLoadFilter(loadFilter);
    }

    @Override
    public int size() {
        return tree.size();
    }
}
