package com.storedobject.ui;

import com.storedobject.core.Filtered;
import com.storedobject.ui.util.DataLoadedListener;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.ui.util.ViewFilterSupport;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractListProvider<T> extends ListDataProvider<T> implements ViewFilterSupport<T> {

    private final List<DataLoadedListener> loadedListeners = new ArrayList<>();
    private ViewFilter<T> viewFilter;
    private Predicate<T> viewFilterPredicate;
    private final Class<T> dataClass;
    private String tokens;
    Application a;

    public AbstractListProvider(Class<T> dataClass, DataList<T> data) {
        super(data);
        this.dataClass = dataClass;
        a = Application.get();
    }

    @Override
    public DataList<T> getItems() {
        return (DataList<T>) super.getItems();
    }

    public Filtered<T> getData() {
        //noinspection unchecked
        return (Filtered<T>) getItems().getData();
    }

    @Override
    public final void setSortComparator(SerializableComparator<T> comparator) {
    }

    @Override
    public final void setFilter(SerializablePredicate<T> filter) {
        saveFilter(filter);
    }

    abstract void saveFilter(Predicate<T> filter);

    abstract Predicate<T> retrieveFilter();

    @Override
    public void refreshAll() {
        super.refreshAll();
        loadedListeners.forEach(DataLoadedListener::dataLoaded);
    }

    public int getObjectCount() {
        return getItems().size();
    }

    public T get(int index) {
        return getItems().get(index);
    }

    public int indexOf(T object) {
        return getItems().indexOf(object);
    }

    public Stream<T> streamAll() {
        return getItems().stream();
    }

    public Stream<T> streamFiltered() {
        Stream<T> s = streamAll();
        Predicate<T> filter = retrieveFilter();
        if(filter != null) {
            s = s.filter(filter);
        }
        return s;
    }

    public void clear() {
        getItems().clear();
    }

    public void close() {
        clear();
    }

    @Override
    public ViewFilter<T> getViewFilter() {
        if(viewFilter == null) {
            viewFilter = new ViewFilter<>(dataClass);
        }
        return viewFilter;
    }

    @Override
    public void filterView(String filters) {
        this.tokens = filters;
        synchronized(loadedListeners) {
            refreshAll();
        }
    }

    /**
     * Whenever data is loaded from the DB, this listener will be informed.
     *
     * @param listener Listener.
     */
    public Registration addDataLoadedListener(DataLoadedListener listener) {
        loadedListeners.add(listener);
        return () -> loadedListeners.remove(listener);
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        getData().order(query.getSortingComparator().orElse(null));
        if(viewFilterPredicate == null) {
            return getData().stream(query.getOffset(), query.getOffset() + query.getLimit());
        }
        return getData().stream(0, Integer.MAX_VALUE).filter(viewFilterPredicate).skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> query) {
        viewFilterPredicate = getViewFilter().getPredicate(tokens, query.getFilter().orElse(null));
        if(viewFilterPredicate == null) {
            return getData().size(query.getOffset(), query.getLimit());
        }
        return (int) getData().stream(0, Integer.MAX_VALUE).filter(viewFilterPredicate).skip(query.getOffset())
                .limit(query.getLimit()).count();
    }

    protected interface LoadCallBack {
        void load(Runnable loadFunction);
    }

    @Override
    public final Class<T> getObjectClass() {
        return dataClass;
    }

    @Override
    public final DataProvider<?, ?> getDataProvider() {
        return this;
    }
}
