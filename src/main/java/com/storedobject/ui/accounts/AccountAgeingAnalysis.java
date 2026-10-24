package com.storedobject.ui.accounts;

import com.storedobject.accounts.EntityAccount;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;

public class AccountAgeingAnalysis extends DataForm implements Transactional {

    private final IntegerField bucketDays = new IntegerField("Ageing Bucket in Days");
    private final BooleanField printAccountNumbers = new BooleanField("Print Account Numbers");
    private final BooleanField printPartyShortNames = new BooleanField("Print Party Short Names");
    private final RadioChoiceField type = new RadioChoiceField("Type", new String[] { "Receivables", "Payables" });

    public AccountAgeingAnalysis() {
        super("Account Ageing Analysis");
        bucketDays.setValue(EntityAccount.getAgeingBucketDays(getTransactionManager().getEntity()));
        addField(bucketDays, type, printAccountNumbers, printPartyShortNames);
    }

    @Override
    protected boolean process() {
        close();
        //noinspection resource
        new Report().execute();
        return true;
    }

    protected ObjectIterator<EntityAccount> getAccounts(String filter) {
        return StoredObject.list(EntityAccount.class, filter, true);
    }

    private class Report extends com.storedobject.report.AccountAgeingAnalysis {

        public Report() {
            super(Application.get(), type.getValue() == 0, bucketDays.getValue());
        }

        @Override
        protected ObjectIterator<EntityAccount> getAccounts(String filter) {
            return AccountAgeingAnalysis.this.getAccounts(filter);
        }

        @Override
        protected boolean printAccountNumber() {
            return printAccountNumbers.getValue();
        }

        @Override
        protected boolean printEntityShortName() {
            return printPartyShortNames.getValue();
        }
    }
}
