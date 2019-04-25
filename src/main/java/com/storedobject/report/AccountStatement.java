package com.storedobject.report;

import java.sql.Date;
import com.storedobject.core.Account;
import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.Ledger;
import com.storedobject.core.LedgerEntry;
import com.storedobject.core.Money;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

/**
 * Account Statement
 */
public class AccountStatement extends PDFReport {

    private final Ledger ledger;
    private String title;

    public AccountStatement(Device device, Account account, DatePeriod datePeriod) {
    	super(device);
        ledger = new Ledger(account, datePeriod);
        title = account.getName();
        if(title.length() > 40) {
        	title = "\n" + title;
        } else {
        	title = " - " + title;
        }
        title = "Account: " + account.getNumber() + title + "\n" + "Statememt For The Period: " + datePeriod;
        setEntity(account.getSystemEntity().getEntity());
    }

    @Override
	public String getTitle() {
        return title;
    }

    @Override
	public void generateContent() {
        PDFTable table = createTable(15, 35, 25, 25);
        table.addCell(createCenteredCell("Date"));
        table.addCell(createCell("Narration"));
        table.addCell(createCenteredCell("Debit"));
        table.addCell(createCenteredCell("Credit"));
        table.addBlankCell();
        table.addCell(createCell("* * * Opening Balance As Of " + DateUtility.format(ledger.getDate())));
        Money a = ledger.getBalance(), b = a;
        if(a.isDebit()) {
            table.addCell(createCell(a.negate(), true));
            table.addBlankCell();
        } else {
            table.addBlankCell();
            table.addCell(createCell(a, true));
        }
        Date d = ledger.getDate();
        for(LedgerEntry le: ledger) {
            if(!DateUtility.isSameDate(le.getDate(), d)) {
                table.addBlankCell();
                table.addCell(createCell("* * * Balance As Of " + DateUtility.format(d)));
                if(b.isDebit()) {
                    table.addCell(createCell(b.negate(), true));
                    table.addBlankCell();
                } else {
                    table.addBlankCell();
                    table.addCell(createCell(b, true));
                }
            }
            a = le.getAmount();
            d = le.getDate();
            table.addCell(createCell(d));
            table.addCell(createCell(le.getNarration()));
            if(a.isDebit()) {
                table.addCell(createCell(a.negate(), true));
                table.addBlankCell();
            } else {
                table.addBlankCell();
                table.addCell(createCell(a, true));
            }
            b = ledger.getBalance();
        }
        table.addBlankCell();
        table.addCell(createCell("* * * Closing Balance As Of " + DateUtility.format(ledger.getDate())));
        a = ledger.getBalance();
        if(a.isDebit()) {
            table.addCell(createCell(a.negate(), true));
            table.addBlankCell();
        } else {
            table.addBlankCell();
            table.addCell(createCell(a, true));
        }
        add(table);
    }
}