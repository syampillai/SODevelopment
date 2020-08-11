package com.storedobject.ui.inventory;

import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class Rebin extends DataForm implements Transactional {

    public Rebin() {
        super("");
    }

    @Override
    protected boolean process() {
        return true;
    }
}
