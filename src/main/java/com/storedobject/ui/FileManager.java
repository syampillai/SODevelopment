package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.ObjectForest;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.MultiSelectGrid;
import com.storedobject.vaadin.TokensField;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FileManager extends ObjectForestBrowser<FileFolder> implements Transactional {

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
        getDataProvider().getForest().setListLinks(FileViewer::list);
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
        addConstructedListener(e -> expandRecursively(Stream.of(root), 1));
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
    public void createFooters() {
        appendFooter().join().setComponent(
                new ELabel("Right-click on Files/Folders for options related to circulation or version control",
                        "blue")
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

    private void circulate(FileFolder folder, boolean recursive) {
        new ActionForm("All files under the folder '"  + folder.getName() + "'" +
                (recursive ? " and its sub-folders" : "") + " will be circulated now!\nAre you sure?",
                () -> transact(t -> folder.circulate(t, recursive))).execute();
    }

    private void circulate(FileData file) {
        new ActionForm("The file '" + file.getName() + "' will be circulated now!\nAre you sure?",
                () -> transact(file::circulate)).execute();
    }

    private void createNewVersion(FileData file) {
        new ActionForm("A new version will replace the current version of the file '" + file.getName() +
                "'.\nAre you sure?", () -> createNewVersion2(file)).execute();
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
                refresh(currentLinkNode);
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
        new ActionForm("The previous version of the file '" + file.getName() +
                "' will be restored and current version will be lost!\nAre you sure?",
                () -> restorePrevVersion2(file)).execute();
    }

    private void restorePrevVersion2(FileData file) {
        transact(file::restore);
        refresh(currentLinkNode);
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

    private List<SystemUser> sus;
    private List<SystemUser> sus() {
        if(sus == null) {
            sus = StoredObject.list(SystemUser.class).toList();
        }
        return sus;
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
        private final TokensField<SystemUser> ffUser = new TokensField<>("Users", sus()),
                fUser = new TokensField<>("Users", sus());

        public CEditor() {
            super("Manage Circulation", false);
            setButtonsAtTop(true);
            ffUser.setItemLabelGenerator(u -> u.getPerson().getName());
            fUser.setItemLabelGenerator(u -> u.getPerson().getName());
            add(ffCaption);
            addField(ffGroup, ffUser);
            add(fCaption);
            addField(fGroup, fUser);
            setColumns(1);
        }

        void set(FileFolder ff, FileData f) {
            this.ff = ff;
            this.f = f;
            ffCaption.clearContent().append("Members of folder ")
                    .append("'" + ff.getName() + "'", "blue").update();
            ffGroup.setValue(new HashSet<>(ff.listLinks(SystemUserGroup.class).toList()));
            ffUser.setValue(new HashSet<>(ff.listLinks(SystemUser.class).toList()));
            setFieldReadOnly(f != null, ffGroup, ffUser);
            if(f == null) {
                fCaption.setVisible(false);
                fGroup.setVisible(false);
                fUser.setVisible(false);
            } else {
                fCaption.setVisible(true);
                fGroup.setVisible(true);
                fUser.setVisible(true);
                fCaption.clearContent().append("Members of document ")
                        .append("'" + f.getName() + "'", "blue").update();
                fGroup.setValue(new HashSet<>(f.listLinks(SystemUserGroup.class).toList()));
                fUser.setValue(new HashSet<>(f.listLinks(SystemUser.class).toList()));
            }
        }

        @Override
        protected boolean process() {
            return transact(this::save);
        }

        private void save(Transaction t) throws Exception {
            if(f == null) {
                saveLinks(t, SystemUserGroup.class, ff, ffGroup.getValue());
                saveLinks(t, SystemUser.class, ff, ffUser.getValue());
                return;
            }
            saveLinks(t, SystemUserGroup.class, f, fGroup.getValue());
            saveLinks(t, SystemUser.class, f, fUser.getValue());
        }

        private <T extends StoredObject> void saveLinks(Transaction t, Class<T> objectClass, StoredObject parent,
                                                        Set<T> children) throws Exception {
            List<T> all = parent.listLinks(objectClass).toList();
            List<T> toAdd = new ArrayList<>();
            children.forEach(c -> {
                if(all.contains(c)) {
                    all.remove(c);
                } else  {
                    toAdd.add(c);
                }
            });
            for(T child: all) {
                parent.removeLink(t, child);
            }
            for(T child: toAdd) {
                parent.addLink(t, child);
            }
        }
    }
}