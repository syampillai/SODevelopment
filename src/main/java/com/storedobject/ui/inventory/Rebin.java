package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryStoreBin;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class Rebin extends DataForm implements Transactional {

    private final LocationField locationField = LocationField.create("Store", 0);

    public Rebin() {
        super("Re-bin");
        addField(locationField);
        setRequired(locationField);
    }

    @Override
    protected boolean process() {
        LocateItem locateItem = new LocateItem(true);
        locateItem.setStore(((InventoryStoreBin) locationField.getValue()).getStore());
        close();
        locateItem.execute();
        return true;
    }
}
