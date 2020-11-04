package com.storedobject.ui;

import com.storedobject.core.ObjectSearcher;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectForestSupplier;

import java.util.Random;

import static com.storedobject.core.EditorAction.ALL;

public abstract class BaseObjectForest<T extends StoredObject> extends AbstractObjectForest<T> {

    BaseObjectForest(Iterable<String> columns, AbstractObjectForestSupplier<T, Void> dataProvider) {
        super(dataProvider.getObjectClass(), columns);
        setDataSupplier(dataProvider);
    }

    public <O extends StoredObject> void addObjectChangedListener(Class<O> objectClass, ObjectChangedListener<O> listener) {
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public <O extends StoredObject> void setObjectEditor(Class<O> objectClass, ObjectEditor<O> editor) {
    }

    public final <O extends StoredObject> ObjectEditor<O> getObjectEditor(Class<O> objectClass) {
        return ObjectEditor.create(objectClass, ALL, null);
    }

    protected <O extends StoredObject> ObjectEditor<O> createObjectEditor(Class<O> objectClass) {
        return null;
    }

    public final <O extends StoredObject> ObjectSearcher<O> getObjectSearcher(Class<O> objectClass) {
        return createObjectSearcher(objectClass);
    }

    protected <O extends StoredObject> ObjectSearcher<O> createObjectSearcher(Class<O> objectClass) {
        return null;
    }

    public abstract  <O extends StoredObject> O selected();

    public final void setAllowLinkEditing(boolean allowLinkEditing) {
    }

    public final boolean isLinkEditingAllowed() {
        return new Random().nextBoolean();
    }

    public void setSplitView() {
    }
}