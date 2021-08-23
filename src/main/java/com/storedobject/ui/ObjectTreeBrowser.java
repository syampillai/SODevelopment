package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Constructor;

import static com.storedobject.core.EditorAction.*;
import static com.storedobject.core.EditorAction.AUDIT;
import static com.storedobject.core.EditorAction.EXCEL;

public class ObjectTreeBrowser<T extends StoredObject> extends ObjectTree<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected Button add, edit, delete, reload, view, report, excel, audit, exit;
    private String allowedActions;

    public ObjectTreeBrowser(Class<T> objectClass) {
        this(objectClass, ALL);
    }

    public ObjectTreeBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, ALL);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions) {
        this(objectClass, columns, actions, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption) {
        this(objectClass, columns, actions, caption, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, ObjectTreeBuilder treeBuilder) {
        this(objectClass, null, treeBuilder, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder) {
        this(objectClass, columns, treeBuilder, null);
    }

    public ObjectTreeBrowser(Class<T> objectClass, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, null, treeBuilder, caption);
    }

    public ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, ObjectTreeBuilder treeBuilder, String caption) {
        this(objectClass, columns, 0, caption, null, treeBuilder);
    }

    ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions) {
        this(objectClass, columns, actions, caption, allowedActions, ObjectTreeBuilder.create((actions & ALLOW_ANY) == ALLOW_ANY));
    }

    ObjectTreeBrowser(Class<T> objectClass, Iterable<String> columns, int actions, String caption, String allowedActions, ObjectTreeBuilder treeBuilder) {
        super(objectClass, columns, treeBuilder);
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
                reload = new Button("Load", e -> load());
                buttonPanel.add(reload);
                if(ObjectHint.isSmallList(getObjectClass(),isAllowAny())) {
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
            if(nm && ((actions & AUDIT) == AUDIT)) {
                audit = new Button("Audit", "user", this);
                buttonPanel.add(audit);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ObjectTreeBrowser(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                ObjectBrowser.actions(className, Application.get().getServer().isDeveloper()),
                Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className));
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), actions, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectTreeBrowser<O> create(Class<O> objectClass, Iterable<String> columns,
                                                                       int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "TreeBrowser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance(columns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance(columns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance(actions);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance(columns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectTreeBrowser<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectTreeBrowser<>(objectClass, columns, actions, title);
    }

    private void con() {
        createExtraButtons();
        addExtraButtons();
        if(!isCloseable()) {
            exit = new Button("Exit", this);
            buttonPanel.add(exit);
        }
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
        return buttonPanel;
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

    protected boolean canDelete(T object) {
        return true;
    }

    protected boolean canEdit(T object) {
        return true;
    }

    protected boolean canAdd(T parentObject) {
        return true;
    }

    @Override
    public void clicked(Component c) {
        if (c == exit) {
            close();
            return;
        }
        if(c == add) {
            T object;
            object = getSelected();
            if(object == null && dataProvider.getObjectCount() > 0) {
                Application.warning(ObjectBrowser.NOTHING_SELECTED);
                return;
            }
            ObjectEditor<T> editor = getObjectEditor();
            editor.setParentObject(object, dataProvider.getTreeBuilder().getLinkType());
            editor.addObject(getView());
            return;
        }
        if(c == edit || c == delete || c == view) {
            T object = getSelected();
            if(object == null) {
                Application.warning(ObjectBrowser.NOTHING_SELECTED);
                return;
            }
            if(c == delete) {
                doDelete(object);
                return;
            }
            if(c == view) {
                getObjectEditor().viewObject(object, getView(), true);
                return;
            }
            if(c == edit) {
                getObjectEditor().editObject(object, getView(), true);
                return;
            }
        }
        super.clicked(c);
    }

    private boolean checkDelete() {
        T object = getSelected();
        if(object == null) {
            Application.warning(ObjectBrowser.NOTHING_SELECTED);
            return false;
        }
        return canDelete(object);
    }

    private void doDelete(T object) {
        getObjectEditor().setObject(object);
        getObjectEditor().deleteObject();
        boolean deleted = getObjectEditor().getObject() == null;
        if(deleted) {
            Application.message("Deleted");
        }
    }
}
