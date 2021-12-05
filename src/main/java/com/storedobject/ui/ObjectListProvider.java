package com.storedobject.ui;

import com.storedobject.common.ResourceOwner;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.vaadin.DataList;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectListProvider<T extends StoredObject> extends AbstractListProvider<T>
        implements ObjectLoader<T>, AutoCloseable, ResourceOwner {

    private int linkType = 0;
    private StoredObject master;
    private String orderBy;
    private final ObjectLoadFilter<T> loadFilter = new ObjectLoadFilter<>(),
            systemFilter = new ObjectLoadFilter<>(),
            fixedFilter = new ObjectLoadFilter<>();
    private LoadCallBack loadCallBack;

    public ObjectListProvider(Class<T> objectClass, DataList<T> data) {
        super(objectClass, data);
        if(!(getItems().getData() instanceof ObjectList)) {
            throw new SORuntimeException();
        }
    }

    public ObjectListProvider(ObjectList<T> cache) {
        this(cache.getObjectClass(), new DataList<>(cache));
    }

    ObjectLoadFilter<T> getSystemFilter() {
        return systemFilter;
    }

    @Override
    public ObjectLoadFilter<T> getFixedFilter() {
        return fixedFilter;
    }

    public ObjectList<T> getData() {
        return (ObjectList<T>) getItems().getData();
    }

    @Override
    public Id getId(T item) {
        return item.getId();
    }

    public boolean contains(T item) {
        return getData().contains(item);
    }

    @Override
    void saveFilter(Predicate<T> filter) {
        ObjectLoader.super.setFilter(filter, false);
    }

    @Override
    Predicate<T> retrieveFilter() {
        return loadFilter.getLoadedPredicate();
    }

    @Override
    public String getFilterCondition() {
        return loadFilter.getFilter();
    }

    public final void setOrderBy(String orderBy, boolean load) {
        this.orderBy = orderBy;
        if(load) {
            reload();
        }
    }

    @Override
    public final String getOrderBy() {
        return orderBy;
    }

    @Override
    public final boolean isAllowAny() {
        return getData().isAllowAny();
    }

    public final void setMaster(StoredObject master, boolean load) {
        this.master = master;
        if(load) {
            load(linkType, master);
        }
    }

    public final StoredObject getMaster() {
        return master;
    }

    public void setLinkType(int linkType, boolean load) {
        this.linkType = linkType;
        if(master != null && load) {
            load(linkType, master);
        }
    }

    public final int getLinkType() {
        return linkType;
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
        this.orderBy = orderBy;
        this.master = null;
        getData().load(cond(condition), orderBy, any);
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
        this.orderBy = orderBy;
        this.master = master;
        this.linkType = linkType;
        getData().load(linkType, master, cond(condition), orderBy, any);
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
        getData().load(objects);
        refreshAll();
    }

    @Override
    public void reload() {
        getData().refresh();
        refreshAll();
    }

    @Override
    public void applyFilterPredicate() {
        getData().filter(loadFilter.getLoadedPredicate());
        refreshAll();
    }

    @Override
    public int getObjectCount() {
        return getData().size();
    }

    @Override
    public T get(int index) {
        return getData().get(index);
    }

    @Override
    public int indexOf(T object) {
        return getData().indexOf(object);
    }

    @Override
    public Stream<T> streamAll() {
        return getData().streamAll(0, getData().sizeAll());
    }

    @Override
    public Stream<T> streamFiltered() {
        return getData().stream(0, size());
    }

    @Override
    public int getCacheLevel() {
        return getData().getCacheLevel();
    }

    @Override
    public void clear() {
        getData().close();
        refreshAll();
    }

    @Override
    public final void close() {
        clear();
    }

    @Override
    public AutoCloseable getResource() {
        return new Cleaner(getData());
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return loadFilter;
    }

    void setLoadCallBack(LoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    private record Cleaner(ObjectList<?> cache) implements AutoCloseable {

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
}
