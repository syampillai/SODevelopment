package com.storedobject.ui.inventory;

import com.storedobject.core.EditorAction;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;

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

    @Override
    protected ObjectEditor<PackingUnit> createObjectEditor() {
        return new PackingUnitEditor();
    }
}
