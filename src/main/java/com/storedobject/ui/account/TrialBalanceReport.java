package com.storedobject.ui.account;

import com.storedobject.core.SystemEntity;
import com.storedobject.report.TrialBalance;
import com.storedobject.ui.Application;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class TrialBalanceReport extends DataForm implements Transactional {

    private DateField date;

    public TrialBalanceReport() {
        super("Trial Balance As On", "View", "Quit");
    }

    @Override
    protected void buildFields() {
        form.addField(date = new DateField("Date"));
    }

    @Override
    protected boolean process() {
        SystemEntity se = getTransactionManager().getEntity();
        if(se == null) {
            warning("Your login doesn't belong to any entities in the system!");
        } else {
            ((Application)getApplication()).view(new TrialBalance(se, date.getValue()));
        }
        return true;
    }
}
