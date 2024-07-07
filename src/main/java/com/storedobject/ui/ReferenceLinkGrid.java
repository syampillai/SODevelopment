package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LinkGridButtons;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.data.provider.ListDataProvider;

public class ReferenceLinkGrid<T extends StoredObject> extends AbstractLinkGrid<T> {

    private final LinkGridButtons<T> buttonPanel;
    private ObjectSearcher<T> searcher;
    private boolean fromClient;

    private final ObjectSetter<T> setter = new ObjectsSetter<>() {

        @Override
        public void setObjects(Iterable<T> objects) {
            objects.forEach(so -> {
                T o = convert(so);
                if(o != null && canChange(o, EditorAction.NEW)) {
                    ReferenceLinkGrid.this.itemInserted(o);
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
            return getDelegatedLoader().isAllowAny();
        }
    };

    public ReferenceLinkGrid(ObjectLinkField<T> linkField) {
        this(linkField, null);
    }

    public ReferenceLinkGrid(ObjectLinkField<T> linkField, Iterable<String> columns) {
        super(linkField, new ObjectMemoryList<>(linkField.getObjectClass(), linkField.isAllowAny()), columns);
        setFilter((String) null, false);
        buttonPanel = new LinkGridButtons<>(this);
        //noinspection unchecked
        createColumn("*", o -> isEdited(o) ? "*" : (isAdded(o) ? "+" : (isDeleted(o) ? "-" : "")));
        addDataLoadedListener(buttonPanel::changed);
        setLinkType(link.getType(), false);
    }

    protected final EditableList<T> createEditableList() {
        return new RList();
    }

    @Override
    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof ReferenceLinkListProvider<T> && super.isValid(dataProvider);
    }

    @Override
    protected AbstractListProvider<T> createListDataProvider(DataList<T> data) {
        return new ReferenceLinkListProvider<>(getObjectClass(), data);
    }

    private ReferenceLinkListProvider<T> provider() {
        return (ReferenceLinkListProvider<T>)getDataProvider();
    }

    @Override
    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    @Override
    public boolean isFromClient() {
        return fromClient;
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

    public boolean contains(T object) {
        return provider().contains(object);
    }

    @Override
    public boolean add(T item) {
        if(item != null && Id.isNull(item.getId())) {
            itemInserted(item);
        }
        if(item != null) {
            super.add(item);
        }
        return true;
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
    public void applyFilter() {
        super.applyFilter();
        if(searcher instanceof ObjectBrowser<T> s) {
            s.setFilter(getEffectiveCondition(getFilterCondition()));
            s.setLoadFilter(getLoadFilter().getLoadingPredicate());
        }
    }

    @Override
    public void setOrderBy(String orderBy, boolean load) {
        super.setOrderBy(orderBy, load);
        if(searcher instanceof ObjectBrowser<T> s) {
            s.setOrderBy(orderBy, load);
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
            itemDeleted(object);
        }
    }

    @Override
    public void reload() {
        T object = selected();
        if(object == null) {
            return;
        }
        if(canChange(object, EditorAction.RELOAD)) {
            itemUndeleted(object);
        }
    }

    @Override
    public void reloadAll() {
        setMaster(getMaster(),true);
    }

    public ObjectSearcher<T> getSearcher() {
        if (searcher == null) {
            ObjectBrowser<T> s = ObjectBrowser.create(getObjectClass(), null,
                    EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0),
                    null, null);
            s.setFilter(getEffectiveCondition(getFilterCondition()));
            s.setLoadFilter(getLoadFilter().getLoadingPredicate());
            searcher = s;
        }
        return searcher;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        buttonPanel.changed();
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public LinkValue<T> getLinkGrid() {
        return (RList)getEditableList();
    }

    @Override
    protected void doInsertAction(T object) {
        if(provider().add(object, true)) {
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> l.inserted(object));
            }
            select(object);
        }
    }

    @Override
    protected void doUpdateAction(T object) {
        if(provider().update(object, true)) {
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> l.updated(object));
            }
            select(object);
        }
    }

    @Override
    protected void doDeleteAction(T object) {
        if(provider().delete(object, true)) {
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> l.deleted(object));
            }
        }
    }

    @Override
    protected void doUndeleteAction(T object) {
        if(provider().undelete(object, true)) {
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> l.undeleted(object));
            }
            select(object);
        }
    }

    @Override
    protected void doReloadAllAction() {
        refresh();
        reloadedAllNow();
    }

    @Override
    protected final boolean canChange(T item, int editorAction) {
        return ((ObjectEditor<?>)getButtonPanel().getMasterView()).acceptChange(getField(), item, editorAction);
    }

    private class RList extends EList implements LinkValue<T> {

        @Override
        public StoredObjectUtility.Link<T> getLink() {
            return link;
        }

        @Override
        public EditableProvider<T> getEditableList() {
            //noinspection unchecked
            return (EditableProvider<T>) getDataProvider();
        }

        @Override
        public void clear() {
            ReferenceLinkGrid.this.clear();
        }

        @Override
        public ObjectLinkField<T> getField() {
            return linkField;
        }

        @Override
        public StoredObject getMaster() {
            return ReferenceLinkGrid.this.getMaster();
        }
    }
}
