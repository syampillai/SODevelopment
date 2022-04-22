package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.ObjectForest;
import com.storedobject.core.*;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileViewer extends ObjectForestViewer<FileFolder> implements CloseableView {

    private final Id personId;
    private final Map<Id, FileCirculation> circs = new HashMap<>();
    private final Set<Id> noCircs = new HashSet<>();

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
        this(folder(path, caption), caption);
    }

    public FileViewer(FileFolder root, String caption) {
        super(FileFolder.class, StringList.EMPTY);
        getDataProvider().getForest().setListLinks(FileViewer::list);
        filter.setVisible(false);
        addConstructedListener(o -> con());
        if(root == null) {
            throw new SORuntimeException("No root folder specified!");
        }
        setRoot(root);
        if(caption == null) {
            caption = StringUtility.makeLabel(root.getName());
        }
        setCaption(caption);
        createHTMLHierarchyColumn("_Name", this::nodeDisplay);
        personId = getTransactionManager().getUser().getPersonId();
    }

    private static FileFolder folder(String path, String caption) {
        FileFolder folder;
        if(path == null) {
            path = caption;
        }
        if(path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        if(path == null || (folder = FileFolder.get(path)) == null) {
            throw new SORuntimeException("Folder not found - " + path);
        }
        return folder;
    }

    private void con() {
        addComponentColumn(this::createViewMenu).setFlexGrow(0).setWidth("120px");
        addComponentColumn(this::createDownloadMenu).setFlexGrow(0).setWidth("150px");
        addComponentColumn(this::createReadMenu).setFlexGrow(0).setWidth("200px");
        addComponentColumn(this::createReadStamp).setFlexGrow(0).setWidth("200px");
        addColumn(v -> " ").setFlexGrow(0);
        expandRecursively(Stream.of(getRoot()), 2);
    }

    @Override
    public ObjectSearchBuilder<FileFolder> createSearchBuilder(
            StringList searchColumns, Consumer<ObjectSearchBuilder<FileFolder>> changeConsumer) {
        return super.createSearchBuilder(searchColumns, changeConsumer);
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
        if(item instanceof ObjectForest.LinkObject) {
            item = ((ObjectForest.LinkObject) item).getObject();
        }
        return "<vaadin-icon icon='vaadin:" +
                (item instanceof FileData ? "file-o" : "folder-o") +
                "' style='height:15px;'></vaadin-icon>" +
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
        Object o = item;
        if(item instanceof ObjectForest.LinkObject lo) {
            o = lo.getObject();
        }
        if(o instanceof FileData fileData) {
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
            return new Button(view, icon, e -> opened(item)).asSmall();
        }
        return null;
    }

    private void opened(Object item) {
        select(item);
        getSelected();
        if(item instanceof ObjectForest.LinkObject lo && lo == currentLinkObject) {
            if(lo.getObject() instanceof FileData fileData) {
                FileCirculation fc = circ(fileData);
                if(fc != null && fc.getStatus() == 0) {
                    fc.setStatus(1);
                    transact(fc::save);
                    circs.remove(fileData.getId());
                    refreshCurrentNode(lo.getObject());
                }
                getApplication().view(fileData.getName(), fileData.getFile());
            }
        }
    }

    private Component createDownloadMenu(Object item) {
        if(item instanceof ObjectForest.LinkObject lo) {
            if(lo.getObject() instanceof FileData fileData) {
                return new Button("Download", e -> getApplication().download(fileData.getFile())).asSmall();
            }
        }
        return new Span();
    }

    private Component createReadStamp(Object item) {
        if(item instanceof ObjectForest.LinkObject lo) {
            if(lo.getObject() instanceof FileData fileData) {
                String rs = fileData.getReadStamp();
                if(rs != null) {
                    return new Span(rs);
                }
            }
        }
        return new Span();
    }

    private Component createReadMenu(Object item) {
        FileCirculation fc = circ(item);
        if(fc != null) {
            return fc.getStatus() == 1 ? new Button("Confirm & Sign", VaadinIcon.SIGN_IN, e -> markAsRead(item)) :
                    new ELabel(fc.getStatusValue(), fc.getStatus() == 0 ? "red" : "blue");
        }
        return new Span();
    }

    private void markAsRead(Object item) {
        select(item);
        getSelected();
        if(item instanceof ObjectForest.LinkObject lo && lo == currentLinkObject) {
            if(lo.getObject() instanceof FileData fileData) {
                final StreamData sd = fileData.getFile();
                ELabel m = new ELabel("I confirm that I ");
                if(sd.isAudio()) {
                    m.append("listened to the audio");
                } else if(sd.isVideo()) {
                    m.append("watched the video");
                } else if(sd.isImage()) {
                    m.append("viewed the content of");
                } else {
                    m.append("read the content of");
                }
                m.append(" '", "blue").append(fileData.getName(), "blue").
                        append("'", "blue").append(" and understood it.");
                m.update();
                new ActionForm(m, () -> {
                    FileCirculation fc = circ(fileData);
                    if(fc != null) {
                        fc.setStatus(2);
                        if(transact(fc::save)) {
                            refreshCurrentNode(lo.getObject());
                        }
                    }
                }, null, "Confirm", "Cancel").execute();
            }
        }
    }

    public static ObjectIterator<? extends StoredObject> list(StoredObjectUtility.Link<?> link, StoredObject master) {
        if(master instanceof FileFolder) {
            if(FileFolder.class.isAssignableFrom(link.getObjectClass())) {
                return ((FileFolder) master).listFolders();
            }
            return ((FileFolder) master).listFiles();
        }
        return ObjectIterator.create();
    }

    private FileCirculation circ(Object o) {
        if(o == null) {
            return null;
        }
        if(o instanceof ObjectForest.LinkObject lo) {
            o = lo.getObject();
        }
        return o instanceof FileData f ? circ(f) : null;
    }

    private FileCirculation circ(FileData f) {
        if(f == null) {
            return null;
        }
        FileCirculation fc = circs.get(f.getId());
        if(fc == null) {
            if(!noCircs.contains(f.getId())) {
                fc = f.getCirculation(personId);
                if(fc == null) {
                    noCircs.add(f.getId());
                } else {
                    circs.put(f.getId(), fc);
                }
            }
        }
        return fc;
    }
}