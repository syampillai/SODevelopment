package com.storedobject.ui.inventory;

import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class DataPickup extends DataForm implements Transactional {

    public DataPickup() {
        super("");
    }

    @Override
    protected boolean process() {
        return false;
    }
}