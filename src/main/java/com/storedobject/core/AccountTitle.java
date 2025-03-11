package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * A virtual account to support multiple titles for a real account.
 *
 * @author Syam
 */
public final class AccountTitle extends Account {

    private Id accountId;

    public AccountTitle() {
    }

    public static void columns(Columns columns) {
        columns.add("Account", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Account");
    }

    public static String[] protectedColumns() {
        return new String[] { "SystemEntity", "OpeningBalance", "LocalCurrencyOpeningBalance",
                "LocalCurrencyBalance", "AccountStatus", "Chart" };
    }

    public void setAccount(Id accountId) {
        this.accountId = accountId;
    }

    public void setAccount(BigDecimal idValue) {
        setAccount(new Id(idValue));
    }

    public void setAccount(Account account) {
        setAccount(account == null ? null : account.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getAccountId() {
        return accountId;
    }

    public Account getAccount() {
        return getRelated(Account.class, accountId, true);
    }

    public static AccountTitle get(SystemEntity systemEntity, String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        AccountTitle account = get(AccountTitle.class, (systemEntity == null ? "" :
                ("SystemEntity=" + systemEntity.getId() + " AND ")) + "lower(Name)='"
                + name.trim().toLowerCase().replace("'", "''") + "'");
        return account == null ? list(systemEntity, name).single(false) : account;
    }

    public static ObjectIterator<? extends AccountTitle> list(SystemEntity systemEntity, String name) {
        if(name == null || name.isEmpty()) {
            return ObjectIterator.create();
        }
        return list(AccountTitle.class, (systemEntity == null ? "" : ("SystemEntity=" + systemEntity.getId()
                + " AND ")) + "lower(Name) LIKE '" + name.toLowerCase().replace("'", "''") + "%'");
    }

    static <A extends Account> ObjectIterator<A> list(SystemEntity systemEntity, String name,
                                                                           Class<A> accountClass, boolean any) {
        //noinspection unchecked
        return (ObjectIterator<A>) list(systemEntity, name).map(AccountTitle::getAccount)
                .filter(a -> any ? accountClass.isAssignableFrom(a.getClass()) : accountClass == a.getClass());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        accountId = tm.checkTypeAny(this, accountId, Account.class, false);
        Account a = getAccount();
        if(a instanceof AccountTitle) {
            throw new Invalid_State("Not a real account - " + a.toDisplay());
        }
        int i = 0;
        while (true) {
            number = a.getNumber() + "-ALT" + (i == 0 ? "" : ("-" + i));
            if(!exists(Account.class, "lower(Number)='" + number.toLowerCase() + "'", true)) {
                break;
            }
            ++i;
        }
        setSystemEntity(a.getSystemEntityId());
        setChart(a.getChartId());
        setCurrency(a.getCurrency());
        super.validateData(tm);
        if(getName().equals(a.getName()) ||
                exists(AccountTitle.class, "Account=" + accountId + " AND Name='"
                        + getName().replace("'", "''") + "'")) {
            throw new Invalid_State("Duplicate title");
        }
    }
}
