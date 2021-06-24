package com.storedobject.ui;

import com.storedobject.common.LogicalOperator;
import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.*;
import com.storedobject.ui.util.*;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ObjectGrid<T extends StoredObject> extends DataGrid<T> implements ObjectGridData<T>, ResourceOwner {

    static final String NOTHING_SELECTED = "Nothing selected";
    private final List<ObjectDataLoadedListener> dataLoadedListeners = new ArrayList<>();
    ObjectSetter<T> objectSetter;
    private ObjectDataProvider<T, Void> dataProvider;
    private ViewFilter<T> viewFilter;
    private String orderBy;
    private List<ObjectChangedListener<T>> objectChangedListeners;
    private Registration loadedIndicator;

    public ObjectGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, new ObjectSupplier<>(objectClass, null, null, any));
    }

    public ObjectGrid(Class<T> objectClass, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, dataProvider);
    }

    public ObjectGrid(Class<T> objectClass, Iterable<String> columns, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, columns == null ? StoredObjectUtility.browseColumns(objectClass) : columns);
        addDetachListener(e -> ResourceDisposal.gc());
        this.dataProvider = dataProvider;
        super.setItems(dataProvider);
        viewFilter = viewFilter();
        ResourceDisposal.register(this);
        loadedIndicator = dataProvider.addObjectDataLoadedListener(this::loadedInt);
    }

    private ViewFilter<T> viewFilter() {
        if(viewFilter == null) {
            viewFilter = this.dataProvider.getViewFilter();
            if(viewFilter == null) {
                viewFilter = new ViewFilter<>(this.dataProvider);
            }
        }
        return viewFilter;
    }

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    @Override
    public GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        GridDataView<T> gv;
        if(dataProvider instanceof ObjectDataProvider) {
            ObjectDataProvider<T, Void> odp = (ObjectDataProvider<T, Void>) dataProvider;
            if(this.dataProvider != null) {
                this.dataProvider.close();
            }
            if(loadedIndicator != null) {
                loadedIndicator.remove();
            }
            if(viewFilter != null) {
                odp.setViewFilter(viewFilter);
            } else {
                viewFilter = odp.getViewFilter();
                if(viewFilter == null) {
                    viewFilter = new ViewFilter<>(odp);
                }
            }
            gv = super.setItems(dataProvider);
            this.dataProvider = odp;
            ResourceDisposal.register(this);
            loadedIndicator = this.dataProvider.addObjectDataLoadedListener(this::loadedInt);
        } else {
            gv = null;
        }
        setFilter((ObjectSearchFilter)null);
        return gv;
    }

    @Override
    public ObjectDataProvider<T, Void> getDataProvider() {
        return dataProvider;
    }

    @Override
    public final AutoCloseable getResource() {
        return dataProvider.getResource();
    }

    @Override
    public List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        if(objectChangedListeners == null && create) {
            objectChangedListeners = new ArrayList<>();
        }
        return objectChangedListeners;
    }

    @Override
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
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
                    o = getItem(0);
                    select(o);
                    return o;
                }
            }
            warning(NOTHING_SELECTED);
        }
        return o;
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
        this.objectSetter = setter;
    }

    public void search() {
        if(objectSetter != null) {
            search(objectSetter);
        }
    }

    @Override
    public void loaded() {
    }

    private void loadedInt() {
        loaded();
        dataLoadedListeners.forEach(ObjectDataLoadedListener::dataLoaded);
    }

    public void filterView(String filters) {
        dataProvider.filterView(filters);
    }

    public void configureFilterView(BiFunction<T, String[], Boolean> matchFunction) {
        if(viewFilter().setMatcher(matchFunction)) {
            refresh();
        }
    }

    public void configureFilterView(ObjectToString<T> objectToString) {
        if(viewFilter().setObjectConverter(objectToString)) {
            refresh();
        }
    }

    public void configureFilterView(String... attributes) {
        if(attributes == null || attributes.length == 0) {
            configureFilterView((ObjectToString<T>)null);
            return;
        }
        configureFilterView(ObjectToString.create(getObjectClass(), attributes));
    }

    public void configureFilterView(LogicalOperator logicalOperator) {
        if(logicalOperator == null) {
            return;
        }
        viewFilter().setFilterLogic(logicalOperator);
    }
}