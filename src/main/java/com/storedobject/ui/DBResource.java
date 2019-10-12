package com.storedobject.ui;

import com.storedobject.core.*;
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

    public DBResource(MediaFile mediaFile) {
        this(new Id());
    }
}
