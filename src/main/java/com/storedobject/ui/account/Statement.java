package com.storedobject.ui.account;

import com.storedobject.core.Account;
import com.storedobject.core.DatePeriod;
import com.storedobject.pdf.PDFReport;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.vaadin.DataForm;

public class Statement extends DataForm {

    private final AccountField<Account> accountField;
    private final DatePeriodField datePeriodField;

    public Statement() {
        super("Account Statement");
        accountField = new AccountField<>("Account");
        addField(accountField);
        datePeriodField = new DatePeriodField("Statement Period");
        addField(datePeriodField);
    }

    public Statement(Account account) {
        this();
        if(account != null) {
            accountField.setValue(account);
            accountField.setReadOnly(true);
        }
    }

    public Statement(Account account, DatePeriod datePeriod) {
        this();
        if(datePeriod != null) {
            datePeriodField.setValue(datePeriod);
        }
    }

    @Override
    protected boolean process() {
        Account account = accountField.getAccount();
        if(account == null) {
            warning("Please select an account");
            return false;
        }
        clearAlerts();
        close();
        Application a = Application.get();
        PDFReport statement = new com.storedobject.report.AccountStatement(a, account, datePeriodField.getValue());
        a.view("Statement - " + account.toDisplay(), statement);
        return true;
    }
}