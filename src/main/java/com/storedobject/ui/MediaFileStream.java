package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.StoredObject;

public class MediaFileStream extends DBResource {

    public MediaFileStream(String name) {
        super(MediaFile.get(name));
    }

    public MediaFileStream(Id mediaFileId) {
        this(StoredObject.get(MediaFile.class, mediaFileId));
    }

    public MediaFileStream(MediaFile mediaFile) {
        super(mediaFile);
    }

    public MediaFileStream(FileData fileData) {
        super(fileData);
    }
}