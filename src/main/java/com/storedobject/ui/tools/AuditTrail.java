package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.AuditTrailConfiguration;
import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectHistoryGrid;
import com.storedobject.vaadin.DataForm;

public class AuditTrail extends DataForm {

    private ObjectGetField<AuditTrailConfiguration> atcField;
    private AuditTrailConfiguration atc;

    public AuditTrail() {
        super("Audit Trail");
    }

    @Override
    protected void buildFields() {
        atcField = new ObjectGetField<>("Select Data", AuditTrailConfiguration.class);
        atcField.setFilter("Menu", false);
        addField(atcField);
    }

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
        close();
        ObjectBrowser<?> obx = new ObjectBrowser<>(objectClass, StoredObjectUtility.browseColumns(objectClass),
                EditorAction.SEARCH, StringList.create(atc.getSearchFields()));
        @SuppressWarnings("unchecked") ObjectBrowser<StoredObject> ob = (ObjectBrowser<StoredObject>) obx;
        ob.search(null, object -> new ObjectHistoryGrid<>(object, atc).executeAll());
        return true;
    }
}
