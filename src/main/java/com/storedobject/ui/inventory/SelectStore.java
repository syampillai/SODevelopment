package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.vaadin.DataForm;
import java.util.List;

public class SelectStore extends DataForm {

    private final Logic logic;

    private final ObjectComboField<InventoryStore> storeField;

    public SelectStore() {
        super("Select Store");
        List<InventoryStore> stores = Application.get().getTransactionManager().getUser()
                .listLinks(InventoryStore.class, true).toList();
        if(stores.isEmpty()) {
            stores = StoredObject.list(InventoryStore.class, true).toList();
        }
        storeField = new ObjectComboField<>("Store", InventoryStore.class, stores);
        addField(storeField);
        setRequired(storeField);
        this.logic = Application.get().getRunningLogic();
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
        assignment.store = storeField.getValue();
        if(logic != null) {
            Application.get().execute(logic);
        }
        return true;
    }

    public static InventoryStoreBin get() {
        InventoryStore store = getStore();
        return store == null ? null : store.getStoreBin();
    }

    public static InventoryStore getStore() {
        Application a = Application.get();
        if(a == null) {
            return null;
        }
        Assignment assignment = a.getData(Assignment.class);
        if(assignment != null && assignment.store != null) {
            return assignment.store;
        }
        InventoryStore store = a.getTransactionManager().getUser().listLinks(InventoryStore.class, true)
                .single(false);
        if(store == null) {
            return null;
        }
        if(assignment == null) {
            assignment = new Assignment();
            a.setData(Assignment.class, assignment);
        }
        assignment.store = store;
        return store;
    }

    static class Assignment {
        InventoryStore store;
        InventoryLocation location;
    }
}
