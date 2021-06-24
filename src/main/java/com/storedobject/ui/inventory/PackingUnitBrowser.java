package com.storedobject.ui.inventory;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;

public class PackingUnitBrowser extends ObjectBrowser<PackingUnit> {

    private Button deploy;

    public PackingUnitBrowser() {
        super(PackingUnit.class);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns) {
        super(PackingUnit.class, browseColumns);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(PackingUnit.class, browseColumns, filterColumns);
    }

    public PackingUnitBrowser(int actions) {
        super(PackingUnit.class, actions);
    }

    public PackingUnitBrowser(int actions, String caption) {
        super(PackingUnit.class, actions, caption);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns, int actions) {
        super(PackingUnit.class, browseColumns, actions);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(PackingUnit.class, browseColumns, actions, filterColumns);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns, int actions, String caption) {
        super(PackingUnit.class, browseColumns, actions, caption);
    }

    public PackingUnitBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(PackingUnit.class, browseColumns, actions, filterColumns, caption);
    }

    public PackingUnitBrowser(String className) throws Exception {
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
