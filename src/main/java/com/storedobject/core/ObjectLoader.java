package com.storedobject.core;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Methods for loading instances of {@link StoredObject}.
 *
 * @param <T> Type of {@link StoredObject}.
 * @author Syam
 */
public interface ObjectLoader<T extends StoredObject> extends FilterMethods<T> {

    /**
     * @return Whether instanced of the subclasses to be retrieved or not.
     */
    default boolean isAllowAny() {
        return getLoadFilter().isAny();
    }

    /**
     * @return Whether instanced of the subclasses to be retrieved or not. (Same as {@link #isAllowAny()}).
     */
    default boolean getAllowAny() {
        return isAllowAny();
    }

    /**
     * Get the current "ORDER BY" clause.
     * @return Current "ORDER BY" clause.
     */
    default String getOrderBy() {
        return getLoadFilter().getOrderBy();
    }

    /**
     * Set the "ORDER BY" clause.
     * @param orderBy "ORDER BY" clause to set.
     */
    default void setOrderBy(String orderBy) {
        setOrderBy(orderBy, true);
    }

    /**
     * Set the "ORDER BY" clause.
     * @param orderBy "ORDER BY" clause to set.
     * @param load Whether to immediately reload with this "ORDER BY" clause or not.
     */
    default void setOrderBy(String orderBy, boolean load) {
        getLoadFilter().setOrderBy(orderBy);
        if(load) {
            load();
        }
    }

    /**
     * Get the current master instance if available.
     * @return Current master instance if available.
     */
    default StoredObject getMaster() {
        return getLoadFilter().getMaster();
    }

    /**
     * Set the master instance.
     * @param master Master instance.
     */
    default void setMaster(StoredObject master) {
        setMaster(master, true);
    }

    /**
     * Set the master instance.
     * @param master Master instance.
     * @param load Whether to immediately reload with this master or not.
     */
    default void setMaster(StoredObject master, boolean load) {
        getLoadFilter().setMaster(master);
        if(load) {
            if(master == null) {
                clear();
            } else {
                reload();
            }
        }
    }

    /**
     * Get the current link type.
     * @return Link type.
     */
    default int getLinkType() {
        return getLoadFilter().getLinkType();
    }

    /**
     * Set the link type.
     * @param linkType Link type to set.
     */
    default void setLinkType(int linkType) {
        setLinkType(linkType, true);
    }

    /**
     * Set the link type.
     * @param linkType Link type to set.
     * @param load Whether to immediately reload with this link type or not. (Applicable only if master is available).
     */
    default void setLinkType(int linkType, boolean load) {
        getLoadFilter().setLinkType(linkType);
        if(load) {
            if(getMaster() == null) {
                clear();
            } else {
                reload();
            }
        }
    }

    /**
     * Reload all entries.
     */
    default void reload() {
        load();
    }

    @Override
    default void applyFilter() {
        load();
    }

    /**
     * Clear - remove all entries.
     */
    default void clear() {
        load(ObjectIterator.create());
    }

    /**
     * Load the instances. Current filtering conditions (return value of {@link #getFilterCondition()}) including
     * "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * If "master" is already set, "links" of the "master" will be loaded.
     */
    default void load() {
        load(getFilterCondition(), getOrderBy());
    }

    /**
     * Load the instances. Current filtering conditions (return value of {@link #getFilterCondition()}) including
     * "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * If "master" is already set, "links" of the "master" will be loaded.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(boolean any) {
        load(getFilterCondition(), getOrderBy(), any);
    }

    /**
     * Load the instances. Current "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * If "master" is already set, "links" of the "master" will be loaded.
     * @param condition Filtering condition to be applied.
     */
    default void load(String condition) {
        load(condition, getOrderBy());
    }

    /**
     * Load the instances. Current "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * If "master" is already set, "links" of the "master" will be loaded.
     * @param condition Filtering condition to be applied.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(String condition, boolean any) {
        load(condition, getOrderBy(), any);
    }

    /**
     * Load the instances. Current "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * If "master" is already set, "links" of the "master" will be loaded.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     */
    default void load(String condition, String orderedBy) {
        load(condition, orderedBy, getAllowAny());
    }

