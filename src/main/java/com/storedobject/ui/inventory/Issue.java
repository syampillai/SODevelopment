package com.storedobject.ui.inventory;

import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class Issue extends DataForm implements Transactional {

    public Issue() {
        super("");
    }

    @Override
    protected boolean process() {
        return true;
    }
}
