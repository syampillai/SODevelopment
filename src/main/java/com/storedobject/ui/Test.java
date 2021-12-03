package com.storedobject.ui;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.inventory.LocationField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() throws ClassNotFoundException {
        super("Test");
        Class<? extends StoredObject> c = (Class<? extends StoredObject>) JavaClassLoader.getLogic("com.engravsystems.emqim.engineering.ItemType");
        System.err.println(c);
        System.err.println(ObjectGetField.canCreate(c));
    }

    @Override
    protected boolean process() {
        return false;
    }
}
