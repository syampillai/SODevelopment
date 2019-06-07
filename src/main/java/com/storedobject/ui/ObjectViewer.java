package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ObjectViewer extends Executor implements ObjectSetter, AlertHandler {

    public ObjectViewer(Application a) {
        super(a);
    }

    @Override
    public void execute() {
        view();
    }

    @Override
    public void setObject(StoredObject object) {
    }

    @Override
    public void setObject(Id objectId) {
    }

    public void view() {
        view((String)null);
    }

    public void view(String caption) {
    }

    public void view(StoredObject object) {
        view(null, object);
    }

    public void view(String caption, StoredObject object) {
    }

    public void view(String caption, Id objectId) {
    }

    public void view(Id objectId) {
        view(null, objectId);
    }

    public boolean executing() {
        return false;
    }

    public void close() {
    }

    @Override
    public void handleAlert(StoredObject object) {
        view(object);
    }

    @Override
    public String getAlertButtonCaption() {
        return "View";
    }

    @Override
    public Icon getAlertButtonIcon() {
        return new Icon(VaadinIcon.EYE);
    }
}