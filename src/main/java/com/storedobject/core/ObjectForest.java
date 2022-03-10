package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectForest<T extends StoredObject> implements Filtered<T>, ObjectLoader<T>, AutoCloseable {

    private final Function<Class<T>, ObjectList<T>> listSupplier;
    private ObjectList<T> list;
    private final Class<T> objectClass;
    private Comparator<? super T> comparator;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();
    private Map<Class<? extends StoredObject>, List<StoredObjectUtility.Link<?>>> linkMaps;
    private final Map<StoredObjectUtility.Link<?>, Map<Id, LinkNode>> linkNodeMap = new HashMap<>();
    private BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> listLinks;
    private Predicate<StoredObjectUtility.Link<?>> linkVisibility;
    private boolean hideLinkLabels;

    public ObjectForest(boolean large, int linkType, Class<T> objectClass, boolean any) {
        this(linkType, objectClass, any, large ? ObjectCacheList::new : ObjectMemoryList::new);
    }

    public ObjectForest(int linkType, Class<T> objectClass, boolean any, Function<Class<T>,
            ObjectList<T>> listSupplier) {
        this.objectClass = objectClass;
        this.filter.setLinkType(linkType);
        this.filter.setAny(any);
        this.listSupplier = listSupplier;
    }

    public void hideLinkLabels() {
        this.hideLinkLabels = true;
    }

    public void setLinkVisibility(Predicate<StoredObjectUtility.Link<?>> linkVisibility) {
        this.linkVisibility = linkVisibility;
    }

    public Predicate<StoredObjectUtility.Link<?>> getLinkVisibility() {
        return linkVisibility;
    }

    public void setListLinks(BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> listLinks) {
        this.listLinks = listLinks;
    }

    public BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> getListLinks() {
        return listLinks;
    }

    private List<StoredObjectUtility.Link<?>> linkDetails(Class<? extends StoredObject> masterClass) {
        List<StoredObjectUtility.Link<?>> links = StoredObjectUtility.linkDetails(masterClass);
        if(linkVisibility != null) {
            links.removeIf(linkVisibility.negate());
        }
        return links;
    }

    private List<StoredObjectUtility.Link<?>> links(boolean create, Class<? extends StoredObject> klass) {
        if(linkMaps == null && !create) {
            return Collections.emptyList();
        }
        if(linkMaps == null) {
            linkMaps = new HashMap<>();
            Class<? extends StoredObject> ks = getObjectClass();
            List<StoredObjectUtility.Link<?>> links = linkDetails(ks);
            linkMaps.put(ks, links);
            if(klass == ks) {
                return links;
            }
        }
        return linkMaps.computeIfAbsent(klass, k -> linkDetails(klass));
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        this.filter.setLoadingPredicate(loadFilter);
        if(list != null) {
            list.setLoadFilter(loadFilter);
        }
    }

    @Override
    public void applyFilterPredicate() {
        if(list != null) {
            list.filter(filter.getViewFilter());
        }
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return filter;
    }

    private ObjectList<T> list() {
        if(list == null) {
            list = listSupplier.apply(objectClass);
            list.setLoadFilter(filter.getLoadingPredicate());
            list.filter(filter.getViewFilter(), comparator);
        }
        return list;
    }

    public List<T> getRoots() {
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void order(Comparator<? super T> comparator) {
        this.comparator = comparator;
        if(list != null) {
            list.order(comparator);
        }
    }

    @Override
    public void filter(Predicate<? super T> filter) {
        this.filter.setViewFilter(filter);
        if(list != null) {
            list.filter(filter);
        }
    }

    @Override
    public void filter(Predicate<? super T> filter, Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.filter.setViewFilter(filter);
        if(list != null) {
            list.filter(filter, comparator);
        }
    }

    @Override
    public Predicate<? super T> getFilter() {
        return filter.getViewFilter();
    }

    @Override
    public Comparator<? super T> getComparator() {
        return comparator;
    }

    private List<?> list(Object parent) {
        return list(true, parent);
    }

    private List<?> list(boolean create, Object parent) {
        if(parent == null) {
            return list;
        }
        if(parent instanceof LinkObject p) {
            if(hideLinkLabels) {
                parent = p.getObject();
            } else {
                if(p.linkNode.link.isDetail()) {
                    return links(create, p.object.getClass()).stream().map(link -> getLinkNode(link, p.object, listLinks))
                            .toList();
                }
            }
        }
        if(parent instanceof LinkNode p) {
            return p.links(create);
        }
        if(parent instanceof StoredObject p) {
            List<LinkNode> linkNodes = links(create, p.getClass()).stream()
                    .map(link -> getLinkNode(link, p, listLinks)).toList();
            if(!hideLinkLabels) {
                return linkNodes;
            }
            if(linkNodes.isEmpty()) {
                return Collections.emptyList();
            }
            List<LinkObject> linkObjects = new ArrayList<>();
            linkNodes.forEach(linkNode -> linkObjects.addAll(linkNode.links(true)));
            return linkObjects;
        }
        return null;
    }

    private int size(ObjectList<T> list) {
        return list == null ? 0 : list.size();
    }

    @Override
    public int size() {
        return size(list);
    }

    public int size(Object parent) {
        return size(list(parent));
    }

    private int size(List<?> list) {
        return size(list, 0, Integer.MAX_VALUE);
    }

    private int size(List<?> list, int startingIndex, int endingIndex) {
        if(list == null) {
            return 0;
        }
        if(list instanceof ObjectList o) {
            return o.size(startingIndex, endingIndex);
        }
        return Utility.size(list, startingIndex, endingIndex);
    }

    @Override
    public int size(int startingIndex, int endingIndex) {
        return size(list, startingIndex, endingIndex);
    }

    public int size(Object parent, int startingIndex, int endingIndex) {
        return size(list(parent), startingIndex, endingIndex);
    }

    private int sizeAll(List<?> list) {
        if(list == null) {
            return 0;
        }
        if(list instanceof ObjectList<?> o) {
            return o.sizeAll();
        }
        return list.size();
    }

    @Override
    public int sizeAll() {
        return sizeAll(list);
    }

    public int sizeAll(Object parent) {
        return sizeAll(list(parent));
    }

    private Stream<Object> stream(List<?> list, int startingIndex, int endingIndex) {
        if(list == null) {
            return Stream.empty();
        }
        if(list instanceof ObjectList<?> o) {
            return o.stream(startingIndex, endingIndex).map(e -> e);
        }
        return Utility.stream(list, startingIndex, endingIndex).map(e -> e);
    }

    @Override
    public Stream<T> stream(int startingIndex, int endingIndex) {
        if(list == null) {
            return Stream.empty();
        }
        return list.stream(startingIndex, endingIndex);
    }

    public Stream<Object> stream(Object parent, int startingIndex, int endingIndex) {
        if(parent == null) {
            return stream(startingIndex, endingIndex).map(e -> e);
        }
        return stream(list(parent), startingIndex, endingIndex);
    }

    private Stream<Object> streamAll(List<?> list, int startingIndex, int endingIndex) {
        if(list == null) {
            return Stream.empty();
        }
        if(list instanceof ObjectList<?> o) {
            return o.streamAll(startingIndex, endingIndex).map(e -> e);
        }
        return Utility.stream(list, startingIndex, endingIndex).map(e -> e);
    }

    @Override
    public Stream<T> streamAll(int startingIndex, int endingIndex) {
        return list == null ? Stream.empty() : list.streamAll(startingIndex, endingIndex);
    }

    public Stream<Object> streamAll(Object parent, int startingIndex, int endingIndex) {
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
        return ObjectLoader.super.isAllowAny();
    }

    @Override
    public void close() {
        if(linkMaps != null) {
            linkMaps.clear();
        }
        linkNodeMap.clear();
        if(list != null) {
            list.close();
        }
    }

    public void refresh() {
        if(linkMaps != null) {
            linkMaps.clear();
        }
        linkNodeMap.clear();
        if(list != null) {
            list.refresh();
        }
    }

    public void refresh(Object item) {
        if(list == null || item == null) {
            return;
        }
        if(getObjectClass().isAssignableFrom(item.getClass())) {
            //noinspection unchecked
            list.refresh((T)item);
            return;
        }
        if(item instanceof LinkNode i) {
            i.refresh();
            return;
        }
        if(item instanceof LinkObject i) {
            i.linkNode.refresh();
        }
    }

    public void refresh(Object item, boolean refreshChildren) {
        if(list == null || item == null) {
            return;
        }
        refresh(item);
        if(refreshChildren) {
            List<?> list = list(false, item);
            if(list != null) {
                list.forEach(i -> refresh(i, true));
            }
        }
    }

    public <M extends StoredObject> boolean hideLink(Class<M> masterClass, String linkName) {
        return false;
    }

    private LinkNode getLinkNode(StoredObjectUtility.Link<?> link, StoredObject parent,
                                 BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> listLinks) {
        LinkNode node;
        Map<Id, LinkNode> nodes = linkNodeMap.get(link);
        if(nodes == null) {
            nodes = new HashMap<>();
            linkNodeMap.put(link, nodes);
            node = null;
        } else {
            node = nodes.get(parent.getId());
        }
        if(node == null) {
            node = new LinkNode(link, parent, listLinks);
            nodes.put(parent.getId(), node);
        }
        return node;
    }

    public static class LinkNode implements HasId {

        private final Id id = new Id();
        private final StoredObjectUtility.Link<?> link;
        private final StoredObject parent;
        private List<LinkObject> links;
        private final BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> listLinks;

        private LinkNode(StoredObjectUtility.Link<?> link, StoredObject parent,
                         BiFunction<StoredObjectUtility.Link<?>, StoredObject, ObjectIterator<? extends StoredObject>> listLinks) {
            this.link = link;
            this.parent = parent;
            this.listLinks = listLinks;
        }

        public StoredObject getParent() {
            return parent;
        }

        public StoredObjectUtility.Link<?> getLink() {
            return link;
        }

        public List<LinkObject> links(boolean create) {
            if(links == null && !create) {
                return Collections.emptyList();
            }
            if(links == null) {
                links = new ArrayList<>();
                ObjectIterator<? extends StoredObject> list = null;
                if(listLinks != null) {
                    list = listLinks.apply(link, parent);
                }
                if(list == null) {
                    list = link.list(parent);
                }
                list.forEach(o -> links.add(new LinkObject(this, o)));
            }
            return links;
        }

        public boolean isEmpty() {
            return links(true).isEmpty();
        }

        public void refresh() {
            if(links != null) {
                links.clear();
                links = null;
            }
        }

        @Override
        public String toString() {
            return link.getName();
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }
            LinkNode linkNode = (LinkNode) o;
            return Objects.equals(id, linkNode.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public Id getId() {
            return id;
        }
    }

    public static class LinkObject implements HasId {

        private final Id id = new Id();
        private final LinkNode linkNode;
        private StoredObject object;

        private LinkObject(LinkNode linkNode, StoredObject object) {
            this.linkNode = linkNode;
            this.object = object;
        }

        public LinkNode getLinkNode() {
            return linkNode;
        }

        public StoredObject getObject() {
            return object;
        }

        public void refresh() {
            object = StoredObject.get(object.getClass(), object.getId());
        }

        @Override
        public String toString() {
            return linkNode.toString() + ": " + object.toDisplay();
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }
            LinkObject linkObject = (LinkObject) o;
            return Objects.equals(id, linkObject.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public Id getId() {
            return id;
        }
    }
}
