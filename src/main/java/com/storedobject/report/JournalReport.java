package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class JournalReport extends PDFReport implements JSONParameter {

    private DatePeriod datePeriod;
    private final SystemEntity systemEntity;
    private boolean includeLedger = false;
    private ForeignFinancialSystem origin;

    public JournalReport(Device device) {
        this(device, null);
    }

    public JournalReport(Device device, DatePeriod datePeriod) {
        this(device, null, datePeriod);
    }

    public JournalReport(Device device, SystemEntity systemEntity, DatePeriod datePeriod) {
        super(device);
        this.datePeriod = datePeriod;
        this.systemEntity = systemEntity == null ? device.getServer().getTransactionManager().getEntity() : systemEntity;
    }

    @Override
    public void setParameters(JSON json) {
        datePeriod = json.getDatePeriod();
        if(datePeriod == null) {
            Date date = json.getDate();
            if(date != null) {
                datePeriod = DatePeriod.create(date);
            }
        }
        Boolean ledger = json.getBoolean("includeLedger");
        if(ledger != null) {
            includeLedger = ledger;
        }
        String origin = json.getString("origin");
        if (origin != null) {
            this.origin = ForeignFinancialSystem.get(origin);
        }
    }

    public void setDatePeriod(DatePeriod datePeriod) {
        this.datePeriod = datePeriod;
    }

    public void setDate(Date date) {
        setDatePeriod(DatePeriod.create(date));
    }

    public void setIncludeLedger(boolean includeLedger) {
        this.includeLedger = includeLedger;
    }

    public void setOrigin(ForeignFinancialSystem origin) {
        this.origin = origin;
    }

    @Override
    public Object getTitleText() {
        String title = "Journal Report";
        if(datePeriod == null) {
            return title;
        }
        if(DateUtility.isSameDate(datePeriod.getFrom(), datePeriod.getTo())) {
            title += " - " + DateUtility.format(datePeriod.getFrom());
        } else {
            title += " - Period " + datePeriod;
        }
        title += "\nEntity: " + systemEntity.toDisplay();
        if(origin != null) {
            title += "\nOrigin: " + origin.toDisplay();
        }
        return title;
    }

    @Override
    public void generateContent() throws Exception {
        if(datePeriod == null) {
            add("Date period not specified");
            return;
        }
        Set<Id> accounts = new HashSet<>();
        final AtomicBoolean tooManyAccounts = new AtomicBoolean(false);
        PDFTable table;
        String filter = "SystemEntity=" + systemEntity.getId() + " AND Date" + datePeriod.getDBCondition();
        if(origin != null) {
            filter += " AND Origin=" + origin.getId();
        }
        String foreignRef;
        ForeignFinancialSystem foreign;
        for(JournalVoucher jv: StoredObject.list(JournalVoucher.class, filter, "SystemEntity,Date,No",true)) {
            table = createTable(60, 20, 20);
            table.addCell(createCell(createTitleText(jv.getReference() + " dated " + DateUtility.format(jv.getDate()))));
            table.addCell(createCell(createTitleText("Debit"), true));
            table.addCell(createCell(createTitleText("Credit"), true));
            foreignRef = jv.getForeignReference();
            if(origin != null && origin.getId().equals(jv.getOriginId())) {
                foreign = origin;
            } else {
                foreign = jv.getOrigin();
            }
            if(foreignRef.isBlank()) {
                if(foreign != null && foreign != origin) {
                    table.addRowCell(createCell(createTitleText("Origin: " + foreign.getName())));
                }
            } else {
                table.addRowCell(createCell(createTitleText((foreign == null ? "" : (foreign.getName() + ": ")) + foreignRef)));
            }
            PDFTable finalTable = table;
            jv.entries().forEach(e -> {
                if(includeLedger) {
                    if (accounts.size() <= 1000) {
                        accounts.add(e.getAccount().getId());
                    } else if (!tooManyAccounts.get()) {
                        Id id = e.getAccount().getId();
                        int size = accounts.size();
                        accounts.add(id);
                        if (accounts.size() > size) {
                            tooManyAccounts.set(true);
                        }
                    }
                }
                finalTable.addCell(createCell(e.getAccount()));
                if(e.getAmount().isDebit()) {
                    finalTable.addCell(cell(e.getAmount(), e.getLocalCurrencyAmount()));
                    finalTable.addBlankCell();
                } else {
                    finalTable.addBlankCell();
                    finalTable.addCell(cell(e.getAmount(), e.getLocalCurrencyAmount()));
                }
            });
            add(table);
            addGap(10);
        }
        if(!includeLedger || accounts.isEmpty()) {
            return;
        }
        if(tooManyAccounts.get()) {
            add(createTitleText("Note: Ledger entries are not included - Too many accounts."));
            return;
        }
        Account account;
        for(Id a: accounts) {
            account = StoredObject.get(Account.class, a, true);
            add(createTitleText(account.toDisplay()));
            AccountStatement.generateStatement(this, account, datePeriod);
            addGap(10);
        }
    }

    private PDFCell cell(Money fc, Money lc) {
        if(lc.equals(fc)) {
            return createCell(fc.absolute());
        }
        return createCell(fc.absolute() + "\n" + lc.absolute(), true);
    }
}
