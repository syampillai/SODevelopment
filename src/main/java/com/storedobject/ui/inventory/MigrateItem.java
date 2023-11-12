package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;
import java.util.HashMap;
import java.util.Map;

public class MigrateItem extends DataForm implements Transactional {

    private final ItemField<InventoryItem> itemField = new ItemField<>("Item to Migrate", InventoryItem.class, true);
    private final ItemTypeGetField<InventoryItemType> pnField = new ItemTypeGetField<>("Migrate to",
            InventoryItemType.class, true);

    public MigrateItem() {
        super("Migrate Item");
        addField(itemField, pnField);
        setRequired(itemField);
        setRequired(pnField);
        pnField.setLoadFilter(pn -> {
            InventoryItem ii = itemField.getValue();
            return ii != null && !ii.getPartNumber().getId().equals(pn.getId());
        });
    }

    @Override
    protected boolean process() {
        clearAlerts();
        InventoryItem item = itemField.getObject();
        InventoryItemType pnOriginal = item.getPartNumber();
        InventoryItemType pn = pnField.getObject();
        close();
        StringBuilder s = new StringBuilder("Item: ");
        s.append(item.toDisplay()).append("\nMigrate ");
        if(pn.getClass().equals(pnOriginal.getClass())) {
            s.append(": From ").append(pnOriginal.getPartNumber()).append(" to ").append(pn.getPartNumber());
        } else {
            s.append(" from: ").append(pnOriginal.toDisplay()).append("\nMigrate to: ").append(pn.toDisplay());
        }
        new ActionForm(s + "\nAre you sure?",
                () -> migrate(item, pn)).execute();
        return true;
    }

    private void migrate(InventoryItem item, InventoryItemType newPN) {
        try {
            item.migrate(getTransactionManager(), newPN, ii -> convert(ii, newPN));
            message("Migrated successfully");
        } catch(Exception e) {
            error(e);
        }
    }

    private InventoryItem convert(InventoryItem item, InventoryItemType newPN) {
        InventoryItem newItem = newPN.createItem();
        Map<String, Object> map = new HashMap<>();
        try {
            item.save(map);
            map.remove("PartNumber");
            newItem.load(map);
        } catch(Throwable e) {
            error(e);
            throw new SORuntimeException(e);
        }
        migrate(item, newItem);
        return newItem;
    }

    protected void migrate(InventoryItem from, InventoryItem to) {
    }
}
