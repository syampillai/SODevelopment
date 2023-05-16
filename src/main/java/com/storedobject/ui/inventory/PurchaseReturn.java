package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.GridMenu;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.ArrayList;
import java.util.List;

public final class PurchaseReturn extends DataForm implements Transactional {

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
            purchaseReturn();
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
            purchaseReturn();
            return true;
        }
        message("Please select both the store and the supplier");
        return false;
    }

    private void purchaseReturn() {
        List<GlobalProperty> prClasses = new ArrayList<>();
        StoredObject.list(GlobalProperty.class, "SystemEntity=" + getTransactionManager().getEntity().getId()
                        + " AND Name LIKE 'PURCHASE-RETURN-CLASS%'", "Name").collectAll(prClasses);
        if(prClasses.isEmpty()) {
            StoredObject.list(GlobalProperty.class, "SystemEntity=0 AND Name LIKE 'PURCHASE-RETURN-CLASS%'",
                    "Name").collectAll(prClasses);
            if(prClasses.isEmpty()) {
                error("Unable to determine Purchase Return details, please contact Technical Support!");
                return;
            }
        }
        if(prClasses.size() == 1) {
            purchaseReturn(prClasses.get(0).getValue());
            return;
        }
        GridMenu menu = new GridMenu("Purchase Return");
        prClasses.forEach(gp -> menu.add(gp.getDescription(), () -> purchaseReturn(gp.getValue())));
        menu.setAutoClose(true);
        menu.execute();
    }

    private <M extends MaterialReturned, L extends MaterialReturnedItem> void purchaseReturn(String className) {
        try {
            @SuppressWarnings("unchecked") Class<M> mrClass = (Class<M>) JavaClassLoader.getLogic(className);
            @SuppressWarnings("unchecked") Class<L> mriClass = (Class<L>) JavaClassLoader.getLogic(className
                    + "Item");
            new ReturnMaterial<>(mrClass, mriClass, from, to).execute();
        } catch(Throwable e) {
            error(e);
        }
    }
}
