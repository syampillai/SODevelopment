package com.storedobject.ui.util;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.vaadin.flow.component.grid.Grid;

import java.util.Objects;
import java.util.stream.Stream;

public class ExtraInfoValue<T extends StoredObject> implements StoredObjectLink<T> {

    private final ExtraInfo<T> extraInfo;
    private final StoredObject master;
    private T info;
    private int status = -1; // 0: Added, 1: Edited, 2: Deleted, 3: Undeleted
    private String old = "";

    public ExtraInfoValue(ExtraInfo<T> extraInfo) {
        this.extraInfo = extraInfo;
        this.master = extraInfo.master;
        load();
    }

    ExtraInfo<T> getExtraInfo() {
        return extraInfo;
    }

    public T getInfo() {
        return info;
    }

    private void load() {
        if(master == null) {
            info = null;
            old = "";
            return;
        }
        info = master.listLinks(extraInfo.infoClass).single(false);
        if(info == null) {
            try {
                info = extraInfo.infoClass.getDeclaredConstructor().newInstance();
                ObjectEditor<?> oe = (ObjectEditor<?>)extraInfo.field.getDependentView();
                oe.extraInfoCreated(info);
                Grid<?> grid = oe.getGrid();
                if(grid instanceof ObjectBrowser ob) {
                    //noinspection unchecked
                    ob.extraInfoCreated(master, info);
                }
                status = 0;
            } catch(Throwable ignored) {
            }
            old = "";
        } else {
            status = -1;
            try {
                old = info.stringify();
                ObjectEditor<?> oe = (ObjectEditor<?>)extraInfo.field.getDependentView();
                oe.extraInfoLoaded(info);
                Grid<?> grid = oe.getGrid();
                if(grid instanceof ObjectBrowser ob) {
                    //noinspection unchecked
                    ob.extraInfoLoaded(master, info);
                }
            } catch(Exception e) {
                throw new SORuntimeException(e);
            }
        }
    }

    void changed() {
        try {
            String now = info == null ? "" : info.stringify();
            if(now.equals(old)) {
               return;
            }
            status = switch(status) {
                case 0, 3 -> status;
                default -> 1;
            };
        } catch(Exception e) {
            throw new SORuntimeException(e);
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
    public boolean contains(Object info) {
        return this.info.equals(info);
    }

    @Override
    public boolean isAdded(T info) {
        return this.info == info && status == 0;
    }

    @Override
    public boolean isDeleted(T info) {
        return this.info == info && status == 2;
    }

    @Override
    public boolean isEdited(T info) {
        if(status != 1) {
            changed();
        }
        return this.info == info && status == 1;
    }

    @Override
    public Stream<T> streamAll() {
        return info == null ? Stream.empty() : Stream.of(info);
    }

    @Override
    public int size() {
        return info == null ? 0 : 1;
    }

    @Override
    public boolean append(T info) {
        if(info != this.info) {
            return false;
        }
        status = -1;
        return true;
    }

    @Override
    public boolean add(T info) {
        if(info != this.info) {
            return false;
        }
        status = 0;
        return true;
    }

    @Override
    public boolean delete(T info) {
        if(info != this.info) {
            return false;
        }
        status = 2;
        return true;
    }

    @Override
    public boolean undelete(T info) {
        if(info != this.info) {
            return false;
        }
        status = 3;
        return true;
    }

    @Override
    public boolean update(T info) {
        if(info != this.info) {
            return false;
        }
        status = 1;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(extraInfo, master, info, status, old);
    }
}
