package com.storedobject.ui.inventory;

import com.storedobject.core.PackingUnit;
import com.storedobject.ui.ObjectBrowser;

public class PackingUnitBrowser extends ObjectBrowser<PackingUnit> {

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
}
