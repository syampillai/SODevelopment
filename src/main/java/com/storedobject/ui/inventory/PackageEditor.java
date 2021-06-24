package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditor;

public class PackageEditor extends ObjectEditor<InventoryPackage> {

    protected StoredObject parent;
    protected int linkType;

    public PackageEditor() {
        super(InventoryPackage.class);
        setCaption("Package");
    }

    public void setParent(StoredObject parent, int linkType) {
        this.parent = parent;
        this.linkType = linkType;
    }

    public void setParent(StoredObject parent) {
        setParent(parent, 0);
    }

    @Override
    public void save(Transaction t) throws Exception {
        if(parent == null || Id.isNull(parent.getId())) {
            throw new SOException("Parent not set!");
        }
        InventoryPackage ip = getObject();
        super.save(t);
        parent.addLink(t, ip, linkType);
    }
}
