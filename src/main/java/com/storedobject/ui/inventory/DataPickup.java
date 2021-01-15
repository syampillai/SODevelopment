package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryTransaction;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class DataPickup extends DataForm implements Transactional {

    private static final String CAPTION = "Inventory Data Pick-up";
    private final LocationField locationField = LocationField.create("Location", 0);

    public DataPickup() {
        super(CAPTION);
        addField(locationField);
        setRequired(locationField);
    }

    @Override
    protected boolean process() {
        InventoryLocation location = locationField.getValue();
        if(location.getType() == 0) {
            close();
            new GetItems(CAPTION, location, InventoryTransaction.forDataPickup(getTransactionManager()),
                    m -> m.getTransaction().dataPickup(m.getItem(), m.getLocationTo())).execute();
            return true;
        }
        warning("Movement to '" + location + "' is not supported");
        return false;
    }
}
