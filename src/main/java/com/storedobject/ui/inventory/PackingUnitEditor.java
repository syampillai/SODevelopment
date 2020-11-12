package com.storedobject.ui.inventory;

import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectEditor;

public class PackingUnitEditor extends ObjectEditor<PackingUnit> {

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
}