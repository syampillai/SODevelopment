package com.storedobject.ui.account;

import com.storedobject.core.Account;
import com.storedobject.report.AccountStatement;
import com.storedobject.core.DatePeriod;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class AccountStatementReport extends DataForm {

    private ObjectField<Account> account;
    private DateField fromDate, toDate;

    public AccountStatementReport() {
        super("Statement of Account", "View", "Quit");
    }

    @Override
    protected void buildFields() {
        form.addField(account = new ObjectField<>("Account", Account.class, true));
        form.addField(fromDate = new DateField("From Date"));
        form.addField(toDate = new DateField("To Date"));
    }

    @Override
    protected boolean process() {
        Account a = account.getObject();
        ((Application)getApplication()).view(new AccountStatement(getApplication(), a, new DatePeriod(fromDate.getValue(), toDate.getValue())));
        return true;
    }
}