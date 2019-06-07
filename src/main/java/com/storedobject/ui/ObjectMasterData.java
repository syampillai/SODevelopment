package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.Transaction;

import java.util.List;

public final class ObjectMasterData<M extends StoredObject> {

    public ObjectMasterData(M object) {
    }

    public M getObject() {
        return null;
    }

    public void addLink(ObjectLinkData<?> link) {
    }

    public List<ObjectLinkData<?>> getLinks() {
        return null;
    }

    public ObjectLinkData<?> getLink(String fieldName) {
        return null;
    }

    public void save(Transaction transaction) throws Exception {
    }

    public void setParentObject(StoredObject parentObject, int parentLinkType) {
    }

    public StoredObject getParentObject() {
        return null;
    }

    public int getParentLinkType() {
        return 0;
    }
}
