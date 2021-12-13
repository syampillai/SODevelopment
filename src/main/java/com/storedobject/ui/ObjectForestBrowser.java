package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LoadFilterButtons;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.lang.reflect.Constructor;

import static com.storedobject.core.EditorAction.*;

public class ObjectForestBrowser<T extends StoredObject> extends ObjectForest<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, filter, load, view, report, excel, audit, exit;
    private String allowedActions;
    private LoadFilterButtons<T> loadFilterButtons;
    private final boolean anchorsExist;

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
        super(objectClass, columns, (actions & ALLOW_ANY) == ALLOW_ANY);
        anchorsExist = !ClassAttribute.get(getObjectClass()).getAnchors().isEmpty();
        addConstructedListener(o -> con());
        getDataProvider().setLoadCallBack(this::loadInt);
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
            boolean nm = !MasterObject.class.isAssignableFrom(getObjectClass());
            if(nm && ((actions & NEW) == NEW)) {
                add = new Button("New", this);
                buttonPanel.add(add);
            }
            if(nm && ((actions & EDIT) == EDIT)) {
                edit = new Button("Edit", this);
                buttonPanel.add(edit);
            }
            if(nm && ((actions & DELETE) == DELETE)) {
                delete = new ConfirmButton("Delete", this);
                ((ConfirmButton)delete).setPreconfirm(this::checkDelete);
                buttonPanel.add(delete);
            }
            if((actions & RELOAD) == RELOAD) {
                loadFilterButtons = new LoadFilterButtons<>(this, filterColumns);
                filter = loadFilterButtons.getFilterButton();
                load = loadFilterButtons.getLoadButton();
                loadFilterButtons.addTo(buttonPanel);
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
            if(nm && ((actions & AUDIT) == AUDIT)) {
                audit = new Button("Audit", "user", this);
                buttonPanel.add(audit);
            }
        }
        exit = new Button("Exit", this);
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
    public static <O extends StoredObject> ObjectForestBrowser<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                         int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "ForestBrowser"));
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
        ObjectSearchBuilder<?> sb;
        if(loadFilterButtons == null || (sb = loadFilterButtons.getSearchBuilder()) == null) {
            return buttonPanel;
        }
        VerticalLayout v = new VerticalLayout(buttonPanel);
        Component f = null;
        if(sb instanceof Component) {
            f = (Component) sb;
        }
        if(f != null) {
            v.add(f);
        }
        v.setPadding(false);
        v.setMargin(false);
        v.setSpacing(false);
        return v;
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
        clearAlerts();
        if (c == exit) {
            close();
            return;
        }
        if(clickedInt(c)) {
            return;
        }
        super.clicked(c);
    }

    private void loadInt(Runnable loadFunction) {
        if(anchorsExist) {
            ObjectEditor<T> oe = getObjectEditor(getObjectClass());
            if(oe.anchorAction) {
                oe.executeAnchorForm(() -> loadInt(loadFunction));
                return;
            }
            getDelegatedLoader().getSystemFilter().setCondition(oe.getAnchorFilter());
        }
        Application a = Application.get();
        if(a == null) {
            loadFunction.run();
        } else {
            a.access(loadFunction::run);
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
            if(o instanceof com.storedobject.core.ObjectForest.LinkNode) {
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
                warning("Select the type of item you want to add or select nothing");
            }
            return true;
        }
        if(c == edit || c == delete || c == view) {
            if(o == null) {
                warning(ObjectBrowser.NOTHING_SELECTED);
                return true;
            }
            O object = selected();
            if(object == null) {
                actionWarn(c);
                return true;
            }
            if(!getObjectClass().isAssignableFrom(o.getClass())) {
                if(!(o instanceof com.storedobject.core.ObjectForest.LinkObject)) {
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
        if(!(o instanceof com.storedobject.core.ObjectForest.LinkObject)) {
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
            message("Deleted");
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
