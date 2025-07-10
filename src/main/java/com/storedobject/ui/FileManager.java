package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.ObjectForest;
import com.storedobject.core.*;
import com.storedobject.report.FileCirculationStatus;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class FileManager extends ObjectForestBrowser<FileFolder> implements Transactional, CloseableView {

    private List<DocumentConfiguration> dcs;
    private CEditor cEditor;

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
        if(FileFolder.getRoot() == null) {
            transact(t -> {
                FileFolder ff = new FileFolder();
                ff.setName("/");
                ff.save(t);
            });
        }
        getDataProvider().getForest().setListLinks(FileViewer::list);
        if(path == null) {
            path = caption;
        } else if(!path.startsWith("/")) {
            path = "/" + path;
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
        addConstructedListener(e -> expandRecursively(Stream.of(root), 2));
        GridContextMenu<Object> contextMenu = new GridContextMenu<>(this);
        GridMenuItem<Object> circF = contextMenu.addItem("Circulate This File", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileData f) {
                circulate(f);
            }
        });
        GridMenuItem<Object> circFF = contextMenu.addItem("Circulate This Folder", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileFolder ff) {
                circulate(ff, false);
            }
        });
        GridMenuItem<Object> circFFAll = contextMenu.addItem("Circulate This & Sub-folders", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileFolder ff) {
                circulate(ff, true);
            }
        });
        contextMenu.addItem("Manage Circulation", e -> manageCirculation());
        contextMenu.addItem("View Circulation Status", e -> circulationStatus());
        GridMenuItem<Object> newVer = contextMenu.addItem("Create New Version", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileData f) {
                createNewVersion(f);
            }
        });
        GridMenuItem<Object> viewPrevVer = contextMenu.addItem("View Previous Version", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileData f) {
                viewPrevVersion(f);
            }
        });
        GridMenuItem<Object> prevVer = contextMenu.addItem("Restore Previous Version", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileData f) {
                restorePrevVersion(f);
            }
        });
        GridMenuItem<Object> allowedTypes = contextMenu.addItem("Set Allowed Document Types", e -> {
            if(o(e.getItem().orElse(null)) instanceof FileFolder ff) {
                allowedFileTypes(ff);
            }
        });
        contextMenu.setDynamicContentHandler(o -> {
            clearAlerts();
            if(o instanceof ObjectForest.LinkObject lo) {
                select(o);
                getSelected();
                o = lo.getObject();
            } else {
                return false;
            }
            if(o instanceof FileFolder) {
                circF.setVisible(false);
                circFF.setVisible(true);
                circFFAll.setVisible(true);
                newVer.setVisible(false);
                viewPrevVer.setVisible(false);
                prevVer.setVisible(false);
                allowedTypes.setVisible(true);
            } else if(o instanceof FileData) {
                circF.setVisible(true);
                circFF.setVisible(false);
                circFFAll.setVisible(false);
                newVer.setVisible(true);
                FileData p = ((FileData)o).getPreviousVersion();
                viewPrevVer.setVisible(p != null);
                prevVer.setVisible(p != null);
                allowedTypes.setVisible(false);
            }
            return true;
        });
    }

    private Object o(Object o) {
        return o instanceof ObjectForest.LinkObject lo ? lo.getObject() : o;
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(new ELabel("Name Search:"), new TreeSearchField<>(this),
                new Button("Exit", e -> close()));
    }

    @Override
    public void createFooters() {
        appendFooter().join().setComponent(
                new ELabel("Right-click on Files/Folders for options related to circulation and version control",
                        Application.COLOR_SUCCESS)
        );
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

    private void manageCirculation() {
        if(currentLinkObject == null) {
            return;
        }
        StoredObject so = currentLinkObject.getObject(), pso = currentLinkNode.getParent();
        if(so instanceof FileData f) {
            cEditor((FileFolder) pso, f);
        } else {
            cEditor((FileFolder) so, null);
        }
    }

    private void circulationStatus() {
        if(currentLinkObject == null) {
            return;
        }
        StoredObject so = currentLinkObject.getObject();
        FileCirculationStatus cs;
        if(so instanceof FileData f) {
            cs = new FileCirculationStatus(getApplication(), f);
        } else {
            cs = new FileCirculationStatus(getApplication(), (FileFolder) so);
        }
        Application.get().view("Circulation Status", cs);
    }

    private void circulate(FileFolder folder, boolean recursive) {
        ELabel m = new ELabel();
        m.append("All files under the folder '", Application.COLOR_SUCCESS)
                .append(folder.getName(), Application.COLOR_ERROR)
                .append("'" + (recursive ? " and its sub-folders" : "")
                        + " will be circulated now!", Application.COLOR_SUCCESS)
                .newLine().append("Are you sure?", Application.COLOR_ERROR).update();
        new ActionForm(m, () -> transact(t -> folder.circulate(t, recursive))).execute();
    }

    private void circulate(FileData file) {
        ELabel m = new ELabel();
        m.append("The file '", Application.COLOR_SUCCESS).append(file.getName(), Application.COLOR_ERROR)
                .append("' will be circulated now!", Application.COLOR_SUCCESS).newLine()
                .append("Are you sure?", Application.COLOR_ERROR).update();
        new ActionForm(m, () -> transact(file::circulate)).execute();
    }

    private void createNewVersion(FileData file) {
        ELabel m = new ELabel();
        m.append("A new version will replace the current version of the file '", Application.COLOR_SUCCESS)
                .append(file.getName(), Application.COLOR_ERROR)
                .append("'", Application.COLOR_SUCCESS).newLine()
                .append("Are you sure?", Application.COLOR_ERROR).update();
        new ActionForm(m, () -> createNewVersion2(file)).execute();
    }

    private <F extends FileData> void createNewVersion2(F file) {
        @SuppressWarnings("unchecked") Class<F> fClass = (Class<F>) file.getClass();
        ObjectEditor<F> oe = ObjectEditor.create(fClass);
        oe.setSaver(e -> transact(t -> {
           e.save(t);
           file.replaceWith(t, e.getObject());
        }));
        oe.addObjectChangedListener(new ObjectChangedListener<>() {
            @Override
            public void saved(F object) {
                refreshCurrentNode(object);
            }
        });
        try {
            @SuppressWarnings("unchecked") F f = (F) file.copy();
            f.setFile((Id)null);
            oe.editObject(f, getView());
        } catch(Exception e) {
            warning(e);
        }
    }

    private void viewPrevVersion(FileData file) {
        @SuppressWarnings("rawtypes") ObjectEditor oe = ObjectEditor.create(file.getClass());
        oe.setCaption("Previous Version");
        //noinspection unchecked
        oe.viewObject(file.getPreviousVersion(), getView(true));
    }

    private void restorePrevVersion(FileData file) {
        ELabel m = new ELabel();
        m.append("The previous version of the file '", Application.COLOR_SUCCESS)
                .append(file.getName(), Application.COLOR_ERROR)
                .append("' will be restored and current version will be lost!", Application.COLOR_SUCCESS)
                .newLine().append("Are you sure?", Application.COLOR_ERROR).update();
        new ActionForm(m, () -> restorePrevVersion2(file)).execute();
    }

    private void restorePrevVersion2(FileData file) {
        AtomicReference<FileData> p = new AtomicReference<>(null);
        transact(t -> p.set(file.restore(t)));
        refreshCurrentNode(p.get());
    }

    private void allowedFileTypes(FileFolder folder) {
        if(dcs == null) {
            dcs = StoredObject.list(DocumentConfiguration.class, null, "Name").toList();
        }
        if(dcs.isEmpty()) {
            warning("No document types configured!");
            return;
        }
        MultiSelectGrid<DocumentConfiguration> dcGrid =
                new MultiSelectGrid<>(DocumentConfiguration.class, dcs,
                        StringList.create("Name"), s -> addFileTypes(folder, s));
        dcGrid.setCaption("Select Document Types");
        dcGrid.select(folder.getConfiguration());
        dcGrid.execute();
    }

    private void addFileTypes(FileFolder folder, Set<DocumentConfiguration> dcs) {
        transact(t -> {
           folder.removeAllLinks(t, DocumentConfiguration.class);
           for(DocumentConfiguration dc: dcs) {
               folder.addLink(t, dc);
           }
        });
        folder.resetCache();
    }

    private List<SystemUserGroup> sugs;

    private List<SystemUserGroup> sugs() {
        if(sugs == null) {
            sugs = StoredObject.list(SystemUserGroup.class).toList();
        }
        return sugs;
    }

    private void cEditor(FileFolder ff, FileData f) {
        if(cEditor == null) {
            cEditor = new CEditor();
        }
        cEditor.set(ff, f);
        cEditor.execute();
    }

    private class CEditor extends DataForm {

        private FileFolder ff;
        private FileData f;
        private final ELabel ffCaption = new ELabel(), fCaption = new ELabel();
        private final TokensField<SystemUserGroup> ffGroup = new TokensField<>("Groups", sugs()),
                fGroup = new TokensField<>("Groups", sugs());

        public CEditor() {
            super("Manage Circulation", false);
            setButtonsAtTop(true);
            add(ffCaption);
            addField(ffGroup);
            add(fCaption);
            addField(fGroup);
            setColumns(1);
        }

        void set(FileFolder ff, FileData f) {
            this.ff = ff;
            this.f = f;
            ffCaption.clearContent().append("Members of folder ")
                    .append("'" + ff.getName() + "'", Application.COLOR_SUCCESS).update();
            ffGroup.setValue(new HashSet<>(ff.listLinks(SystemUserGroup.class).toList()));
            setFieldReadOnly(f != null, ffGroup);
            if(f == null) {
                fCaption.setVisible(false);
                fGroup.setVisible(false);
            } else {
                fCaption.setVisible(true);
                fGroup.setVisible(true);
                fCaption.clearContent().append("Members of document ")
                        .append("'" + f.getName() + "'", Application.COLOR_SUCCESS).update();
                fGroup.setValue(new HashSet<>(f.listLinks(SystemUserGroup.class).toList()));
            }
        }

        @Override
        protected boolean process() {
            return transact(this::save);
        }

        private void save(Transaction t) throws Exception {
            if(f == null) {
                saveLinks(t, ff, ffGroup.getValue());
                return;
            }
            saveLinks(t, f, fGroup.getValue());
        }

        private void saveLinks(Transaction t, StoredObject parent,
                                                        Set<SystemUserGroup> children) throws Exception {
            List<SystemUserGroup> all = parent.listLinks(SystemUserGroup.class).toList();
            List<SystemUserGroup> toAdd = new ArrayList<>();
            children.forEach(c -> {
                if(all.contains(c)) {
                    all.remove(c);
                } else  {
                    toAdd.add(c);
                }
            });
            for(SystemUserGroup child: all) {
                parent.removeLink(t, child);
            }
            for(SystemUserGroup child: toAdd) {
                parent.addLink(t, child);
            }
        }
    }
}