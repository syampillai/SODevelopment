package com.storedobject.core;

import java.util.Collection;
import java.util.stream.Stream;

public interface ObjectLoader<T extends StoredObject> {

    default String getFilterCondition() {
        return null;
    }

    default String getOrderBy() {
        return null;
    }

    default void setOrderBy(String orderBy, boolean load) {
    }

    default StoredObject getMaster() {
        return null;
    }

    default void setMaster(StoredObject master, boolean load) {
    }

    default int getLinkType() {
        return 0;
    }

    default void setLinkType(int linkType, boolean load) {
    }

    default void load() {
        load(getFilterCondition(), getOrderBy());
    }

    default void load(boolean any) {
        load(getFilterCondition(), getOrderBy(), any);
    }

    default void load(String condition) {
        load(condition, getOrderBy());
    }

    default void load(String condition, boolean any) {
        load(condition, getOrderBy(), any);
    }

    default void load(String condition, String orderedBy) {
        load(condition, orderedBy, getAllowAny());
    }

    default void load(String condition, String orderedBy, boolean any) {
        StoredObject master = getMaster();
        if(master == null) {
            load(StoredObject.list(getObjectClass(), condition, orderedBy, any));
        } else {
            load(getLinkType(), master, condition, orderedBy, any);
        }
    }

    default void load(StoredObject master) {
        load(master, getFilterCondition());
    }

    default void load(StoredObject master, boolean any) {
        load(master, getFilterCondition(), any);
    }

    default void load(StoredObject master, String condition) {
        load(master, condition, getOrderBy());
    }

    default void load(StoredObject master, String condition, boolean any) {
        load(master, condition, getOrderBy(), any);
    }

    default void load(StoredObject master, String condition, String orderedBy) {
        load(master, condition, orderedBy, getAllowAny());
    }

    default void load(StoredObject master, String condition, String orderedBy, boolean any) {
        load(getLinkType(), master, condition, orderedBy, any);
    }

    default void load(int linkType, StoredObject master) {
        load(linkType, master, getFilterCondition());
    }

    default void load(int linkType, StoredObject master, boolean any) {
        load(linkType, master, getFilterCondition(), any);
    }

    default void load(int linkType, StoredObject master, String condition) {
        load(linkType, master, condition, getOrderBy());
    }

    default void load(int linkType, StoredObject master, String condition, boolean any) {
        load(linkType, master, condition, getOrderBy(), any);
    }

    default void load(int linkType, StoredObject master, String condition, String orderedBy) {
        load(linkType, master, condition, orderedBy, getAllowAny());
    }

    default void load(int linkType, StoredObject master, String condition, String orderedBy, boolean any) {
        load(master.listLinks(linkType, getObjectClass(), condition, orderedBy, any));
    }

    @SuppressWarnings("unchecked")
    default void load(T... objects) {
        load(ObjectIterator.create(objects));
    }

    default void load(Collection<T> objects) {
        load(ObjectIterator.create(objects));
    }

    default void load(Query query) {
        load(query, getAllowAny());
    }

    default void load(Query query, boolean any) {
    }

    void load(Iterable<Id> idList);

    void load(ObjectIterator<T> objects);

    void load(Stream<T> objects);

    Class<T> getObjectClass();

    boolean isAllowAny();

    default boolean getAllowAny() {
        return isAllowAny();
    }
}
