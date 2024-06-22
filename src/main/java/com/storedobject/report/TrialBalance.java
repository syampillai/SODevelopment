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
        Money b, dTotal = new Money(entity.getCurrency()), cTotal = new Money(entity.getCurrency());
        ObjectIterator<Account> list =
                Account.list(Account.class, "SystemEntity=" + entity.getId(), "T_Family", true)
                        .filter(a -> !(a instanceof AccountTitle));
        int rows = 0;
        String as;
        int bracket;
        for(Account a: list) {
            as = a.toString();
            if((bracket = as.indexOf(") ")) > 0) {
                as = as.substring(0, bracket + 1) + "\n" + as.substring(bracket + 2);
            }
            b = a.getLocalCurrencyBalance(date);
            table.addCell(createCell(as));
            if(b.isDebit()) {
                b = b.negate();
                dTotal = dTotal.add(b);
                table.addCell(createCell(b.toString(false), true));
                table.addBlankCell();
            } else {
                cTotal = cTotal.add(b);
                table.addBlankCell();
                table.addCell(createCell(b.toString(false), true));
            }
            if(++rows >= 80) {
                add(table);
                rows = 0;
            }
        }
        table.addCell(createCenteredCell("* * * Total"));
        table.addCell(createCell(dTotal, true));
        table.addCell(createCell(cTotal, true));
        add(table);
    }
}
