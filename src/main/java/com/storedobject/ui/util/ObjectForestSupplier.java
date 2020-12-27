package com.storedobject.ui.util;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ObjectForestSupplier<T extends StoredObject, F> extends AbstractObjectSupplier<T, Object, F> implements AbstractObjectForestSupplier<T, F> {

    private final HashMap<Class<? extends StoredObject>, ArrayList<StoredObjectUtility.Link<?>>> linkMaps = new HashMap<>();
    private ListLinks listLinks;

    public ObjectForestSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any);
    }

    @SuppressWarnings("unchecked")
    public ObjectForestSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        //noinspection rawtypes
        super(new ObjectsCached(linkType, master, objectClass, condition, orderBy, any, false), false);
        ArrayList<StoredObjectUtility.Link<?>> links = StoredObjectUtility.linkDetails(getObjectClass());
        for(StoredObjectUtility.Link<?> link: links) {
            if(link.getObjectClass() == getObjectClass()) {
                supplier.tree = true;
                break;
            }
        }
        linkMaps.put(getObjectClass(), links);
    }

    @Override
    public void setListLinks(ListLinks listLinks) {
        this.listLinks = listLinks;
    }

    private ArrayList<StoredObjectUtility.Link<?>> links(Class<? extends StoredObject> klass) {
        return linkMaps.computeIfAbsent(klass, k -> StoredObjectUtility.linkDetails(klass));
    }

    @Override
    public List<T> listRoots() {
        return supplier == null || supplier.cache == null ? new ArrayList<>() : supplier.cache.list();
    }

    @Override
    public Stream<Object> fetch(Query<Object, F> query) {
        return super.fetch(query);
    }

    @Override
    public int size(Query<Object, F> query) {
        return super.size(query);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getChildCount(HierarchicalQuery<Object, F> query) {
        Object p = query.getParent();
        if(p == null) {
            return size(query);
        }
        if(p instanceof StoredObject) {
            return AbstractObjectForestSupplier.subListSize(links((Class<? extends StoredObject>) p.getClass()), query);
        }
        if(p instanceof LinkNode) {
            return ((LinkNode) p).size(query);
        }
        if(p instanceof LinkObject) {
            LinkObject lo = (LinkObject) p;
            if(lo.linkNode.link.isDetail()) {
                return AbstractObjectForestSupplier.subListSize(links(lo.object.getClass()), query);
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, F> query) {
        Object p = query.getParent();
        if(p == null) {
            return fetch(query);
        }
        if(p instanceof StoredObject) {
            return AbstractObjectForestSupplier.subList(links((Class<? extends StoredObject>) p.getClass()), query).stream().map(link -> new LinkNode(link, (StoredObject)p, listLinks));
        }
        if(p instanceof LinkNode) {
            return AbstractObjectForestSupplier.subList(((LinkNode) p).links(), query).stream().map(o -> o);
        }
        if(p instanceof LinkObject) {
            LinkObject lo = (LinkObject) p;
            if(lo.linkNode.link.isDetail()) {
                return AbstractObjectForestSupplier.subList(links(lo.object.getClass()), query).stream().map(link -> new LinkNode(link, lo.object, listLinks));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChildren(Object item) {
        if(item instanceof StoredObject) {
            return !links((Class<? extends StoredObject>) item.getClass()).isEmpty();
        }
        if(item instanceof LinkNode) {
            return !((LinkNode) item).isEmpty();
        }
        if(item instanceof LinkObject) {
            LinkObject lo = (LinkObject) item;
            if(lo.linkNode.link.isDetail()) {
                return !links(lo.object.getClass()).isEmpty();
            }
        }
        return false;
    }

    public static class LinkNode {

        private final StoredObjectUtility.Link<?> link;
        private final StoredObject parent;
        private ArrayList<LinkObject> links;
        private final ListLinks listLinks;

        private LinkNode(StoredObjectUtility.Link<?> link, StoredObject parent, ListLinks listLinks) {
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

        public ArrayList<LinkObject> links() {
            if(links == null) {
                links = new ArrayList<>();
                ObjectIterator<? extends StoredObject> list = null;
                if(listLinks != null) {
                    list = listLinks.list(link, parent);
                }
                if(list == null) {
                    list = link.list(parent);
                }
                list.forEach(o -> links.add(new LinkObject(this, o)));
            }
            return links;
        }

        public int size(HierarchicalQuery<?, ?> query) {
            return AbstractObjectForestSupplier.subListSize(links(), query);
        }

        public boolean isEmpty() {
            return links().isEmpty();
        }

        private void refresh() {
            links.clear();
            links = null;
        }

        @Override
        public String toString() {
            return link.getName();
        }
    }

    public static class LinkObject {

        private final LinkNode linkNode;
        private final StoredObject object;

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
    }

    @Override
    @SuppressWarnings("unchecked")
    public void refreshItem(Object item) {
        if(supplier.cache != null && supplier.objectClass.isAssignableFrom(item.getClass())) {
            supplier.cache.refresh((T)item);
        } else if(item instanceof LinkNode) {
            ((LinkNode) item).refresh();
            super.refreshAll();
            return;
        }
        super.refreshItem(item);
    }
}