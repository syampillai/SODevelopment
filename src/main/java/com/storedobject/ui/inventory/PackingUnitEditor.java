package com.storedobject.ui.inventory;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;

public class PackingUnitEditor extends ObjectEditor<PackingUnit> {

    private Button deploy;

    public PackingUnitEditor() {
        super(PackingUnit.class);
    }

    public PackingUnitEditor(int actions) {
        super(PackingUnit.class, actions);
    }

    public PackingUnitEditor(int actions, String caption) {
        super(PackingUnit.class, actions, caption);
    }

    public PackingUnitEditor(String className) throws Exception {
        super(className);
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
            warning("All 'Packing Units' are updated");
            return;
        }
        super.clicked(c);
    }
}