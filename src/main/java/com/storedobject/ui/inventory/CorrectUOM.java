package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class CorrectUOM extends DataForm implements Transactional {

    private final ObjectField<InventoryItemType> pnField;
    private final TextField uom = new TextField("Unit of Measurement");
    private final UOMField from = new UOMField("Incorrect Unit"), to = new UOMField("Correct Unit");
    public CorrectUOM() {
        super("Change UoM");
        pnField = new ObjectField<>("Part Number", InventoryItemType.class, true);
        pnField.addValueChangeListener(e -> uom.setValue(e.getValue() == null ? "N/A" :
                pnField.getObject().getUnitOfMeasurement().getUnit().getUnit()));
        addField(pnField, uom, from, to);
        setFieldReadOnly(uom);
        setRequired(pnField);
        setRequired(from);
        setRequired(to);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(from.getValue().getUnit().equals(to.getValue().getUnit())) {
            warning("Not updated... Units are same!");
            return false;
        }
        InventoryItemType pn = pnField.getObject();
        MeasurementUnit fromU = from.getValue().getUnit(), toU = to.getValue().getUnit();
        try {
            pn.validateUoMCorrection(fromU, toU);
        } catch(Exception e) {
            warning(e);
            return false;
        }
        new ActionForm("Item: " + pn.toDisplay() + "\nUnit of measurement will be corrected - From: " + fromU
                + " To: " + toU + "\n Are you sure?", () -> correct(pn, fromU, toU)).execute();
        return true;
    }

    private void correct(InventoryItemType pn, MeasurementUnit from, MeasurementUnit to) {
        try {
            pn.correctUoM(getTransactionManager(), from, to);
            message("Updated...");
        } catch(Exception e) {
            error(e);
        }
    }
}
