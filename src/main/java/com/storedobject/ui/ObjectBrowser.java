package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.inventory.POBrowser;
import com.storedobject.ui.inventory.POItemBrowser;
import com.storedobject.ui.util.ObjectDataProvider;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.storedobject.core.EditorAction.*;

public class ObjectBrowser<T extends StoredObject> extends ObjectGrid<T> implements EditableDataGrid<T> {

    private ObjectSearchBuilder<T> searchBuilder;
    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected PrintButton print;
    protected Button add, edit, delete, search, filter, load, view, report, excel, audit, exit, save, cancel;
    private String allowedActions;
    ObjectEditor<T> editor;
    private List<ObjectEditorListener> objectEditorListeners;
    private T editingItem;
    private boolean rowMode = false;
    private boolean readOnly = false;
    private final Map<String, HasValue<?, ?>> fields = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Span> spans = new HashMap<>();
    private Logic logic;
    private SplitLayout layout;
    private boolean anchorsExist;
    private String anchorFilter;
    private final ObjectSearchFilter extraFilter = new ObjectSearchFilter();

    public ObjectBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public ObjectBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    public ObjectBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, (String)null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        this(objectClass, null, actions, caption, dataProvider);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption, ObjectDataProvider<T, Void> dataProvider) {
        super(objectClass, browseColumns, dataProvider);
        init(actions & (~RELOAD), null, caption, null);
    }

    protected ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, (actions & ALLOW_ANY) == ALLOW_ANY);
        init(actions, filterColumns, caption, allowedActions);
    }

    private void init(int actions, Iterable<String> filterColumns, String caption, String allowedActions) {
        if( // Do not allow certain special classes to directly inherit this class with etitability
                (InventoryPO.class.isAssignableFrom(getObjectClass()) && !(this instanceof POBrowser)) ||
                        (InventoryPOItem.class.isAssignableFrom(getObjectClass()) && !(this instanceof POItemBrowser))
        ) {
            actions &= (~NEW) & (~EDIT) & (~DELETE);
        }
        if(InventoryItem.class.isAssignableFrom(getObjectClass())) {
            actions &= (~NEW) & (~DELETE);
        }
        anchorsExist = !ClassAttribute.get(getObjectClass()).getAnchors().isEmpty();
        addConstructedListener(o -> gridCreated());
        if(caption != null && !caption.isEmpty()) {
            setCaption(caption);
        }
        buttonPanel.add(getConfigureButton());
        save = new Button("Save Changes", "Save", e-> saveEditedRow());
        cancel = new Button("Abandon Changes", "Cancel", e -> cancelRowEdit());
        buttonPanel.add(save, cancel);
        save.setVisible(false);
        cancel.setVisible(false);
        this.allowedActions = allowedActions;
        if(actions < 0) {
            actions = (-actions) | StoredObjectUtility.statusUI(getObjectClass());
        }
        actions = filterActionsInternal(actions);
        if((actions & ALLOW_ANY) == ALLOW_ANY) {
            actions &= ~ALLOW_ANY;
        }
        if(filterColumns != null) {
            actions |= RELOAD;
        }
        if(actions > 0) {
            if(actions == SEARCH || actions == (SEARCH | RELOAD)) {
                search = new Button("Set", this);
                buttonPanel.add(search);
                actions |= RELOAD;
            }
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
                print = PrintButton.create(this);
                if(print != null) {
                    buttonPanel.add(print);
                }
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
        exit = new Button(actions == SEARCH ? "Quit" : "Exit", this);
        if((actions & RELOAD) == RELOAD) {
            StringList filters = null;
            if(filterColumns != null) {
                if(filterColumns instanceof StringList) {
                    filters = (StringList)filterColumns;
                } else {
                    filters = StringList.create(filterColumns);
                }
            }
            searchBuilder = createSearchBuilder(filters);
            if(searchBuilder != null && searchBuilder.getSearchFieldCount() == 0) {
                searchBuilder = null;
            }
        }
        addItemDoubleClickListener(e -> {
            T item = e.getItem();
            if(item != null) {
                rowDoubleClicked(item);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public ObjectBrowser(String className) throws Exception {
        this((Class<T>)JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                actions(className, Application.get().getServer().isDeveloper()), null,
                Application.get().getRunningLogic().getTitle(), ObjectEditor.allowedActions(className));
    }

    private void gridCreated() {
        getRowEditor().addConstructedListener(o -> getEditor().setBinder(editor.getForm().getBinder()));
        createExtraButtons();
        addExtraButtons();
        if(buttonPanel.getComponentCount() > 1 && !isCloseable()) {
            buttonPanel.add(exit);
        } else {
            exit = null;
        }
    }

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

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, null, actions, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns, int actions, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(ApplicationServer.createLogicName(Application.getPackageTag(), objectClass, "Browser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(browseColumns, actions, title);
            }
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(actions, title);
            }
            try {
                c = logic.getConstructor(Iterable.class, int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(browseColumns, actions);
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(actions);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(browseColumns);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POBrowser(objectClass, actions, title);
        }
        if(InventoryPOItem.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POItemBrowser(objectClass, actions, title);
        }
        return new ObjectBrowser<>(objectClass, browseColumns, actions, title);
    }

    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
            if(logic.getApprovalCount() > 0 && getTransactionManager().needsApprovals()) {
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
                if(editor != null) {
                    editor.setLogic(logic);
                }
            }
        }
    }

    @Override
    public final Logic getLogic() {
        return logic;
    }

    @Override
    public void setCaption(String caption) {
        if(caption == null || caption.isEmpty()) {
            caption = getCaption();
            if(caption == null || caption.isEmpty()) {
                return;
            }
            error("Error: Please inform Syam about this error");
            return;
        }
        super.setCaption(caption);
        if(editor != null) {
            editor.setCaption(caption);
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

    static int actions(String className, boolean developer) {
        if(!className.startsWith("(")) {
            return -(ALL | RELOAD | VIEW | (developer ? PDF : 0));
        }
        int p = className.indexOf(')');
        if(p < 0) {
            return -(ALL | RELOAD | VIEW | (developer ? PDF : 0));
        }
        return EditorAction.getActions(className.substring(1, p), developer);
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

    public boolean canDelete(T object) {
        return true;
    }

    public boolean canEdit(T object) {
        return true;
    }

    protected boolean canAdd() {
        return true;
    }

    protected boolean canSearch() {
        return true;
    }

    @Override
    public void close() {
        if(getEditor().isOpen()) {
            cancelRowEdit();
        }
        super.close();
    }

    @Override
    public T selected() {
        clearAlerts();
        T o = getSelected();
        if(o == null) {
            o = getEditingItem();
        }
        if(o == null) {
            switch(size()) {
                case 0:
                    warning("No item to select!");
                    return null;
                case 1:
                    o = getItem(0);
                    select(o);
                    return o;
            }
            warning(NOTHING_SELECTED);
        }
        return o;
    }

    public void rowDoubleClicked(T object) {
        if(canSearch() && objectSetter != null && search != null && search.isVisible() && search.isEnabled()) {
            close();
            objectSetter.setObject(object);
        } else {
            if(edit != null && edit.isVisible() && edit.isEnabled() && canEdit(object)) {
                if(layout == null) {
                    editRowInt(object);
                } else {
                    getObjectEditor().editObject(object, getView(), true);
                }
            } else {
                getObjectEditor().viewObject(object, getView(), true);
            }
        }
    }

    @Override
    public void clicked(Component c) {
        if(c == exit) {
            close();
            return;
        }
        if(c == add) {
            doAdd();
            return;
        }
        if(c == report) {
            getObjectEditor().doReport();
            return;
        }
        if(c == edit || c == delete || c == view || c == audit) {
            T object = selected();
            if(object == null) {
                return;
            }
            if(c == delete) {
                if(canDelete(object)) {
                    doDelete(object);
                }
                return;
            }
            if(c == view) {
                doView(object);
                return;
            }
            if(c == edit) {
                doEdit(object);
                return;
            }
            if(c == audit) {
                new ObjectHistoryGrid<>(object).executeAll();
                return;
            }
        }
        if(c == search) {
            if (!canSearch()) {
                return;
            }
            Set<T> selection = getSelectedItems();
            if(objectSetter == null) {
                close();
                return;
            }
            if(selection != null && selection.size() == 0) {
                if(size() == 1) {
                    select(getDataProvider().getItem(0));
                    selection = getSelectedItems();
                } else {
                    selection = null;
                }
            }
            if(selection == null || selection.size() == 0) {
                warning(NOTHING_SELECTED);
                return;
            }
            close();
            if(objectSetter instanceof ObjectsSetter) {
                ((ObjectsSetter<T>) objectSetter).setObjects(selection);
            } else {
                objectSetter.setObject(selection.stream().findAny().orElse(null));
            }
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
        if(anchorsExist) {
            ObjectEditor<T> oe = getObjectEditor();
            if(oe != null) {
                if(oe.anchorAction) {
                    oe.executeAnchorForm(() -> reload(true));
                    return;
                }
                String af = oe.getAnchorFilter();
                again = !Objects.equals(anchorFilter, af);
                if(again) {
                    anchorFilter = af;
                }
            }
        }
        if(filter != null) {
            filter.removeTheme(ThemeStyle.ERROR);
        }
        if(getEditor().isOpen()) {
            cancelRowEdit();
        }
        if(searchBuilder == null) {
            clear(false);
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
                    clear(false);
                    filter = anchorFilter;
                    String ef = extraFilter.getFilter();
                    if(ef != null) {
                        if(filter == null) {
                            filter = ef;
                        } else {
                            filter += " AND (" + ef + ")";
                        }
                    }
                    load(filter);
                }
            } else {
                Predicate<T> filterFunction = null;
                if(!again && isFullyLoaded() && isFullyCached()) {
                    filterFunction = searchBuilder.getFilterPredicate();
                }
                if(again || filterFunction == null) {
                    if(again) {
                        clear(false);
                    }
                    if(anchorFilter != null) {
                        filter += " AND (" + anchorFilter + ")";
                    }
                    String ef = extraFilter.getFilter();
                    if(ef != null) {
                        filter += " AND (" + ef + ")";
                    }
                    load(filter);
                } else {
                    filter(filterFunction);
                }
            }
        }
    }

    public void setExtraFilter(FilterProvider filterProvider) {
        setExtraFilter(filterProvider, true);
    }

    public void setExtraFilter(FilterProvider filterProvider, boolean reload) {
        if(extraFilter.getFilterProvider() == filterProvider) {
            return;
        }
        extraFilter.setFilterProvider(filterProvider);
        if(reload) {
            reload(true);
        }
    }

    public void setExtraFilter(String extraFilter) {
        setExtraFilter(extraFilter, true);
    }

    public void setExtraFilter(String extraFilter, boolean reload) {
        if(Objects.equals(extraFilter, this.extraFilter.getCondition())) {
            return;
        }
        this.extraFilter.setCondition(extraFilter);
        if(reload) {
            reload(true);
        }
    }

    /**
     * Validate the anchor values. This is invoked when anchor values are set while adding a new instance.
     *
     * @param object The instance that contains the currently accepted anchor values.
     * @throws SOException If thrown, the message is displayed as a warning and the add operation is aborted.
     */
    public void validateAnchorValues(T object) throws SOException {
    }

    /**
     * Reset the anchor values so that it will be asked again for the next add/search actions.
     */
    public void resetAnchor() {
        if(anchorsExist) {
            getObjectEditor();
            if(editor != null) {
                editor.anchorAction = true;
            }
        }
    }

    /**
     * This method is invoked when anchor values are set via the anchor form and if any exception is
     * thrown from this method, anchor values will asked again.
     *
     * @throws Exception If anchor values are not acceptable for some reason.
     */
    @SuppressWarnings("RedundantThrows")
    protected void anchorsSet() throws Exception {
    }

    /**
     * This method will be invoked if the "Cancel" button is pressed on the anchor form.
     *
     */
    protected void anchorsCancelled() {
    }

    /**
     * Execute the "anchor form" and run some specified action.
     *
     * @param action Action to run.
     */
    public void executeAnchorForm(Runnable action) {
        getObjectEditor();
        if(editor != null) {
            editor.executeAnchorForm(action);
        }
    }

    @Override
    public List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        if(objectEditorListeners == null && create) {
            objectEditorListeners = new ArrayList<>();
        }
        return objectEditorListeners;
    }

    private boolean checkDelete() {
        T object = getSelected();
        if(object == null) {
            Application.warning(NOTHING_SELECTED);
            return false;
        }
        return canDelete(object);
    }

    public void doAdd() {
        if(canAdd()) {
            getObjectEditor().addObject(getView());
        }
    }

    public void doEdit(T object) {
        if(object != null && canEdit(object)) {
            getObjectEditor().editObject(object, getView(), true);
        }
    }

    public void doDelete(T object) {
        if(object == null) {
            return;
        }
        getObjectEditor().setObject(object);
        editor.deleteObject();
        boolean deleted = editor.getObject() == null;
        if(deleted) {
            Application.message("Deleted");
        }
    }

    public void doView(T object) {
        if(object != null) {
            getObjectEditor().viewObject(object, getView(), true);
        }
    }

    @Override
    public Component getViewComponent() {
        return layout == null ? super.getViewComponent() : layout;
    }

    public void setSplitView() {
        if(getView(false) != null) {
            return;
        }
        layout = new SplitLayout();
        layout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        layout.setSplitterPosition(50);
        layout.addToPrimary(this);
        addItemSelectedListener((grid, item) -> itemSelected());
        if(view != null) {
            buttonPanel.remove(view);
            view = null;
        }
    }

    private void itemSelected() {
        if(layout == null) {
            return;
        }
        if(editor == null) {
            getObjectEditor();
        } else {
            editor.abort();
        }
        editor.viewObject(getSelected());
    }

    public final ObjectEditor<T> getRowEditor() {
        if(editor == null) {
            constructEditor();
        }
        if(!rowMode) {
            fields.keySet().forEach(fieldName -> {
                HasValue<?, ?> field = fields.get(fieldName);
                editor.setFieldLabel(field, null);
                Span span = spans.get(fieldName);
                span.add((Component) field);
                rowMode = true;
            });
        }
        return editor;
    }

    public void setObjectEditor(ObjectEditor<T> editor) {
        if(this.editor != null && this.editor.executing()) {
            this.editor.abort();
            if(layout != null) {
                layout.remove(this.editor.getComponent());
            }
        }
        this.editor = editor;
        constructEditor();
    }

    public final ObjectEditor<T> getObjectEditor() {
        if(editor == null) {
            constructEditor();
        }
        if(!rowMode) {
            return editor;
        }
        if(getEditor().isOpen()) {
            cancelRowEdit();
        }
        HasValue<?, ?> field;
        String fieldName;
        for(int i = 0; i < editor.fieldPositions().size(); i++) {
            fieldName = editor.fieldPositions().get(i);
            field = fields.get(fieldName);
            if(field != null) {
                spans.get(fieldName).removeAll();
                editor.setFieldLabel(field, labels.get(fieldName));
                editor.getForm().getContainer().addComponentAtIndex(i, (Component)field);
            }
        }
        rowMode = false;
        return editor;
    }

    private void constructEditor() {
        if(editor == null) {
            editor = createObjectEditor();
        }
        if(editor == null) {
            editor = constructObjectEditor();
        }
        if(editor == null) {
            editor = ObjectEditor.create(getObjectClass());
            editor.setCaption(getCaption());
        }
        if(layout == null) {
            editor.fieldPositions(new ArrayList<>());
            editor.getComponent();
            String fieldName;
            HasValue<?, ?> field;
            int p;
            for(Column<?> c : getColumns()) {
                fieldName = c.getKey();
                p = fieldName.indexOf(' ');
                if(p > 0) {
                    fieldName = fieldName.substring(0, p);
                }
                if(isColumnEditable(fieldName)) {
                    field = editor.getField(fieldName);
                    if(field != null) {
                        labels.put(fieldName, editor.getFieldLabel(fieldName));
                        fields.put(fieldName, field);
                        Span span = spans.get(fieldName);
                        if(span == null) {
                            span = new Span();
                            c.setEditorComponent(span);
                            spans.put(fieldName, span);
                        }
                    } else {
                        fields.remove(fieldName);
                        Span span = spans.get(fieldName);
                        if(span != null) {
                            span.removeAll();
                        }
                    }
                }
            }
        }
        editor.addObjectChangedListener(this);
        editor.addObjectEditorListener(this);
        editor.grid = this;
        editor.setLogic(logic);
        if(layout != null) {
            editor.setEmbeddedView(getView(true));
            layout.addToSecondary(editor.getComponent());
            layout.setSplitterPosition(50);
            editor.buttonsOff();
        }
    }

    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    @Override
    public boolean isColumnEditable(String columnName) {
        return true;
    }

    @Override
    public Stream<HasValue<?, ?>> streamEditableFields() {
        return fields.values().stream();
    }

    @Override
    public boolean isSearchMode() {
        return search != null;
    }

    @Override
    public ObjectSearchBuilder<T> getSearchBuilder() {
        return searchBuilder;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            cancelRowEdit();
        }
    }

    public final boolean isReadOnly() {
        return readOnly;
    }

    @Override
    protected final Editor<T> createEditor() {
        Editor<T> editor = super.createEditor();
        editor.setBuffered(true);
        return editor;
    }

    public void editRow(T item) {
        if(canEdit(item)) {
            editRowInt(item);
        }
    }

    public boolean canRowEdit(T item) {
        return true;
    }

    private void editRowInt(T item) {
        if(!readOnly && editingItem != item && canRowEdit(item)) {
            editingItem = item;
            getRowEditor();
            if(rowMode && editor.editItem(item)) {
                save.setVisible(true);
                cancel.setVisible(true);
            } else {
                warning("Not editable!");
            }
        }
    }

    public void cancelRowEdit() {
        if(editor != null) {
            Editor<T> e = getEditor();
            if(e.isOpen()) {
                e.cancel();
            }
            getRowEditor().doCancel();
            editingItem = null;
            save.setVisible(false);
            cancel.setVisible(false);
        }
    }

    public void saveEditedRow() {
        if(editor != null && editingItem != null && getRowEditor().saveEdited()) {
            T item = editingItem;
            editingItem = null;
            select(item);
            save.setVisible(false);
            cancel.setVisible(false);
        }
    }

    public final T getEditingItem() {
        return editingItem;
    }
}
