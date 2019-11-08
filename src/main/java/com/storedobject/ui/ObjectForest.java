package com.storedobject.ui;

import com.storedobject.core.ObjectSearcher;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectForestSupplier;

public class ObjectForest<T extends StoredObject> extends AbstractObjectForest<T> {

    ObjectForestSupplier.LinkNode currentLinkNode;

    public ObjectForest(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectForest(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectForest(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectForest(Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(objectClass, columns);
    }

    public <O extends StoredObject> void addObjectChangedListener(Class<O> objectClass, ObjectChangedListener<O> listener) {
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    public <O extends StoredObject> void setObjectEditor(Class<O> objectClass, ObjectEditor<O> editor) {
    }

    public final <O extends StoredObject> ObjectEditor<O> getObjectEditor(Class<O> objectClass) {
        return null;
    }

    protected <O extends StoredObject> ObjectEditor<O> createObjectEditor(@SuppressWarnings("unused") Class<O> objectClass) {
        return null;
    }

    /**
     * Allow/disallow link editing.
     *
     * @param allowLinkEditing True if link editing needs to be allowed
     */
    public final void setAllowLinkEditing(boolean allowLinkEditing) {
    }

    /**
     * Check whether link editing is allowed or not.
     *
     * @return True if link editing is allowed.
     */
    public final boolean isLinkEditingAllowed() {
        return false;
    }

    public final <O extends StoredObject> ObjectSearcher<O> getObjectSearcher(Class<O> objectClass) {
        return null;
    }

    protected <O extends StoredObject> ObjectSearcher<O> createObjectSearcher(@SuppressWarnings("unused") Class<O> objectClass) {
        return null;
    }
}