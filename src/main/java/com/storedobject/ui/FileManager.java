package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.FileFolder;

public class FileManager extends ObjectForestBrowser<FileFolder> {

    public FileManager(Application application) {
        this(application, null, null);
    }

    public FileManager(Application application, String path) {
        this(application, path,null);
    }

    public FileManager(Application application, String path, String caption) {
        this(path, application.getLogicTitle(caption));
    }

    public FileManager(String path) {
        this(path, null);
    }

    public FileManager(String path, String caption) {
        super(FileFolder.class, StringList.EMPTY);
    }
}