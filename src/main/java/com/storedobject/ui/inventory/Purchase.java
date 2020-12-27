package com.storedobject.ui.inventory;

import com.storedobject.core.Entity;
import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;

public class Purchase extends DataForm {

    private static final String CAPTION = "Purchase";
    private final DateField dateField = new DateField("Date");
    private final TextField referenceField = new TextField("Reference");
    private final ObjectField<Entity> entityField = new ObjectField<>("Supplier", Entity.class);
    private final LocationField locationField = LocationField.create("Location", 0);

    public Purchase() {
        super(CAPTION);
        addField(dateField, referenceField, entityField, locationField);
        setRequired(referenceField);
        setRequired(entityField);
        setRequired(locationField);
    }

    @Override
    protected boolean process() {
        Entity supplier = entityField.getObject();
        String ref = referenceField.getText().trim();
        if(ref.isEmpty()) {
            return false;
        }
        InventoryLocation location = locationField.getValue();
        if(location.getType() == 0) {
            close();
            GetItems gi = new GetItems(CAPTION, location, dateField.getValue(), ref,
                    m -> m.getTransaction().purchase(m.getItem(), m.getReference(), m.getLocationTo(), supplier));
            ELabelField s = new ELabelField("Supplier");
            s.append(supplier.toDisplay()).update();
            gi.addField(s);
            gi.execute();
            return true;
        }
        warning("Movement to '" + location + "' is not supported");
        return false;
    }
}
