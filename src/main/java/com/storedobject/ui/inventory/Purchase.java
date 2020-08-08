package com.storedobject.ui.inventory;

import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class Purchase extends DataForm implements Transactional {

    public Purchase() {
        super("");
    }

    @Override
    protected boolean process() {
        return false;
    }
}
