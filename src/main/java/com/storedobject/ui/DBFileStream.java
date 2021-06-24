package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.StoredObject;

public class DBFileStream extends DBResource {

    public DBFileStream(String fileName) {
        super(FileData.get(fileName));
    }

    public DBFileStream(Id fileDataId) {
        this(StoredObject.get(FileData.class, fileDataId, true));
    }

    public DBFileStream(FileData fileData) {
        super(fileData);
    }

    public DBFileStream(MediaFile mediaFile) {
        super(mediaFile);
    }
}