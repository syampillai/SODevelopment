package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.DataList;
import com.storedobject.vaadin.View;

import java.util.stream.Stream;

public class ReferenceLinkGrid<T extends StoredObject> extends ObjectGrid<T> implements LinkGrid<T>, StoredObjectLink<T> {

    private final StoredObjectUtility.Link<T> link;
    private final ObjectLinkField<T> linkField;
    private final LinkGridButtons<T> buttonPanel;
    private ObjectSearcher<T> searcher;
    private StoredObject master;
    private boolean readOnly;

    private final ObjectSetter<T> setter = new ObjectsSetter<>() {

        @Override
        public void setObjects(Iterable<T> objects) {
            objects.forEach(so -> {
                T o = convert(so);
                if(o != null && canChange(o, EditorAction.NEW)) {
                    added(o);
                }
            });
        }

        @Override
        public void setObject(T object) {
            setObjects(ObjectIterator.create(object));
        }

        @Override
        public Class<T> getObjectClass() {
            return ReferenceLinkGrid.this.getObjectClass();
        }

        @Override
        public boolean isAllowAny() {
            return getObjectLoader().isAllowAny();
        }
    };

    public ReferenceLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public ReferenceLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(new ObjectMemoryList<>(linkField.getObjectClass(), linkField.isAllowAny()), columns);
        this.linkField = linkField;
        this.link = linkField.getLink();
        buttonPanel = new LinkGridButtons<>(this);
        //noinspection unchecked
        createColumn("*", o -> isEdited(o) ? "*" : (isAdded(o) ? "+" : (isDeleted(o) ? "-" : "")));
    }

    @Override
    protected EditableObjectListProvider<T> createListDataProvider(DataList<T> data) {
        return new EditableObjectListProvider<>(getObjectClass(), data);
    }

    @Override
    public EditableObjectListProvider<T> getDataProvider() {
        return (EditableObjectListProvider<T>) super.getDataProvider();
    }

    @Override
    public int getColumnOrder(String columnName) {
        if("*".equals(columnName)) {
            return Integer.MIN_VALUE + 1;
        }
        return super.getColumnOrder(columnName);
    }

    @Override
    public void customizeColumn(String columnName, Column<T> column) {
        if("*".equals(columnName)) {
            column.setHeader("");
            column.setFlexGrow(0).setWidth("20px").setResizable(false).setFrozen(true);
        }
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(buttonPanel);
    }

    @Override
    public LinkGridButtons<T> getButtonPanel() {
        return buttonPanel;
    }

    @Override
    public StoredObjectUtility.Link<T> getLink() {
        return link;
    }

    @Override
    public boolean isAllowAny() {
        return super.isAllowAny();
    }

    @Override
    public T getSelected() {
        return super.getSelected();
    }

    @Override
    public final boolean isDetail() {
        return false;
    }

    @Override
    public EditableObjectListProvider<T> getEditableList() {
        return (EditableObjectListProvider<T>) getObjectLoader();
    }

    @Override
    public View createView() {
        return null;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public View getView(boolean create) {
        return null;
    }

    public boolean contains(T object) {
        return getEditableList().contains(object);
    }

    @Override
    public void edited(T object) {
    }

    @Override
    public boolean append(T object) {
        if(contains(object)) {
            return false;
        }
        try {
            return getEditableList().append(object);
        } finally {
            if(size() == 1) {
                buttonPanel.changed();
            }
        }
    }

    @Override
    public void added(T object) {
        if(contains(object)) {
            return;
        }
        getEditableList().add(object);
        if(size() == 1) {
            buttonPanel.changed();
        }
    }

    @Override
    public void deleted(T object) {
        getEditableList().delete(object);
        if(size() == 0) {
            buttonPanel.changed();
        }
    }

    @Override
    public void reloaded(T object) {
    }

    @Override
    public void add() {
        if(!buttonPanel.isAllowAdd()) {
            return;
        }
        searcher = getSearcher();
        if (searcher != null) {
            searcher.search(getTransactionManager().getEntity(), setter);
        }
    }

    @Override
    public void edit() {
    }

    @Override
    public void delete() {
        if(!isAllowDelete()) {
            return;
        }
        T object = selected();
        if(object == null) {
            return;
        }
        if(canChange(object, EditorAction.DELETE)) {
            delete(object);
        }
    }

    @Override
    public void reload() {
        T item = selected();
        if(item != null && canReload(item)) {
            getEditableList().reload(selected());
        }
    }

    private boolean canReload(T item) {
        @SuppressWarnings("unchecked") T o = (T) StoredObject.get(item.getClass(), item.getId());
        return canChange(o, EditorAction.RELOAD);
    }

    @Override
    public void reloadAll() {
        setMaster(getMaster(),true);
    }

    public ObjectSearcher<T> getSearcher() {
        if (searcher == null) {
            searcher = ObjectBrowser.create(getObjectClass(), null,
                    EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0), null);
        }
        return searcher;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public final boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        buttonPanel.changed();
    }

    @Override
    public StoredObject getMaster() {
        return master;
    }

    @Override
    public void setMaster(StoredObject master, boolean load) {
        this.master = master;
        if(load) {
            loadMaster();
        }
    }

    @Override
    public int size() {
        return LinkGrid.super.size();
    }

    @Override
    public void clear() {
        LinkGrid.super.clear();
    }

    @Override
    public T getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public ObjectLinkField<T> getField() {
        return linkField;
    }

    @Override
    public Stream<T> streamAll() {
        return super.streamAll();
    }

    @Override
    public Stream<T> stream() {
        return super.stream();
    }
}
