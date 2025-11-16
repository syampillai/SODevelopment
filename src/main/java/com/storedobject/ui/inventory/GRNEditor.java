package com.storedobject.ui.inventory;

import com.storedobject.core.EditorAction;
import com.storedobject.core.Entity;
import com.storedobject.core.InventoryGRN;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.vaadin.flow.component.HasValue;

public class GRNEditor extends ObjectEditor<InventoryGRN> {

    private ObjectField<Entity> supplierField;

    public GRNEditor() {
        super(InventoryGRN.class, EditorAction.EDIT);
        setCaption("GRN");
        addField("Reference");
        addField("Status", InventoryGRN::getStatus, (grn, v) -> {});
    }

    @Override
    protected void formConstructed() {
        super.formConstructed();
        setFieldReadOnly("Type", "Items.l");
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("Supplier".equals(fieldName)) {
            //noinspection unchecked
            supplierField = (ObjectField<Entity>) field;
        }
        super.customizeField(fieldName, field);
    }

    @Override
    public void setObject(InventoryGRN object, boolean load) {
        super.setObject(object, load);
        if(object != null && supplierField != null) {
            supplierField.setLabel(object.getTypeValue());
        }
    }

    @Override
    public boolean isFieldEditable(String fieldName) {
        if("Date".equals(fieldName)) {
            InventoryGRN grn = getObject();
            return grn == null || grn.getStatus() == 0;
        }
        return super.isFieldEditable(fieldName);
    }
}