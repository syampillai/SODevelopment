package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectTree<T extends StoredObject> implements Filtered<T>, ObjectLoader<T>, AutoCloseable {

    private final Function<Class<T>, ObjectList<T>> listSupplier;
    private ObjectList<T> roots;
    private final WeakHashMap<Id, ObjectList<T>> childMap = new WeakHashMap<>();
    private final WeakHashMap<Id, Boolean> childExistsMap = new WeakHashMap<>();
    private final Class<T> objectClass;
    private Comparator<? super T> comparator;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();
    private Builder<T> builder;

    public ObjectTree(boolean large, int linkType, Class<T> objectClass, boolean any) {
        this(linkType, objectClass, any,
                large ? c -> new ObjectCacheList<>(c, any) : c -> new ObjectMemoryList<>(c, any));
    }

    public ObjectTree(int linkType, Class<T> objectClass, boolean any, Function<Class<T>, ObjectList<T>> listSupplier) {
        this.objectClass = objectClass;
        this.filter.setLinkType(linkType);
        this.filter.setAny(any);
        this.listSupplier = listSupplier;
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        this.filter.setLoadingPredicate(loadFilter);
        if(roots != null) {
            roots.setLoadFilter(loadFilter);
        }
    }

    @Override
    public void applyFilterPredicate() {
        if(roots != null) {
            roots.filter(filter.getViewFilter());
        }
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return filter;
    }

    private ObjectList<T> list() {
        if(roots == null) {
            roots = listSupplier.apply(objectClass);
            roots.setLoadFilter(filter.getLoadingPredicate());
            roots.filter(filter.getViewFilter(), comparator);
        }
        return roots;
    }

    public List<T> getRoots() {
        return roots == null ? Collections.emptyList() : roots;
    }

    @Override
    public void order(Comparator<? super T> comparator) {
        this.comparator = comparator;
        visitNodes(list -> list.order(comparator));
    }

    @Override
    public void filter(Predicate<? super T> filter) {
        this.filter.setViewFilter(filter);
        visitNodes(list -> list.filter(filter));
    }

    @Override
    public void filter(Predicate<? super T> filter, Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.filter.setViewFilter(filter);
        visitNodes(list -> list.filter(filter, comparator));
    }

    @Override
    public Predicate<? super T> getFilter() {
        return filter.getViewFilter();
    }

    @Override
    public Comparator<? super T> getComparator() {
        return comparator;
    }

    public final T getParent(T child) {
        if(roots != null && roots.contains(child)) {
            return null;
        }
        if(builder == null) {
            return child.getMaster(filter.getLinkType(), objectClass, isAllowAny());
        }
        return builder.getParent(this, child);
    }

    private ObjectList<T> list(T parent) {
        if(parent == null) {
            return roots;
        }
        ObjectList<T> list = childMap.get(parent.getId());
        if(list == null) {
            Boolean exists = childExistsMap.get(parent.getId());
            if(exists != null && !exists) {
                return null;
            }
            list = listSupplier.apply(objectClass);
            if(builder == null) {
                list.load(filter.getLinkType(), parent, isAllowAny());
            } else {
                list.load(builder.listChildren(this, parent));
            }
            if(list.sizeAll() == 0) {
                list.close();
                childExistsMap.put(parent.getId(), Boolean.FALSE);
                return null;
            } else {
                childExistsMap.put(parent.getId(), Boolean.TRUE);
                childMap.put(parent.getId(), list);
            }
        }
        return list;
    }

    private int size(ObjectList<T> list) {
        return list == null ? 0 : list.size();
    }

    @Override
    public int size() {
        return size(roots);
    }

    public int size(T parent) {
        return size(list(parent));
    }

    private int size(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? 0 : list.size(startingIndex, endingIndex);
    }

    @Override
    public int size(int startingIndex, int endingIndex) {
        return size(roots, startingIndex, endingIndex);
    }

    public int size(T parent, int startingIndex, int endingIndex) {
        return size(list(parent), startingIndex, endingIndex);
    }

    private int sizeAll(ObjectList<T> list) {
        return list == null ? 0 : list.sizeAll();
    }

    @Override
    public int sizeAll() {
        return sizeAll(roots);
    }

    public int sizeAll(T parent) {
        return sizeAll(list(parent));
    }

    private Stream<T> stream(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? Stream.empty() : list.stream(startingIndex, endingIndex);
    }

    @Override
    public Stream<T> stream(int startingIndex, int endingIndex) {
        return stream(roots, startingIndex, endingIndex);
    }

    public Stream<T> stream(T parent, int startingIndex, int endingIndex) {
        return stream(list(parent), startingIndex, endingIndex);
    }

    private Stream<T> streamAll(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? Stream.empty() : list.streamAll(startingIndex, endingIndex);
    }

    @Override
    public Stream<T> streamAll(int startingIndex, int endingIndex) {
        return streamAll(roots, startingIndex, endingIndex);
    }

    public Stream<T> streamAll(T parent, int startingIndex, int endingIndex) {
        return streamAll(list(parent), startingIndex, endingIndex);
    }

    @Override
    public void load(String condition, String orderedBy, boolean any) {
        close();
        list().load(condition, orderedBy, any);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderedBy, boolean any) {
        close();
        list().load(linkType, master, condition, orderedBy, any);
    }

    @Override
    public void load(Iterable<Id> idList) {
        close();
        list().load(idList);
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        close();
        list().load(objects);
    }

    @Override
    public void load(Stream<T> objects) {
        close();
        list().load(objects);
    }

    @Override
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    private void visitNodes(Consumer<ObjectList<T>> consumer, boolean skipRoot) {
        if(roots != null) {
            if(!skipRoot) {
                consumer.accept(roots);
            }
            for(ObjectList<T> child: childMap.values()) {
                if(child != null) {
                    consumer.accept(child);
                }
            }
        }
    }

    private void visitNodes(Consumer<ObjectList<T>> consumer) {
        visitNodes(consumer, false);
    }

    @Override
    public void close() {
        childMap.clear();
        childExistsMap.clear();
        visitNodes(ObjectList::close);
    }

    public void refresh() {
        childMap.clear();
        childExistsMap.clear();
        visitNodes(ObjectList::close, true);
        if(roots != null) {
            roots.refresh();
        }
    }

    public void refresh(T item) {
        if(roots != null) {
            roots.refresh(item);
        }
    }

    public void refreshItem(T item, boolean refreshChildren) {
        visitNodes(list -> list.refresh(item), false);
    }

    public void setBuilder(Builder<T> builder) {
        this.builder = builder;
    }

    public interface Builder<O extends StoredObject> {
        ObjectIterator<O> listChildren(ObjectTree<O> tree, O parent);
        O getParent(ObjectTree<O> tree, O child);
    }
}
