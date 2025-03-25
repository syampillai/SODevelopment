package com.storedobject.ui;

import com.storedobject.core.*;

import java.util.function.Consumer;

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

    @SuppressWarnings("unchecked")
    public void view(String caption, String actionName, Consumer<StoredObject> action) {
        if(object == null) {
            if(Id.isNull(id)) {
                return;
            }
            object = StoredObject.get(id);
        }
        if(object == null) {
            return;
        }
        if(object instanceof FileFolder ff) {
            new FileViewer(ff, caption).execute();
            return;
        }
        if(viewer != null) {
            if(viewer.getObjectClass() != object.getClass()) {
                viewer = null;
            }
        }
        if(caption == null || caption.isEmpty() || "_".equals(caption)) {
            caption = StringUtility.makeLabel(object.getClass());
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(object.getClass(), EditorAction.VIEW, caption);
        } else {
            viewer.setCaption(caption);
        }
        viewer.viewObject(object, actionName, action);
    }

    public void view() {
        view((String) null, null, null);
    }

    public void view(String actionName, Consumer<StoredObject> action) {
        view((String) null, actionName, action);
    }

    public void view(String caption) {
        view(caption, null, null);
    }

    public void view(StoredObject object) {
        view(null, object);
    }

    public void view(StoredObject object, String actionName, Consumer<StoredObject> action) {
        view(null, object, actionName, action);
    }

    public void view(String caption, StoredObject object) {
        view(caption, object, null, null);
    }

    public void view(String caption, StoredObject object, String actionName, Consumer<StoredObject> action) {
        setObject(object);
        view(caption, actionName, action);
    }

    public void view(String caption, Id objectId) {
        view(caption, objectId, null, null);
    }

    public void view(String caption, Id objectId, String actionName, Consumer<StoredObject> action) {
        setObject(objectId);
        view(caption, actionName, action);
    }

    public void view(Id objectId) {
        view(null, objectId);
    }

    public void view(Id objectId, String actionName, Consumer<StoredObject> action) {
        view(null, objectId, actionName, action);
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

    @Override
    public String getAlertCaption() {
        return "View";
    }
}