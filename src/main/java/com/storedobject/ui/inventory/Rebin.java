package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Rebin extends DataForm implements Transactional {

    private static final String CAPTION = "Rebin";
    private final DateField dateField = new DateField("Date");
    private final LocationField locationField = LocationField.create("Store", 0);

    public Rebin() {
        super(CAPTION, "Ok", "Quit");
        addField(dateField, locationField);
        setRequired(locationField);
    }

    @Override
    protected boolean process() {
        InventoryLocation location = locationField.getValue();
        close();
        new IssueItems(CAPTION, location, location, dateField.getValue()).execute();
        return true;
    }
}
