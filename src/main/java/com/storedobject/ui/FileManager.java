package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.FileFolder;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;

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
        getDataSupplier().setListLinks(new FileViewer.FolderLister());
        if(path == null) {
            path = caption;
        }
        FileFolder root;
        if(path == null || path.isEmpty() || (root = FileFolder.get(path)) == null) {
            throw new SORuntimeException("Folder not found - " + path);
        }
        setRoot(root);
        if(caption == null) {
            int p;
            if((p = path.lastIndexOf('/')) >= 0) {
                path = path.substring(p + 1);
            }
            caption = StringUtility.makeLabel(path);
        }
        setCaption(caption);
    }

    @Override
    public void load() {
        setRoot(getRoot());
    }

    @Override
    protected boolean canAdd(StoredObject parentObject) {
        if(parentObject == null) {
            warning("Can not add at this location!");
            return false;
        }
        return super.canAdd(parentObject);
    }

    @Override
    protected boolean canEdit(StoredObject object) {
        if(object instanceof FileFolder && ((FileFolder) object).isRoot()) {
            warning("Can not edit the root folder!");
            return false;
        }
        return super.canEdit(object);
    }

    @Override
    protected boolean canDelete(StoredObject object) {
        if(object instanceof FileFolder && ((FileFolder) object).isRoot()) {
            warning("Can not delete the root folder!");
            return false;
        }
        return super.canDelete(object);
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("_Name".equals(columnName)) {
            return "Folders & Files";
        }
        return super.getColumnCaption(columnName);
    }
}