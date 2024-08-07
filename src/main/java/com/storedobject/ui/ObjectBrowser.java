package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.accounts.JournalVoucherBrowser;
import com.storedobject.ui.inventory.POBrowser;
import com.storedobject.ui.inventory.POItemBrowser;
import com.storedobject.ui.util.LoadFilterButtons;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.shared.Registration;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.storedobject.core.EditorAction.*;

public class ObjectBrowser<T extends StoredObject> extends ObjectGrid<T>
        implements EditableDataGrid<T>, ObjectEditorListener {

    private LoadFilterButtons<T> loadFilterButtons;
    protected final ButtonLayout buttonPanel = new ButtonLayout();
    protected PrintButton print;
    protected Button add, edit, delete, search, filter, load, view, report, excel, audit, exit, save, cancel, ledger;
    private final String allowedActions;
    ObjectEditor<T> editor;
    private T editingItem;
    private boolean rowMode = false;
    private boolean readOnly = false;
    private final Map<String, HasValue<?, ?>> fields = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Span> spans = new HashMap<>();
    private Logic logic;
    private SplitLayout layout;
    private final boolean anchorsExist;
    private final InternalObjectChangedListener internalObjectChangedListener = new InternalObjectChangedListener();

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

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, SearchBuilder<T> searchBuilder) {
        this(objectClass, browseColumns, ALL, searchBuilder);
    }

    public ObjectBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, (String) null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, SearchBuilder<T> searchBuilder) {
        this(objectClass, null, actions, searchBuilder, null);
    }

    public ObjectBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, (Iterable<String>) null, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<T> searchBuilder) {
        this(objectClass, browseColumns, actions, searchBuilder, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, (Iterable<String>) null, caption);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                         Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                         SearchBuilder<T> searchBuilder, String caption) {
        this(objectClass, browseColumns, actions, searchBuilder, caption, null);
    }

    protected ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                            Iterable<String> filterColumns, String caption, String allowedActions) {
        this(objectClass, browseColumns, actions, filterColumns, null, caption, allowedActions);
    }

    protected ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                            SearchBuilder<T> searchBuilder, String caption, String allowedActions) {
        this(objectClass, browseColumns, actions, null, searchBuilder, caption, allowedActions);
    }

    private ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                          Iterable<String> filterColumns, SearchBuilder<T> searchBuilder, String caption, String allowedActions) {
        super(objectClass, browseColumns, (actions & ALLOW_ANY) == ALLOW_ANY);
        addItemDoubleClickListener(e -> {
            T item = e.getItem();
            if(item != null) {
                rowDoubleClicked(item);
            }
        });
        getDataProvider().setLoadCallBack(this::loadInt);
        if( // Do not allow certain special editable classes to directly inherit this class
                (InventoryPO.class.isAssignableFrom(getObjectClass()) && !(this instanceof POBrowser))
                        || (InventoryPOItem.class.isAssignableFrom(getObjectClass()) && !(this instanceof POItemBrowser))
                        || (JournalVoucher.class.isAssignableFrom(getObjectClass()) && !(this instanceof JournalVoucherBrowser))
        ) {
            boolean a = actions < 0;
            if(a) {
                actions = -actions;
            }
            actions &= (~NEW) & (~EDIT) & (~DELETE);
            if(a) {
                actions = -actions;
            }
        }
        if(InventoryItem.class.isAssignableFrom(getObjectClass())) {
            boolean a = actions < 0;
            if(a) {
                actions = -actions;
            }
            actions &= (~NEW) & (~DELETE);
            if(a) {
                actions = -actions;
            }
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
            boolean nm = !MasterObject.class.isAssignableFrom(getObjectClass());
            if(actions == SEARCH || actions == (SEARCH | RELOAD)) {
                search = new Button("Set", this);
                buttonPanel.add(search);
                actions |= RELOAD;
            }
            if(nm && ((actions & NEW) == NEW) && actionAllowed("NEW")) {
                add = new Button("New", this);
                buttonPanel.add(add);
            }
            if(nm && ((actions & EDIT) == EDIT) && actionAllowed("EDIT")) {
                edit = new Button("Edit", this);
                buttonPanel.add(edit);
            }
            if(nm && ((actions & DELETE) == DELETE) && actionAllowed("DELETE")) {
                delete = new ConfirmButton("Delete", this);
                ((ConfirmButton)delete).setPreconfirm(this::checkDelete);
                buttonPanel.add(delete);
            }
            if((actions & RELOAD) == RELOAD) {
                loadFilterButtons = new LoadFilterButtons<>(this, filterColumns, searchBuilder);
                filter = loadFilterButtons.getFilterButton();
                load = loadFilterButtons.getLoadButton();
                loadFilterButtons.addTo(buttonPanel);
            }
            if((actions & VIEW) == VIEW) {
                view = new Button("View", this);
                buttonPanel.add(view);
            }
            if((actions & PDF) == PDF && actionAllowed("PDF")) {
                print = PrintButton.create(this);
                if(print != null) {
                    buttonPanel.add(print);
                }
                if(print == null && ((actions & PRINT) == PRINT) && actionAllowed("PRINT")) {
                    report = new Button("Report", this);
                    buttonPanel.add(report);
                }
            }
            if((actions & EXCEL) == EXCEL && actionAllowed("EXCEL")) {
                excel = new Button("Excel", this);
                buttonPanel.add(excel);
            }
            if(nm && ((actions & AUDIT) == AUDIT) && actionAllowed("AUDIT")) {
                audit = new Button("Audit", "user", this);
                buttonPanel.add(audit);
            }
            if(Financial.class.isAssignableFrom(getObjectClass())
                    && nm && ((actions & LEDGER) == LEDGER) && actionAllowed("LEDGER")) {
                ledger = new Button("View/Post Ledger", VaadinIcon.BOOK_DOLLAR, this);
                buttonPanel.add(ledger);
            }
        }
        if(!((actions & EditorAction.NO_EXIT) == EditorAction.NO_EXIT)) {
            exit = new Button(search == null ? "Exit" : "Quit", this);
        }
    }

    @SuppressWarnings("unchecked")
    public ObjectBrowser(String className) throws Exception {
        this((Class<T>)JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null,
                actions(className, Application.get().getServer().isDeveloper()), null, null,
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

    @SuppressWarnings("DuplicatedCode")
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
        if(ledger != null) {
            buttonPanel.remove(ledger);
            ledger = null;
        }
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions) {
        return create(objectClass, browseColumns, actions, (String) null);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions, Iterable<String> filterColumns) {
        return create(objectClass, browseColumns, actions, filterColumns, null);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, null, actions, title);
    }

    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions, String title) {
        return create(objectClass, browseColumns, actions, null, title);
    }

    @SuppressWarnings("unchecked")
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions, Iterable<String> filterColumns, String title) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "Browser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, int.class, Iterable.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectBrowser<O>) c.newInstance(browseColumns, actions, filterColumns, title);
            }
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
        return LogicParser.createInternalBrowser(objectClass, browseColumns, actions, filterColumns, title);
    }

    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
            if(editor != null) {
                editor.setLogic(logic);
            }
        }
    }

    @Override
    public final Logic getLogic() {
        return logic;
    }

    @Override
    public void setCaption(String caption) {
        setCaption(caption, false);
    }

    void setCaption(String caption, boolean fromEditor) {
        if(caption == null || caption.isEmpty()) {
            caption = getCaption();
            if(caption == null || caption.isEmpty()) {
                return;
            }
            return;
        }
        super.setCaption(caption);
        if(!fromEditor && editor != null) {
            editor.setCaption(caption);
        }
    }

    @Override
    public Component createHeader() {
        return header(buttonPanel, loadFilterButtons);
    }

    static Component header(ButtonLayout buttonPanel, LoadFilterButtons<?> loadFilterButtons) {
        ObjectSearchBuilder<?> sb;
        if(loadFilterButtons == null || (sb = loadFilterButtons.getSearchBuilder()) == null) {
            return buttonPanel;
        }
        VerticalLayout v = new VerticalLayout(buttonPanel);
        if(sb instanceof Component f) {
            v.add(f);
        }
        v.setPadding(false);
        v.setMargin(false);
        v.setSpacing(false);
        return v;
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

    /**
     * List of more buttons to be added to the {@link PrintButton}, in addition to the configured buttons.
     *
     * @return List of more buttons to be added. The list could contain any type of components.
     */
    protected List<Component> listMoreButtons() {
        return null;
    }

    protected boolean canAdd() {
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
                case 0 -> {
                    warning("Empty, no item to select!");
                    return null;
                }
                case 1 -> {
                    o = getItem(0);
                    select(o);
                    return o;
                }
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
        clearAlerts();
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
        if(c == edit || c == delete || c == view || c == audit || c == ledger) {
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
            if(c == ledger) {
                postLedger(object);
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
            if(selection != null && selection.isEmpty()) {
                if(size() == 1) {
                    select(getDataProvider().get(0));
                    selection = getSelectedItems();
                } else {
                    selection = null;
                }
            }
            if(selection == null || selection.isEmpty()) {
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
    public void load(int linkType, StoredObject master, String condition, String orderedBy, boolean any) {
        getDataProvider().load(linkType, master, condition, orderedBy, any);
    }

    @Override
    public void load(String condition, String orderedBy, boolean any) {
        getDataProvider().load(condition, orderedBy, any);
    }

    private void loadInt(Runnable loadFunction) {
        if(getEditor().isOpen()) {
            cancelRowEdit();
        }
        if(anchorsExist) {
            ObjectEditor<T> oe = getObjectEditor();
            if(oe != null) {
                if(oe.anchorAction) {
                    oe.executeAnchorForm(() -> loadInt(loadFunction));
                    return;
                }
                getDataProvider().getSystemFilter().setCondition(oe.getAnchorFilter());
            }
        }
        Application a = Application.get();
        if(a == null) {
            loadFunction.run();
        } else {
            a.access(loadFunction::run);
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
     * This method is invoked when anchor fields are created for the first time.
     */
    public void anchorFieldsCreated() {
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
     * thrown from this method, anchor values will be asked again.
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

    public void postLedger(T object) {
        if(!(object instanceof Financial f)) {
            clearAlerts();
            warning("Not a financial entry");
            return;
        }
        if(f.isLedgerPosted()) {
            if(canViewLedger(object)) {
                getObjectEditor().postLedger(object);
            }
        } else {
            if(canPostLedger(object)) {
                new ActionForm("Post Ledger", ObjectEditor.CONFIRM_LEDGER, () -> {
                    getObjectEditor().postLedger(object);
                    refresh(object);
                }).execute();
            }
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
        layout = splitLayout(this);
        addItemSelectedListener((grid, item) -> itemSelected());
        if(view != null) {
            buttonPanel.remove(view);
            view = null;
        }
    }

    static SplitLayout splitLayout(Component me) {
        SplitLayout layout = new SplitLayout();
        layout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        layout.setSplitterPosition(50);
        layout.addToPrimary(me);
        return layout;
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

    @SuppressWarnings("DuplicatedCode")
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
        if(this.editor == editor) {
            return;
        }
        if(this.editor != null && this.editor.executing()) {
            this.editor.abort();
            if(layout != null) {
                layout.remove(this.editor.getComponent());
            }
        }
        this.editor = editor;
        constructEditor();
    }

    @SuppressWarnings("DuplicatedCode")
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
        for(ObjectEditor.FieldPosition p: editor.fieldPositions()) {
            fieldName = p.name();
            field = fields.get(fieldName);
            if(field != null) {
                spans.get(fieldName).removeAll();
                editor.setFieldLabel(field, labels.get(fieldName));
                p.container().addComponentAtIndex(p.position(), (Component)field);
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
                        labels.put(fieldName, getFieldLabel(fieldName));
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
        internalObjectChangedListener.set(editor);
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

    private String getFieldLabel(String fieldName) {
        int p = fieldName.indexOf('.');
        if(p < 0) {
            return editor.getFieldLabel(fieldName);
        }
        HasValue<?, ?> field = editor.getField(fieldName.substring(0, p));
        ObjectEditor<?> oe;
        if(field instanceof ObjectField<?> of && of.getField() instanceof ObjectFormField<?> off) {
            oe = off.getFormEditor();
        } else if(field instanceof ObjectFormField<?> off) {
            oe = off.getFormEditor();
        } else {
            return editor.getFieldLabel(fieldName);
        }
        return oe.getFieldLabel(fieldName.substring(p + 1));
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
        return loadFilterButtons == null ? null : loadFilterButtons.getSearchBuilder();
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
        return canEdit(item);
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

    /**
     * This is invoked when an "Extra Information" instance is created. At this point, you may set your own values
     * if required.
     *
     * @param object The object instance for which the "Extra Information" is created.
     * @param extraInfo Newly created "Extra Information" instance.
     */
    public void extraInfoCreated(T object, StoredObject extraInfo) {
    }

    /**
     * This is invoked when an existing "Extra Information" instance is loaded for the current object.
     * At this point, you may set your own values if required.
     *
     * @param object The object instance for which the "Extra Information" is loaded.
     * @param extraInfo The "Extra Information" instance loaded now.
     */
    public void extraInfoLoaded(T object, StoredObject extraInfo) {
    }

    /**
     * This is invoked when an existing "Extra Information" instance is being saved.
     * At this point, you may set your own values if required. ({@link ObjectEditor#validateData()} and
     * {@link StoredObject#validateData(TransactionManager)} are be invoked after this).
     * <p>If an exception is thrown from this method, the save process will not happen.</p>
     *
     * @param object The object instance for which the "Extra Information" is getting saved.
     * @param extraInfo The "Extra Information" instance to be saved.
     * @throws Exception if any validation error to be notified.
     */
    @SuppressWarnings("RedundantThrows")
    public void savingExtraInfo(T object, StoredObject extraInfo) throws Exception {
    }

    private class InternalObjectChangedListener implements ObjectChangedListener<T> {

        private Registration registration;
        private ObjectEditor<T> editor;

        private InternalObjectChangedListener() {
        }

        private void set(ObjectEditor<T> editor) {
            if(this.editor == editor) {
                return;
            }
            if(registration != null) {
                registration.remove();
            }
            this.editor = editor;
            registration = editor.addObjectChangedListener(this);
        }

        @Override
        public void inserted(T object) {
            reload();
            select(object);
            scrollTo(object);
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> inserted(object));
            }
        }

        @Override
        public void updated(T object) {
            refresh(object);
            select(object);
            scrollTo(object);
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> updated(object));
            }
        }

        @Override
        public void deleted(T object) {
            reload();
            deselectAll();
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> deleted(object));
            }
        }

        @Override
        public void undeleted(T object) {
            reload();
            select(object);
            scrollTo(object);
            if(objectChangedListeners != null) {
                objectChangedListeners.forEach(l -> undeleted(object));
            }
        }
    }

    @Override
    public boolean actionAllowed(String action) {
        return (allowedActions == null || allowedActions.contains(action)) && super.actionAllowed(action);
    }
}
