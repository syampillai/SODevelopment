package com.storedobject.core;

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
    private ObjectList<T> list;
    private final WeakHashMap<Id, ObjectList<T>> childMap = new WeakHashMap<>();
    private final WeakHashMap<Id, Boolean> childExistsMap = new WeakHashMap<>();
    private final Class<T> objectClass;
    private final int linkType;
    private final boolean any;
    private Comparator<? super T> comparator;
    private Predicate<? super T> filter;
    private Builder<T> builder;

    public ObjectTree(boolean large, int linkType, Class<T> objectClass, boolean any) {
        this(linkType, objectClass, any, large ? ObjectCacheList::new : ObjectMemoryList::new);
    }

    public ObjectTree(int linkType, Class<T> objectClass, boolean any, Function<Class<T>, ObjectList<T>> listSupplier) {
        this.objectClass = objectClass;
        this.linkType = linkType;
        this.any = any;
        this.listSupplier = listSupplier;
    }

    private ObjectList<T> list() {
        if(list == null) {
            list = listSupplier.apply(objectClass);
            list.filter(filter, comparator);
        }
        return list;
    }

    public List<T> getRoots() {
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void order(Comparator<? super T> comparator) {
        this.comparator = comparator;
        visitNodes(list -> list.order(comparator));
    }

    @Override
    public void filter(Predicate<? super T> filter) {
        this.filter = filter;
        visitNodes(list -> list.filter(filter));
    }

    @Override
    public void filter(Predicate<? super T> filter, Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.filter = filter;
        visitNodes(list -> list.filter(filter, comparator));
    }

    @Override
    public Predicate<? super T> getFilter() {
        return filter;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return comparator;
    }

    public final T getParent(T child) {
        if(list != null && list.contains(child)) {
            return null;
        }
        if(builder == null) {
            return child.getMaster(linkType, objectClass, isAllowAny());
        }
        return builder.getParent(this, child);
    }

    private ObjectList<T> list(T parent) {
        if(parent == null) {
            return list;
        }
        ObjectList<T> list = childMap.get(parent.getId());
        if(list == null) {
            Boolean exists = childExistsMap.get(parent.getId());
            if(exists != null && !exists) {
                return null;
            }
            list = listSupplier.apply(objectClass);
            if(builder == null) {
                list.load(linkType, parent, isAllowAny());
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
        return size(list);
    }

    public int size(T parent) {
        return size(list(parent));
    }

    private int size(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? 0 : list.size(startingIndex, endingIndex);
    }

    @Override
    public int size(int startingIndex, int endingIndex) {
        return size(list, startingIndex, endingIndex);
    }

    public int size(T parent, int startingIndex, int endingIndex) {
        return size(list(parent), startingIndex, endingIndex);
    }

    private int sizeAll(ObjectList<T> list) {
        return list == null ? 0 : list.sizeAll();
    }

    @Override
    public int sizeAll() {
        return sizeAll(list);
    }

    public int sizeAll(T parent) {
        return sizeAll(list(parent));
    }

    private Stream<T> stream(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? Stream.empty() : list.stream(startingIndex, endingIndex);
    }

    @Override
    public Stream<T> stream(int startingIndex, int endingIndex) {
        return stream(list, startingIndex, endingIndex);
    }

    public Stream<T> stream(T parent, int startingIndex, int endingIndex) {
        return stream(list(parent), startingIndex, endingIndex);
    }

    private Stream<T> streamAll(ObjectList<T> list, int startingIndex, int endingIndex) {
        return list == null ? Stream.empty() : list.streamAll(startingIndex, endingIndex);
    }

    @Override
    public Stream<T> streamAll(int startingIndex, int endingIndex) {
        return streamAll(list, startingIndex, endingIndex);
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

    @Override
    public final boolean isAllowAny() {
        return any;
    }

    @Override
    public int getLinkType() {
        return linkType;
    }

    private void visitNodes(Consumer<ObjectList<T>> consumer, boolean skipRoot) {
        if(list != null) {
            if(!skipRoot) {
                consumer.accept(list);
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
        list.refresh();
    }

    public void refresh(T item) {
        if(list != null) {
            list.refresh(item);
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
