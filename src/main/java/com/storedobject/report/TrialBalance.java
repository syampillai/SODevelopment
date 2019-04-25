package com.storedobject.report;

import java.io.OutputStream;
import java.util.Date;

import com.storedobject.core.Account;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Money;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.SystemEntity;
import com.storedobject.pdf.PDF;
import com.storedobject.pdf.PDFTable;

/**
 * Trial Balance
 */
public class TrialBalance extends PDF {

    private final SystemEntity entity;
    private final Date date;

    public TrialBalance(SystemEntity entity, Date date) {
    	this(entity, date, null);
    }

    public TrialBalance(SystemEntity entity, Date date, OutputStream out) {
    	super(out);
        this.entity = entity;
        this.date = date;
        setEntity(entity.getEntity());
    }

    @Override
	public String getTitle() {
        return "Trial Balance As On " + DateUtility.format(date);
    }

	@Override
	public void generateContent() {
        PDFTable table = createTable(15, 35, 25, 25);
        table.addCell(createCenteredCell("Account#"));
        table.addCell(createCenteredCell("Account Description"));
        table.addCell(createCenteredCell("Debit Balance"));
        table.addCell(createCenteredCell("Credit Balance"));
        Money b, dTotal = new Money(entity.getCurrency()), cTotal = new Money(entity.getCurrency());
        ObjectIterator<Account> list = Account.list(Account.class, "SystemEntity=" + entity.getId(), "T_Family", true);
        int rows = 0;
        for(Account a: list) {
            b = a.getLocalCurrencyBalance();
            table.addCell(createCell(a.getNumber()));
            table.addCell(createCell(a.getName()));
            if(b.isDebit()) {
                dTotal = dTotal.subtract(b);
                table.addCell(createCell(b.negate(), true));
                table.addBlankCell();
            } else {
                cTotal = cTotal.add(b);
                table.addBlankCell();
                table.addCell(createCell(b, true));
            }
            if(++rows >= 80) {
                add(table);
                rows = 0;
            }
        }
        table.addBlankCell();
        table.addCell(createCenteredCell("* * * Total"));
        table.addCell(createCell(dTotal, true));
        table.addCell(createCell(cTotal, true));
        add(table);
    }
}
