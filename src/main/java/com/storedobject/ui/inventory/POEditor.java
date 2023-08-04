package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.ActionForm;
import com.vaadin.flow.component.HasValue;

import java.util.Collection;

public class POEditor<T extends InventoryPO> extends ObjectEditor<T> {

    private ObjectField<Entity> supplierField;
    InventoryStore store;

    public POEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    public POEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public POEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, actions, caption, null);
    }

    public POEditor(String className) throws Exception {
        super(className);
    }

    POEditor(Class<T> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
        Collection<Entity> suppliers = GRN.suppliers(grnType());
        if(suppliers != null) {
            if(suppliers.isEmpty()) {
                throw new SORuntimeException("No suppliers found!");
            }
            supplierField = new ObjectField<>(suppliers);
        }
        addField("Reference");
        addConstructedListener(o -> {
            if(getObjectClass() == InventoryPO.class) {
                setCaption("Purchase Order");
            }
        });
    }

    @Override
    protected String getActionPrefix() {
        return "PO";
    }

    private int grnType() {
        T po = createNewInstance();
        return po == null ? 0 : po.getGRNType();
    }

    @Override
    public boolean canEdit() {
        return canEdit(getObject()) && super.canEdit();
    }

    public boolean canEdit(T po) {
        if(po != null && po.getStatus() > 0) {
            warning("Can't edit with status = '" + po.getStatusValue() + "'");
            return false;
        }
        return true;
    }

    @Override
    public boolean canDelete() {
        return canDelete(getObject()) && super.canDelete();
    }

    public boolean canDelete(InventoryPO po) {
        if(po != null) {
            int status = po.getStatus();
            if(status > 0 && status < 4) {
                warning("Can't delete with status = '" + po.getStatusValue() + "'");
                return false;
            }
        }
        return true;
    }

    @Override
    public void doDelete() {
        T po = getObject();
        if(deletePO(po, super::doDelete)) {
            super.doDelete();
        }
    }

    public boolean deletePO(T po, Runnable how) {
        switch(po.getStatus()) {
            case 0 -> {
            }
            case 3 -> { // Closed entry
                new ActionForm(
                        "Entry will be removed but this will not affect the items already received via this order.\nAre you sure?",
                        how).
                        execute();
                return false;
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if(supplierField != null && "Supplier".equals(fieldName)) {
            supplierField.setLabel(label);
            return supplierField;
        }
        return super.createField(fieldName, label);
    }

    @Override
    public ObjectSearcher<T> getSearcher() {
        return super.getSearcher();
    }

    @Override
    protected void anchorsSet() throws Exception {
        super.anchorsSet();
        store = (InventoryStore) ((ObjectField<?>)getAnchorField("Store")).getObject();
    }

    @Override
    protected void saveObject(Transaction t, T object) throws Exception {
        if(object.getStatus() == 0 && !object.getApprovalRequired()) {
            object.setApprovalRequired(true);
        }
        super.saveObject(t, object);
    }
}
