package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.ui.util.ObjectForestSupplier;

import java.util.List;

public class ObjectForest<T extends StoredObject> extends BaseObjectForest<T> {

    ObjectForestSupplier.LinkNode currentLinkNode;
    ObjectForestSupplier.LinkObject currentLinkObject;

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
        this(columns, new ObjectForestSupplier<>(objectClass, null, null, any));
    }

    ObjectForest(Iterable<String> columns, AbstractObjectForestSupplier<T, Void> dataProvider) {
        super(columns, dataProvider);
    }

    @Override
    public Object getSelected() {
        Object o = super.getSelected();
        if(o instanceof ObjectForestSupplier.LinkNode) {
            currentLinkNode = (ObjectForestSupplier.LinkNode) o;
            currentLinkObject = null;
        } else if(o instanceof ObjectForestSupplier.LinkObject) {
            currentLinkObject = (ObjectForestSupplier.LinkObject) o;
            currentLinkNode = currentLinkObject.getLinkNode();
        } else {
            currentLinkNode = null;
            currentLinkObject = null;
        }
        return o;
    }

    @Override
    public  <O extends StoredObject> O selected() {
        Object o = getSelected();
        if(o == null) {
            return null;
        }
        if(o instanceof ObjectForestSupplier.LinkObject) {
            o = ((ObjectForestSupplier.LinkObject) o).getObject();
        }
        if(!StoredObject.class.isAssignableFrom(o.getClass())) {
            return null;
        }
        //noinspection unchecked
        return (O)o;
    }

    <O extends StoredObject> ObjectChangedListener<O> createInternalChangedListener() {
        return new InternalChangedListener<>();
    }

    class InternalChangedListener<O extends StoredObject> implements ObjectChangedListener<O> {

        @Override
        public final void updated(O object) {
            Object o = object;
            if(currentLinkObject != null && currentLinkObject.getObject().equals(o)) {
                o = currentLinkObject;
            }
            refresh(o);
            List<ObjectChangedListener<?>> list = getListenerList(object.getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).updated(object));
            }
            select(o);
        }

        @Override
        public final void inserted(O object) {
            if(currentLinkNode == null && currentLinkObject == null) { // Added at root
                //noinspection unchecked
                getDataSupplier().added((T)object);
                refresh();
                select(object);
            } else if(currentLinkNode != null) {
                refresh(currentLinkNode);
                expand(currentLinkNode);
                currentLinkNode.links().stream().filter(lo -> object.getId().equals(lo.getObject().getId())).findAny().ifPresent(ObjectForest.this::select);
            }
            fireInserted(object);
        }

        private void fireInserted(O object) {
            List<ObjectChangedListener<?>> list = getListenerList(object.getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).inserted(object));
            }
        }

        @Override
        public final void deleted(O object) {
            deselectAll();
            if(currentLinkNode == null && currentLinkObject == null) { // Deleted from root
                //noinspection unchecked
                getDataSupplier().deleted((T)object);
                refresh();
            } else if(currentLinkObject != null) {
                refresh(currentLinkNode);
                expand(currentLinkNode);
            }
            fireDeleted(object);
        }

        private void fireDeleted(O object) {
            List<ObjectChangedListener<?>> list = getListenerList(object.getClass(), false);
            if(list != null) {
                //noinspection unchecked
                list.forEach(ocl -> ((ObjectChangedListener<O>) ocl).deleted(object));
            }
        }
    }
}