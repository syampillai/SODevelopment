package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IronIcon;

public class ItemMovementReport extends DataForm {

    private final ItemField<InventoryItem> itemField = new ItemField<>("Item", InventoryItem.class, true);

    public ItemMovementReport(Application a) {
        super(a.getLogicTitle("Item Movement"));
        addField(itemField);
        setRequired(itemField);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.removeAll();
        ok.setText("Print");
        ok.setIcon("pdf");
        buttonPanel.add(new Button("View", e -> process(true)), ok, cancel);
    }

    @Override
    protected boolean process() {
        return process(false);
    }

    private boolean process(boolean view) {
        InventoryItem item = itemField.getValue();
        if(!item.isSerialized()) {
            warning("Not a trackable item: " + item.toDisplay());
            return false;
        }
        close();
        if(view) {
            new ItemMovementView(item).execute();
        } else {
            new com.storedobject.report.ItemMovementReport(getApplication(), item).execute();
        }
        return true;
    }
}
