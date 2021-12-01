package com.storedobject.ui;

import com.storedobject.common.LogicalOperator;
import com.storedobject.common.ToString;
import com.storedobject.core.Filtered;
import com.storedobject.ui.util.ViewFilter;
import com.storedobject.ui.util.ViewFilterSupport;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.provider.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class AbstractListGrid<T> extends com.storedobject.vaadin.ListGrid<T> implements ViewFilterSupport<T> {

    static final String NOTHING_SELECTED = "Nothing selected";
    private GridListDataView<T> dataView;

    public AbstractListGrid(Class<T> objectClass, Filtered<T> list, Iterable<String> columns) {
        //noinspection unchecked
        super(objectClass, (List<T>)list, columns);
    }

    protected abstract boolean isValid(ListDataProvider<T> dataProvider);

    @Override
    public final GridListDataView<T> setItems(ListDataProvider<T> dataProvider) {
        if(isValid(dataProvider)) {
            dataView = super.setItems(dataProvider);
        } else {
            clear();
        }
        return dataView;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback) {
        clear();
        return null;
    }

    @Override
    public final GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        clear();
        return null;
    }

    @Override
    public final GridDataView<T> setItems(InMemoryDataProvider<T> inMemoryDataProvider) {
        clear();
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                              CallbackDataProvider.CountCallback<T, Void> countCallback) {
        clear();
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(BackEndDataProvider<T, Void> dataProvider) {
        clear();
        return null;
    }

    @SafeVarargs
    @Override
    public final GridListDataView<T> setItems(T... items) {
        clear();
        load(items);
        return dataView;
    }

    @Override
    public final GridListDataView<T> setItems(Collection<T> items) {
        clear();
        load(items);
        return dataView;
    }

    @SuppressWarnings("unchecked")
    public void load(T... items) {
        load(Arrays.asList(items));
    }

    public void load(Collection<T> items) {
        clear();
        addAll(items);
    }

    @Override
    protected AbstractListProvider<T> createListDataProvider(DataList<T> data) {
        return null;
    }

    @Override
    public AbstractListProvider<T> getDataProvider() {
        return (AbstractListProvider<T>) super.getDataProvider();
    }

    public T selected() {
        clearAlerts();
        T o = getSelected();
        if(o == null) {
            switch(size()) {
                case 0 -> {
                    warning("No item to select!");
                    return null;
                }
                case 1 -> {
                    o = get(0);
                    select(o);
                    return o;
                }
            }
            warning(NOTHING_SELECTED);
        }
        return o;
    }

    @Override
    public final void sort(Comparator<? super T> comparator) {
        //noinspection unchecked
        ((List<T>)getDataProvider().getData()).sort(comparator);
        refresh();
    }

    @Override
    public final void sort(List<GridSortOrder<T>> order) {
        super.sort(order);
    }

    @Override
    public final Class<T> getObjectClass() {
        return getDataClass();
    }
}
