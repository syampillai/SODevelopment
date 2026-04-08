package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectLinkField;
import com.storedobject.ui.ReferenceLinkGrid;

import java.util.stream.Stream;

public class AssignStoresAndLocations extends ObjectEditor<SystemUser> {

    public AssignStoresAndLocations() {
        super(SystemUser.class, EditorAction.EDIT | EditorAction.SEARCH);
    }

    @Override
    protected boolean includeField(String fieldName) {
        if(fieldName.endsWith(".l")) {
            return fieldName.equals("Stores.l") || fieldName.equals("Locations.l");
        }
        return super.includeField(fieldName);
    }

    @Override
    public boolean isFieldEditable(String fieldName) {
        return fieldName.endsWith(".l");
    }

    @Override
    protected void customizeLinkField(ObjectLinkField<?> field) {
        if(field.getFieldName().equals("Locations.l")) {
            //noinspection unchecked
            ((ReferenceLinkGrid<InventoryVirtualLocation>)field.getGrid()).setFilter("Type IN (4, 5, 10, 11, 16)");
        }
        super.customizeLinkField(field);
    }

    @Override
    public Stream<Link<?>> extraLinks() {
        Link<InventoryStore> link1 = new Link<>(SystemUser.class);
        link1.setName("Stores");
        link1.setLinkClass(InventoryStore.class);
        link1.setAny();
        Link<InventoryVirtualLocation> link2 = new Link<>(SystemUser.class);
        link2.setName("Locations");
        link2.setLinkClass(InventoryVirtualLocation.class);
        return Stream.of(link1, link2);
    }
}
