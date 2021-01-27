package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryPOItem;
import com.storedobject.core.Quantity;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.QuantityField;
import com.vaadin.flow.component.HasValue;

public class POItemEditor<T extends InventoryPOItem> extends ObjectEditor<T> {

    private ObjectField<InventoryItemType> pnField;
    private QuantityField qField;
    private InventoryItemType pn;

    public POItemEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    public POItemEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public POItemEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, actions, caption, null);
    }

    public POItemEditor(String className) throws Exception {
        super(className);
    }

    POItemEditor(Class<T> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }

    public POItemEditor() {
        this(0, null);
    }

    public POItemEditor(int actions) {
        this(actions, null);
    }

    public POItemEditor(int actions, String caption) {
        //noinspection unchecked
        this((Class<T>) InventoryPOItem.class, actions, caption, null);
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("PartNumber".equals(fieldName)) {
            //noinspection unchecked
            pnField = (ObjectField<InventoryItemType>) field;
            pnField.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    pnChanged(pnField.getObject());
                }
            });
        } else if("Quantity".equals(fieldName)) {
            qField = (QuantityField) field;
            qField.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    qChanged(e.getValue());
                }
            });
        }
        super.customizeField(fieldName, field);
    }

    private void qChanged(Quantity q) {
        if(pn == null) {
            return;
        }
        Quantity uom = pn.getUnitOfMeasurement();
        if(q.isZero() || !q.isConvertible(uom)) {
            qField.setValue(Quantity.create(q.getValue(), uom.getUnit()));
            qField.focus();
        }
    }

    private void pnChanged(InventoryItemType pn) {
        clearAlerts();
        this.pn = pn;
        if(pn == null) {
            return;
        }
        if(pn.isBlocked()) {
            warning("Blocked item: " + pn.toDisplay());
            pnField.focus();
            return;
        }
        if(pn.isObsolete()) {
            warning("Obsolete item: " + pn.toDisplay());
            pnField.focus();
            return;
        }
        Quantity q = qField.getValue();
        Quantity uom = pn.getUnitOfMeasurement();
        if(q.isZero() || !q.isConvertible(uom)) {
            qField.setValue(Quantity.create(q.getValue(), uom.getUnit()));
            qField.focus();
        }
    }
}
