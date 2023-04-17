package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.MapComboField;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MigratePartNumber extends DataForm implements Transactional {

    private final ItemTypeGetField<InventoryItemType> pnField = new ItemTypeGetField<>("P/N to Migrate",
            InventoryItemType.class, true);
    private final MapComboField<Class<?>> newTypeField;

    public MigratePartNumber() {
        super("Migrate Part Number Type");
        Map<Class<?>, String> map = new HashMap<>();
        map.put(InventoryItemType.class, "Inventory Item Type (Basic)");
        ClassAttribute<?> ca = ClassAttribute.get(InventoryItemType.class);
        for(Class<?> k: ca.listChildClasses(true)) {
            if(!Modifier.isAbstract(k.getModifiers())) {
                map.put(k, StringUtility.makeLabel(k));
            }
        }
        newTypeField = new MapComboField<>("Migrate to", map);
        addField(pnField, newTypeField);
        setRequired(pnField);
        setRequired(newTypeField);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        InventoryItemType pn = pnField.getObject();
        Class<?> to = newTypeField.getValue();
        if(pn.getClass().equals(to)) {
            warning("Same type selected!");
            return false;
        }
        close();
        InventoryItemType newPN = convert(pn, to);
        if(newPN == null) {
            return false;
        }
        new ActionForm("Part Number: " + pn.toDisplay() + "\nP/N Type: " + StringUtility.makeLabel(pn.getClass())
                + "\nMigrate to: " + StringUtility.makeLabel(to)
                + "\nAll items having the above P/N will be migrated!\nAre you sure?",
                () -> convert(pn, newPN)).execute();
        return true;
    }

    private void convert(InventoryItemType pn, InventoryItemType newPN) {
        try {
            pn.migrate(getTransactionManager(), newPN, item -> convert(item, newPN));
        } catch(Exception e) {
            error(e);
        }
    }

    private InventoryItemType convert(InventoryItemType pn, Class<?> to) {
        try {
            InventoryItemType newPN = (InventoryItemType) to.getConstructor().newInstance();
            HashMap<String, Object> map = new HashMap<>();
            pn.save(map);
            newPN.load(map);
            migrate(pn, newPN);
            return newPN;
        } catch(Throwable e) {
            error(e);
        }
        return null;
    }

    private InventoryItem convert(InventoryItem item, InventoryItemType newPN) {
        InventoryItem newItem = newPN.createItem();
        HashMap<String, Object> map = new HashMap<>();
        try {
            item.save(map);
            newItem.load(map);
        } catch(Throwable ignored) {
        }
        migrate(item, newItem);
        return newItem;
    }

    protected void migrate(InventoryItemType from, InventoryItemType to) {
    }

    protected void migrate(InventoryItem from, InventoryItem to) {
    }
}
