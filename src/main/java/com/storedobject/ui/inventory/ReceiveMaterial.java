package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStoreBin;
import com.storedobject.ui.HTMLText;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

/**
 * Receive material from another location.
 *
 * @author Syam
 */
public class ReceiveMaterial extends DataForm {

    private static final String[] TYPE_4_STORES = new String[] {
            "GRN - Purchases",
            "Returned from Other Locations",
            "Transferred from Other Stores",
            "Returned from Repair/Maintenance Organizations",
            "Lease Returns"
    };
    private static final String[] TYPE_4_LOCATIONS = new String[] {
            "Issued from Stores",
            "Transferred from Stores",
    };
    private boolean valuesSet = false;
    private final LocationField toField;
    private final ChoiceField typeFieldForStores = new ChoiceField("Type", TYPE_4_STORES);
    private final ChoiceField typeFieldForLocations = new ChoiceField("Type", TYPE_4_LOCATIONS);

    /**
     * Constructor.
     */
    public ReceiveMaterial() {
        this(null, null, -1);
    }

    /**
     * Constructor.
     *
     * @param locationName Name of the location.
     */
    public ReceiveMaterial(String locationName) {
        this(LocationField.getLocation(locationName, true, 0, 4, 5, 11), type(locationName));
    }

    /**
     * Constructor.
     *
     * @param to Location to.
     */
    public ReceiveMaterial(InventoryLocation to) {
        this(to, -1);
    }

    /**
     * Constructor.
     *
     * @param to Location to.
     * @param type Type of receipt.
     */
    public ReceiveMaterial(InventoryLocation to, int type) {
        this(null, to, type);
    }

    private ReceiveMaterial(LocationField toField, InventoryLocation to, int type) {
        super("Receive Material");
        if(toField == null) {
            if(to == null) {
                this.toField = LocationField.create("Receive to", 0, 4, 5, 11);
            } else {
                this.toField = LocationField.create("To", to);
                setFieldReadOnly(this.toField);
            }
            this.toField.setLabel("Receive to");
        } else {
            this.toField = toField;
            if(to != null) {
                this.toField.setValue(to);
                setFieldReadOnly(this.toField);
            }
        }
        addField(this.toField, typeFieldForStores, typeFieldForLocations);
        setRequired(this.toField);
        toChanged(this.toField.getValue());
        this.toField.addValueChangeListener(e -> toChanged(e.getValue()));
        if(type >= 0 && to != null) {
            if(to instanceof InventoryStoreBin) {
                if(type < TYPE_4_STORES.length) {
                    typeFieldForStores.setValue(type);
                    typeFieldForStores.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type, TYPE_4_STORES);
                }
            } else {
                if(type < TYPE_4_LOCATIONS.length) {
                    typeFieldForLocations.setValue(type);
                    typeFieldForLocations.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type, TYPE_4_LOCATIONS);
                }
            }
        }
    }

    private void showTypeError(int type, String[] values) {
        HTMLText h = new HTMLText("Incorrect type value: " + type);
        h.newLine().append("Please set one of the following values.");
        for(int i = 0; i < values.length; i++) {
            h.newLine().append(i + ": " + values[i]);
        }
        throw new SORuntimeException(h.getHTML());
    }

    private static int type(String locationName) {
        int p = locationName.indexOf('|');
        if(p < 0) {
            return -1;
        }
        locationName = locationName.substring(p + 1).trim();
        try {
            return Integer.parseInt(locationName);
        } catch(Throwable error) {
            if(locationName.isEmpty()) {
                return -1;
            }
            throw new SORuntimeException("Can't find type value from '" + locationName + "'");
        }
    }

    private void toChanged(InventoryLocation location) {
        boolean isStore = location instanceof InventoryStoreBin;
        typeFieldForStores.setVisible(isStore);
        typeFieldForLocations.setVisible(!isStore);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(valuesSet) {
            process();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        InventoryLocation to = toField.getValue();
        if(to == null) {
            return false;
        }
        close();
        if(to instanceof InventoryStoreBin) {
            switch(typeFieldForStores.getValue()) {
                case 0:
                    GRN grn = new GRN(((InventoryStoreBin)to).getStore());
                    grn.execute();
                    return true;
                case 1:
                    new ReceiveMaterialReturned(to).execute();
                    return true;
                case 2:
                    new ReceiveMaterialTransferred(to).execute();
                    return true;
                case 3:
                    new ReceiveReturnedItems(3, (InventoryStoreBin) to).execute();
                    return true;
                case 4:
                    new ReceiveReturnedItems(8, (InventoryStoreBin) to).execute();
                    return true;
            }
        } else {
            switch(typeFieldForLocations.getValue()) {
                case 0:
                    new ReceiveMaterialRequested(to).execute();
                    return true;
                case 1:
                    new ReceiveMaterialTransferred(to).execute();
                    return true;
            }
        }
        return true;
    }
}
