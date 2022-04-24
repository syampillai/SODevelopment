package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.inventory.LocationField;
import com.storedobject.ui.inventory.ReturnMaterial;
import com.storedobject.vaadin.DataForm;

import java.math.BigDecimal;

public class Test extends DataForm implements Transactional {

    public Test() {
        super("Test");
    }

    /*
    @Override
    public void execute() {
        List<SystemUser> list = StoredObject.list(SystemUser.class).skip(3).limit(1).toList();
        Map<String, Object> map = new HashMap<>();
        try {
            list.forEach(su -> {
                try {
                    su.save(map, true, true);
                } catch(Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            System.err.println(new JSON(map).toPrettyString());
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }

     */

    @Override
    protected boolean process() {
        close();
        InventoryTransaction it = new InventoryTransaction(getTransactionManager(), DateUtility.addDay(DateUtility.today(), -20));
        it.setDataPickupMode(true);
        InventoryItemType pn = StoredObject.list(InventoryItemType.class, true).filter(t -> !t.isSerialized())
                .findFirst();
        System.err.println(pn);
        InventoryItem item = pn.createItem();
        item.setInTransit(false);
        item.setSerialNumber("MY-SERIAL");
        System.err.println(item);
        InventoryStore store = StoredObject.get(InventoryStore.class, "Name='Main Store'", true);
        System.err.println(store);
        Entity entity = StoredObject.get(Entity.class);
        System.err.println(entity);
        Quantity q = pn.getUnitOfMeasurement().add(new BigDecimal(5));
        System.err.println(q);
        it.purchase(item, q,"TEST (Date:Mar 3, 2021)", store.getStoreBin(), entity);
        try {
            it.save();
        } catch(Exception e) {
            error(e);
        }
        return true;
    }
}
