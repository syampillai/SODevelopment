package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.*;

import java.sql.Date;
import java.util.function.Function;

/**
 * Account Statement.
 *
 * @author Syam
 */
public class AccountStatement extends PDFReport implements JSONParameter {

    private Ledger ledger;
    private DatePeriod datePeriod;
    private Account account;
    private Text title;
    private String currency;
    private Boolean init = null;

    public AccountStatement(Device device) {
        this(device, null, null);
    }

    public AccountStatement(Device device, Account account, DatePeriod datePeriod) {
    	super(device);
        this.account = account;
        this.datePeriod = datePeriod;
    }

    private boolean init() {
        if(init != null) {
            return init;
        }
        if(datePeriod == null || account == null) {
            title = new Text().append("Statement of Account");
            init = false;
            return false;
        }
        if(datePeriod.getTo().after(DateUtility.today())) {
            datePeriod = new DatePeriod(datePeriod.getFrom(), DateUtility.today());
        }
        if(datePeriod.getFrom().after(datePeriod.getTo())) {
            datePeriod = new DatePeriod(datePeriod.getTo(), datePeriod.getTo());
        }
        ledger = account.getLedger(datePeriod);
        title = new Text().append(14, PDFFont.BOLD).append(account.toDisplay())
                .append(12, PDFFont.BOLD).newLine().newLine(true)
                .append("Statement for the Period: ").append(datePeriod);
        title.update();
        currency = account.getCurrency().getCurrencyCode();
        setEntity(account.getSystemEntity().getEntity());
        init = true;
        return true;
    }

    @Override
    public void setParameters(JSON json) {
        datePeriod = json.getDatePeriod();
        if(datePeriod == null) {
            return;
        }
        Id accountId = json.getId("account");
        if(accountId != null) {
            account = StoredObject.get(Account.class, accountId, true);
        }
    }

    @Override
	public Object getTitleText() {
        init();
        PDFCell cell = createCell(title);
        cell.setBorder(0);
        return cell;
    }

    public Ledger getLedger() {
        return ledger;
    }

    @Override
	public void generateContent() {
        if(!init()) {
            add("Invalid parameters!");
            return;
        }
        Function<PDFCell, PDFCell> grey = c -> {
            c.setGrayFill(0.9f);
            return c;
        };
        Function<PDFCell, PDFCell> hollow = c -> {
            c.setBorder(PDFCell.LEFT | PDFCell.RIGHT);
            return c;
        };
        PDFTable table = createTable(13, 47, 20, 20);
        table.addCell(createCell(createTitleText("Date")));
        table.addCell(createCell(createTitleText("Particulars")));
        table.addCell(createCell(createTitleText("Debit (" + currency + ")"), true));
        table.addCell(createCell(createTitleText("Credit (" + currency + ")"), true));
        PDFCell cell;
        for(int i = 0; i < table.getNumberOfColumns(); i++) {
            cell = createCell("");
            cell.setBorder(PDFRectangle.TOP);
            table.addCell(cell);
        }
        table.setHeaderRows(2);
        table.setFooterRows(1);
        table.addCell(createCell(ledger.getPeriod().getFrom()), grey);
        Money a = ledger.getOpeningBalance(), b = a;
        table.addCell(createCell("Previous " + b(a), true, grey));
        if(a.isDebit()) {
            table.addCell(createCell(a.negate(), grey));
            table.addBlankCell(grey);
        } else {
            table.addBlankCell(grey);
            table.addCell(createCell(a), grey);
        }
        Date d = null;
        for(LedgerEntry le: ledger) {
            if(d != null && !DateUtility.isSameDate(le.getDate(), d)) {
                table.addBlankCell(grey);
                table.addCell(createCell(b(b), true), grey);
                if(b.isDebit()) {
                    table.addCell(createCell(b.negate()), grey);
                    table.addBlankCell(grey);
                } else {
                    table.addBlankCell(grey);
                    table.addCell(createCell(b), grey);
                }
            }
            a = le.getAmount();
            d = le.getDate();
            table.addCell(createCell(d), hollow);
            table.addCell(createCell(le.getParticulars(true)), hollow);
            if(a.isDebit()) {
                table.addCell(createCell(a.negate()), hollow);
                table.addBlankCell(hollow);
            } else {
                table.addBlankCell(hollow);
                table.addCell(createCell(a), hollow);
            }
            b = le.getBalance();
        }
        table.addCell(createCell(ledger.getPeriod().getTo(), grey));
        table.addCell(createCell("Closing " + b(b), true), grey);
        if(b.isDebit()) {
            table.addCell(createCell(b.negate()), grey);
            table.addBlankCell(grey);
        } else {
            table.addBlankCell(grey);
            table.addCell(createCell(b), grey);
        }
        add(table);
    }

    private String b(Money m) {
        return "Balance (" + (m.isDebit() ? "DB" : "CR") + ")";
    }
}