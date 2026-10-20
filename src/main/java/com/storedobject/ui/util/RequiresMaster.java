package com.storedobject.ui.util;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;

import java.util.function.Consumer;

public interface RequiresMaster {

    void setMaster(StoredObject master);

    StoredObject getMaster();

    default Id getMasterId() {
        StoredObject so = getMaster();
        return so == null ? null : so.getId();
    }

    void reloadMaster();

    void setReloadListener(Consumer<RequiresMaster> reloadMaster);
}
