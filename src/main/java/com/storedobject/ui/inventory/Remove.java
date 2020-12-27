package com.storedobject.ui.inventory;

import com.storedobject.core.Entity;
import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Remove extends DataForm {

    private static final String CAPTION = "Remove Items";
    private final DateField dateField = new DateField("Date");
    private final LocationField locationFromField = LocationField.create("Store", 0);
    private final ChoiceField actionField =
            new ChoiceField("Remove by", RemoveItems.removalAction);
    private final ObjectField<Entity> orgField = new ObjectField<>("To", Entity.class);

    public Remove() {
        super(CAPTION);
        addField(dateField, locationFromField, actionField, orgField);
        setRequired(locationFromField);
        actionField.addValueChangeListener(e -> setFieldVisible(RemoveItems.requiresEntity(e.getValue()), orgField));
    }

    @Override
    protected boolean process() {
        InventoryLocation from = locationFromField.getValue();
        int action = actionField.getValue();
        Entity entity = null;
        if(RemoveItems.requiresEntity(action)) {
            entity = orgField.getObject();
            if(entity == null) {
                warning("Please select organization");
                return false;
            }
        }
        close();
        new RemoveItems(from, action, entity, dateField.getValue()).execute();
        return true;
    }
}
