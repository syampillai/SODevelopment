package com.storedobject.ui;

import com.storedobject.core.Filtered;
import com.storedobject.ui.util.DataLoadedListener;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.ui.util.ViewFilterSupport;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractTreeProvider<T, O> extends AbstractHierarchicalDataProvider<T, SerializablePredicate<T>>
        implements ViewFilterSupport<O> {

    private final List<DataLoadedListener> loadedListeners = new ArrayList<>();
    private ViewFilter<O> viewFilter;
    private ItemLabelGenerator<T> itemLabelGenerator;
    private final Class<O> dataClass;
    String tokens;

    protected AbstractTreeProvider(Class<O> dataClass) {
        this.dataClass = dataClass;
    }

    public abstract List<? extends T> getRoots();

    public abstract Filtered<? extends T> getData();

    abstract void saveFilter(Predicate<T> filter);

    abstract Predicate<T> retrieveFilter();

    public final void setFilter(SerializablePredicate<T> filter) {
        saveFilter(filter);
    }

    public int getObjectCount() {
        return getRoots().size();
    }

    public T get(int index) {
        return getRoots().get(index);
    }

    public int indexOf(T object) {
        return getRoots().indexOf(object);
    }

    public Stream<? extends T> streamAll() {
        return getRoots().stream();
    }

    public Stream<? extends T> streamFiltered() {
        Stream<? extends T> s = streamAll();
        Predicate<T> filter = retrieveFilter();
        if(filter != null) {
            s = s.filter(filter);
        }
        return s;
    }

    public void clear() {
        getRoots().clear();
    }

    public void close() {
        clear();
    }

    @Override
    public ViewFilter<O> getViewFilter() {
        if(viewFilter == null) {
            viewFilter = new ViewFilter<>(getObjectClass());
        }
        return viewFilter;
    }

    @Override
    public void filterView(String filters) {
        this.tokens = filters;
        refreshAll();
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
    public void refreshAll() {
        super.refreshAll();
        loadedListeners.forEach(DataLoadedListener::dataLoaded);
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
    }

    public ItemLabelGenerator<T> getItemLabelGenerator() {
        return itemLabelGenerator;
    }

    protected interface LoadCallBack {
        void load(Runnable loadFunction);
    }

    @Override
    public final Class<O> getObjectClass() {
        return dataClass;
    }

    @Override
    public final DataProvider<?, ?> getDataProvider() {
        return this;
    }
}
