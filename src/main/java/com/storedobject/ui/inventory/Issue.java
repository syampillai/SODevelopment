package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class
Issue extends DataForm {

    private static final String CAPTION = "Stock Issue";
    private final DateField dateField = new DateField("Date");
    private final LocationField locationFromField = LocationField.create("From", 0);
    private final LocationField locationToField = LocationField.create("To", 0, 3, 5, 8, 11, 16);

    public Issue() {
        super(CAPTION);
        addField(dateField, locationFromField, locationToField);
        setRequired(locationFromField);
        setRequired(locationToField);
    }

    @Override
    protected boolean process() {
        InventoryLocation from = locationFromField.getValue(), to = locationToField.getValue();
        if(from.getId().equals(to.getId())) {
            warning("Please select different locations");
            return false;
        }
        if(from.getType() != 0) {
            warning("Can not be issued from '" + from.toDisplay() + "'");
            return false;
        }
        close();
        new IssueItems(CAPTION, from, to, dateField.getValue()).execute();
        return true;
    }
}
