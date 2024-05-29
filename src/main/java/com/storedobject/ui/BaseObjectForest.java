package com.storedobject.ui;

import com.storedobject.core.EditorAction;
import com.storedobject.core.Logic;
import com.storedobject.core.ObjectSearcher;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewOpenedListener;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storedobject.core.EditorAction.ALL;

public abstract class BaseObjectForest<T extends StoredObject> extends AbstractObjectForest<T> {

    private final Map<Class<?>, List<ObjectChangedListener<?>>> objectChangedListeners = new HashMap<>();
    private final Map<Class<?>, ObjectChangedListener<?>> internalListeners = new HashMap<>();
    private final Map<Class<?>, ObjectEditor<? extends StoredObject>> editors = new HashMap<>();
    private final Map<Class<?>, ObjectSearcher<? extends StoredObject>> searchers = new HashMap<>();
    private boolean allowLinkEditing = true;
    private SplitLayout layout;
    private final ViewOpenedCheck viewOpenedCheck = new ViewOpenedCheck();
    private ObjectEditor<?> currentEditor;

    BaseObjectForest(boolean large, boolean forViewing, Class<T> objectClass, Iterable<String> columns, boolean any) {
        super(large, forViewing, objectClass, columns, any);
    }

    public <O extends StoredObject> void addObjectChangedListener(Class<O> objectClass, ObjectChangedListener<O> listener) {
        if(listener != null) {
            getListenerList(objectClass, true).add(listener);
        }
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        //noinspection ResultOfMethodCallIgnored
        objectChangedListeners.keySet().stream().anyMatch(k -> objectChangedListeners.get(k).remove(listener));
    }

    List<ObjectChangedListener<?>> getListenerList(Class<?> objectClass, boolean create) {
        List<ObjectChangedListener<?>> list = objectChangedListeners.get(objectClass);
        if(list == null && create) {
            list = new ArrayList<>();
            internalListeners.put(objectClass, createInternalChangedListener());
            objectChangedListeners.put(objectClass, list);
        }
        return list;
    }

    private ObjectChangedListener<?> getInternalListener(Class<?> objectClass) {
        getListenerList(objectClass, true);
        return internalListeners.get(objectClass);
    }

    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
            if(logic.getApprovalCount() > 0 && getTransactionManager().needsApprovals()) {
                editors.values().forEach(e -> e.setLogic(logic));
                protect();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <O extends StoredObject> void setObjectEditor(Class<O> objectClass, ObjectEditor<O> editor) {
        if(objectClass == null) {
            return;
        }
        ObjectEditor<O> ed = (ObjectEditor<O>) editors.get(objectClass);
        if(ed != null) {
            if(editor == ed) {
                return;
            }
            if (ed.executing()) {
                ed.abort();
            }
            ObjectChangedListener<O> internalChangedListener = (ObjectChangedListener<O>) internalListeners.get(objectClass);
            ed.removeObjectChangedListener(internalChangedListener);
            if(editor != null) {
                editor.addObjectChangedListener(internalChangedListener);
                if(layout != null) {
                    editor.setEmbeddedView(getView(true));
                    editor.addOpenedListener(viewOpenedCheck);
                    editor.buttonsOff();
                }
            }
        } else {
            if(editor != null) {
                editor.addObjectChangedListener((ObjectChangedListener<O>) getInternalListener(objectClass));
                editor.setLogic(logic);
                if(layout != null) {
                    editor.setEmbeddedView(getView(true));
                    editor.addOpenedListener(viewOpenedCheck);
                    editor.buttonsOff();
                }
            }
        }
        if(editor == null) {
            if(ed != null) {
                editors.remove(objectClass);
            }
        } else {
            editors.put(objectClass, editor);
        }
    }

    @SuppressWarnings("unchecked")
    public final <O extends StoredObject> ObjectEditor<O> getObjectEditor(Class<O> objectClass) {
        ObjectEditor<O> editor = (ObjectEditor<O>) editors.get(objectClass);
        if(editor == null) {
            editor = createObjectEditor(objectClass);
            if(editor == null) {
                editor = ObjectEditor.create(objectClass, ALL, null);
                editor.addIncludeFieldChecker( n -> {
                    if(n.endsWith(".l")) {
                        n = n.substring(0, n.length() - 2);
                        return !hideLink(objectClass, n);
                    }
                    return true;
                });
            }
            editor.addObjectChangedListener((ObjectChangedListener<O>) getInternalListener(objectClass));
            if(layout != null) {
                editor.setEmbeddedView(getView(true));
                editor.addOpenedListener(viewOpenedCheck);
            }
            editor.setAllowDoNotSave(allowLinkEditing);
            editor.setLogic(logic);
            editors.put(objectClass, editor);
            if(layout != null) {
                editor.buttonsOff();
            }
        }
        return editor;
    }

    protected <O extends StoredObject> ObjectEditor<O> createObjectEditor(Class<O> objectClass) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public final <O extends StoredObject> ObjectSearcher<O> getObjectSearcher(Class<O> objectClass) {
        ObjectSearcher<O> searcher = (ObjectSearcher<O>) searchers.get(objectClass);
        if(searcher == null) {
            searcher = createObjectSearcher(objectClass);
            if(searcher == null) {
                searcher = ObjectBrowser.create(objectClass, null,
                        EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0),
                        null, null);
            }
            searchers.put(objectClass, searcher);
        }
        return searcher;
    }

    protected <O extends StoredObject> ObjectSearcher<O> createObjectSearcher(Class<O> objectClass) {
        return null;
    }

    public abstract  <O extends StoredObject> O selected();
    /**
     * Allow/disallow link editing.
     *
     * @param allowLinkEditing True if link editing needs to be allowed
     */
    public final void setAllowLinkEditing(boolean allowLinkEditing) {
        this.allowLinkEditing = allowLinkEditing;
        for(ObjectEditor<?> e: editors.values()) {
            e.setAllowDoNotSave(allowLinkEditing);
        }
    }

    /**
     * Check whether link editing is allowed or not.
     *
     * @return True if link editing is allowed.
     */
    public final boolean isLinkEditingAllowed() {
        return allowLinkEditing;
    }

    public void setSplitView() {
        if(getView(false) != null) {
            return;
        }
        layout = new SplitLayout();
        layout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        layout.setSplitterPosition(50);
        layout.addToPrimary(this);
        addItemSelectedListener((forest, item) -> itemSelected());
    }

    private void itemSelected() {
        if(layout == null) {
            return;
        }
        StoredObject so = selected();
        if(so == null) {
            return;
        }
        if(currentEditor != null) {
            currentEditor.abort();
        }
        ObjectEditor<?> oe = getObjectEditor(so.getClass());
        oe.setRawObject(so);
        oe.viewObject();
        layout.setSplitterPosition(50);
    }

    @Override
    public Component getViewComponent() {
        return layout == null ? super.getViewComponent() : layout;
    }

    private class ViewOpenedCheck implements ViewOpenedListener {

        @Override
        public void viewOpening(View view) {
            if(layout == null) {
                return;
            }
            if(view instanceof ObjectEditor<?> oe) {
                if(oe.getEmbeddedView() != getView(false)) {
                    return;
                }
                if(currentEditor != oe) {
                    if(currentEditor != null) {
                        layout.remove(currentEditor.getComponent());
                    }
                    currentEditor = oe;
                    layout.addToSecondary(oe.getComponent());
                }
            }
        }

        @Override
        public void viewOpened(View view) {
        }
    }

    abstract <O extends StoredObject> ObjectChangedListener<O> createInternalChangedListener();
}