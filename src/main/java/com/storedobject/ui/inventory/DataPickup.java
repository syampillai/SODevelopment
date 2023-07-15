package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryTransaction;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class DataPickup extends DataForm implements Transactional {

    private static final String CAPTION = "Inventory Data Pick-up";
    private final LocationField locationField = LocationField.create("Location", 0);
    private final TextField referenceField = new TextField("Reference");

    public DataPickup() {
        super(CAPTION);
        addField(locationField, referenceField);
        setRequired(locationField);
        setRequired(referenceField);
    }

    @Override
    protected boolean process() {
        InventoryLocation location = locationField.getValue();
        if(location.getType() == 0) {
            close();
            new GetItems(CAPTION, location,
                    InventoryTransaction.forDataPickup(getTransactionManager(), referenceField.getValue()),
                    m -> m.getTransaction().dataPickup(m.getItem(), m.getLocationTo())).execute();
            return true;
        }
        warning("Movement to '" + location + "' is not supported");
        return false;
    }
}
