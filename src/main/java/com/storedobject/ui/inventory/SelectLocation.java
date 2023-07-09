package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryVirtualLocation;
import com.storedobject.core.Logic;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.vaadin.DataForm;

import java.util.List;

public class SelectLocation extends DataForm {

    private final Logic logic;

    private final ObjectComboField<InventoryLocation> locField;

    public SelectLocation(int... types) {
        super("Select Your Location");
        List<InventoryLocation> locations = Application.get().getTransactionManager().getUser()
                .listLinks(InventoryVirtualLocation.class, "Status=0").map(o -> (InventoryLocation)o).toList();
        if(types.length > 0) {
            locations.removeIf(loc -> !checkType(loc, types));
        }
        if(locations.isEmpty()) {
            locField = LocationField.create(types);
        } else {
            locField = new ObjectComboField<>(InventoryLocation.class, locations);
        }
        locField.setLabel("Location");
        addField(locField);
        setRequired(locField);
        this.logic = Application.get().getRunningLogic();
    }

    private static boolean checkType(InventoryLocation loc, int... types) {
        int type = loc.getType();
        for(int t: types) {
            if(t == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean process() {
        Application a = Application.get();
        close();
        SelectStore.Assignment assignment = a.getData(SelectStore.Assignment.class);
        if(assignment == null) {
            assignment = new SelectStore.Assignment();
            a.setData(SelectStore.Assignment.class, assignment);
        }
        assignment.location = locField.getValue();
        if(logic != null) {
            Application.get().execute(logic);
        }
        return true;
    }

    public static InventoryLocation get(int... types) {
        Application a = Application.get();
        if(a == null) {
            return null;
        }
        SelectStore.Assignment assignment = a.getData(SelectStore.Assignment.class);
        if(assignment != null && assignment.location != null && checkType(assignment.location, types)) {
            return assignment.location;
        }
        InventoryLocation loc = a.getTransactionManager().getUser()
                .listLinks(InventoryVirtualLocation.class, "Status=0")
                .filter(l -> checkType(l, types)).single(false);
        if(loc == null) {
            return null;
        }
        if(assignment == null) {
            assignment = new SelectStore.Assignment();
            a.setData(SelectStore.Assignment.class, assignment);
        }
        assignment.location = loc;
        return loc;
    }
}
