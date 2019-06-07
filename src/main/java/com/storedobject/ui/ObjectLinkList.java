package com.storedobject.ui;

import com.storedobject.core.StoredObject;

public class ObjectLinkList<L extends StoredObject> extends EditableObjectList<L> implements ObjectLinkData<L> {

    private final int linkType;
    private final String orderBy;
    private StoredObject master;
    private final String fieldName;

    public ObjectLinkList(String fieldName, Class<L> objectClass, int linkType, String orderBy) {
        this(fieldName, objectClass, linkType, orderBy, false);
    }

    public ObjectLinkList(String fieldName, Class<L> objectClass, int linkType, String orderBy, boolean allowAny) {
        super(objectClass, allowAny);
        this.fieldName = fieldName;
        this.linkType = linkType;
        this.orderBy = orderBy;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public int getLinkType() {
        return linkType;
    }

    @Override
    public void reloadAll() {
        load(linkType, master, null, orderBy);
        refreshAll();
    }

    public void setMaster(StoredObject master) {
        this.master = master;
        refreshAll();
    }

    @Override
    public StoredObject getMaster() {
        return master;
    }
}
