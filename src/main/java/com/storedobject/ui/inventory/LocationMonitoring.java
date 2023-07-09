package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

public abstract class LocationMonitoring<I extends InventoryItem, L extends InventoryLocation>
        extends ObjectGrid<I> {

    private final Class<L> locationClass;
    private ObjectInput<L> locationField;
    private String locationLabel;

    public LocationMonitoring(String caption, Class<L> locationClass) {
        this(caption, null, locationClass);
    }

    public LocationMonitoring(String caption, Class<I> itemClass, Class<L> locationClass) {
        //noinspection unchecked
        super(itemClass == null ? (Class<I>) InventoryItem.class : itemClass,
                StringList.create("Name", "PartNumber", "SerialNumber", "Location"), true);
        this.locationClass = locationClass;
        setCaption(caption);
    }

    @Override
    public Component createHeader() {
        ButtonLayout buttonLayout = new ButtonLayout();
        buttonLayout.add(new ELabel(locationLabel()));
        buttonLayout.add((Component) locationField);
        buttonLayout.add(new ELabel("(Leave it empty for all)"));
        buttonLayout.add(new Button("Load", e -> loadItems()));
        buttonLayout.add(new Button("Exit", e -> close()));
        return buttonLayout;
    }

    @Override
    public void constructed() {
        getColumnByKey("Location").setHeader(locationLabel);
        loadItems();
    }

    private void loadItems() {
        L location = locationField().getObject();
        ObjectIterator<L> locations;
        if(location == null) {
            int type = getLocationType();
            if(type < 0) {
                locations = StoredObject.list(locationClass, true);
            } else {
                locations = StoredObject.list(locationClass, "Type=" + type + " AND Status=0");
            }
        } else {
            locations = ObjectIterator.create(location);
        }
        load(locations.expand(loc ->
                StoredObject.list(getObjectClass(), "Location=" + loc.getId(), true)));
    }

    public Object getName(InventoryItem inventoryItem) {
        return inventoryItem.getPartNumber().getName();
    }

    public Object getPartNumber(InventoryItem inventoryItem) {
        return inventoryItem.getPartNumber().getPartNumber();
    }

    public Object getLocation(InventoryItem inventoryItem) {
        return inventoryItem.getLocation();
    }

    @Override
    public void loaded() {
        clearAlerts();
        message("Items: " + size());
    }

    private String locationLabel() {
        if(locationField == null) {
            locationField = getLocationField();
            locationLabel = locationField.getLabel();
            if(locationLabel == null) {
                locationLabel = "Location";
            } else {
                locationField.setLabel(null);
            }
            int type = getLocationType();
            if(type >= 0) {
                locationField.setFilter("Type=" + type);
            }
            if(locationField instanceof ObjectComboField<L> f) {
                f.setClearButtonVisible(true);
            }
            @SuppressWarnings("unchecked") HasValue<?, L> field = (HasValue<?, L>) locationField;
            field.addValueChangeListener(e -> loadItems());
        }
        return locationLabel;
    }

    private ObjectInput<L> locationField() {
        if(locationField == null) {
            locationLabel();
        }
        return locationField;
    }

    protected abstract ObjectInput<L> getLocationField();

    protected int getLocationType() {
        return -1;
    }
}
