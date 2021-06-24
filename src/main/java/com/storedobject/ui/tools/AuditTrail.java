package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectHistoryGrid;
import com.storedobject.vaadin.DataForm;

public class AuditTrail extends DataForm implements ObjectSetter<StoredObject> {

    private ObjectGetField<AuditTrailConfiguration> atcField;
    private AuditTrailConfiguration atc;

    public AuditTrail() {
        super("Audit Trail");
    }

    @Override
    protected void buildFields() {
        atcField = new ObjectGetField<>("Select Data", AuditTrailConfiguration.class);
        addField(atcField);
    }

    @SuppressWarnings({"resource" })
    @Override
    protected boolean process() {
        atc = atcField.getObject();
        Class<? extends StoredObject> objectClass;
        try {
            objectClass = atc.getObjectClass();
        } catch (Exception e) {
            error(e);
            return true;
        }
        ObjectBrowser<?> obx = new ObjectBrowser<>(objectClass, StoredObjectUtility.browseColumns(objectClass), EditorAction.SEARCH, StringList.create(atc.getSearchFields()));
        @SuppressWarnings("unchecked") ObjectBrowser<StoredObject> ob = (ObjectBrowser<StoredObject>) obx;
        ob.search(null, this);
        return true;
    }

    @Override
    public void setObject(StoredObject object) {
        new ObjectHistoryGrid<>(object, atc).executeAll();
    }

    @Override
    public void setObject(Id objectId) {
        setObject(StoredObject.get(objectId));
    }
}
