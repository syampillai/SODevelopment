package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.ui.util.ObjectForestSupplier;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

import static com.storedobject.core.EditorAction.*;

public class ObjectForestBrowser<T extends StoredObject> extends ObjectForest<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, filter, load, view, report, excel, audit, exit;
    private String allowedActions;
    private ObjectSearchBuilder<T> searchBuilder;

    public ObjectForestBrowser(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectForestBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectForestBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectForestBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        this(objectClass, columns, actions, null, caption, null);
    }

    ObjectForestBrowser(Class<T> objectClass, Iterable<String> columns, int actions, Iterable<String> filterColumns,
                        String caption, String allowedActions) {
        this(columns, actions, filterColumns, caption, allowedActions,
                new ObjectForestSupplier<>(objectClass, null, null, (actions & ALLOW_ANY) == ALLOW_ANY));
    }

    ObjectForestBrowser(Iterable<String> columns, int actions, Iterable<String> filterColumns,
                        String caption, String allowedActions, AbstractObjectForestSupplier<T, Void> dataProvider) {
        super(columns, dataProvider);
        addConstructedListener(o -> con());
        setCaption(caption);
        this.allowedActions = allowedActions;
        if(actions < 0) {
            actions = (-actions) | StoredObjectUtility.statusUI(getObjectClass());
        }
        actions = filterActionsInternal(actions);
        if((actions & ALLOW_ANY) == ALLOW_ANY) {
            actions &= ~ALLOW_ANY;
        }
        if(actions > 0) {
            if((actions & NEW) == NEW) {
                add = new Button("New", this);
                buttonPanel.add(add);
            }
            if((actions & EDIT) == EDIT) {
                edit = new Button("Edit", this);
                buttonPanel.add(edit);
            }
            if((actions & DELETE) == DELETE) {
                delete = new ConfirmButton("Delete", this);
                ((ConfirmButton)delete).setPreconfirm(this::checkDelete);
                buttonPanel.add(delete);
            }
            if((actions & RELOAD) == RELOAD) {
                boolean smallList = filterColumns == null && ObjectHint.isSmallList(getObjectClass(), isAllowAny());
                if(!smallList) {
                    filter = new Button("Filter", e -> reload(false));
                    buttonPanel.add(filter);
                }
                load = new Button("Load", e -> reload(true));
                buttonPanel.add(load);
                if(smallList) {
                    actions &= ~RELOAD;
                    load();
                }
            }
            if((actions & VIEW) == VIEW) {
                view = new Button("View", this);
                buttonPanel.add(view);
            }
            if((actions & PDF) == PDF) {
                report = new Button("Report", this);
                buttonPanel.add(report);
            }
            if((actions & EXCEL) == EXCEL) {
                excel = new Button("Excel", this);
                buttonPanel.add(excel);
            }
            if((actions & AUDIT) == AUDIT) {
                audit = new Button("Audit", "user", this);
                buttonPanel.add(audit);
            }
        }
        exit = new Button("Exit", this);
        if((actions & RELOAD) == RELOAD) {
            searchBuilder = createSearchBuilder(filterColumns == null ? null : StringList.create(filterColumns));
        }
    }

    @SuppressWarnings("unchecked")
    public ObjectForestBrowser(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                ObjectBrowser.actions(className, Application.get().getServer().isDeveloper()), null,
                Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className));
    }

    private void con() {
        createExtraButtons();
        addExtraButtons();
        if(exit != null && !isCloseable()) {
            buttonPanel.add(exit);
        }
    }

    public static <O extends StoredObject> ObjectForestBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectForestBrowser<O> create(Class<O> objectClass, Iterable<String> columns, int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "ForestBrowser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance(columns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance(columns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance(actions);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectForestBrowser<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectForestBrowser<>(objectClass, columns, actions, title);
    }

    @Override
    final void protect() {
        if(add != null) {
            buttonPanel.remove(add);
            add = null;
        }
        if(edit != null) {
            buttonPanel.remove(edit);
            edit = null;
        }
        if(delete != null) {
            buttonPanel.remove(delete);
            delete = null;
        }
    }

    @Override
    public Component createHeader() {
        if(searchBuilder == null) {
            return buttonPanel;
        }
        VerticalLayout v = new VerticalLayout(buttonPanel);
        Component f = null;
        if(searchBuilder instanceof Component) {
            f = (Component) searchBuilder;
        }
        if(f != null) {
            v.add(f);
        }
        v.setPadding(false);
        v.setMargin(false);
        v.setSpacing(false);
        return v;
    }

    public ObjectSearchBuilder<T> createSearchBuilder(StringList searchColumns) {
        return new ObjectFilter<>(getObjectClass(), searchColumns, s -> filter.addTheme(ThemeStyle.ERROR));
    }

    protected boolean isActionAllowed(String action) {
        return allowedActions == null || allowedActions.contains(action);
    }

    protected void removeAllowedAction(String action) {
        if(allowedActions != null) {
            allowedActions = allowedActions.replace(action, "-");
        }
    }

    int filterActionsInternal(int actions) {
        return filterActions(actions);
    }

    protected int filterActions(int actions) {
        return actions;
    }

    protected void createExtraButtons() {
    }

    protected void addExtraButtons() {
    }

    @Override
    public void setSplitView() {
        super.setSplitView();
        if(view != null) {
            buttonPanel.remove(view);
            view = null;
        }
    }

    private boolean canModify(StoredObject object) {
        if(object.isVirtual()) {
            warning("Neither you can edit/delete virtual entries nor add any further entries under that!");
            return false;
        }
        return true;
    }

    protected boolean canDelete(StoredObject object) {
        return canModify(object);
    }

    protected boolean canEdit(StoredObject object) {
        return canModify(object);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean canAdd(StoredObject parentObject) {
        return parentObject == null || canModify(parentObject);
    }

    @Override
    public void clicked(Component c) {
        if (c == exit) {
            close();
            return;
        }
        if(clickedInt(c)) {
            return;
        }
        super.clicked(c);
    }

    @Override
    public void loaded() {
        super.loaded();
        Application a = Application.get();
        if(a == null) {
            confReloadButton();
        } else {
            a.access(this::confReloadButton);
        }
    }

    private void confReloadButton() {
        if(load != null) {
            if ("Load".equals(load.getText())) {
                load.setIcon("reload");
                load.setText("Reload");
            }
            load.setVisible(isFullyLoaded());
        }
    }

    private void reload(boolean again) {
        if(filter != null) {
            filter.removeTheme(ThemeStyle.ERROR);
        }
        if(searchBuilder == null) {
            getDataSupplier().clear(false);
            load();
        } else {
            if(searchBuilder instanceof Form && !((Form)searchBuilder).commit()) {
                return;
            }
            String filter = searchBuilder.getFilterText();
            if(filter == null) {
                if(!again && isFullyLoaded()) {
                    filter(null);
                } else {
                    getDataSupplier().clear(false);
                    load();
                }
            } else {
                Predicate<T> filterFunction = null;
                if(!again && isFullyLoaded() && isFullyCached()) {
                    filterFunction = searchBuilder.getFilterPredicate();
                }
                if(again || filterFunction == null) {
                    if(again) {
                        getDataSupplier().clear(false);
                    }
                    load(filter);
                } else {
                    filter(filterFunction);
                }
            }
        }
    }

    private <O extends StoredObject> boolean clickedInt(Component c) {
        Object o = getSelected();
        if(c == add) {
            if(o == null || this instanceof ObjectForestEditor) { // Adding another root
                if(!canAdd(null)) {
                    return false;
                }
                ObjectEditor<T> oe = getObjectEditor(getObjectClass());
                oe.setParentObject(null, 0);
                oe.addObject(getView(), true);
                return true;
            }
            if(o instanceof ObjectForestSupplier.LinkNode) {
                if(currentLinkNode.getLink().isDetail()) {
                    ObjectEditor<?> oe = getObjectEditor(currentLinkNode.getLink().getObjectClass());
                    StoredObject parent = currentLinkNode.getParent();
                    if(!canAdd(parent)) {
                        return false;
                    }
                    oe.setParentObject(parent, currentLinkNode.getLink().getType());
                    oe.addObject(getView(), true);
                } else {
                    StoredObject parent = currentLinkNode.getParent();
                    if(!canAdd(parent)) {
                        return false;
                    }
                    ObjectSearcher<?> osx = getObjectSearcher(currentLinkNode.getLink().getObjectClass());
                    @SuppressWarnings("unchecked") ObjectSearcher<StoredObject> os = (ObjectSearcher<StoredObject>)osx;
                    ObjectsSetter<StoredObject> setter = new ObjectsSetter<>() {

                        @Override
                        public void setObjects(Iterable<StoredObject> objects) {
                            transact(t -> {
                                for(StoredObject object : objects) {
                                    parent.addLink(t, object, currentLinkNode.getLink().getType());
                                }
                                refresh(o);
                                expand(o);
                            });
                        }

                        @Override
                        public void setObject(StoredObject object) {
                            setObjects(ObjectIterator.create(object));
                        }
                    };
                    os.search(getTransactionManager().getEntity(), setter);
                }
            } else {
                Application.warning("Select the type of item you want to add or select nothing");
            }
            return true;
        }
        if(c == edit || c == delete || c == view) {
            if(o == null) {
                Application.warning(ObjectBrowser.NOTHING_SELECTED);
                return true;
            }
            O object = selected();
            if(object == null) {
                actionWarn(c);
                return true;
            }
            if(!getObjectClass().isAssignableFrom(o.getClass())) {
                if(!(o instanceof ObjectForestSupplier.LinkObject)) {
                    actionWarn(c);
                    return true;
                }
                if(!currentLinkNode.getLink().isDetail()) {
                    if(c == edit) {
                        actionWarn(edit);
                        return true;
                    } else if(c == delete) {
                        transact(t -> currentLinkNode.getParent().removeLink(t, currentLinkObject.getObject(), currentLinkNode.getLink().getType()));
                        refresh(currentLinkNode);
                        expand(currentLinkNode);
                        deselectAll();
                        return true;
                    }
                }
            }
            if(c == delete) {
                doDelete(object);
                return true;
            }
            if(c == view) {
                doView(object);
                return true;
            }
            if(c == edit) {
                doEdit(object);
                return true;
            }
        }
        return false;
    }

    private void actionWarn(Component c) {
        warning("That can't be " + (c == edit ? "edit" : (c == delete ? "delet" : "view")) + "ed!");
    }

    private boolean checkDelete() {
        Object o = getSelected();
        if(o == null) {
            warning(ObjectBrowser.NOTHING_SELECTED);
            return false;
        }
        if(getObjectClass().isAssignableFrom(o.getClass())) {
            //noinspection unchecked
            return canDelete((T)o);
        }
        if(!(o instanceof ObjectForestSupplier.LinkObject)) {
            warning("That can't be deleted!");
            return false;
        }
        return true;
    }

    private <O extends StoredObject> void doDelete(O object) {
        @SuppressWarnings("unchecked") ObjectEditor<O> editor = getObjectEditor((Class<O>)object.getClass());
        editor.setObject(object);
        editor.deleteObject();
        boolean deleted = editor.getObject() == null;
        if(deleted) {
            Application.message("Deleted");
        }
    }

    @SuppressWarnings("unchecked")
    private <O extends StoredObject> void doEdit(O object) {
        if(!canEdit(object)) {
            return;
        }
        getObjectEditor((Class<O>)object.getClass()).editObject(object, getView(), true);
    }

    @SuppressWarnings("unchecked")
    private <O extends StoredObject> void doView(O object) {
        getObjectEditor((Class<O>)object.getClass()).viewObject(object, getView(), true);
    }
}
