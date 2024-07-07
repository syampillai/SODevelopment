package com.storedobject.ui.accounts;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.core.ForeignFinancialSystem;
import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

public class JournalReport extends DataForm implements Transactional {

    private final ObjectField<SystemEntity> entityField = new ObjectField<>("Entity", SystemEntity.class);
    private final ObjectField<ForeignFinancialSystem> originField = new ObjectField<>("Origin", ForeignFinancialSystem.class);
    private final DatePeriodField dpField = new DatePeriodField("Period");
    private final BooleanField includeLedger = new BooleanField("Include Ledger");

    public JournalReport() {
        super("Journal report");
        addField(entityField, originField, dpField, includeLedger);
        setRequired(entityField, true);
        setRequired(dpField, true);
        entityField.setValue(getTransactionManager().getEntity());
        dpField.setValue(DatePeriod.create(DateUtility.today()));
    }

    @Override
    protected boolean process() {
        close();
        Application a = getApplication();
        DatePeriod dp = dpField.getValue();
        com.storedobject.report.JournalReport jr;
        jr = new com.storedobject.report.JournalReport(a, entityField.getObject(), dp);
        jr.setOrigin(originField.getObject());
        jr.setIncludeLedger(includeLedger.getValue());
        a.view("Journal Report, " + dp, jr);
        return true;
    }
}
