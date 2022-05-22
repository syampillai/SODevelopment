package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

public final class PurchaseReturn extends DataForm {

    private final LocationField fromField;
    private final LocationField toField;
    private InventoryLocation from, to;

    public PurchaseReturn() {
        this("");
    }

    public PurchaseReturn(String fromStore) {
        this(fromStore, null, null);
    }

    public PurchaseReturn(InventoryStore fromStore) {
        this(fromStore, null);
    }

    public PurchaseReturn(InventoryStore fromStore, Entity supplier) {
        this(null, fromStore, supplier);
    }

    private PurchaseReturn(String fromStore, InventoryStore from, Entity supplier) {
        super("Purchase Return");
        if(from == null && fromStore != null) {
            this.from = LocationField.getLocation(fromStore, 0);
        } else {
            this.from = from == null ? null : from.getStoreBin();
        }
        this.to = supplier == null ? null : StoredObject.list(InventoryVirtualLocation.class, "Type=1")
                .filter(loc -> loc.getEntityId().equals(supplier.getId())).findFirst();
        if(this.from != null && this.to != null) {
            fromField = null;
            toField = null;
        } else {
            if(this.from == null) {
                fromField = LocationField.create("From Store", 0);
            } else {
                fromField = LocationField.create("From Store", this.from);
                setFieldReadOnly(fromField);
            }
            if(this.to == null) {
                toField = LocationField.create("Return to", 1);
            } else {
                toField = LocationField.create("Return to", this.to);
                setFieldReadOnly(toField);
            }
            addField(fromField, toField);
        }
    }


    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(from != null && to != null) {
            returnMaterial();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        from = fromField.getObject();
        to = toField.getObject();
        if(from != null && to != null) {
            close();
            returnMaterial();
            return true;
        }
        message("Please select both the store and the supplier");
        return false;
    }

    private void returnMaterial() {
        new ReturnMaterial(from, to).execute();
    }
}