    /**
     * Load the instances. Current "load filter" if set will be applied.
     * If "master" is already set, "links" of the "master" will be loaded.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(String condition, String orderedBy, boolean any) {
        ObjectLoadFilter<T> f = getLoadFilter();
        f.setOrderBy(orderedBy);
        f.setAny(any);
        condition = getEffectiveCondition(condition);
        StoredObject master = getMaster();
        if(master == null) {
            load(StoredObject.list(getObjectClass(), condition, orderedBy, any));
        } else {
            load(getLinkType(), master, condition, orderedBy, any);
        }
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     */
    default void load(StoredObject master, String condition) {
        load(master, condition, getOrderBy());
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(StoredObject master, String condition, boolean any) {
        load(master, condition, getOrderBy(), any);
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     */
    default void load(StoredObject master, String condition, String orderedBy) {
        load(master, condition, orderedBy, getAllowAny());
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(StoredObject master, String condition, String orderedBy, boolean any) {
        load(getLinkType(), master, condition, orderedBy, any);
    }

    /**
     * Load the links of the given "master" instance. Current filtering conditions (return value of
     * {@link #getFilterCondition()}) including "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param linkType Link type.
     * @param master Master instance.
     */
    default void load(int linkType, StoredObject master) {
        load(linkType, master, getFilterCondition());
    }

    /**
     * Load the links of the given "master" instance. Current filtering conditions (return value of
     * {@link #getFilterCondition()}) including "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param linkType Link type.
     * @param master Master instance.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(int linkType, StoredObject master, boolean any) {
        load(linkType, master, getFilterCondition(), any);
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * retrieved or not.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param linkType Link type.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     */
    default void load(int linkType, StoredObject master, String condition) {
        load(linkType, master, condition, getOrderBy());
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param linkType Link type.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(int linkType, StoredObject master, String condition, boolean any) {
        load(linkType, master, condition, getOrderBy(), any);
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * The order will be determined by the return value of {@link #getOrderBy()}.
     * @param linkType Link type.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     */
    default void load(int linkType, StoredObject master, String condition, String orderedBy) {
        load(linkType, master, condition, orderedBy, getAllowAny());
    }

    /**
     * Load the links of the given "master" instance. Current "load filter" if set will be applied.
     * @param linkType Link type.
     * @param master Master instance.
     * @param condition Filtering condition to be applied.
     * @param orderedBy "ORDER BY" clause to use while loading.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(int linkType, StoredObject master, String condition, String orderedBy, boolean any) {
        setMaster(master, false);
        setLinkType(linkType, false);
        ObjectLoadFilter<T> f = getLoadFilter();
        f.setOrderBy(orderedBy);
        f.setAny(any);
        condition = getEffectiveCondition(condition);
        load(master.listLinks(linkType, getObjectClass(), condition, orderedBy, any));
    }

    /**
     * Load the given instances. Current "load filter" if set will be applied. However, {@link #getOrderBy()} result
     * will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param objects Objects to be loaded.
     */
    @SuppressWarnings("unchecked")
    default void load(T... objects) {
        load(ObjectIterator.create(objects));
    }

    /**
     * Load the given instances. Current "load filter" if set will be applied. However, {@link #getOrderBy()} result
     * will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param objects Objects to be loaded.
     */
    default void load(Collection<T> objects) {
        load(ObjectIterator.create(objects));
    }

    /**
     * Load the instances created from the query. Current "load filter" if set will be applied. However,
     * {@link #getOrderBy()} result will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param query Query from which objects to be loaded.
     */
    default void load(Query query) {
        load(query, getAllowAny());
    }

    /**
     * Load the instances created from the query. Current "load filter" if set will be applied. However,
     * {@link #getOrderBy()} result will be ignored.
     * @param query Query from which objects to be loaded.
     * @param any Whether instanced of the subclasses to be retrieved or not.
     */
    default void load(Query query, boolean any) {
        load(ObjectIterator.create(null, null, query, getObjectClass(), any));
    }

    /**
     * Load the instances created from the list of {@link Id}s. Current "load filter" if set will be applied. However,
     * {@link #getOrderBy()} result will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param idList List of {@link Id}s from which objects to be loaded.
     */
    default void load(Iterable<Id> idList) {
        load(ObjectIterator.create(idList, id -> StoredObject.get(getObjectClass(), id, isAllowAny())));
    }

    /**
     * Load the given instances. Current "load filter" if set will be applied. However, {@link #getOrderBy()} result
     * will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param objects Objects to be loaded.
     */
    default void load(Stream<T> objects) {
        load(ObjectIterator.create(objects.iterator()));
    }

    /**
     * Load the given instances. Current "load filter" if set will be applied. However, {@link #getOrderBy()} result
     * will be ignored.
     * Also, the value returned by {@link #getAllowAny()} will determine whether instanced of the subclasses to be
     * restricted or not.
     * @param objects Objects to be loaded.
     */
    void load(ObjectIterator<T> objects);

    /**
     * The class of the instances.
     * @return The class of the instances to load.
     */
    Class<T> getObjectClass();

    /**
     * Get the instances count.
     *
     * @return Count.
     */
    int size();

    /**
     * Check whether the given object could be there in the objects loaded by this loader.
     * <p>Note: For customized loaders, this may not check the custom conditions.</p>
     * @param object Object to check.
     * @return True/false.
     */
    default boolean canContain(T object) {
        if(object == null) {
            return false;
        }
        String condition = getEffectiveCondition(getFilterCondition());
        if(condition != null && !condition.equalsIgnoreCase("true")) {
            condition = "T.Id=" + object.getId() + " AND (" + condition + ")";
            StoredObject m = getMaster();
            if(m == null) {
                object = StoredObject.get(getObjectClass(), condition, getAllowAny());
            } else {
                object = m.listLinks(getObjectClass(), condition, getAllowAny()).findFirst();
            }
        }
        if(object == null) {
            return false;
        }
        Predicate<T> predicate = getLoadFilter().getLoadingPredicate();
        return predicate == null || predicate.test(object);
    }
}
