package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.sql.Date;

/**
 * Trial Balance.
 *
 * @author Syam
 */
public class TrialBalance extends PDFReport implements JSONParameter {

    private SystemEntity entity;
    private Date date;

    public TrialBalance(Device device) {
        this(device, null, null);
    }

    public TrialBalance(Device device, Date date) {
        this(device, null, date);
    }

    public TrialBalance(Device device, SystemEntity entity, Date date) {
        super(device);
        if(entity == null) {
            entity = getTransactionManager().getEntity();
        }
        this.entity = entity;
        this.date = date;
        setEntity(entity == null ? null : entity.getEntity());
    }

    @Override
    public void setParameters(JSON json) {
        date = json.getDate("date");
        Id entityId = json.getId("systemEntity");
        if(entityId != null) {
            entity = StoredObject.get(SystemEntity.class, entityId);
            setEntity(entity.getEntity());
        }
    }

    @Override
    public Object getTitleText() {
        if(date == null || entity == null) {
            return "Trial Balance";
        }
        Text title = new Text().append(16, PDFFont.BOLD);
        title.append(entity);
        title.newLine().newLine(true).append(12, PDFFont.BOLD).append("Trial Balance as on ").append(date);
        title.newLine().newLine(true).append("(Currency: ").append(entity.getCurrency()).append(")");
        PDFCell cell = new PDFCell(title);
        cell.setBorder(0);
        return cell;
    }

	@Override
	public void generateContent() {
        if(date == null || entity == null) {
            add("Parameters missing");
            return;
        }
        PDFTable table = createTable(50, 25, 25);
        table.addCell(createCell(createTitleText("Account")));
        table.addCell(createCell(createTitleText("Debit Balance"), true));
        table.addCell(createCell(createTitleText("Credit Balance"), true));
        table.setHeaderRows(1);
        Money b, dTotal = new Money(entity.getCurrency()), cTotal = dTotal, dChart = dTotal, cChart = cTotal,
                asset = dTotal, liability = dTotal, stock = dTotal, contingentAsset = dTotal,
                contingentLiability = dTotal, income = dTotal, expense = dTotal;
        ObjectIterator<Account> list;
        int count, rows = 0, totalCount = 0;
        String as;
        int bracket;
        for(AccountChart ac: StoredObject.list(AccountChart.class, null, "")) {
            count = 0;
            dChart = dChart.zero();
            cChart = cChart.zero();
            list = Account.list(Account.class, "SystemEntity=" + entity.getId() + " AND Chart=" + ac.getId(),
                            "T_Family,Number", true).filter(a -> !(a instanceof AccountTitle));
            for (Account a : list) {
                as = a.toString();
                if ((bracket = as.indexOf(") ")) > 0) {
                    as = as.substring(0, bracket + 1) + "\n" + as.substring(bracket + 2);
                }
                b = a.getLocalCurrencyBalance(date);
                if (b.isZero() && (a instanceof OffsetAccount || a.isClosed())) {
                    continue;
                }
                ++count;
                if(count == 1) {
                    table.addCell(createCell(ac.toDisplay() + "\nCategory: " + ac.getCategoryValue()));
                    table.addBlankCell(2);
                    ++rows;
                }
                table.addCell(createCell(as));
                if (b.isDebit()) {
                    dTotal = dTotal.add(b.negate());
                    dChart = dChart.add(b.negate());
                    table.addCell(createCell(b.toString(false), true));
                    table.addBlankCell();
                } else {
                    cTotal = cTotal.add(b);
                    cChart = cChart.add(b);
                    table.addBlankCell();
                    table.addCell(createCell(b.toString(false), true));
                }
                switch (ac.getCategory()) {
                    case 0 -> {
                        if (ac.getBalanceType() == 0) {
                            asset = asset.add(b.negate());
                        } else {
                            liability = liability.add(b);
                        }
                    }
                    case 1 -> {
                        if(ac.getBalanceType() == 0) {
                            expense = expense.add(b.negate());
                        } else {
                            income = income.add(b);
                        }
                    }
                    case 2 -> stock = stock.add(b.negate());
                    case 3 -> {
                        if(ac.getBalanceType() == 0) {
                            contingentAsset = contingentAsset.add(b.negate());
                        } else {
                            contingentLiability = contingentLiability.add(b);
                        }
                    }
                }
                if (++rows >= 80) {
                    add(table);
                    rows = 0;
                }
            }
            if(count > 0) {
                totalCount += count;
                table.addCell(createCell("Accounts: " + count + ", Net balance: "
                        + cChart.subtract(dChart).toString(false, true), true));
                table.addCell(createCell(dChart.toString(false), true));
                table.addCell(createCell(cChart.toString(false), true));
                table.addBlankRow();
            }
        }
        table.addCell(createCenteredCell("* * * Total (Accounts: " + totalCount + ")"));
        table.addCell(createCell(dTotal, true));
        table.addCell(createCell(cTotal, true));
        table.addBlankRow();
        if(!stock.isZero()) {
            table.addCell(createCenteredCell(" Total Stock"));
            table.addCell(createCell(stock.toString(false), true));
            table.addBlankCell();
            table.addBlankRow();
        }
        table.addCell(createCenteredCell(" Total Assets"));
        table.addCell(createCell(asset.toString(false), true));
        table.addBlankCell();
        table.addCell(createCenteredCell(" Total Liabilities"));
        table.addBlankCell();
        table.addCell(createCell(liability.toString(false), true));
        b = asset.subtract(liability).add(stock);
        table.addCell(createCenteredCell(b.isNegative() ? "Loss" : "Profit"));
        if(b.isNegative()) {
            table.addBlankCell();
            table.addCell(createCell(b.negate().toString(false), true));
        } else {
            table.addCell(createCell(b.toString(false), true));
            table.addBlankCell();
        }
        table.addBlankRow();
        table.addCell(createCenteredCell(" Total Revenue"));
        table.addBlankCell();
        table.addCell(createCell(income.toString(false), true));
        table.addCell(createCenteredCell(" Total Expenditure"));
        table.addCell(createCell(expense.toString(false), true));
        table.addBlankCell();
        b = income.subtract(expense).add(stock);
        table.addCell(createCenteredCell(b.isNegative() ? "Loss" : "Profit"));
        if(b.isNegative()) {
            table.addCell(createCell(b.negate().toString(false), true));
            table.addBlankCell();
        } else {
            table.addBlankCell();
            table.addCell(createCell(b.toString(false), true));
        }
        if(!contingentAsset.isZero() || !contingentLiability.isZero()) {
            table.addBlankRow();
            table.addCell(createCenteredCell(" Total Contingent Assets"));
            table.addCell(createCell(contingentAsset.toString(false), true));
            table.addBlankCell();
            table.addCell(createCenteredCell(" Total Contingent Liabilities"));
            table.addBlankCell();
            table.addCell(createCell(contingentLiability.toString(false), true));
        }
        add(table);
    }
}
