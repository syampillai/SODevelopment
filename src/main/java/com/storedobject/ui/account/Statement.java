package com.storedobject.ui.account;

import com.storedobject.core.Account;
import com.storedobject.core.DatePeriod;
import com.storedobject.pdf.PDFReport;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;

public class Statement extends DataForm {

    private final ObjectField<Account> accountField;
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
        close();
        Application a = Application.get();
        PDFReport statement = new com.storedobject.report.AccountStatement(a, accountField.getObject(),
                datePeriodField.getValue());
        a.view("Statement", statement);
        return true;
    }
}