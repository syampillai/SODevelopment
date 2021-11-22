package com.storedobject.ui;

import com.storedobject.common.ResourceOwner;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectCacheList;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectListDataProvider<T extends StoredObject> extends ListDataProvider<T>
        implements ObjectLoader<T>, AutoCloseable, ResourceOwner {

    private int linkType = 0;
    private StoredObject master;
    private String condition, orderBy;

    public ObjectListDataProvider(DataList<T> data) {
        super(data);
        if(!(getItems().getData() instanceof ObjectCacheList)) {
            throw new SORuntimeException();
        }
    }

    @Override
    public DataList<T> getItems() {
        return (DataList<T>) super.getItems();
    }

    public ObjectCacheList<T> getData() {
        return (ObjectCacheList<T>) getItems().getData();
    }

    @Override
    public Object getId(T item) {
        return item.getId();
    }

    @Override
    public final SerializableComparator<T> getSortComparator() {
        return null;
    }

    @Override
    public final void setSortComparator(SerializableComparator<T> comparator) {
    }

    @Override
    public final SerializablePredicate<T> getFilter() {
        return null;
    }

    @Override
    public final void setFilter(SerializablePredicate<T> filter) {
    }

    public T getItem(int index) {
        return getItems().get(index);
    }

    public void setCondition(String condition) {
        this.condition = condition;
        reload();
    }

    @Override
    public String getCondition() {
        return condition;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        reload();
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public boolean isAllowAny() {
        return getData().isAllowAny();
    }

    public void setMaster(StoredObject master) {
        load(linkType, master);
    }

    public StoredObject getMaster() {
        return master;
    }

    public void setLinkType(int linkType) {
        if(master != null) {
            load(linkType, master);
        } else {
            this.linkType = linkType;
        }
    }

    public int getLinkType() {
        return linkType;
    }

    @Override
    public void load(String condition, String orderBy) {
        this.condition = condition;
        this.orderBy = orderBy;
        this.master = null;
        getData().load(condition, orderBy);
        refreshAll();
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        this.condition = condition;
        this.orderBy = orderBy;
        this.master = master;
        this.linkType = linkType;
        getData().load(linkType, master, condition, orderBy);
        refreshAll();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        getData().load(objects);
        refreshAll();
    }

    @Override
    public void reload() {
        if(master == null) {
            load(condition, orderBy);
        } else {
            load(linkType, master, condition, orderBy);
        }
    }

    @Override
    public void clear() {
        getData().close();
        refreshAll();
    }

    @Override
    public void close() {
        getData().close();
    }

    @Override
    public void added(T object) {
        getItems().add(object);
    }

    @Override
    public void edited(T object) {
        getData().refresh(object);
        refreshItem(object);
    }

    @Override
    public void deleted(T object) {
        getData().remove(object);
        refreshAll();
    }

    @Override
    public AutoCloseable getResource() {
        return new Cleaner(getData());
    }

    private record Cleaner(ObjectCacheList<?> cache) implements AutoCloseable {

        @Override
        public void close() {
            if(cache != null) {
                cache.close();
            }
        }
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        getData().filter(query.getFilter().orElse(null), query.getSortingComparator().orElse(null));
        return getData().stream(query.getOffset(), query.getOffset() + query.getLimit());
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> query) {
        getData().filter(query.getFilter().orElse(null));
        return getData().size(query.getOffset(), query.getLimit());
    }
}
