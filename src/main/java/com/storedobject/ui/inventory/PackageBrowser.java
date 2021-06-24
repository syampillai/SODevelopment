package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectBrowser;

public class PackageBrowser extends ObjectBrowser<InventoryPackage> {

    public PackageBrowser() {
        super(InventoryPackage.class);
        setCaption("Packages");
    }

    @Override
    public ObjectSearchBuilder<InventoryPackage> createSearchBuilder(StringList searchColumns) {
        return null;
    }

    public void setParent(StoredObject parent, int linkType) {
        ((PackageEditor)getObjectEditor()).setParent(parent, linkType);
        load();
    }

    public void setParent(StoredObject parent) {
        setParent(parent, 0);
    }

    @Override
    protected PackageEditor createObjectEditor() {
        return new PackageEditor();
    }

    @Override
    public void load() {
        PackageEditor pe = (PackageEditor) getObjectEditor();
        if(pe.parent == null || Id.isNull(pe.parent.getId())) {
            clear();
            return;
        }
        load(pe.parent.listLinks(pe.linkType, getObjectClass()));
    }
}
