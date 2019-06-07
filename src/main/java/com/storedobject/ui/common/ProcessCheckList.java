package com.storedobject.ui.common;

import com.storedobject.core.AbstractCheckList;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.HasComponents;

public class ProcessCheckList extends DataForm implements Transactional {

    public ProcessCheckList(AbstractCheckList checkList) {
        super("Check List - " + checkList.getName());
    }

    @Override
    protected HasComponents createFieldContainer() {
        return null;
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected boolean process() {
        return true;
    }
}