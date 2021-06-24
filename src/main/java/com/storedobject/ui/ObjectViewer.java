package com.storedobject.ui;

import com.storedobject.core.*;

public class ObjectViewer extends Executor implements ObjectSetter<StoredObject>, AlertHandler {

    private StoredObject object;
    private Id id;
    @SuppressWarnings("rawtypes")
    private ObjectEditor viewer;

    public ObjectViewer(Application a) {
        super(a);
    }

    @Override
    public void execute() {
        view();
    }

    @Override
    public void setObject(StoredObject object) {
        this.object = object;
    }

    @Override
    public void setObject(Id objectId) {
        this.id = objectId;
        this.object = null;
    }

    public void view() {
        view((String)null);
    }

    public void view(String caption) {
        if(object == null) {
            if(Id.isNull(id)) {
                return;
            }
            object = StoredObject.get(id);
        }
        if(object == null) {
            return;
        }
        if(viewer != null) {
            if(viewer.getObjectClass() != object.getClass()) {
                viewer = null;
            }
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(object.getClass(), EditorAction.VIEW, caption == null ? "_" : caption);
        } else {
            if(caption != null) {
                viewer.setCaption("_".equals(caption) ? StringUtility.makeLabel(object.getClass()) : caption);
            }
        }
        //noinspection unchecked
        viewer.viewObject(object);
    }

    public void view(StoredObject object) {
        view(null, object);
    }

    public void view(String caption, StoredObject object) {
        setObject(object);
        view(caption);
    }

    public void view(String caption, Id objectId) {
        setObject(objectId);
        view(caption);
    }

    public void view(Id objectId) {
        view(null, objectId);
    }

    public boolean executing() {
        return viewer != null && viewer.executing();
    }

    public void close() {
        if(viewer != null) {
            viewer.close();
            viewer = null;
            object = null;
        }
    }

    @Override
    public void handleAlert(StoredObject object) {
        view(object);
    }

    @Override
    public String getAlertIcon() {
        return "vaadin:eye";
    }
}