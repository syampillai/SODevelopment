package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectForestSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storedobject.core.EditorAction.ALL;

public class ObjectForest<T extends StoredObject> extends AbstractObjectForest<T> {

    private Map<Class<?>, List<ObjectChangedListener<?>>> objectChangedListeners = new HashMap<>();
    private Map<Class<?>, InternalChangedListener<?>> internalListeners = new HashMap<>();
    private Map<Class<?>, ObjectEditor<? extends StoredObject>> editors = new HashMap<>();

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
        dataProvider = new ObjectForestSupplier<>(objectClass, null, null, any);
        setDataProvider(dataProvider);
    }

    public <O extends StoredObject> void addObjectChangedListener(Class<O> objectClass, ObjectChangedListener<O> listener) {
        if(listener != null) {
            getListenerList(objectClass, true).add(listener);
        }
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        //noinspection ResultOfMethodCallIgnored
        objectChangedListeners.keySet().stream().anyMatch(k -> objectChangedListeners.get(k).remove(listener));
    }

    private List<ObjectChangedListener<?>> getListenerList(Class<?> objectClass, boolean create) {
        List<ObjectChangedListener<?>> list = objectChangedListeners.get(objectClass);
        if(list == null && create) {
            list = new ArrayList<>();
            internalListeners.put(objectClass, new InternalChangedListener<>());
            objectChangedListeners.put(objectClass, list);
        }
        return list;
    }

    private InternalChangedListener getInternalListener(Class<?> objectClass) {
        getListenerList(objectClass, true);
        return internalListeners.get(objectClass);
    }

    @SuppressWarnings("unchecked")
    public <O extends StoredObject> void setObjectEditor(Class<O> objectClass, ObjectEditor<O> editor) {
        if(objectClass == null) {
            return;
        }
        ObjectEditor<O> ed = (ObjectEditor<O>) editors.get(objectClass);
        if(ed != null) {
            if(editor == ed) {
                return;
            }
            if (ed.executing()) {
                ed.abort();
            }
            InternalChangedListener<O> internalChangedListener = (InternalChangedListener<O>) internalListeners.get(objectClass);
            ed.removeObjectChangedListener(internalChangedListener);
            if(editor != null) {
                editor.addObjectChangedListener(internalChangedListener);
            }
        } else {
            if(editor != null) {
                editor.addObjectChangedListener(getInternalListener(objectClass));
            }
        }
        if(editor == null) {
            if(ed != null) {
                editors.remove(objectClass);
            }
        } else {
            editors.put(objectClass, editor);
        }
    }

    @SuppressWarnings("unchecked")
    public final <O extends StoredObject> ObjectEditor<O> getObjectEditor(Class<O> objectClass) {
        ObjectEditor<O> editor = (ObjectEditor<O>) editors.get(objectClass);
        if(editor == null) {
            editor = createObjectEditor(objectClass);
            if(editor == null) {
                editor = ObjectEditor.create(objectClass, ALL, null);
            }
            editor.addObjectChangedListener(getInternalListener(objectClass));
            editors.put(objectClass, editor);
        }
        return editor;
    }

    protected <O extends StoredObject> ObjectEditor<O> createObjectEditor(@SuppressWarnings("unused") Class<O> objectClass) {
        return null;
    }

    private class InternalChangedListener<O extends StoredObject> implements ObjectChangedListener<O> {

        @Override
        public final void updated(ObjectMasterData<O> object) {
            refresh(object.getObject());
            List<ObjectChangedListener<?>> list = getListenerList(object.getObject().getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).updated(object));
            }
        }

        @Override
        public final void inserted(ObjectMasterData<O> object) {
            dataProvider.close();
            refresh();
            List<ObjectChangedListener<?>> list = getListenerList(object.getObject().getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).inserted(object));
            }
            select(object.getObject());
        }

        @Override
        public final void deleted(ObjectMasterData<O> object) {
            dataProvider.close();
            refresh();
            List<ObjectChangedListener<?>> list = getListenerList(object.getObject().getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).deleted(object));
            }
        }
    }
}