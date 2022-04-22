package com.storedobject.ui;

import com.storedobject.core.StoredObject;

import java.util.List;

public class ObjectForest<T extends StoredObject> extends BaseObjectForest<T> {

    com.storedobject.core.ObjectForest.LinkNode currentLinkNode;
    com.storedobject.core.ObjectForest.LinkObject currentLinkObject;

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
        this(false, false, objectClass, columns, any);
    }

    ObjectForest(boolean large, boolean forViewing, Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(large, forViewing, objectClass, columns, any);
    }

    @Override
    public Object getSelected() {
        Object o = super.getSelected();
        if(o instanceof com.storedobject.core.ObjectForest.LinkNode) {
            currentLinkNode = (com.storedobject.core.ObjectForest.LinkNode) o;
            currentLinkObject = null;
        } else if(o instanceof com.storedobject.core.ObjectForest.LinkObject) {
            currentLinkObject = (com.storedobject.core.ObjectForest.LinkObject) o;
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
        if(o instanceof com.storedobject.core.ObjectForest.LinkObject) {
            o = ((com.storedobject.core.ObjectForest.LinkObject) o).getObject();
        }
        if(!StoredObject.class.isAssignableFrom(o.getClass())) {
            return null;
        }
        //noinspection unchecked
        return (O)o;
    }

    @Override
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
                load();
                select(object);
            } else if(currentLinkNode != null) {
                refreshCurrentNode(object);
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
                load();
            } else if(currentLinkObject != null) {
                refreshCurrentNode(null);
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

    /**
     * Refresh all items under the current node and select the object that is passed.
     * <p>Note: If no object was selected earlier nothing happens.</p>
     *
     * @param select Object to be selected after refreshing. If this object is null or, it is not under the
     *               current node, it will be ignored.
     */
    public void refreshCurrentNode(StoredObject select) {
        if(currentLinkNode != null) {
            refresh(currentLinkNode, true);
            expand(currentLinkNode);
            if(select != null) {
                currentLinkNode.links(true).stream().filter(lo -> select.getId().equals(lo.getObject().getId()))
                        .findAny().ifPresent(this::select);
            }
        }
    }
}