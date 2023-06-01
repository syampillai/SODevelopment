package com.storedobject.ui.inventory;

import com.storedobject.core.EditorAction;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.PackingUnit;
import com.storedobject.ui.MeasurementUnitField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;

public final class PackingUnitEditor extends ObjectEditor<PackingUnit> {

    private Button deploy;

    public PackingUnitEditor() {
        super(PackingUnit.class, EditorAction.ALL & (~EditorAction.DELETE));
    }

    @Override
    protected void createExtraButtons() {
        deploy = new ConfirmButton("Deploy", "system", this);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(deploy);
    }

    @Override
    public void clicked(Component c) {
        if(c == deploy) {
            MeasurementUnit.reload();
            MeasurementUnitField.packingUnitsChanged();
            warning("All 'Packing Units' are updated");
            return;
        }
        super.clicked(c);
    }
}