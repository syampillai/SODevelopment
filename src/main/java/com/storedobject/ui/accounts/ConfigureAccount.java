package com.storedobject.ui.accounts;

import com.storedobject.core.Account;
import com.storedobject.core.Money;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;

import java.util.Currency;

public class ConfigureAccount extends DataForm implements Transactional {

    private final AccountField<Account> account = new AccountField<>("Account");
    private final MoneyField openingBalance = new MoneyField("Opening Balance");
    private final MoneyField openingBalanceLC;
    private final MoneyField balance = new MoneyField("Opening Balance");
    private final MoneyField balanceLC;
    private final Currency currency = getTransactionManager().getCurrency();

    public ConfigureAccount() {
        super("Account Configuration");
        openingBalanceLC = new MoneyField("Opening Balance in " + currency.getCurrencyCode());
        openingBalanceLC.setAllowedCurrencies(currency);
        balanceLC = new MoneyField("Opening Balance in " + currency.getCurrencyCode());
        balanceLC.setAllowedCurrencies(currency);
        setFieldReadOnly(true, balance, balanceLC);
        addField(account, openingBalance, openingBalanceLC, balance, balanceLC);
        setRequired(account);
        account.addValueChangeListener(e -> {
            Account a = account.getAccount();
            if(a == null) {
                Money m = new Money(currency);
                openingBalance.setValue(m);
                openingBalanceLC.setValue(m);
                balance.setValue(m);
                balanceLC.setValue(m);
            } else {
                a.refresh();
                openingBalance.setValue(a.getOpeningBalance());
                openingBalanceLC.setValue(a.getLocalCurrencyOpeningBalance());
                balance.setValue(a.getBalance());
                balanceLC.setValue(a.getLocalCurrencyBalance());
            }
        });
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Proceed");
    }

    @Override
    protected boolean process() {
        clearAlerts();
        Account a = account.getAccount();
        Money m = openingBalance.getValue(), mLC = openingBalanceLC.getValue();
        if(m.getCurrency() == a.getCurrency()) {
            set(a, m, mLC);
        } else {
            new ActionForm("Change Currency",
                    "Currency of the account will be changed from "
                            + a.getCurrency().getCurrencyCode() + " to " + m.getCurrency().getCurrencyCode()
                            + ".\nAre you sure?",
                    () -> set(a, m, mLC)).execute();
        }
        return false;
    }

    private void set(Account a, Money m, Money mLC) {
        Currency c = a.getCurrency();
        try {
            a.setOpeningBalance(getTransactionManager(), m, mLC);
            message("Opening balance updated successfully");
            if(c != a.getCurrency()) {
                message("Currency changed from " + c.getCurrencyCode() + " to " + a.getCurrency().getCurrencyCode());
                close();
            }
        } catch (Exception e) {
            warning(e);
        }
    }
}
