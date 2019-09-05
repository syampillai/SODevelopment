package com.storedobject.core;

public final class PseudoTransaction {

    PseudoTransaction(TransactionManager tm, Object key) {
    }

    public void commit(Transaction transaction) throws Exception {
    }

    public void clear() {
    }

    public Id save(StoredObject object) {
        return null;
    }

    public void delete(StoredObject object) {
    }

    public void undelete(StoredObject object) {
    }

    public void addLink(Id parentId, Id childId) {
        addLink(parentId, childId,0);
    }

    public void addLink(StoredObject parent, Id childId) {
        addLink(parent, childId,0);
    }

    public void addLink(StoredObject parent, StoredObject child) {
        addLink(parent, child,0);
    }

    public void addLink(Id parentId, StoredObject child) {
        addLink(parentId, child,0);
    }

    public void addLink(Id parentId, Id childId, int linkType) {
    }

    public void addLink(StoredObject parent, Id childId, int linkType) {
    }

    public void addLink(StoredObject parent, StoredObject child, int linkType) {
    }

    public void addLink(Id parentId, StoredObject child, int linkType) {
    }

    public void removeLink(Id parentId, Id childId) {
        removeLink(parentId, childId,0);
    }

    public void removeLink(StoredObject parent, Id childId) {
    }

    public void removeLink(Id parentId, StoredObject child) {
        removeLink(parentId, child.getId());
    }

    public void removeLink(StoredObject parent, StoredObject child) {
        removeLink(parent, child.getId());
    }

    public void removeLink(Id parentId, Id childId, int linkType) {
    }

    public void removeLink(StoredObject parent, Id childId, int linkType) {
    }

    public void removeLink(Id parentId, StoredObject child, int linkType) {
    }

    public void removeLink(StoredObject parent, StoredObject child, int linkType) {
    }

    public void removeAllLinks(Id parentId) {
        removeAllLinks(parentId,0);
    }

    public void removeAllLinks(StoredObject parent) {
        removeAllLinks(parent, 0);
    }

    public void removeAllLinks(Id parentId, Class<? extends StoredObject> linkClass) {
    }

    public void removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass) {
    }

    public void removeAllLinks(Id parentId, int linkType) {
    }

    public void removeAllLinks(StoredObject parent, int linkType) {
    }

    public void removeAllLinks(Id parentId, Class<? extends StoredObject> linkClass, int linkType) {
    }

    public void removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass, int linkType) {
    }

    public Id getId(StoredObject object) {
        return null;
    }

    public void replace(Id idToreplace, StoredObject newObject) {
    }
}