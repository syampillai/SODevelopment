package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectDataLoadedListener;
import com.storedobject.ui.util.ObjectTreeListProvider;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ObjectTree<T extends StoredObject> extends DataTreeGrid<T> implements Transactional, ObjectsSetter<T> {

    private final List<ObjectDataLoadedListener> dataLoadedListeners = new ArrayList<>();
    ObjectTreeListProvider<T> dataProvider;
    private final List<ObjectChangedListener<T>> objectChangedListeners = new ArrayList<>();
    private ObjectEditor<T> editor;
    private InternalChangedListener internalChangedListener;
    private NOGenerator noGenerator;
    private NewObject<T> newObject;
    private Logic logic;
    private Registration loadedIndicator;
    private SplitLayout layout;

    public ObjectTree(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectTree(Class<T> objectClass, int linkType) {
        this(objectClass, false, linkType);
    }

    public ObjectTree(Class<T> objectClass, Iterable<String> columns, int linkType) {
        this(objectClass, columns, false, linkType);
    }

    public ObjectTree(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectTree(Class<T> objectClass, boolean any, int linkType) {
        this(objectClass, null, any, linkType);
    }

    public ObjectTree(Class<T> objectClass, boolean any) {
        this(objectClass, null, any, 0);
    }

    public ObjectTree(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(objectClass, columns, any, 0);
    }

    public ObjectTree(Class<T> objectClass, Iterable<String> columns, boolean any, int linkType) {
        this(objectClass, columns, ObjectTreeBuilder.create(linkType, any));
    }

    public ObjectTree(Class<T> objectClass, ObjectTreeBuilder treeBuilder) {
        this(objectClass, null, treeBuilder);
    }

    public ObjectTree(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder) {
        super(objectClass, columns);
        setDataProvider(new ObjectTreeListProvider<>(new ArrayList<>(), treeBuilder));
    }

    @Override
    public void setDataProvider(HierarchicalDataProvider<T, ?> dataProvider) {
        if(dataProvider instanceof ObjectTreeListProvider) {
            //noinspection unchecked
            this.dataProvider = (ObjectTreeListProvider<T>) dataProvider;
            if(loadedIndicator != null) {
                loadedIndicator.remove();
            }
            loadedIndicator = this.dataProvider.addObjectDataLoadedListener(this::loadedInt);
            super.setDataProvider(dataProvider);
        }
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getDataClass();
    }

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        dataProvider.setItemLabelGenerator(itemLabelGenerator);
    }

    void protect() {
    }

    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
            if(logic.getApprovalCount() > 0 && getTransactionManager().needsApprovals()) {
                if(editor != null) {
                    editor.setLogic(logic);
                }
                protect();
            }
        }
    }

    @Override
    public final Logic getLogic() {
        return logic;
    }

    @SuppressWarnings("unchecked")
    private void loadFiltered(Iterable<? extends StoredObject> list, boolean filter) {
        deselectAll();
        ObjectIterator<T> oi;
        if(list == null) {
            oi = ObjectIterator.create();
        } else {
            ObjectIterator<StoredObject> oiSO = (ObjectIterator<StoredObject>)ObjectIterator.create(list.iterator()).filter(Objects::nonNull);
            oi = oiSO.map(this::convert).filter(Objects::nonNull);
        }
        Stream<T> objects;
        if(filter) {
            objects = oi.filter(o -> dataProvider.getTreeBuilder().getParent(o) == null).stream();
        } else {
            objects = oi.stream();
        }
        setDataProvider(new ObjectTreeListProvider<>(objects, dataProvider.getTreeBuilder()));
        objects.close();
        refresh();
    }

    private void loadFiltered(Iterable<? extends StoredObject> list) {
        loadFiltered(list, true);
    }

    public void load() {
        loadFiltered(StoredObject.list(getObjectClass()));
    }

    public void load(Iterable<? extends StoredObject> list) {
        loadFiltered(list, false);
    }

    public void load(T root) {
        load(ObjectIterator.create(root));
    }

    public void loaded() {
    }

    private void loadedInt() {
        loaded();
        dataLoadedListeners.forEach(ObjectDataLoadedListener::dataLoaded);
    }

    @Override
    public Component getViewComponent() {
        return layout == null ? super.getViewComponent() : layout;
    }

    public void setSplitView() {
        if(getView(false) != null) {
            return;
        }
        layout = new SplitLayout();
        layout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        layout.setSplitterPosition(50);
        layout.addToPrimary(this);
        addItemSelectedListener((forest, item) -> itemSelected());
    }

    private void itemSelected() {
        if(layout == null) {
            return;
        }
        if(editor == null) {
            getObjectEditor();
        } else {
            editor.abort();
        }
        editor.viewObject(getSelected());
    }

    public void clear() {
        load(ObjectIterator.create());
        loaded();
    }

    @Override
    public void setObject(T object) {
        deselectAll();
        if(object == null || !getObjectClass().isAssignableFrom(object.getClass())) {
            return;
        }
        select(object);
    }

    @Override
    public void setObjects(Iterable<T> objects) {
        load(objects);
    }

    @SuppressWarnings("unchecked")
    private T convert(StoredObject so) {
        if(so == null || !getObjectClass().isAssignableFrom(so.getClass())) {
            return null;
        }
        if(!dataProvider.getTreeBuilder().isAllowAny() && getObjectClass() != so.getClass()) {
            return null;
        }
        return (T)so;
    }

    public T getRoot() {
        List<T> roots = dataProvider.listRoots();
        return roots.size() == 1 ? roots.get(0) : null;
    }

    public List<T> listRoots() {
        return dataProvider.listRoots();
    }

    public void addObjectChangedListener(ObjectChangedListener<T> listener) {
        if(listener != null) {
            objectChangedListeners.add(listener);
        }
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        if(listener != internalChangedListener) {
            objectChangedListeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }


    public void setObjectEditor(ObjectEditor<T> editor) {
        if(this.editor != null) {
            if(this.editor.executing()) {
                this.editor.abort();
            }
            if(layout != null) {
                layout.remove(this.editor.getComponent());
            }
            this.editor.removeObjectChangedListener(internalChangedListener);
            if(layout != null) {
                layout.remove(this.editor.getComponent());
            }
        }
        this.editor = editor;
        if(this.editor != null) {
            constructEditor();
        }
    }

    public final ObjectEditor<T> getObjectEditor() {
        if(editor == null) {
            editor = createObjectEditor();
            if(editor == null) {
                editor = ObjectEditor.create(getObjectClass());
            }
            constructEditor();
        }
        return editor;
    }

    private void constructEditor() {
        if(internalChangedListener == null) {
            internalChangedListener = new InternalChangedListener();
            noGenerator = new NOGenerator();
        }
        editor.addObjectChangedListener(internalChangedListener);
        editor.setNewObjectGenerator(noGenerator);
        editor.setLogic(logic);
        if(layout != null) {
            editor.setEmbeddedView(getView(true));
            layout.addToSecondary(editor.getComponent());
            layout.setSplitterPosition(50);
            if(editor.add != null) {
                editor.add.setVisible(false);
            }
            if(editor.edit != null) {
                editor.edit.setVisible(false);
            }
            if(editor.delete != null) {
                editor.delete.setVisible(false);
            }
            if(editor.search != null) {
                editor.search.setVisible(false);
            }
            if(editor.searcherField != null) {
                editor.searcherField.setVisible(false);
            }
            if(editor.exit != null) {
                editor.exit.setVisible(false);
            }
        }
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    public void setNewObjectGenerator(NewObject<T> newObject) {
        this.newObject = newObject;
    }

    private class NOGenerator implements NewObject<T> {

        @Override
        public T newObject() {
            return null;
        }

        @Override
        public T newObject(TransactionManager tm) throws Exception {
            if(newObject != null) {
                return newObject.newObject(getTransactionManager());
            }
            return getObjectClass().getDeclaredConstructor().newInstance();
        }
    }

    private class InternalChangedListener implements ObjectChangedListener<T> {

        @Override
        public final void updated(T object) {
            refresh(object);
            objectChangedListeners.forEach(ocl -> ocl.updated(object));
        }

        @Override
        public final void inserted(T object) {
            refresh();
            objectChangedListeners.forEach(ocl -> ocl.inserted(object));
            select(object);
        }

        @Override
        public final void deleted(T object) {
            load();
            objectChangedListeners.forEach(ocl -> ocl.deleted(object));
        }
    }

    /**
     * Select all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void selectChildren(T parent, boolean includeGrandChildren) {
        HierarchicalQuery<T, String> q = new HierarchicalQuery<>(null, parent);
        dataProvider.fetchChildren(q).forEach(o -> {
            select(o);
            if(includeGrandChildren) {
                selectChildren(o, true);
            }
        });
    }

    /**
     * Deselect all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void deselectChildren(T parent, boolean includeGrandChildren) {
        HierarchicalQuery<T, String> q = new HierarchicalQuery<>(null, parent);
        dataProvider.fetchChildren(q).forEach(o -> {
            deselect(o);
            if(includeGrandChildren) {
                deselectChildren(o, true);
            }
        });
    }
}
