package com.storedobject.ui;

import com.storedobject.common.ArrayListSet;
import com.storedobject.core.*;
import com.storedobject.vaadin.DataGrid;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewDependent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ObjectLinkField<T extends StoredObject>
        implements HasValue<HasValue.ValueChangeEvent<StoredObjectLink<T>>, StoredObjectLink<T>>, ViewDependent,
        ObjectsSetter<T> {

    private final StoredObjectUtility.Link<T> link;
    private String label;
    private boolean readOnly;
    private LinkGrid<T> grid;
    private List<String> invisible = new ArrayListSet<>();
    private Set<String> readOnlyColumns;
    private Tab tab;
    @SuppressWarnings("unchecked")
    private StoredObjectLink<T> value = (StoredObjectLink<T>) StoredObjectLink.EMPTY;
    private ArrayList<TrackerRegistration> registrations;

    public ObjectLinkField(String label, StoredObjectUtility.Link<T> link) {
        this.link = link;
        this.label = label;
    }

    @SuppressWarnings("unchecked")
    void setGrid(LinkGrid<?> linkGrid) {
        if(linkGrid != null) {
            if(linkGrid.getLink() != link) {
                linkGrid = null;
            } else {
                if (link.isDetail()) {
                    if (!(linkGrid instanceof DetailLinkGrid)) {
                        linkGrid = null;
                    }
                } else {
                    if (!(linkGrid instanceof ReferenceLinkGrid)) {
                        linkGrid = null;
                    }
                }
            }
        }
        if(linkGrid == null) {
            if(link.isDetail()) {
                linkGrid = new DetailLinkGrid<>(this);
            } else {
                linkGrid = new ReferenceLinkGrid<>(this);
            }
        }
        this.grid = (LinkGrid<T>) linkGrid;
        if(isReadOnly()) {
            grid.setReadOnly(true);
        }
        ((Grid<?>)grid).setAllRowsVisible(true);
        invisible.forEach(fieldName -> ((DataGrid<T>) grid).setColumnVisible(fieldName, false));
        invisible.clear();
        invisible = null;
        grid.setValue(value);
        value = null;
        if(registrations != null) {
            registrations.forEach(TrackerRegistration::attachGrid);
            registrations.clear();
            registrations = null;
        }
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        if(tab != null) {
            tab.setLabel(label);
        }
    }

    public StoredObjectUtility.Link<T> getLink() {
        return link;
    }

    public Class<? extends StoredObject> getMasterClass() {
        return link.getMasterClass();
    }

    public void hideColumn(String columnName) {
        if(grid == null) {
            invisible.add(columnName);
            return;
        }
        if(grid instanceof DataGrid) {
            ((DataGrid<?>) grid).setColumnVisible(columnName, false);
        }
    }

    public void unhideColumn(String columnName) {
        if(grid == null) {
            invisible.remove(columnName);
            return;
        }
        if(grid instanceof DataGrid) {
            ((DataGrid<?>) grid).setColumnVisible(columnName, true);
        }
    }

    public String getFieldName() {
        return grid.getName() + ".l";
    }

    public StoredObjectLink<T> getOldValue() {
        return grid.getOldValue();
    }

    public void edited(T object) {
        grid.itemUpdated(object);
    }

    public void added(T object) {
        grid.itemInserted(object);
    }

    public void deleted(T object) {
        grid.itemDeleted(object);
    }

    public void reloaded(T object) {
        grid.itemReloaded(object);
    }

    public boolean isEdited(T object) {
        return grid.getLinkGrid().isEdited(object);
    }

    public boolean isAdded(T object) {
        return grid.getLinkGrid().isAdded(object);
    }

    public boolean isDeleted(T object) {
        return grid.getLinkGrid().isDeleted(object);
    }

    public boolean isAllowAdd() {
        return grid.isAllowAdd();
    }

    public void setAllowAdd(boolean allowAdd) {
        grid.setAllowAdd(allowAdd);
    }

    public boolean isAllowEdit() {
        return grid.isAllowEdit();
    }

    public void setAllowEdit(boolean allowEdit) {
        grid.setAllowAdd(allowEdit);
    }

    public boolean isAllowDelete() {
        return grid.isAllowDelete();
    }

    public void setAllowDelete(boolean allowDelete) {
        grid.setAllowDelete(allowDelete);
    }

    public boolean isAllowReload() {
        return grid.isAllowReload();
    }

    public void setAllowReload(boolean allowReload) {
        grid.setAllowReload(allowReload);
    }

    public boolean isAllowReloadAll() {
        return grid.isAllowReloadAll();
    }

    public void setAllowReloadAll(boolean allowReloadAll) {
        grid.setAllowReloadAll(allowReloadAll);
    }

    public void add() {
        grid.add();
    }

    public void edit() {
        grid.edit();
    }

    public void delete() {
        grid.delete();
    }

    public void reload() {
        grid.reload();
    }

    public void reloadAll() {
        grid.reloadAll();
    }

    public void view() {
        grid.view();
    }

    @Override
    public boolean isAllowAny() {
        return link.isAny();
    }

    public Stream<T> getItems() {
        return grid.getLinkGrid().streamAll();
    }

    public void add(T object) {
        add(ObjectIterator.create(object));
    }

    public void add(Stream<T> objects) {
        add(objects.collect(Collectors.toList()));
    }

    public void add(Iterator<T> objects) {
        add(ObjectIterator.create(objects));
    }

    public void add(ObjectIterator<T> objects) {
        //noinspection unchecked
        ((Grid<T>)grid).deselectAll();
        objects.forEach(grid::itemInserted);
    }

    public void add(Iterable<T> objects) {
        add(objects.iterator());
    }

    public void scrollTo(T object) {
    }

    @Override
    public void setObject(T object) {
        @SuppressWarnings("unchecked") ObjectGridData<T, ?> g = (ObjectGridData<T, ?>) grid;
        T o = g.convert(object);
        if(o == null) {
            return;
        }
        g.deselectAll();
        g.select(o);
        scrollTo(object);
    }

    @Override
    public void setObjects(Iterable<T> objects) {
        @SuppressWarnings("unchecked") ObjectGridData<T, ?> g = (ObjectGridData<T, ?>) grid;
        g.deselectAll();
        ObjectIterator<T> oi = ObjectIterator.create(objects.iterator()).filter(Objects::nonNull);
        add(oi.map(g::convert).filter(Objects::nonNull));
    }

    @Override
    public Class<T> getObjectClass() {
        return link.getObjectClass();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(grid != null) {
            grid.setReadOnly(readOnly);
        }
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public StoredObject getMaster() {
        return grid.getMaster();
    }

    public void setMaster(StoredObject master) {
        grid.setMaster(master,true);
    }

    @Override
    public void clear() {
        if(grid != null) {
            grid.clear();
        } else {
            //noinspection unchecked
            value = (StoredObjectLink<T>) StoredObjectLink.EMPTY;
        }
    }

    @Override
    public void setValue(StoredObjectLink<T> value) {
        if(value == null) {
            clear();
            return;
        }
        setFromClient(false);
        if(grid == null) {
            this.value = value;
        } else {
            grid.setValue(value);
        }
        setFromClient(true);
    }

    @Override
    public StoredObjectLink<T> getValue() {
        return grid == null ? value : grid.getLinkGrid();
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
        grid.setObjectEditor(editor);
    }

    public ObjectEditor<T> getObjectEditor() {
        return grid.getObjectEditor();
    }

    @Override
    public void setDependentView(View masterView) {
        if(grid != null) {
            grid.setMasterView(masterView);
        }
    }

    @Override
    public View getDependentView() {
        return grid.getButtonPanel().getMasterView();
    }

    public void setFromClient(boolean fromClient) {
        //noinspection unchecked
        ((AbstractEditableGrid<T>)grid).setFromClient(fromClient);
    }

    void trackChanges(BiConsumer<ObjectLinkField<T>, Boolean> tracker) {
        BiConsumer<AbstractListProvider<T>, Boolean> t = (list, fromClient) -> tracker.accept(this, fromClient);
        if (grid == null) {
            new TrackerRegistration(t);
        } else {
            provider().addValueChangeTracker(t);
        }
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<StoredObjectLink<T>>> valueChangeListener) {
        BiConsumer<AbstractListProvider<T>, Boolean> t = (list, fromClient) -> {
            ValueChangeEvent<StoredObjectLink<T>> e = new ValueChangeEvent<>() {
                @Override
                public HasValue<?, StoredObjectLink<T>> getHasValue() {
                    return ObjectLinkField.this;
                }

                @Override
                public boolean isFromClient() {
                    return fromClient;
                }

                @Override
                public StoredObjectLink<T> getOldValue() {
                    return ObjectLinkField.this.getOldValue();
                }

                @Override
                public StoredObjectLink<T> getValue() {
                    return grid.getLinkGrid();
                }
            };
            valueChangeListener.valueChanged(e);
        };
        return grid == null ? new TrackerRegistration(t) : provider().addValueChangeTracker(t);
    }

    private EditableProvider<T> provider() {
        //noinspection unchecked
        return (EditableProvider<T>) ((AbstractListGrid<T>)grid).getDataProvider();
    }

    public boolean isColumnEditable(String columnName) {
        if(readOnlyColumns == null) {
            return true;
        }
        return !readOnlyColumns.contains(columnName);
    }

    public void setColumnReadOnly(String... columnNames) {
        if(readOnlyColumns == null) {
            readOnlyColumns = new TreeSet<>();
        }
        for(String columnName: columnNames) {
            if (columnName.endsWith(".l")) {
                columnName = columnName.substring(0, columnName.length() - 2);
            }
            readOnlyColumns.add(columnName);
        }
    }

    public AbstractListGrid<T> getGrid() {
        //noinspection unchecked
        return (AbstractListGrid<T>) grid;
    }

    public static class Tabs extends com.storedobject.vaadin.Tabs {

        public Tabs() {
            getTabs().addThemeVariants(TabsVariant.LUMO_SMALL);
            getElement().setAttribute("colspan", "2");
        }

        public void addField(ObjectLinkField<?> field) {
            Tab tab = new Tab(field.getLabel());
            add(tab, (Component)field.grid);
            field.tab = tab;
        }
    }

    private class TrackerRegistration implements Registration {

        private Registration gridRegistration;
        private final BiConsumer<AbstractListProvider<T>, Boolean> tracker;

        private TrackerRegistration(BiConsumer<AbstractListProvider<T>, Boolean> tracker) {
            this.tracker = tracker;
            if(registrations == null) {
                registrations = new ArrayList<>();
            }
            registrations.add(this);
        }

        @Override
        public void remove() {
            if(registrations != null) {
                registrations.remove(this);
            }
            if(gridRegistration != null) {
                gridRegistration.remove();
            }
        }

        private void attachGrid() {
            gridRegistration = provider().addValueChangeTracker(tracker);
        }
    }
}