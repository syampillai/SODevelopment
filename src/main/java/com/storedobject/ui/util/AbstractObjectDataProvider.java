package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.EditorAction;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.*;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.shared.Registration;

import java.io.Closeable;
import java.util.Objects;
import java.util.stream.Stream;

public interface AbstractObjectDataProvider<T extends StoredObject, M, F> extends
        DataProvider<M, F>, FilterMethods<T>, Closeable, ResourceOwner, ObjectLoader<T> {

    boolean isAllowAny();

    Class<T> getObjectClass();

    Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener);

    @Override
    void close();

    void setViewFilter(ViewFilter<T> viewFilter);

    ViewFilter<T> getViewFilter();

    void filterView(String filters);

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     */
    @Override
    default void setFilter(FilterProvider filter) {
        ObjectSearchFilter f = getFilter(false);
        if(f == null) {
            if(filter == null) {
                return;
            }
            f = getFilter(true);
        } else {
            if(f.getFilterProvider() == filter) {
                return;
            }
        }
        f.setFilterProvider(filter);
        filterChanged();
    }

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     * @param extraFilterClause Extra filter clause
     */
    @Override
    default void setFilter(FilterProvider filter, String extraFilterClause) {
        ObjectSearchFilter f = getFilter(false);
        if(f == null) {
            if(filter == null) {
                setFilter(extraFilterClause);
                return;
            }
            f = getFilter(true);
        } else {
            if(f.getFilterProvider() == filter) {
                setFilter(extraFilterClause);
                return;
            }
        }
        f.setFilterProvider(filter);
        if(!Objects.equals(f.getCondition(), extraFilterClause)) {
            f.setCondition(extraFilterClause);
        }
        filterChanged();
    }

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     */
    @Override
    default void setFilter(String filter) {
        ObjectSearchFilter f = getFilter(false);
        if(f == null) {
            if(filter == null || filter.isEmpty()) {
                return;
            }
            f = getFilter(true);
        } else {
            if(Objects.equals(f.getCondition(), filter)) {
                return;
            }
        }
        f.setCondition(filter);
        filterChanged();
    }

    Stream<T> streamAll();

    default Stream<T> streamFiltered() {
        return streamAll().filter(this::validateFilterCondition);
    }

    boolean validateFilterCondition(T value);

    default int getObjectCount() {
        return -1;
    }

    int indexOf(T object);

    T getItem(int index);

    default void clear() {
        clear(true);
    }

    default void clear(boolean refresh) {
        load(ObjectIterator.create());
    }

    boolean isFullyLoaded();

    boolean isFullyCached();

    void filterChanged();

    class ObjectAdder<O extends StoredObject> {

        private ObjectEditor<O> adder;
        private AbstractObjectDataProvider<O, ?, ?> dataProvider;
        private ObjectInput<O> field;

        private ObjectAdder() {
        }

        public static <OT extends StoredObject> ObjectAdder<OT> create(AbstractObjectDataProvider<OT, ?, ?> dataProvider, ObjectInput<OT> field) {
            ObjectAdder<OT> oa = new ObjectAdder<>();
            oa.dataProvider = dataProvider;
            oa.field = field;
            return oa;
        }

        public void add() {
            if(adder == null) {
                adder = ObjectEditor.create(dataProvider.getObjectClass(), EditorAction.NEW);
                adder.addObjectChangedListener(new Changed());
            }
            adder.addObject(Application.get().getActiveView());
        }

        private class Changed implements ObjectChangedListener<O> {

            @Override
            public void inserted(O object) {
                dataProvider.added(object);
                field.setValue(object);
            }

            @Override
            public void updated(O object) {
                dataProvider.edited(object);
                field.setValue(object);
            }

            @Override
            public void deleted(O object) {
                dataProvider.deleted(object);
                if(field.getValue().equals(object)) {
                    field.setValue((O)null);
                }
            }
        }
    }
}