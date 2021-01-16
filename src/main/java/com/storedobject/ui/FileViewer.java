package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.util.ObjectForestSupplier;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

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
        filter.setVisible(false);
        addConstructedListener(o -> con());
        getDataSupplier().setListLinks(new FolderLister());
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
        createHTMLHierarchyColumn("_Name", this::nodeDisplay);
    }

    private void con() {
        addComponentColumn(this::createViewMenu).setFlexGrow(0).setWidth("120px");
        addComponentColumn(this::createDownloadMenu).setFlexGrow(0).setWidth("150px");
        addColumn(v -> " ").setFlexGrow(0);
    }

    @Override
    public ObjectSearchBuilder<FileFolder> createSearchBuilder(StringList searchColumns) {
        return null;
    }

    @Override
    public Component createHeader() {
        return null;
    }

    @Override
    public void load() {
        setRoot(getRoot());
    }

    private String nodeDisplay(Object item) {
        if(item instanceof ObjectForestSupplier.LinkObject) {
            item = ((ObjectForestSupplier.LinkObject) item).getObject();
        }
        return "<iron-icon icon='vaadin:" +
                (item instanceof FileData ? "file-o" : "folder-o") +
                "' style='height:15px;'></iron-icon>" +
                (item instanceof StoredObject ? HTMLText.encode(((StoredObject) item).toDisplay()) : item.toString());
    }

    @Override
    public String getColumnCaption(String columnName) {
        if(columnName.equals("_Name")) {
            return "Files & Folders";
        }
        return super.getColumnCaption(columnName);
    }

    private Component createViewMenu(Object item) {
        Component vc = createViewMenu2(item);
        return vc == null ? new Span() : vc;
    }

    private Component createViewMenu2(Object item) {
        if(item instanceof ObjectForestSupplier.LinkObject) {
            item = ((ObjectForestSupplier.LinkObject) item).getObject();
        }
        if(item instanceof FileData) {
            FileData fileData = (FileData)item;
            final StreamData sd = fileData.getFile();
            final String view, icon;
            if(sd != null) {
                if(sd.isAudio()) {
                    view = "Play";
                    icon = "volume_up";
                } else if(sd.isVideo()) {
                    view = "Play";
                    icon = "movie";
                } else if(sd.isImage() || sd.getMimeType().equals("application/pdf")){
                    view = "View";
                    icon = view;
                } else {
                    return null;
                }
            } else {
                return null;
            }
            return new Button(view, icon, e -> getApplication().view(fileData.getName(), sd)).asSmall();
        }
        return null;
    }

    private Component createDownloadMenu(Object item) {
        if(item instanceof ObjectForestSupplier.LinkObject) {
            item = ((ObjectForestSupplier.LinkObject) item).getObject();
        }
        if(item instanceof FileData) {
            FileData fileData = (FileData) item;
            return new Button("Download", e -> getApplication().download(fileData.getFile())).asSmall();
        }
        return new Span();
    }


    static class FolderLister implements ObjectForestSupplier.ListLinks {

        @Override
        public ObjectIterator<? extends StoredObject> list(StoredObjectUtility.Link<?> link, StoredObject master) {
            if(master instanceof FileFolder) {
                if(FileFolder.class.isAssignableFrom(link.getObjectClass())) {
                    return ((FileFolder) master).listFolders();
                }
                return ((FileFolder) master).listFiles();
            }
            return null;
        }
    }
}