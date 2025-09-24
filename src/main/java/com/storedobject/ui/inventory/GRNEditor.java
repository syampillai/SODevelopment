package com.storedobject.ui.inventory;

import com.storedobject.core.EditorAction;
import com.storedobject.core.Entity;
import com.storedobject.core.InventoryGRN;
import com.storedobject.core.InventoryGRNItem;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.ObjectLinkField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;

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
    protected void customizeLinkField(ObjectLinkField<?> field) {
        if(field.getFieldName().equals("Items.l")) {
            @SuppressWarnings("unchecked") ItemContextMenu<InventoryGRNItem> m = new ItemContextMenu<>((Grid<InventoryGRNItem>) field.getGrid());
            m.setHideGRNDetails(true);
        }
        super.customizeLinkField(field);
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