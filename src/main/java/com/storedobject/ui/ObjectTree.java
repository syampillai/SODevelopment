package com.storedobject.ui;

import com.storedobject.core.NewObject;
import com.storedobject.core.ObjectsSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectDataLoadedListener;
import com.storedobject.ui.util.ObjectTreeListProvider;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ObjectTree<T extends StoredObject> extends DataTreeGrid<T> implements Transactional, ObjectsSetter<T> {

    ObjectTreeListProvider<T> dataProvider;

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
    }

    @Override
    public Class<T> getObjectClass() {
        return super.getDataClass();
    }

    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        return () -> {};
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        dataProvider.setItemLabelGenerator(itemLabelGenerator);
    }

    public void load() {
    }

    public void load(Iterable<? extends StoredObject> list) {
    }

    public void load(T root) {
    }

    public void loaded() {
    }

    public void clear() {
    }

    @Override
    public void setObject(T object) {
    }

    @Override
    public void setObjects(Iterable<T> objects) {
    }

    public T getRoot() {
        return null;
    }

    public List<T> listRoots() {
        return null;
    }

    public void addObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
    }

    public final ObjectEditor<T> getObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    public void setNewObjectGenerator(NewObject<T> newObject) {
    }

    public void setSplitView() {
    }
}
