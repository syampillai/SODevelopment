package com.storedobject.ui.inventory;

import com.storedobject.core.EditorAction;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;

public class PackingUnitBrowser extends ObjectBrowser<PackingUnit> {

    private Button deploy;

    public PackingUnitBrowser() {
        super(PackingUnit.class, EditorAction.ALL & (~EditorAction.DELETE));
    }

    public PackingUnitBrowser(String className) {
        this();
    }

    @Override
    protected void createExtraButtons() {
        deploy = new ConfirmButton("Deploy", "system", e -> deploy());
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(deploy);
    }

    private void deploy() {
        MeasurementUnit.reload();
        warning("All 'Packing Units' are updated");
    }

    @Override
    protected ObjectEditor<PackingUnit> createObjectEditor() {
        return new PackingUnitEditor();
    }
}
