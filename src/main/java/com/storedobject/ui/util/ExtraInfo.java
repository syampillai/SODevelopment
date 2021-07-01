package com.storedobject.ui.util;

import com.storedobject.core.ExtraInfoDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;

import java.util.stream.Stream;

public class ExtraInfo<T extends StoredObject> implements StoredObjectLink<T> {

    ExtraInfoObjectField<T> field;
    final Class<T> infoClass;
    private StoredObject master;
    private T info;
    private byte status = -1; // 0: Added, 1: Edited, 2: Deleted, 3: Undeleted
    private final int displayOrder;
    
    public ExtraInfo(ExtraInfoDefinition infoDefinition) {
        //noinspection unchecked
        this.infoClass = (Class<T>) infoDefinition.getExtraInfoClass();
        displayOrder = infoDefinition.getDisplayOrder();
    }

    public T getInfo() {
        return info;
    }

    public void setMaster(StoredObject master) {
        if(master == this.master) {
            return;
        }
        this.master = master;
        if(master == null) {
            info = null;
            if(field != null) {
                field.setObject(info);
            }
            return;
        }
        info = master.listLinks(infoClass).single(false);
        if(info == null) {
            try {
                info = infoClass.getDeclaredConstructor().newInstance();
                status = 1;
            } catch(Throwable ignored) {
            }
        } else {
            status = -1;
        }
        if(field != null) {
            field.setObject(info);
        }
    }

    @Override
    public StoredObject getMaster() {
        return master;
    }

    @Override
    public String getName() {
        return "Extra Info";
    }

    @Override
    public boolean contains(T info) {
        return this.info.equals(info);
    }

    @Override
    public boolean isAdded(T info) {
        return this.info == info && status == 1;
    }

    @Override
    public boolean isDeleted(T info) {
        return this.info == info && status == 3;
    }

    @Override
    public boolean isEdited(T info) {
        return this.info == info && status == 2;
    }

    @Override
    public Stream<T> streamAll() {
        return Stream.of(info);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean append(T info) {
        this.info = info;
        status = -1;
        return true;
    }

    @Override
    public boolean add(T info) {
        this.info = info;
        status = 1;
        return true;
    }

    @Override
    public boolean delete(T info) {
        this.info = info;
        status = 3;
        return true;
    }

    @Override
    public boolean undelete(T info) {
        this.info = info;
        status = 4;
        return true;
    }

    @Override
    public boolean update(T info) {
        this.info = info;
        status = 2;
        return true;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
