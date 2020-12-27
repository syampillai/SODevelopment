package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.DataForm;

public class ItemMovementReport extends DataForm {

    private final ItemField<InventoryItem> itemField = new ItemField<>("Item", InventoryItem.class, true);

    public ItemMovementReport(Application a) {
        super(a.getLogicTitle("Item Movement"));
        addField(itemField);
        setRequired(itemField);
    }

    @Override
    protected boolean process() {
        InventoryItem item = itemField.getValue();
        if(!item.isSerialized()) {
            warning("Not a trackable item: " + item.toDisplay());
            return false;
        }
        close();
        new com.storedobject.report.ItemMovementReport(getApplication(), item).execute();
        return true;
    }
}
