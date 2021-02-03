package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.report.ItemMovementReport;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;

import java.sql.Date;

public class ItemMovementView extends ListGrid<InventoryLedger> {

    private final InventoryItem item;

    public ItemMovementView(InventoryItem item) {
        super(InventoryLedger.class, StringList.create("Date", "Reference", "From", "To"));
        if(!item.isSerialized()) {
            throw new SORuntimeException("Not an item with serial number");
        }
        this.item = item;
        setCaption("Movement of P/N: " + item.getPartNumber().getPartNumber() + ", S/N: " + item.getSerialNumber());
        StoredObject.list(InventoryLedger.class, "Item=" + item.getId(), "Date,TranId").
                forEach(this::add);
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Print", "pdf", e -> new ItemMovementReport(getApplication(), item).execute()),
                new Button("Exit", e -> close()),
                new ELabel("Movement of ").append(item.toDisplay(), "blue").update());
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    public Date getDate(InventoryLedger ledger) {
        Date d = ledger.getDate();
        return DateUtility.equals(d, InventoryTransaction.dataPickupDate) ? null : d;
    }

    public String getFrom(InventoryLedger ledger) {
        return loc(ledger.getLocationFrom());
    }

    public String getTo(InventoryLedger ledger) {
        return loc(ledger.getLocationTo());
    }

    private String loc(InventoryLocation loc) {
        return loc instanceof InventoryFitmentPosition ?
                ((InventoryFitmentPosition) loc).toDisplay(false) :
                loc.toDisplay();
    }
}
