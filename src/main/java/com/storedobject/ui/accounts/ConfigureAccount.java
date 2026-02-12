package com.storedobject.ui.accounts;

import com.storedobject.core.Account;
import com.storedobject.core.Money;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

import java.util.Currency;

public class ConfigureAccount extends DataForm implements Transactional {

    private final AccountField<Account> account = new AccountField<>("Account");
    private final TextField number = new TextField("Account Number");
    private final MoneyField openingBalance = new MoneyField("Opening Balance");
    private final MoneyField openingBalanceLC;
    private final MoneyField balance = new MoneyField("Current Balance");
    private final MoneyField balanceLC;
    private final Currency currency = getTransactionManager().getCurrency();
    private Account a;

    public ConfigureAccount() {
        super("Account Configuration");
        number.addValueChangeListener(e -> numberChanged());
        openingBalance.addValueChangeListener(e -> amountChanged());
        openingBalanceLC = new MoneyField("Opening Balance in " + currency.getCurrencyCode() + " (Accounting Currency)");
        openingBalanceLC.setAllowedCurrencies(currency);
        openingBalanceLC.addValueChangeListener(e -> amountChanged());
        balanceLC = new MoneyField("Current Balance in " + currency.getCurrencyCode() + " (Accounting Currency)");
        balanceLC.setAllowedCurrencies(currency);
        setFieldReadOnly(true, balance, balanceLC);
        addField(account, number, openingBalance, openingBalanceLC, balance, balanceLC);
        setRequired(account);
        setRequired(number);
        account.addValueChangeListener(e -> setAccount());
    }

    private void setAccount() {
        setFieldReadOnly(false, number, openingBalance, openingBalanceLC);
        a = account.getAccount();
        if(a == null) {
            number.setValue("");
            Money m = new Money(currency);
            openingBalance.setValue(m);
            openingBalanceLC.setValue(m);
            balance.setValue(m);
            balanceLC.setValue(m);
        } else {
            a.refresh();
            number.setValue(a.getNumber());
            openingBalance.setValue(a.getOpeningBalance());
            openingBalanceLC.setValue(a.getLocalCurrencyOpeningBalance());
            balance.setValue(a.getBalance());
            balanceLC.setValue(a.getLocalCurrencyBalance());
        }
        number.setHelperText("");
        openingBalance.setHelperText("");
        openingBalanceLC.setHelperText("");
    }

    private void numberChanged() {
        clearAlerts();
        if(a == null) return;
        openingBalance.setValue(a.getOpeningBalance());
        openingBalanceLC.setValue(a.getLocalCurrencyOpeningBalance());
        String n = StoredObject.toCode(number.getValue());
        if(n.isBlank()) {
            number.setValue(a.getNumber());
            number.focus();
            return;
        }
        boolean same = n.equals(a.getNumber());
        if(same) {
            number.setHelperText("");
        } else {
            number.setHelperText("Changing from " + a.getNumber() + " to " + n);
        }
        setFieldReadOnly(!same, openingBalance, openingBalanceLC);
    }

    private void amountChanged() {
        clearAlerts();
        if(a == null) return;
        number.setValue(a.getNumber());
        Money b = a.getOpeningBalance(), bLC = a.getLocalCurrencyOpeningBalance();
        Money v = openingBalance.getValue(), vLC = openingBalanceLC.getValue();
        boolean same = b.equals(v) && b.getCurrency() == v.getCurrency();
        if(same) {
            openingBalance.setHelperText("");
        } else {
            openingBalance.setHelperText("Changing from " + b + " to " + v);
        }
        if(bLC.equals(vLC) && bLC.getCurrency() == vLC.getCurrency()) {
            openingBalanceLC.setHelperText("");
            same = true;
        } else {
            openingBalanceLC.setHelperText("Changing from " + bLC + " to " + vLC);
            same = false;
        }
        setFieldReadOnly(!same, number);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Proceed");
    }

    @Override
    protected boolean process() {
        clearAlerts();
        a = account.getAccount();
        String n = StoredObject.toCode(number.getValue());
        if(!n.equals(a.getNumber())) {
            new ActionForm("Change Account Number",
                    "Account number will be changed from "
                            + a.getNumber() + " to " + n + "\nAre you sure?",
                    () -> set(n)).execute();
            return false;
        }
        Money m = openingBalance.getValue(), mLC = openingBalanceLC.getValue();
        if(m.getCurrency() == a.getCurrency()) {
            if(!m.equals(mLC)) {
                error("Opening balance in local and foreign currency are not equal");
                return false;
            }
            set(m, mLC);
        } else {
            new ActionForm("Change Currency",
                    "Currency of the account will be changed from "
                            + a.getCurrency().getCurrencyCode() + " to " + m.getCurrency().getCurrencyCode()
                            + ".\nAre you sure?",
                    () -> set(m, mLC)).execute();
        }
        return false;
    }

    private void set(String number) {
        try {
            a.changeAccountNumber(number, getTransactionManager());
            message("Account number updated to " + number + " successfully");
            close();
        } catch (Exception e) {
            error(e);
        }
    }

    private void set(Money m, Money mLC) {
        Currency c = a.getCurrency();
        try {
            a.setOpeningBalance(getTransactionManager(), m, mLC);
            message("Opening balance updated successfully");
            if(c != a.getCurrency()) {
                message("Currency changed from " + c.getCurrencyCode() + " to " + a.getCurrency().getCurrencyCode());
                close();
            }
            a.refresh();
        } catch (Exception e) {
            warning(e);
        }
    }
}
