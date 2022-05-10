package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.report.ItemMovementReport;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;

import java.sql.Date;

public class ItemMovementView extends ListGrid<InventoryLedger> implements CloseableView {

    private static final String NO_CAPTION = "Item Movement";
    private final ItemField<? extends InventoryItem> itemField;
    private InventoryItem item;

    public ItemMovementView() {
        this((InventoryItem) null);
    }

    public ItemMovementView(InventoryItem item) {
        this(item, null);
    }

    public ItemMovementView(String className) {
        this(null, className(className));
    }

    private ItemMovementView(InventoryItem item, Class<? extends InventoryItem> itemClass) {
        super(InventoryLedger.class, StringList.create("Date", "Reference", "From", "To"));
        if(item != null && !item.isSerialized()) {
            throw new SORuntimeException("Not an item with serial number");
        }
        if(itemClass == null) {
            itemClass = InventoryItem.class;
        }
        itemField = new ItemField<>(itemClass, true);
        if(item != null) {
            itemField.setValue(item.getId());
            loadItem(item);
        } else {
            setCaption(NO_CAPTION);
        }
        itemField.addValueChangeListener(e -> loadItem(e.getValue()));
    }

    private static Class<? extends InventoryItem> className(String className) {
        if(className == null) {
            return InventoryItem.class;
        }
        className = className.trim();
        try {
            if(className.endsWith("Type")) {
                className = className.substring(0, className.length() - 4);
            }
            //noinspection unchecked
            return (Class<? extends InventoryItem>) JavaClassLoader.getLogic(className);
        } catch(ClassNotFoundException e) {
            throw new SORuntimeException("Unable to determine item class from '" + className + "'");
        }
    }

    private void loadItem(InventoryItem item) {
        clearAlerts();
        clear();
        if(item == null || !item.isSerialized()) {
            this.item = null;
            if(item != null) {
                message("Not an item with serial number");
            }
            setCaption(NO_CAPTION);
            return;
        }
        this.item = item;
        setCaption("Movement of P/N: " + item.getPartNumber().getPartNumber() + ", S/N: " + item.getSerialNumber());
        StoredObject.list(InventoryLedger.class, "Item=" + item.getId(), "Date,TranId").
                forEach(this::add);
    }

    @Override
    public Component createHeader() {
        itemField.setWidthFull();
        return new ButtonLayout(itemField, new Button("Print", "pdf", e -> report()).asSmall(),
                new Button("Exit", e -> close()).asSmall());
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

    private void report() {
        clearAlerts();
        if(item == null || isEmpty()) {
            message("Nothing to report!");
            return;
        }
        //noinspection resource
        new ItemMovementReport(getApplication(), item).execute();
    }
}
