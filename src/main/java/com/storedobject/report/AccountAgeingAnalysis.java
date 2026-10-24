package com.storedobject.report;

import com.storedobject.accounts.EntityAccount;
import com.storedobject.core.*;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

public class AccountAgeingAnalysis extends PDFReport {

    private final boolean receivable;
    private final int bucketSize;
    private PDFTable table;
    private final Money[] bucketTotals = new Money[4];

    public AccountAgeingAnalysis(Device device) {
        this(device, false, true, true, 0);
    }

    public AccountAgeingAnalysis(Device device, boolean receivable) {
        this(device, false, true, receivable, 0);
    }

    public AccountAgeingAnalysis(Device device, boolean letterhead, boolean receivable) {
        this(device, letterhead, true, receivable, 0);
    }

    public AccountAgeingAnalysis(Device device, boolean letterhead, boolean printLogo, boolean receivable) {
        this(device, letterhead, printLogo, receivable, 0);
    }

    public AccountAgeingAnalysis(Device device, boolean receivable, int bucketSize) {
        this(device, false, true, receivable, bucketSize);
    }

    public AccountAgeingAnalysis(Device device, boolean letterhead, boolean receivable, int bucketSize) {
        this(device, letterhead, true, receivable, bucketSize);
    }

    public AccountAgeingAnalysis(Device device, boolean letterhead, boolean printLogo, boolean receivable, int bucketSize) {
        super(device, letterhead, printLogo);
        this.receivable = receivable;
        this.bucketSize = EntityAccount.getAgeingBucketDays(device.getServer().getTransactionManager().getEntity());
    }

    protected ObjectIterator<EntityAccount> getAccounts(String filter) {
        return StoredObject.list(EntityAccount.class, filter, true);
    }

    @Override
    public Object getTitle() {
        return "Ageing Analysis of " + (receivable ? "Receivables" : "Payables") + " as of " + DateUtility.format(DateUtility.today());
    }

    @Override
    public void generateContent() throws Exception {
        String filter = "(Balance).Amount" + (receivable ? "<" : ">") + "0";
        Set<Currency> currencies = new HashSet<>();
        for(EntityAccount account : getAccounts(filter)) {
            Currency c = account.getCurrency();
            if(!currencies.contains(c)) {
                currencies.add(account.getCurrency());
            }
        }
        boolean printEntityShortName = printEntityShortName();
        boolean printAccountNumber = printAccountNumber();
        table = createTable(30, 10, 10, 10, 10, 10);
        table.addCell(createTitleText("Account"));
        for(int i = 0; i < 4; i++) {
            table.addCell(createCell(createTitleText(EntityAccount.getAgeingBucketDaysLabel(bucketSize, i)), true));
        }
        table.addCell(createCell(createTitleText("Total " + (receivable ? "Receivable" : "Payable")), true));
        table.setHeaderRows(1);
        Money[] grandTotals = new Money[4];
        Money init = new Money(getTransactionManager().getCurrency());
        Arrays.fill(grandTotals, init);
        int count = 0;
        filter += " AND (Balance).Currency='";
        for(Currency currency : currencies) {
            init = new Money(currency);
            Arrays.fill(bucketTotals, init);
            for (EntityAccount account : getAccounts(filter + currency.getCurrencyCode() + "'")) {
                count += printAccount(account, compute(account), printEntityShortName, printAccountNumber);
                if (count > 100) {
                    add(table);
                    count = 0;
                }
            }
            table.addCell(createCell(createTitleText("Total for " + currency.getCurrencyCode() + ": "), true));
            for (Money m : bucketTotals) {
                table.addCell(createCell(createTitleText(m.toString()), true));
            }
            table.addCell(createCell(createTitleText(Money.sum(bucketTotals).toString()), true));
            for(int i = 0; i < 4; i++) {
                grandTotals[i] = grandTotals[i].add(bucketTotals[i]);
            }
            table.addBlankRow();
        }
        table.addCell(createCell(createTitleText("Grand Total in " + grandTotals[0].getCurrency().getCurrencyCode() + ": "), true));
        for (Money m : grandTotals) {
            table.addCell(createCell(createTitleText(m.toString()), true));
        }
        table.addCell(createCell(createTitleText(Money.sum(grandTotals).toString()), true));
        add(table);
    }

    private Money[] compute(EntityAccount account) {
        Money[] b = receivable ? account.getAgeingBalances(bucketSize) : account.getAgeingPayments(bucketSize);
        boolean any = false;
        for(int i = 0; i < b.length; i++) {
            if(receivable) {
                if(!any) {
                    any = b[i].isDebit();
                }
                b[i] = b[i].negate();
            } else {
                any = b[i].isCredit();
            }
        }
        return any ? b : null;
    }

    private int printAccount(EntityAccount account, Money[] balances, boolean printEntityShortName, boolean printAccountNumber) {
        if(balances == null) return 0;
        StringBuilder s = new StringBuilder();
        if(printAccountNumber) {
            s.append(account.getNumber()).append(" - ");
        }
        if(printEntityShortName) {
            String sn = account.getEntity().getShortName();
            if(!sn.isBlank()) {
                s.append(sn).append(" - ");
            }
        }
        s.append(account.getName());
        table.addCell(createCell(s));
        Money m;
        for(int i = 0; i < balances.length; i++) {
            m = balances[i];
            table.addCell(createCell(m));
            bucketTotals[i] = bucketTotals[i].add(m);
        }
        table.addCell(createCell(Money.sum(balances)));
        return 1;
    }

    protected boolean printAccountNumber() {
        return true;
    }

    protected boolean printEntityShortName() {
        return true;
    }

    @Override
    public int getPageOrientation() {
        return ORIENTATION_LANDSCAPE;
    }
}
