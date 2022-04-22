package com.storedobject.ui;

import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.inventory.LocationField;
import com.storedobject.ui.inventory.ReturnMaterial;
import com.storedobject.vaadin.DataForm;

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
        InventoryLocation ms = LocationField.getLocation("Main Store", 0);
        InventoryLocation other = LocationField.getLocation("Bonded Store", 0);
        new ReturnMaterial(ms, other).execute();
        return true;
    }
}
