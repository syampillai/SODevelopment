package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.DataLoadedListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectTree<T extends StoredObject> extends DataTreeGrid<T> implements ObjectGridData<T, T> {

    private final List<DataLoadedListener> dataLoadedListeners = new ArrayList<>();
    private ObjectEditor<T> editor;
    private InternalChangedListener internalChangedListener;
    private NOGenerator noGenerator;
    private NewObject<T> newObject;
    private Logic logic;
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
        this(false, objectClass, columns, any, linkType);
    }

    public ObjectTree(boolean large, Class<T> objectClass, Iterable<String> columns, boolean any, int linkType) {
        super(objectClass, columns);
        setDataProvider(new ObjectTreeProvider<>(new com.storedobject.core.ObjectTree<>(large, linkType, objectClass, any)));
        getDataProvider().addDataLoadedListener(this::loadedInt);
    }

    @Override
    public final ObjectTreeProvider<T> getDelegatedLoader() {
        return getDataProvider();
    }

    @Override
    public final ObjectTreeProvider<T> getDataProvider() {
        return (ObjectTreeProvider<T>) super.getDataProvider();
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getDataClass();
    }

    public Registration addObjectDataLoadedListener(DataLoadedListener listener) {
        dataLoadedListeners.add(listener);
        return () -> dataLoadedListeners.remove(listener);
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        getDataProvider().setItemLabelGenerator(itemLabelGenerator);
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

    public void loaded() {
    }

    private void loadedInt() {
        loaded();
        dataLoadedListeners.forEach(DataLoadedListener::dataLoaded);
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
        load(ObjectIterator.create(objects.iterator()));
    }

    @Override
    public void setObjectSetter(ObjectSetter<T> setter) {
    }

    public T getRoot() {
        List<T> roots = listRoots();
        return roots.size() == 1 ? roots.get(0) : null;
    }

    public List<T> listRoots() {
        return getDataProvider().getRoots();
    }

    @SafeVarargs
    public final void setRoots(T... roots) {
        setRoots(ObjectIterator.create(roots));
    }

    public final void setRoots(ObjectIterator<T> roots) {
        load(roots);
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
        }

        @Override
        public final void inserted(T object) {
            refresh();
            select(object);
        }

        @Override
        public final void deleted(T object) {
            load();
        }
    }

    /**
     * Select all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void selectChildren(T parent, boolean includeGrandChildren) {
        visitChildren(parent, this::select, includeGrandChildren);
    }

    /**
     * Deselect all children of the parent item.
     *
     * @param parent Parent item.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void deselectChildren(T parent, boolean includeGrandChildren) {
        visitChildren(parent, this::deselect, includeGrandChildren);
    }

    /**
     * Visit the children of the parent item.
     *
     * @param parent Parent item.
     * @param consumer Consumer to consume the visit purpose.
     * @param includeGrandChildren Whether recursively include grand-children or not.
     */
    public void visitChildren(T parent, Consumer<T> consumer, boolean includeGrandChildren) {
        getDataProvider().visitChildren(parent, consumer, includeGrandChildren);
    }
}
