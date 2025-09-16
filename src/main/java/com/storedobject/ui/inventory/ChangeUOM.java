package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.Quantity;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CompoundField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.HasValue;

import java.util.ArrayList;
import java.util.List;

public class ChangeUOM extends DataForm implements Transactional {

    private final List<HasValue<?, Quantity>> qFields = new ArrayList<>();
    private final ObjectField<InventoryItemType> pnField =
            new ObjectField<>("P/N", InventoryItemType.class, true);
    private final TextField currentUOM = new TextField();
    private final UOMField uomField = new UOMField("Unit of Measurement"),
            uoiField = new UOMField("Unit of Issue");

    public ChangeUOM() {
        super("Change UOM");
        currentUOM.setValue("N/A");
        addField(pnField, new CompoundField("Current Unit of Measurement", currentUOM), uomField, uoiField);
        qFields.add(uoiField);
        setRequired(pnField);
        setRequired(uomField);
        setRequired(uoiField);
        setFieldReadOnly(currentUOM);
        pnField.addValueChangeListener(e -> pnChanged());
        qFields.forEach(f -> f.addValueChangeListener(e -> adjustUOM(f)));
        uomField.addValueChangeListener(e -> adjustUOM());
    }

    private void pnChanged() {
        InventoryItemType pn = pnField.getObject();
        if(pn == null) {
            currentUOM.setValue("N/A");
            return;
        }
        currentUOM.setValue(pn.getUnitOfMeasurement().getUnit().getUnit());
        uomField.setValue(pn.getUnitOfMeasurement());
        uoiField.setValue(pn.getUnitOfIssue());
    }

    @Override
    protected boolean process() {
        clearAlerts();
        try {
            pnField.getObject().changeUnitOfMeasurement(getTransactionManager(), uomField.getValue(), uoiField.getValue());
            message("Updated successfully");
        } catch(Exception e) {
            warning(e);
        }
        return false;
    }

    private void adjustUOM(HasValue<?, Quantity> field) {
        adjustUOM(field, uomField.getValue());
    }

    private void adjustUOM(HasValue<?, Quantity> field, Quantity u) {
        Quantity q = field.getValue();
        if(!q.isCompatible(u) || (q.isZero() && !(field instanceof UOMField))) {
            field.setValue(Quantity.create(q.getValue(), u.getUnit()));
        }
    }

    private void adjustUOM() {
        Quantity u = uomField.getValue();
        qFields.forEach(f -> adjustUOM(f, u));
    }
}
