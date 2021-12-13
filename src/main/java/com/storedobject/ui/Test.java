package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.StoredObject;

public class Test extends FileViewerGrid {

    public Test() {
        StoredObject.list(FileData.class, true).limit(10).forEach(f -> {
            FileData fd = new FileData();
            fd.setName(f.getName());
            fd.setFile(f.getFile());
            fd.makeVirtual();
            add(fd);
        });
    }
}
