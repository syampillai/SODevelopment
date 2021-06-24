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
 * Account Statement.
 *
 * @author Syam
 */
public class AccountStatement extends PDFReport {

    private final Ledger ledger;

    public AccountStatement(Device device, Account account, DatePeriod datePeriod) {
    	super(device);
        ledger = new Ledger(account, datePeriod);
    }

    public Ledger getLedger() {
        return ledger;
    }

    @Override
	public void generateContent() {
    }
}