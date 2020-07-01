package com.storedobject.ui.account;

import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

import java.sql.Date;

public class TrialBalance extends DataForm implements Transactional {

    private DateField date;

    public TrialBalance() {
        super("");
    }

    public TrialBalance(SystemEntity systemEntity) {
        this();
    }

    public TrialBalance(SystemEntity systemEntity, Date asOn) {
        this();
    }

    @Override
    protected boolean process() {
        return true;
    }
}
