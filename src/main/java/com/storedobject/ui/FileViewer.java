package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.FileFolder;
import com.storedobject.vaadin.CloseableView;

public class FileViewer extends ObjectForestViewer<FileFolder> implements CloseableView {

    public FileViewer(Application application) {
        this(application, null, null);
    }

    public FileViewer(Application application, String path) {
        this(application, path,null);
    }

    public FileViewer(Application application, String path, String caption) {
        this(path, application.getLogicTitle(caption));
    }

    public FileViewer(String path) {
        this(path, null);
    }

    public FileViewer(String path, String caption) {
        super(FileFolder.class, StringList.EMPTY);
    }
}