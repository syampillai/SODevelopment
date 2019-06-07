package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

public class DBResource extends StreamResource {

    public DBResource(Id streamDataId) {
        this(StoredObject.get(StreamData.class, streamDataId));
    }

    public DBResource(StreamData streamData) {
        super(null, (StreamResourceWriter) null);
    }

    public DBResource(FileData fileData) {
        this(new Id());
    }
}
