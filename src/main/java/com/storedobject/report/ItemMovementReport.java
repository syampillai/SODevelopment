package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.sql.Date;

public class ItemMovementReport extends PDFReport implements JSONParameter {

    private String caption = "Item Movement";
    private InventoryItem item;

    public ItemMovementReport(Device device, InventoryItem item) {
        super(device);
        this.item = item;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public Object getTitleText() {
        Text t = new Text();
        t.append(getFontSize() + 6, PDFFont.BOLD).append(caption);
        if(item != null) {
            t.newLine().append(getFontSize() + 4).append(item.toDisplay());
        }
        PDFCell c = createCell(t);
        c.setGrayFill(0.9f);
        return c;
    }

    @Override
    public void generateContent() {
        if(item == null) {
            add("Item not specified");
            return;
        }
        if(!item.isSerialized()) {
            add("Item selected is not a trackable item");
            return;
        }
        PDFTable table = createTable(26, 40, 67, 67);
        table.addCell(tCell("Date"));
        table.addCell(tCell("Reference"));
        table.addCell(tCell("From"));
        table.addCell(tCell("To"));
        table.setHeaderRows(1);
        InventoryLocation locFrom;
        String ref;
        for(InventoryLedger movement: StoredObject.list(InventoryLedger.class, "Item=" + item.getId(),
                "Date,TranId")) {
            locFrom = movement.getLocationFrom();
            ref = movement.getReference();
            if(locFrom.getType() == 12) { // Data-pickup
                AuditTrail at = AuditTrail.create(movement);
                if(at != null) {
                    ref += " (Created at " + DateUtility.formatWithTimeHHMM(at.getTimestamp()) + ")";
                }
            }
            table.addCell(dCell(movement.getDate()));
            table.addCell(createCell(ref));
            table.addCell(locCell(locFrom));
            table.addCell(locCell(movement.getLocationTo()));
        }
        add(table);
    }

    private PDFCell locCell(InventoryLocation location) {
        return createCell(location instanceof InventoryFitmentPosition ?
                ((InventoryFitmentPosition) location).toDisplay(false) :
                location.toDisplay());
    }

    private PDFCell dCell(Date date) {
        PDFCell c;
        if(DateUtility.equals(date, InventoryTransaction.dataPickupDate)) {
            c = createCell("");
        } else {
            c = createCell(date);
        }
        return c;
    }

    private PDFCell tCell(String s) {
        PDFCell c = createCell(createTitleText(s, getFontSize()));
        c.setGrayFill(0.9f);
        return c;
    }

    @Override
    public void setParameters(JSON json) {
        Id id = json.getId("item");
        if(id == null) {
            return;
        }
        item = StoredObject.get(InventoryItem.class, id, true);
        String c = json.getString("caption");
        if(c != null) {
            caption = c;
        }
    }
}