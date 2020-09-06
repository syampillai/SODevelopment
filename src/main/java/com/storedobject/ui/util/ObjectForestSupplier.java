package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ObjectForestSupplier<T extends StoredObject, F> extends AbstractObjectSupplier<T, Object, F> implements AbstractObjectForestSupplier<T, F> {

    public ObjectForestSupplier(Class<T> objectClass, String condition, String orderBy, boolean any) {
        this(0, null, objectClass, condition, orderBy, any);
    }

    @SuppressWarnings("unchecked")
    public ObjectForestSupplier(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any) {
        //noinspection rawtypes
        super(new ObjectsCached(linkType, master, objectClass, condition, orderBy, any, false), false);
    }

    @Override
    public List<T> listRoots() {
        return null;
    }

    @Override
    public void setListLinks(ListLinks listLinks) {
    }

    @Override
    public Stream<Object> fetch(Query<Object, F> query) {
        return null;
    }

    @Override
    public int size(Query<Object, F> query) {
        return super.size(query);
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, F> query) {
        return 0;
    }

    @Override
    public Stream<Object> fetchChildren(HierarchicalQuery<Object, F> query) {
        return Stream.of();
    }

    @Override
    public boolean hasChildren(Object item) {
        return false;
    }

    public static class LinkNode {

        private LinkNode(StoredObjectUtility.Link<?> link, StoredObject parent, ListLinks listLinks) {
        }

        public StoredObject getParent() {
            return null;
        }

        public StoredObjectUtility.Link<?> getLink() {
            return null;
        }

        public ArrayList<LinkObject> links() {
            return null;
        }

        public int size() {
            return links().size();
        }

        private void refresh() {
        }
    }

    public static class LinkObject {

        private LinkObject(LinkNode linkNode, StoredObject object) {
        }

        public LinkNode getLinkNode() {
            return null;
        }

        public StoredObject getObject() {
            return null;
        }
    }
}