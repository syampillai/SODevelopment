package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.report.ObjectList;
import com.storedobject.ui.common.EntityRoleEditor;
import com.storedobject.ui.common.PersonRoleEditor;
import com.storedobject.ui.common.SystemUserEditor;
import com.storedobject.ui.inventory.POEditor;
import com.storedobject.ui.inventory.POItemEditor;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;

import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The concrete base class for creating
 * <a href="https://en.wikipedia.org/wiki/Create,_read,_update_and_delete">CRUD</a> views for {@link StoredObject}
 * classes. If you want to instantiate this class from within your code, it is recommended to use one of
 * the static create methods ({@link #create(Class, int, String)}, {@link #create(Class, int)}, {@link #create(Class)})
 * rather than using the constructors directly.
 *
 * @param <T> Type of the object.
 * @author Syam
 */
public class ObjectEditor<T extends StoredObject> extends AbstractDataEditor<T>
        implements Transactional, ObjectSetter<T>,
        ObjectChangedListener<T>, ObjectEditorListener, ObjectProvider<T>, AlertHandler, TransactionCreator {

    /**
     * The layout where buttons are displayed.
     */
    protected HasComponents buttonPanel;
    /**
     * Print button if defined. Print button will be defined automatically. Please see {@link PrintButton}.
     */
    protected PrintButton print;
    /**
     * Button - Add.
     */
    protected Button add;
    /**
     * Button - Edit.
     */
    protected Button edit;
    /**
     * Button - Delete.
     */
    protected Button delete;
    /**
     * Button - Search.
     */
    protected Button search;
    /**
     * Button - Report.
     */
    protected Button report;
    /**
     * Button - Audit.
     */
    protected Button audit;
    /**
     * Button - Exit.
     */
    protected Button exit;
    /**
     * Button - Save.
     */
    protected Button save;
    /**
     * Button - Cancel.
     */
    protected Button cancel;
    ObjectSearcherField<T> searcherField;
    private int actions;
    private boolean closeOnSave, editing = false;
    List<ObjectChangedListener<T>> objectChangedListeners = new ArrayList<>();
    private final List<ObjectEditorListener> objectEditorListeners = new ArrayList<>();
    private List<Predicate<T>> validators;
    private ObjectSearcher<T> searcher;
    private Function<ObjectEditor<T>, Boolean> saver, deleter;
    private final TreeSet<String> setNotAllowed = new TreeSet<>();
    private final List<ObjectLinkField<?>> linkFields = new ArrayList<>();
    private StreamAttachmentData streamAttachmentData;
    private ExtraInfo<?> extraInfo;
    private ContactData contactData;
    private boolean doNotSave = false, allowDoNotSave = true;
    private String allowedActions;
    private AnchorForm anchorForm;
    private ObjectLinkField.Tabs linkTabs;
    private StoredObject parentObject;
    private int parentLinkType = 0;
    private NewObject<T> newObject;
    private Transaction tran;
    private TransactionCreator tranCreator;
    private List<String> fieldPositions;
    private ObjectInput<T> formField;
    private Logic logic;
    Grid<T> grid;
    private String savedInstance;
    private boolean buffered = false;
    boolean anchorAction = true;

    /**
     * Constructor.
     *
     * @param objectClass {@link StoredObject} class.
     */
    public ObjectEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }


    /**
     * Constructor.
     *
     * @param objectClass {@link StoredObject} class.
     * @param actions {@link EditorAction} values ORed.
     */
    public ObjectEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }


    /**
     * Constructor.
     *
     * @param objectClass {@link StoredObject} class.
     * @param actions {@link EditorAction} values ORed.
     * @param caption Caption.
     */
    public ObjectEditor(Class<T> objectClass, int actions, String caption) {
        this(objectClass, actions, caption, null);
    }


    /**
     * Constructor.
     *
     * @param className Fully-qualified name of the {@link StoredObject} class. The class name may be decorated to
     *                  specify the allowed actions. <p>For example, if you specify the class name like this
     *                  - "(ADD,EDIT)com.storedobject.core.Person" -, it will allow only ADD and EDIT operations.</p>
     */
    @SuppressWarnings("unchecked")
    public ObjectEditor(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(sanitize(className)), actions(className, Application.get().getServer().isDeveloper()),
                Application.get().getRunningLogic().getTitle(), allowedActions(className));
    }

    /**
     * Constructor.
     *
     * @param objectClass {@link StoredObject} class.
     * @param actions {@link EditorAction} values ORed.
     * @param caption Caption.
     * @param allowedActions Allowed actions as comma-separated string values of {@link EditorAction}.
     */
    protected ObjectEditor(Class<T> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, caption);
        this.allowedActions = allowedActions;
        this.actions = actions == 0 ? EditorAction.ALL : actions;
        this.actions = filterActions(this.actions);
        addConstructedListener(o -> fConstructed());
    }

    /**
     * Create an instance of the editor from the parameters passed. The returned instance would be of an extended class
     * of {@link ObjectEditor} if such a class exists.
     *
     * @param objectClass {@link StoredObject} class.
     * @param <O> Type of {@link StoredObject} class.
     * @return An instacne of {@link ObjectEditor} or its derivative.
     */
    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass) {
        return create(objectClass, EditorAction.ALL);
    }

    /**
     * Create an instance of the editor from the parameters passed. The returned instance would be of an extended class
     * of {@link ObjectEditor} if such a class exists.
     *
     * @param objectClass {@link StoredObject} class.
     * @param actions {@link EditorAction} values ORed.
     * @param <O> Type of {@link StoredObject} class.
     * @return An instacne of {@link ObjectEditor} or its derivative.
     */
    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    /**
     * Create an instance of the editor from the parameters passed. The returned instance would be of an extended class
     * of {@link ObjectEditor} if such a class exists.
     *
     * @param objectClass {@link StoredObject} class.
     * @param actions {@link EditorAction} values ORed.
     * @param title Caption.
     * @param <O> Type of {@link StoredObject} class.
     * @return An instacne of {@link ObjectEditor} or its derivative.
     */
    public static <O extends StoredObject> ObjectEditor<O> create(Class<O> objectClass, int actions, String title) {
        return create(null, objectClass, actions, title);
    }

    static <O extends StoredObject> ObjectEditor<O> create(ObjectEditor<O> another, int actions, String title) {
        return create(another, null, actions, title);
    }

    @SuppressWarnings({"unchecked"})
    private static <O extends StoredObject> ObjectEditor<O> create(ObjectEditor<O> another, Class<O> objectClass, int actions, String title) {
        if(objectClass == SystemUser.class) {
            return (ObjectEditor<O>) new SystemUserEditor(actions, title);
        }
        try {
            Class<?> logic;
            if(another != null) {
                logic = another.getClass();
                objectClass = another.getObjectClass();
            } else {
                logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "Editor"));
            }
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(int.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectEditor<O>) c.newInstance(new Object[] { actions, title });
            }
            try {
                c = logic.getConstructor(int.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectEditor<O>) c.newInstance(new Object[] { actions });
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectEditor<O>) c.newInstance(new Object[] { });
            }
        } catch(Throwable t) {
            Application.get().log(t);
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POEditor(objectClass, actions, title);
        }
        if(InventoryPOItem.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POItemEditor(objectClass, actions, title);
        }
        if(EntityRole.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new EntityRoleEditor(objectClass, actions, title);
        }
        if(PersonRole.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new PersonRoleEditor(objectClass, actions, title);
        }
        return new ObjectEditor<>(objectClass, actions, title);
    }

    static String sanitize(String className) {
        if(!className.startsWith("(")) {
            return className;
        }
        int p = className.indexOf(')');
        return p < 0 ? className : className.substring(p + 2);
    }

    private void fConstructed() {
        anchorForm = new AnchorForm();
        if(anchorForm.getFieldCount() == 0) {
            anchorForm = null;
        }
        setLinkTabColumns(getColumns());
    }

    /**
     * Create an editor for the purpose of embedding an object field.
     *
     * @param formField Field to embed.
     *
     * @param <O> Type of object.
     * @return Editor.
     */
    public static <O extends StoredObject> ObjectEditor<O> create(ObjectInput<O> formField) {
        ObjectEditor<O> oe = ObjectEditor.create(formField.getObjectClass());
        oe.formField = formField;
        return oe;
    }

    @Override
    public void setColumns(int columns) {
        super.setColumns(columns);
        setLinkTabColumns(columns);
    }

    private void setLinkTabColumns(int columns) {
        if(linkTabs != null) {
            linkTabs.getElement().setAttribute("colspan", "" + columns);
        }
    }

    @Override
    public void setCaption(String caption) {
        setCaption(caption, false);
    }

    void setCaption(String caption, boolean internal) {
        if(!internal && (caption == null || caption.isEmpty())) {
            caption = getCaption();
            if(caption == null || caption.isEmpty()) {
                return;
            }
            error("Error: Please inform Syam about this error, trying to overwrite caption '" + caption + "' with empty value!");
            Thread.dumpStack();
            return;
        }
        super.setCaption(caption);
        if(anchorForm != null) {
            anchorForm.setCaption(caption);
        }
        if(searcher instanceof ObjectBrowser) {
            ((ObjectBrowser<T>) searcher).setCaption("Search: " + caption);
        }
    }

    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
        }
    }

    @NoField
    @Override
    public final Logic getLogic() {
        return logic;
    }

    /**
     * Get the grid associated with this editor.
     *
     * @return Associated grid. Could be null.
     */
    public final Grid<T> getGrid() {
        return grid;
    }

    List<String> fieldPositions() {
        return fieldPositions;
    }

    void fieldPositions(List<String> fieldPositions) {
        this.fieldPositions = fieldPositions;
    }

    void formField(ObjectFormField<T> formField) {
        this.formField = formField;
        getComponent();
    }

    @Override
    public final void setTransactionCreator(TransactionCreator tranCreator) {
        if(this.tranCreator == tranCreator) {
            return;
        }
        if(this.tranCreator != null || this.tran != null) {
            throw new SORuntimeException("Unsupported pseudo transaction creation sequence");
        }
        this.tranCreator = tranCreator;
    }

    private class TranCleanUp implements Transaction.CommitListener {

        @Override
        public void committed(Transaction transaction) {
            tran = null;
        }

        @Override
        public void rolledback(Transaction transaction) {
            tran = null;
        }
    }

    @Override
    public final Transaction getTransaction(boolean create) {
        if(tranCreator != null) {
            return tranCreator.getTransaction(create);
        }
        if(tran != null && !tran.isActive()) {
            tran = null;
        }
        if(tran == null && create) {
            tran = getTransactionManager().createPseudoTransaction();
            tran.addCommitListener(new TranCleanUp());
        }
        return tran;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return super.getApplication();
    }

    /**
     * Get the filtered result of actions. This is invoked before allowing the actions and this can disable some of
     * the actions allowed. The default implementation doesn't filter anything.
     *
     * @param actions Actions to filter. (ORed values of {@link EditorAction})
     * @return Filtered action value.
     */
    protected int filterActions(int actions) {
        return actions;
    }

    /**
     * Check whether a specific string representation of action is allowed or not.
     *
     * @param action Action name to check.
     * @return True/false.
     */
    protected boolean isActionAllowed(String action) {
        return allowedActions == null || allowedActions.contains(action);
    }

    /**
     * Remove a specific string representation of action from the allowed list of actions.
     *
     * @param action Action name to remove.
     */
    protected void removeAllowedAction(String action) {
        if(allowedActions != null) {
            allowedActions = allowedActions.replace(action, "-");
        }
    }

    /**
     * Determine the allowed actions from the decorated class name passed. See the constructor
     * {@link #ObjectEditor(String)} to understand the concept of decorated class name.
     *
     * @param className Decorated class name.
     * @return Action names extracted.
     */
    protected static String allowedActions(String className) {
        if(!className.startsWith("(")) {
            return null;
        }
        int p = className.indexOf(')');
        if(p < 0) {
            return null;
        }
        return className.substring(1, p);
    }

    private static int actions(String className, boolean developer) {
        if(!className.startsWith("(")) {
            return allActions(developer);
        }
        int p = className.indexOf(')');
        if(p < 0) {
            return allActions(developer);
        }
        return EditorAction.getActions(className.substring(1, p), developer);
    }

    private static int allActions(boolean developer) {
        return EditorAction.ALL | (developer ? (EditorAction.PDF | EditorAction.AUDIT) : 0);
    }

    @Override
    protected void initUI() {
        buttonPanel = createButtonLayout();
        if(buttonPanel == null) {
            buttonPanel = createDefaultButtonLayout();
        }
        HasComponents containerLayout = createLayout();
        Component container = null;
        if(containerLayout == null) {
            container = createDefaultLayout();
        } else if(containerLayout instanceof Component) {
            container = (Component) containerLayout;
        }
        buildButtons();
        Component fc = form.getComponent();
        if(container != null) {
            setComponent(container);
        }
        if(formField == null) {
            if(container instanceof ContentWithHeader) {
                if(buttonPanel instanceof Component) {
                    ((ContentWithHeader) container).setHeader((Component) buttonPanel);
                }
                ((ContentWithHeader) container).setBody(fc);
            } else if(container instanceof HasComponents) {
                if(buttonPanel instanceof Component) {
                    ((HasComponents) container).add((Component) buttonPanel);
                }
                ((HasComponents) container).add(fc);
            }
        }
        drawButtons();
    }

    @Override
    public boolean skipFirstFocus(Focusable<?> skipFocus) {
        return skipFocus == save || skipFocus == cancel;
    }

    private Component createDefaultLayout() {
        return new ContentWithHeader();
    }

    private HasComponents createDefaultButtonLayout() {
        return new ButtonLayout();
    }

    @Override
    public boolean isFieldEditable(String fieldName) {
        boolean ed = super.isFieldEditable(fieldName);
        if(ed && setNotAllowed.contains(fieldName)) {
            T object = getObject();
            return object == null || object.created();
        }
        return ed;
    }

    /**
     * Set the "set not allowed" flag for a field so that, it will not allow setting that value if the values already
     * exists. (Typically, this is invoked by the platform itself).
     *
     * @param fieldName Field name.
     */
    public void setSetNotAllowed(String fieldName) {
        setNotAllowed.add(fieldName);
    }

    private static boolean isFF(HasValue<?, ?> field) {
        return field instanceof ObjectFormField ||
                (field instanceof ObjectField<?> of && of.getField() instanceof ObjectFormField);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean handleValueSetError(String fieldName, HasValue<?, ?> field, Object fieldValue, Object objectValue, Throwable error) {
        Throwable cause = error.getCause();
        if(cause == null) {
            cause = error;
        }
        if(cause instanceof Set_Not_Allowed) {
            if(field.isReadOnly() || isFF(field)) {
                return false;
            }
            setNotAllowed.add(fieldName);
            HasValue<?, Object> f = (HasValue<?, Object>) field;
            f.setValue(objectValue);
            field.setReadOnly(true);
            return true;
        }
        if(error instanceof IllegalArgumentException && fieldValue == null) {
            return true;
        }
        String e = "Error occurred while setting value for the attribute '" + fieldName + "' of class " +
                getObjectClass().getName() + " from field " + field.getClass() + " (Value: " + fieldValue + ").";
        if(error instanceof IllegalArgumentException) {
            e += "\nPlease check the compatibility of the value.";
            if(field instanceof ObjectInput) {
                e += "\nMaybe, you should wrap your field like - new ObjectField(your field);";
            }
        }
        log(e);
        error(error);
        return true;
    }

    /**
     * Set the "new object" creator so that it will be used for creating new instances. If set, the method
     * {@link #createObjectInstance()} will return instances created by this.
     *
     * @param newObject New object creator to set.
     */
    public void setNewObjectGenerator(NewObject<T> newObject) {
        this.newObject = newObject;
    }

    @Override
    protected T createObjectInstance() {
        T instance;
        if(newObject != null) {
            try {
                instance = newObject.newObject(getTransactionManager());
            } catch(Exception e) {
                return null;
            }
        } else {
            try {
                instance = getObjectClass().getDeclaredConstructor().newInstance();
            } catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                instance = super.createObjectInstance();
            }
        }
        if(instance instanceof OfEntity && Id.isNull(((OfEntity) instance).getSystemEntityId())) {
            try {
                Method m = getObjectClass().getMethod("setSystemEntity", Id.class);
                SystemEntity se = getTransactionManager().getEntity();
                if(se != null) {
                    m.invoke(instance, se.getId());
                }
            } catch(Throwable ignored) {
            }
        }
        return instance;
    }

    @Override
    public T getObject() {
        return getForm().getObject(false);
    }

    private boolean inventoryItem() {
        // Inventory items should not be manipulated directly
        return InventoryItem.class.isAssignableFrom(getObjectClass());
    }

    private boolean inventory() {
        // Do not allow certain special classes to directly inherit this class with editability
        return (InventoryPO.class.isAssignableFrom(getObjectClass()) && !(this instanceof POEditor)) ||
                (InventoryPOItem.class.isAssignableFrom(getObjectClass()) && !(this instanceof POItemEditor));
    }

    private void buildButtons() {
        boolean notInvI = !inventoryItem();
        boolean notInv = !inventory();
        boolean nm = !MasterObject.class.isAssignableFrom(getObjectClass());
        if(nm && notInvI && notInv && ((actions & EditorAction.NEW) == EditorAction.NEW)) {
            add = new Button("Add", this);
        }
        if(nm && notInv && ((actions & EditorAction.EDIT) == EditorAction.EDIT)) {
            edit = new Button("Edit", this);
        }
        if(nm && notInvI && notInv && ((actions & EditorAction.DELETE) == EditorAction.DELETE)) {
            delete = new ConfirmButton("Delete", this);
        }
        if((actions & EditorAction.SEARCH) == EditorAction.SEARCH) {
            searcherField = ObjectSearcherField.create(getObjectClass(), this);
            if(searcherField == null) {
                search = new Button("Search", this);
            }
        }
        print = PrintButton.create(this);
        if((actions & EditorAction.PDF) == EditorAction.PDF) {
            report = new Button("Report", this);
        }
        if(nm && ((actions & EditorAction.AUDIT) == EditorAction.AUDIT)) {
            audit = new Button("Audit", "user", this);
        }
        exit = new Button("Exit", this);
        save = new Button("Save", this).asPrimary();
        cancel = new Button("Cancel", this);
        createExtraButtons();
    }

    /**
     * Add an "object changed" listener to track object changes in the editor. An "object change" event is
     * fired whenever the object is inserted/updated/deleted/saved/undeleted.
     *
     * @param listener Listener to add.
     */
    public void addObjectChangedListener(ObjectChangedListener<T> listener) {
        if(listener != null && listener != this) {
            objectChangedListeners.add(listener);
        }
    }

    /**
     * Remove the "object changed" listener previously added.
     *
     * @param listener Listener to remove.
     */
    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        objectChangedListeners.remove(listener);
    }

    /**
     * Add an "object editor" listener to track editing changes. An "object editor" event is
     * fired whenever a different object is set to the editor.
     *
     * @param listener Listener to add.
     */
    public void addObjectEditorListener(ObjectEditorListener listener) {
        if(listener != null && listener != this) {
            objectEditorListeners.add(listener);
        }
    }

    /**
     * Remove the "object editor" listener previously added.
     *
     * @param listener Listener to remove.
     */
    public void removeObjectEditorListener(ObjectEditorListener listener) {
        objectEditorListeners.remove(listener);
    }

    /**
     * Add a validator that will be checked for validating the object instance.
     *
     * @param validator Validator.
     */
    public void addValidator(Predicate<T> validator) {
        if(validators == null) {
            validators = new ArrayList<>();
        }
        validators.add(validator);
    }

    /**
     * Eemove a validator that was added earlier.
     *
     * @param validator Validator.
     */
    public void removeValidator(Predicate<T> validator) {
        if(validators != null) {
            validators.remove(validator);
        }
    }

    /**
     * This method is invoked at the time of creating the UI and this is where extra buttons need t be created to
     * display as extra buttons on the {@link #buttonPanel}.
     */
    protected void createExtraButtons() {
    }

    /**
     * This method is invoked whenever an instance of the object is set. Typically, "extra buttons" can be added to
     * the {@link #buttonPanel} by examining the object instance value.
     */
    protected void addExtraButtons() {
    }

    /**
     * This method is invoked whenever an instance of the object is added/edited. Typically, "extra buttons" can be
     * added to the {@link #buttonPanel} by examining the object instance value.
     */
    protected void addExtraEditingButtons() {
    }

    /**
     * This method is invoked whenever an instance of the object is set. Typically, "print buttons" can be made
     * visible or hidden by examining the object instance value. The default implementation makes the "print" button
     * visible if the object is non-null. (See {@link PrintButton}).
     */
    protected void enablePrintButtons() {
        if(print != null) {
            print.setVisible(getObject() != null);
        }
    }

    void buttonsOff() {
        getComponent();
        if(add != null) {
            add.setVisible(false);
        }
        if(edit != null) {
            edit.setVisible(false);
        }
        if(delete != null) {
            delete.setVisible(false);
        }
        if(search != null) {
            search.setVisible(false);
        }
        if(searcherField != null) {
            searcherField.setVisible(false);
        }
        if(print != null) {
            print.setVisible(false);
        }
        if(report != null) {
            report.setVisible(false);
        }
        if(audit != null) {
            audit.setVisible(false);
        }
        if(exit != null) {
            exit.setVisible(false);
        }
    }

    private void drawButtons() {
        buttonPanel.removeAll();
        form.setReadOnly(true);
        if(add != null) {
            buttonPanel.add(add);
        }
        T object = getObject();
        if(edit != null && object != null) {
            buttonPanel.add(edit);
        }
        if(delete != null && object != null) {
            buttonPanel.add(delete);
        }
        if(searcherField != null) {
            buttonPanel.add(searcherField);
        } else if(search != null) {
            buttonPanel.add(search);
        }
        addExtraButtons();
        if(print != null) {
            buttonPanel.add(print);
        }
        enablePrintButtons();
        if(report != null) {
            buttonPanel.add(report);
        }
        if(audit != null && object != null) {
            buttonPanel.add(audit);
        }
        if(exit != null) {
            buttonPanel.add(exit);
        }
    }

    @Override
    public void clean() {
        super.clean();
        clearTran();
    }

    @Override
    public void clicked(Component c) {
        if(c == exit) {
            close();
            return;
        }
        if(c == cancel) {
            doCancelInt(true);
            return;
        }
        if(c == save) {
            doSave();
            return;
        }
        if(c == add) {
            doAdd();
            return;
        }
        if(c == edit) {
            doEdit();
            return;
        }
        if(c == delete) {
            doDelete();
            return;
        }
        if(c == search) {
            doSearch();
            return;
        }
        if(c == report) {
            doReport();
            return;
        }
        if(c == audit) {
            doAudit();
        }
        super.clicked(c);
    }

    /**
     * Is editing? (Editing may not have started even if {@link #isReadOnly()} returns <code>false</code>).
     *
     * @return True/false.
     */
    public boolean isEditing() {
        if(formField != null) {
            return !((HasValue<?, ?>)formField).isReadOnly();
        }
        //return save != null && buttonPanel == save.getParent().orElse(null);
        return editing;
    }

    /**
     * Is read-only?
     *
     * @return True/false.
     */
    public boolean isReadOnly() {
        return getForm().isReadOnly();
    }

    /**
     * Save the current object being edited if the editor is in the editing mode. This is equivalent to pressing the
     * "Save" button in editing mode.
     */
    public void doSave() {
        if(isEditing()) {
            doSaveInt();
        }
    }

    private boolean doSaveInt() {
        if(!form.commit()) {
            return false;
        }
        T object = getObject();
        object.clearObjectLinks();
        for(ObjectLinkField<?> linkField : linkFields) {
            linkField.getValue().copy().attach();
        }
        if(streamAttachmentData != null) {
            streamAttachmentData.copy().attach();
        }
        if(extraInfo != null) {
            extraInfo.getValue().copy().attach();
        }
        if(contactData != null && contactData.ownedByMaster()) {
            contactData.copy().attach();
        }
        boolean created = object.created();
        try {
            validateLinks();
            validateData();
            if(validators != null && validators.stream().anyMatch(v -> !v.test(object))) {
                return false;
            }
            object.validateData(getTransactionManager());
        } catch(Exception error) {
            warning(error);
            return false;
        }
        try {
            if(!doNotSave && !saveInt()) {
                if(created) {
                    setObject(null, false);
                }
                return false;
            }
        } catch(Exception error) {
            log(error);
            warning(error);
            doCancelInt(false);
            return false;
        }
        if(grid != null) {
            Editor<T> e = grid.getEditor();
            if(e != null) {
                e.cancel();
            }
        }
        if(!doNotSave) {
            if(saver == null) {
                reloadInt();
            } else {
                clearTran();
            }
        }
        editingEndedInternal();
        drawButtons();
        if(closeOnSave) {
            closeOnSave = false;
            close();
        }
        if(created) {
            inserted(object);
        } else {
            updated(object);
        }
        return true;
    }

    private boolean saveInt() throws Exception {
        boolean saved = save();
        if(saved) {
            clearTran();
        }
        return saved;
    }

    /**
     * Save the current instance to the database. This method carries out the save operation set by the
     * {@link #setSaver(Function)}. If no such operation is specified, then, it invokes {@link #save(Transaction)}
     * to carry out the operation.
     *
     * @return True if the operation was successful. Otherwise, false is returned.
     * @throws Exception Raises when save operation is not successful.
     */
    protected boolean save() throws Exception {
        if(saver != null) {
            return saver.apply(this);
        }
        return transact(logic, getTransaction(false), this::save);
    }

    /**
     * Save the current instance to the database.
     * <p>Note: Not only save operation includes saving the current instance, but also
     * it includes saving the links, connected master (if any), contact data, attachments etc. So, this method may be
     * invoked from within customized save operations too.</p>
     *
     * @param t Transaction.
     * @throws Exception Raises when save operation is not successful.
     */
    public void save(Transaction t) throws Exception {
        T object = getObject();
        if(parentObject != null) {
            object.setMaster(parentObject, parentLinkType);
        }
        object.save(t);
        if(parentObject != null) {
            parentObject.addLink(t, object, parentLinkType);
        }
        if(extraInfo != null) {
            extraInfo.getValue().save(t);
        }
        if(contactData != null && !contactData.ownedByMaster()) {
            contactData.save(t);
        }
    }

    /**
     * This is equivalent to pressing the "Cancel" button while editing the instance.
     */
    public void doCancel() {
        if(isEditing()) {
            doCancelInt(true);
        }
        if(closeOnSave) {
            closeOnSave = false;
            abort();
        }
    }

    private void doCancelInt(boolean clearAlerts) {
        if(clearAlerts) {
            clearAlerts();
        }
        if(grid != null) {
            Editor<T> e = grid.getEditor();
            if(e != null) {
                e.cancel();
            }
        }
        reloadInt();
        editingCancelledInternal();
        drawButtons();
        if(closeOnSave) {
            closeOnSave = false;
            abort();
        }
    }

    private void validateLinks() throws SOException {
        StoredObject duplicate;
        for(ObjectLinkField<?> linkField: linkFields) {
            if(linkField.getLink().isDetail()) {
                @SuppressWarnings("unchecked") EditableList<Detail> value = (EditableList<Detail>) linkField.getValue();
                duplicate = (StoredObject) value.getDuplicate(Detail::getUniqueId);
                if(duplicate == null) {
                    duplicate = (StoredObject) value.getDuplicate(Detail::getUniqueValue);
                }
            } else {
                duplicate = linkField.getValue().getDuplicate(StoredObject::getId);
            }
            if(duplicate != null) {
                throw new SOException("Duplicate entry: " + duplicate.toDisplay());
            }
        }
    }

    /**
     * This method is invoked before saving whenever the object instance is added/edited.
     *
     * @throws Exception If thrown, the save operation is abandoned and fields are enabled for editing again.
     */
    @SuppressWarnings("RedundantThrows")
    public void validateData() throws Exception {
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
        if(anchorForm != null) {
            anchorAction = true;
        }
    }

    /**
     * This method is invoked when anchor values are set via the anchor form and if any exception is
     * thrown from this method, anchor values will be asked again.
     *
     * @throws Exception If anchor values are not acceptable for some reason.
     */
    protected void anchorsSet() throws Exception {
    }

    /**
     * This method will be invoked if the "Cancel" button is pressed on the anchor form.
     *
     */
    protected void anchorsCancelled() {
    }

    /**
     * Execute the "anchor form".
     */
    public void executeAnchorForm() {
        executeAnchorForm(() -> {});
    }

    /**
     * Execute the "anchor form" and run some specified action.
     *
     * @param action Action to run.
     */
    public void executeAnchorForm(Runnable action) {
        if(anchorForm != null && anchorAction) {
            anchorForm.run(action);
        } else {
            action.run();
        }
    }

    @SuppressWarnings("unchecked")
    private void startAdd() {
        setObject(null, true);
        T object = getForm().getObject(true);
        if(object != null && anchorForm != null) {
            try {
                setObject(object, true);
                anchorForm.streamFieldNamesCreated().forEach(fieldName -> {
                    HasValue<?, Object> f = (HasValue<?, Object>) getField(fieldName);
                    HasValue<?, Object> af = (HasValue<?, Object>) anchorForm.getField(fieldName);
                    f.setValue(af.getValue());
                    setFieldReadOnly(fieldName);
                });
                commit();
                validateAnchorValues(object);
                if(grid instanceof ObjectBrowser) {
                    ((ObjectBrowser<T>) grid).validateAnchorValues(object);
                }
            } catch(SOException e) {
                warning(e);
                object = null;
                setObject(null, true);
            }
        }
        if(object == null) {
            if(closeOnSave) {
                closeOnSave = false;
                close();
            }
            return;
        }
        doEditInt(true);
    }

    /**
     * This is equivalent to pressing the "Add" button.
     */
    public void doAdd() {
        if(!canAdd()) {
            return;
        }
        if(anchorForm != null && anchorAction) {
            anchorForm.run(this::startAdd);
        } else {
            startAdd();
        }
    }

    /**
     * This is equivalent to pressing the "Edit" button.
     */
    public void doEdit() {
        doEditInt(false);
    }

    private void doEditInt(boolean adding) {
        doEditInt(adding, null);
    }

    private boolean doEditInt(boolean adding, Grid<T> grid) {
        T object = getObject();
        if(object == null || (!adding && !canEdit())) {
            return false;
        }
        form.setReadOnly(false);
        if(doNotSave) {
            linkFields.forEach(f -> f.setReadOnly(true));
        }
        Stream<HasValue<?, ?>> fields;
        if(grid instanceof EditableDataGrid) {
            fields = ((EditableDataGrid<?>) grid).streamEditableFields();
        } else {
            fields = streamFieldsCreated();
        }
        if(fields.allMatch(HasValue::isReadOnly)) {
            form.setReadOnly(true);
            return false;
        }
        buttonPanel.removeAll();
        buttonPanel.add(save);
        addExtraEditingButtons();
        buttonPanel.add(cancel);
        setObject(object, true);
        editingStartedInternal();
        focus();
        return true;
    }

    /**
     * This is equivalent to pressing the "Delete" button.
     */
    public void doDelete() {
        T object = getObject();
        if(object == null || isEditing() || !canDelete()) {
            return;
        }
        boolean deleted = false;
        try {
            deleted = delete();
        } catch(Exception e) {
            error(e);
        }
        if(deleted) {
            setObject(null, true);
            deleted(object);
        } else {
            reloadInt();
        }
        drawButtons();
    }

    /**
     * This is equivalent to pressing the "Search" button.
     */
    public void doSearch() {
        if(!canSearch()) {
            return;
        }
        if(anchorForm != null && anchorAction) {
            anchorForm.run(this::startSearch);
        } else {
            startSearch();
        }
    }

    /**
     * This is equivalent to pressing the "Report" button.
     */
    public void doReport() {
        new ObjectList<>(getApplication(), getObjectClass()).execute();
    }

    /**
     * This is equivalent to pressing the "Audit" button.
     */
    public void doAudit() {
        T object = getObject();
        if(object != null) {
            new ObjectHistoryGrid<>(object).executeAll();
        }
    }

    void startSearch() {
        searcher = searcher();
        if(searcher != null) {
            searcher.search(getTransactionManager().getEntity(), this);
        }
    }

    private ObjectSearcher<T> searcher() {
        if(searcher == null) {
            ObjectSearcher<T> s = getSearcher();
            if(s == null) { // Searching deliberately switched off
                return null;
            }
            if(s != searcher) { // Custom search, need to be configured
                searcher = s;
                configureSearch();
            }
        }
        return searcher;
    }

    private void configureSearch() {
        if(searcher instanceof ObjectBrowser) {
            ((ObjectBrowser<T>)searcher).setCaption("Search: " + getCaption());
            ((ObjectBrowser<T>)searcher).editor = this;
        }
    }

    private ObjectSearcher<T> constructSearch() {
        if(searcher == null) {
            searcher = new ObjectBrowser<>(getObjectClass(),
                    EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0));
            configureSearch();
        }
        return searcher;
    }

    /**
     * Get the searcher for this editor.
     *
     * @return Searcher.
     */
    public ObjectSearcher<T> getSearcher() {
        return searcher == null ? constructSearch() : searcher;
    }

    /**
     * Do the delete operation of the current object instance.
     *
     * @return True if successful.
     * @throws Exception Exception while deleting.
     */
    protected boolean delete() throws Exception {
        T object = getObject();
        if(deleter != null) {
            return deleter.apply(this);
        }
        return transact(logic, getTransaction(false), object::delete);
    }

    void editingStartedInternal() {
        editingStarted();
        objectEditorListeners.forEach(ObjectEditorListener::editingStarted);
        editing = true;
        T object = getObject();
        if(!buffered || object.created()) {
            savedInstance = null;
        } else {
            try {
                savedInstance = object.stringify();
            } catch(Exception e) {
                savedInstance = null;
            }
        }
    }

    void editingEndedInternal() {
        editingEnded();
        savedInstance = null;
        objectEditorListeners.forEach(ObjectEditorListener::editingEnded);
        editing = false;
    }

    void editingCancelledInternal() {
        editingCancelled();
        savedInstance = null;
        objectEditorListeners.forEach(ObjectEditorListener::editingCancelled);
        editing = false;
    }

    /**
     * Invoked when editing is started.
     */
    @Override
    public void editingStarted() {
    }

    /**
     * Invoked when editing is ended.
     */
    @Override
    public void editingEnded() {
    }

    /**
     * Invoked when editing is cancelled.
     */
    @Override
    public void editingCancelled() {
    }

    /**
     * Check whether delete operation is allowed for the current object instance.
     *
     * @return True if allowed.
     */
    public boolean canDelete() {
        return delete != null && delete.isEnabled();
    }

    /**
     * Check whether edit operation is allowed for the current object instance.
     *
     * @return True if allowed.
     */
    public boolean canEdit() {
        return edit != null && edit.isEnabled();
    }

    /**
     * Check whether add operation is allowed or not.
     *
     * @return True if allowed.
     */
    public boolean canAdd() {
        return add != null && add.isEnabled();
    }

    /**
     * Check whether search operation is allowed or not.
     *
     * @return True if allowed.
     */
    public boolean canSearch() {
        return search != null && search.isEnabled();
    }

    private void clearTran() {
        if(tranCreator == null && tran != null) {
            tran.rollback();
            tran = null;
        }
    }

    /**
     * Reload the current object instance.
     */
    public void reload() {
        reloadInt();
        drawButtons();
    }

    private void reloadInt() {
        clearTran();
        T object = getForm().getObject(false);
        if(object != null) {
            if(object.created()) {
                object = null;
            } else {
                if(savedInstance == null) {
                    object.reload();
                } else {
                    try {
                        object.load(new LineNumberReader(new BufferedReader(new StringReader(savedInstance))));
                    } catch(Exception e) {
                        object.reload();
                    } finally {
                        savedInstance = null;
                    }
                }
            }
        }
        setObject(object, true);
    }

    private void interruptEditing(T objectToSet) {
        getComponent();
        if(cancel.getParent().orElse(null) != null) {
            editingCancelledInternal();
            if(closeOnSave) {
                close();
                closeOnSave = false;
            }
        }
        try {
            setObject(objectToSet, true);
        } catch(Throwable error) {
            log(error);
        }
        drawButtons();
    }

    /**
     * This method is invoked whenever a new object is inserted in the database.
     *
     * @param object Object being inserted
     */
    @Override
    public void inserted(T object) {
        objectChangedListeners.forEach(ocl -> ocl.inserted(object));
        saved(object);
        if(!doNotSave && searcher instanceof ObjectBrowser) {
            ((ObjectBrowser<T>) searcher).inserted(object);
        }
    }

    /**
     * This method is invoked whenever a new object is updated in the database.
     *
     * @param object Object being updated
     */
    @Override
    public void updated(T object) {
        if(object.equals(getObject())) {
            interruptEditing(object);
        }
        saved(object);
        objectChangedListeners.forEach(ocl -> ocl.updated(object));
        if(!doNotSave && searcher instanceof ObjectBrowser) {
            ((ObjectBrowser<T>) searcher).updated(object);
        }
    }

    /**
     * This method is invoked whenever a new object is deleted in the database.
     *
     * @param object Object being deleted
     */
    @Override
    public void deleted(T object) {
        if(object.equals(getObject())) {
            interruptEditing(null);
        }
        objectChangedListeners.forEach(ocl -> ocl.deleted(object));
        if(searcher instanceof ObjectBrowser) {
            ((ObjectBrowser<T>) searcher).deleted(object);
        }
    }

    @Override
    public void saved(T object) {
    }

    /**
     * View the current object. If the editor is in the editing mode, it will be cancelled and it will be switched
     * to viewing mode.
     */
    public void viewObject() {
        viewObject(null, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     */
    public void viewObject(T object) {
        viewObject(object, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param parent Parent view.
     */
    public void viewObject(T object, com.storedobject.vaadin.View parent) {
        viewObject(object, parent, parent == null);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param parent Parent view.
     * @param doNotLock To specify whether the parent to be locked or not.
     */
    public void viewObject(T object, com.storedobject.vaadin.View parent, boolean doNotLock) {
        viewObject(object, null, null, parent, doNotLock);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param actionName Name of the action to be allowed while viewing. This will be used only when an action is
     *                   specified and is used as a label to the button for the action.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     */
    public void viewObject(String actionName, Consumer<T> action) {
        viewObject(null, actionName, action, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param actionName Name of the action to be allowed while viewing. This will be used only when an action is
     *                   specified and is used as a label to the button for the action.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     */
    public void viewObject(T object, String actionName, Consumer<T> action) {
        viewObject(object, actionName, action, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param actionName Name of the action to be allowed while viewing. This will be used only when an action is
     *                   specified and is used as a label to the button for the action.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     * @param parent Parent view.
     */
    public void viewObject(T object, String actionName, Consumer<T> action, com.storedobject.vaadin.View parent) {
        viewObject(object, actionName, action, parent, parent == null);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     */
    public void viewObject(Consumer<T> action) {
        viewObject(getObject(), null, action, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     */
    public void viewObject(T object, Consumer<T> action) {
        viewObject(object, null, action, null, true);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     * @param parent Parent view.
     */
    public void viewObject(T object, Consumer<T> action, com.storedobject.vaadin.View parent) {
        viewObject(object, null, action, parent, parent == null);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     * @param parent Parent view.
     * @param doNotLock To specify whether the parent to be locked or not.
     */
    public void viewObject(T object, Consumer<T> action, com.storedobject.vaadin.View parent, boolean doNotLock) {
        viewObject(object, null, action, parent, doNotLock);
    }

    /**
     * View the the object that is passed. If the editor is in the editing mode, it will be cancelled and it will
     * be switched to viewing mode.
     *
     * @param object Object to view. If null is passed, current object will be used.
     * @param actionName Name of the action to be allowed while viewing. This will be used only when an action is
     *                   specified and is used as a label to the button for the action.
     * @param action Action to carry out. A button will be displayed and that can be pressed for invoking the action.
     * @param parent Parent view.
     * @param doNotLock To specify whether the parent to be locked or not.
     */
    public void viewObject(T object, String actionName, Consumer<T> action,
                           com.storedobject.vaadin.View parent, boolean doNotLock) {
        if(object == null) {
            object = getObject();
        }
        if(object == null) {
            return;
        }
        interruptEditing(object);
        execute(parent, doNotLock);
        buttonPanel.removeAll();
        if(action != null) {
            T o = object;
            String icon;
            if(actionName == null || actionName.isEmpty() || "Process".equals(actionName)) {
                actionName = "Process";
                icon = "process";
            } else {
                int p = actionName.indexOf('|');
                if(p > 0) {
                    icon = actionName.substring(p + 1).trim();
                    actionName = actionName.substring(0, p).trim();
                } else {
                    icon = "";
                }
            }
            buttonPanel.add(new Button(actionName, icon, e -> {
                        close();
                        action.accept(o);
                    })
            );
        }
        buttonPanel.add(exit);
    }

    /**
     * Edit an object.
     *
     * @param object Object to be edited. If null is passed, no action is taken.
     */
    public void editObject(T object) {
        editObject(object, null);
    }

    /**
     * Edit an object.
     *
     * @param object Object to be edited. If null is passed, no action is taken.
     * @param parent Parent view.
     */
    public void editObject(T object, com.storedobject.vaadin.View parent) {
        editObject(object, parent, parent == null);
    }

    /**
     * Edit an object.
     *
     * @param object Object to be edited. If null is passed, no action is taken.
     * @param parent Parent view.
     * @param doNotLock To specify whether the parent to be locked or not.
     */
    public void editObject(T object, com.storedobject.vaadin.View parent, boolean doNotLock) {
        if(object == null) {
            return;
        }
        interruptEditing(object);
        execute(parent, doNotLock);
        closeOnSave = true;
        if(!doEditInt(false, null)) {
            if(doSaveInt()) {
                close();
            } else {
                abort();
            }
        }
    }

    /**
     * Add a new object.
     */
    public void addObject() {
        addObject(null);
    }

    /**
     * Add a new object.
     *
     * @param parent Parent view.
     */
    public void addObject(com.storedobject.vaadin.View parent) {
        addObject(parent, parent == null);
    }

    /**
     * Add a new object.
     *
     * @param parent Parent view.
     * @param doNotLock To specify whether the parent to be locked or not.
     */
    public void addObject(com.storedobject.vaadin.View parent, boolean doNotLock) {
        interruptEditing(null);
        execute(parent, doNotLock);
        closeOnSave = true;
        doAdd();
    }

    /**
     * Delete the current object.
     */
    public void deleteObject() {
        if(getObject() != null) {
            doDelete();
        }
    }

    /**
     * Delete an object.
     *
     * @param object Object to delete.
     */
    public void deleteObject(T object) {
        setObject(object, true);
        deleteObject();
    }

    @Override
    public final void setObject(T object) {
        if(object != null) {
            interruptEditing(object);
        }
    }

    /**
     * Set the parent object. This is used when this editor is used for editing links.
     *
     * @param parentObject Parent object to set.
     * @param parentLinkType Link type of the object.
     */
    public void setParentObject(StoredObject parentObject, int parentLinkType) {
        this.parentObject = parentObject;
        this.parentLinkType = parentLinkType;
    }

    /**
     * Get the parent object previously set via {@link #setParentObject(StoredObject, int)}.
     *
     * @return Parent object.
     */
    public final StoredObject getParentObject() {
        return parentObject;
    }

    /**
     * Get the link type of the parent object previously set via {@link #setParentObject(StoredObject, int)}.
     *
     * @return Link type.
     */
    public final int getParentLinkType() {
        return parentLinkType;
    }

    /**
     * Set a "saver" that will be used for saving the object.
     *
     * @param saver Saver to set.
     */
    public void setSaver(Function<ObjectEditor<T>, Boolean> saver) {
        this.saver = saver;
    }

    /**
     * Set a "deleter" that will be used for deleting the object.
     *
     * @param deleter Deleter to set.
     */
    public void setDeleter(Function<ObjectEditor<T>, Boolean> deleter) {
        this.deleter = deleter;
    }

    /**
     * Check whether the editor is in "view only" mode. The editor is in "view only" mode if the buttons are not
     * available for editing (may be hidden or disabled too).
     *
     * @return True/false.
     */
    public final boolean isViewOnly() {
        return !isEditing() || edit == null || !edit.isVisible() || !edit.isEnabled() || edit.getParent().isEmpty();
    }

    /**
     * For internal use only. If set, saving will be skipped! (It is internally used to switch on/off link
     * editing depending on whether this is a child of another editor or not).
     *
     * @param on True or false.
     */
    public final void setDoNotSave(boolean on) {
        this.doNotSave = on;
    }

    /**
     * Allow/disallow "Do not save" option. (See {@link #setDoNotSave(boolean)}.
     *
     * @param allowDoNotSave True if link editing needs to be allowed
     */
    public void setAllowDoNotSave(boolean allowDoNotSave) {
        this.allowDoNotSave = allowDoNotSave;
    }

    /**
     * Check whether "Do not save" option is allowed or not.
     *
     * @return True/false.
     */
    public boolean isDoNotSaveAllowed() {
        return allowDoNotSave;
    }

    /**
     * Typically, "grids" for "link fields" are created automatically. However, this method can be overridden to
     * create customized link grids.
     *
     * @param fieldName Field for which grid to be created.
     * @param field Field.
     * @return Grid for the link field. A null may be returned so that default grid will be automatically created.
     */
    protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
        return null;
    }

    @Override
    protected void attachField(String fieldName, HasValue<?, ?> field) {
        if(field instanceof NoDisplayField && !((NoDisplayField) field).canDisplay()) {
            return;
        }
        if(field instanceof ObjectLinkField<?> f) {
            f.setGrid(createLinkFieldGrid(f.getLink().getName() + ".l", f));
            customizeLinkField(f);
            attachLinkField(f);
            return;
        }
        super.attachField(fieldName, field);
    }

    @Override
    protected final void fieldAttached(String fieldName, HasValue<?, ?> field) {
        super.fieldAttached(fieldName, field);
        if(field instanceof NoDisplayField && !((NoDisplayField) field).canDisplay()) {
            return;
        }
        if(field instanceof ObjectLinkField) {
            linkFields.add((ObjectLinkField<?>) field);
            return;
        }
        if(field instanceof AttachmentField) {
            StreamAttachment attachment = ((AttachmentField) field).getAttachment();
            if(streamAttachmentData == null) {
                streamAttachmentData = attachment.getAttachmentData();
            }
            if(attachment.isRequired()) {
                setRequired(fieldName);
            }
        }
        if(fieldPositions != null) {
            fieldPositions.add(fieldName);
        }
    }

    /**
     * Customize the link field if needed.
     *
     * @param field Field to customize.
     */
    protected void customizeLinkField(ObjectLinkField<?> field) {
    }

    /**
     * Attach the link field. By default it will be attached as a tab at the bottom of the view.
     *
     * @param field Field to attach.
     */
    protected void attachLinkField(ObjectLinkField<?> field) {
        if(linkTabs == null) {
            linkTabs = new ObjectLinkField.Tabs();
            add(linkTabs);
        }
        linkTabs.addField(field);
    }

    /**
     * Get the link field.
     *
     * @param fieldName Name of the link field.
     * @return Field if found, otherwise null.
     */
    public ObjectLinkField<?> getLinkField(String fieldName) {
        if(!fieldName.endsWith(".l")) {
            fieldName = fieldName + ".l";
        }
        String fn = fieldName;
        return linkFields.stream().filter(lf -> lf.getFieldName().equals(fn)).findAny().orElse(null);
    }

    public List<ObjectLinkField<?>> linkFields() {
        return Collections.unmodifiableList(linkFields);
    }

    /**
     * Get the anchor field.
     *
     * @param fieldName Name of the anchor field.
     * @return Field if found, otherwise null.
     */
    public HasValue<?, ?> getAnchorField(String fieldName) {
        getComponent();
        if(anchorForm == null) {
            return null;
        }
        anchorForm.getComponent();
        return anchorForm.getField(fieldName);
    }

    /**
     * Get the attachment field.
     *
     * @param fieldName Name of the attachment field.
     * @return Field if found, otherwise null.
     */
    public AttachmentField getAttachmentField(String fieldName) {
        if(!fieldName.endsWith(".a")) {
            fieldName = fieldName + ".a";
        }
        return (AttachmentField) getField(fieldName);
    }

    /**
     * Get the contact field.
     *
     * @param fieldName Name of the contact field.
     * @return Field if found, otherwise null.
     */
    public HasValue<?, String> getContactField(String fieldName) {
        if(!fieldName.endsWith(".c")) {
            fieldName = fieldName + ".c";
        }
        //noinspection unchecked
        return (HasValue<?, String>) getField(fieldName);
    }

    /**
     * Get the extra info field if exists.
     *
     * @return Field if found, otherwise null.
     */
    public HasValue<?, StoredObjectLink<?>> getExtraInfoField() {
        //noinspection unchecked
        return extraInfo == null ? null :
                (HasValue<?, StoredObjectLink<?>>) getField(ExtraInfo.getName() + ".e");
    }

    /**
     * For internal use only. Set the contact data.
     *
     * @param contactData Contact data to be set.
     */
    public void setContactData(ContactData contactData) {
        if(this.contactData == null) {
            this.contactData = contactData;
        }
    }

    /**
     * For internal use only. Set extra info.
     *
     * @param extraInfo Extra info to be set.
     */
    public void setExtraInfo(ExtraInfo<?> extraInfo) {
        if(this.extraInfo == null) {
            this.extraInfo = extraInfo;
        }
    }

    @Override
    public final void trackValueChange(HasValue<?, ?> field) {
        if(field instanceof ObjectLinkField) {
            ((ObjectLinkField<?>) field).trackChanges(this::fireChange);
        } else {
            super.trackValueChange(field);
        }
    }

    @SuppressWarnings("unchecked")
    private <L extends StoredObject> void fireChange(ObjectLinkField<L> field, boolean fromClient) {
        ObjectLinkField<L> f = (ObjectLinkField<L>) linkFields.stream().filter(lf -> lf == field).findAny().orElse(null);
        if(f != null) {
            valueChanged(new LinkFieldChanges<>(f, fromClient));
        }
    }

    @Override
    public void handleAlert(StoredObject so) {
        if(so != null && getObjectClass().isAssignableFrom(so.getClass())) {
            //noinspection unchecked
            viewObject((T) so);
        }
    }

    @Override
    public String getAlertIcon() {
        return "vaadin:eye";
    }

    private static class LinkFieldChanges<L extends StoredObject> extends ChangedValues {

        private final ObjectLinkField<L> field;
        private final boolean fromClient;

        public LinkFieldChanges(ObjectLinkField<L> objectLinkField, boolean fromClient) {
            super(null);
            this.field = objectLinkField;
            this.fromClient = fromClient;
        }

        @Override
        public boolean isChanged(HasValue<?, ?> field) {
            return this.field == field;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> ChangedValue<V> getChanged(HasValue<?, V> field) {
            return this.field == field ? (ChangedValue<V>) new LinkChangedValue() : null;
        }

        @Override
        public HasValue<?, ?> getChanged() {
            return field;
        }

        @Override
        public StoredObjectLink<L> getOldValue() {
            return field.getOldValue();
        }

        @Override
        public StoredObjectLink<L> getValue() {
            return field.getValue();
        }

        @Override
        public boolean isFromClient() {
            return fromClient;
        }

        private class LinkChangedValue extends ChangedValue<StoredObjectLink<L>> {

            public LinkChangedValue() {
                super(null);
            }

            @Override
            public StoredObjectLink<L> getValue() {
                return LinkFieldChanges.this.getValue();
            }

            @Override
            public StoredObjectLink<L> getOldValue() {
                return LinkFieldChanges.this.getOldValue();
            }

            @Override
            public boolean isFromClient() {
                return fromClient;
            }

            @Override
            public HasValue<?, StoredObjectLink<L>> getChanged() {
                return field;
            }
        }
    }

    /**
     * Get the anchor filter associated with this editor.
     * <p>Note: Anchor filter will not be available if the anchor form is not yet executed.</p>
     *
     * @return Anchor filter if any, otherwise null.
     */
    public String getAnchorFilter() {
        return anchorForm == null ? null : anchorForm.filter();
    }

    /**
     * Set the anchor fields in read-only mode.
     *
     * @param anchorFields Anchor fields.
     */
    public void setAnchorFieldReadOnly(String... anchorFields) {
        if(anchorForm != null) {
            anchorForm.setFieldReadOnly(anchorFields);
        }
    }

    private class AnchorForm extends DataForm {

        private Runnable action;

        public AnchorForm() {
            super(ObjectEditor.this.getCaption());
            initUI();
        }

        @Override
        protected void buildFields() {
            StringList fields = ((SOFieldCreator<T>) getFieldCreator()).getAnchors();
            if(fields == null || fields.size() == 0) {
                return;
            }
            fields.forEach(fieldName -> {
                HasValue<?, ?> field = ObjectEditor.this.constructField(fieldName);
                if(field != null) {
                    addField(fieldName, field);
                    setRequired(field);
                }
            });
        }

        private int getFieldCount() {
            return this.getData().getFieldCount();
        }

        private String filter() {
            StringBuilder s = new StringBuilder();
            this.streamFieldNamesCreated().forEach(fieldName -> {
                HasValue<?, ?> f = getField(fieldName);
                if(f != null) {
                    Object v = f.getValue();
                    ObjectEditor.this.setFixedValue(fieldName, v);
                    if(s.length() > 0) {
                        s.append(" AND ");
                    }
                    v = f.getValue();
                    s.append(fieldName).append("=");
                    if(v instanceof String) {
                        s.append('\'');
                    }
                    s.append(v);
                    if(v instanceof String) {
                        s.append('\'');
                    }
                }
            });
            return s.toString();
        }

        @Override
        protected boolean process() {
            String filter = filter();
            try {
                anchorsSet();
                if(grid instanceof ObjectBrowser) {
                    ((ObjectBrowser<T>) grid).anchorsSet();
                }
            } catch(Exception e) {
                warning(e);
                return false;
            }
            close();
            ObjectSearcher<T> searcher = getSearcher();
            if(searcher != null && searcher != grid) {
                searcher.getFilter().setCondition(filter);
            }
            anchorAction = false;
            if(action != null) {
                Runnable r = action;
                action = null;
                r.run();
            }
            return true;
        }

        @Override
        protected void cancel() {
            super.cancel();
            anchorAction = true;
            if(closeOnSave) {
                closeOnSave = false;
                ObjectEditor.this.close();
            }
            anchorsCancelled();
            if(grid instanceof ObjectBrowser) {
                ((ObjectBrowser<T>) grid).anchorsCancelled();
            }
        }

        private void run(Runnable action) {
            final AtomicInteger any = new AtomicInteger(0);
            this.streamFieldsCreated().forEach(f -> {
                if(!f.isReadOnly()) {
                    any.incrementAndGet();
                    if(f instanceof com.storedobject.ui.ObjectField) {
                        ObjectInput<?> objectField = ((ObjectField<?>) f).getField();
                        if(objectField instanceof ObjectComboField<?> objectComboField) {
                            if(objectComboField.getObjectCount() == 1) {
                                objectComboField.setFirstValue();
                                any.decrementAndGet();
                            }
                        }
                    }
                }
            });
            this.action = action;
            if(any.get() > 0) {
                execute();
            } else {
                process();
            }
        }
    }

    @Override
    public boolean equals(Object another) {
        return this == another;
    }

    boolean editItem(T item) {
        if(item == null) {
            return false;
        }
        grid.deselectAll();
        if(!saveEdited()) {
            return false;
        }
        setObject(item, false);
        if(!doEditInt(false, grid)) {
            return false;
        }
        grid.getEditor().editItem(item);
        return true;
    }

    boolean saveEdited() {
        Editor<T> e = grid.getEditor();
        if(!e.isOpen()) {
            return true;
        }
        if(doSaveInt()) {
            if(e.isOpen()) {
                e.cancel();
            }
            return true;
        }
        return false;
    }

    /**
     * Set the buffered mode. In buffered mode, instances will be backed up before editing and will be restored if
     * editing is cancelled.
     *
     * @param buffered True/false.
     */
    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    /**
     * Check whether the editor is in buffered mode or not.
     *
     * @return True/false.
     */
    public final boolean isBuffered() {
        return buffered;
    }

    /**
     * Set a instance of a {@link StoredObject} if it is compatible with this editor.
     *
     * @param object Instance of the object to set.
     */
    public void setRawObject(StoredObject object) {
        if(object == null) {
            setObject((Id) null);
            return;
        }
        if(getObjectClass().isAssignableFrom(object.getClass())) {
            //noinspection unchecked
            setObject((T) object);
        }
    }

    /**
     * Accept a change in the link field value.
     *
     * @param linkField Link field that is changed.
     * @param item Item that is changed.
     * @param changeAction Change action (One of the static values defined in the {@link EditorAction}).
     * @param <L> Type of link value.
     * @return True if change is acceptable. If false is returned, value will be ignored.
     */
    public <L extends StoredObject> boolean acceptValueChange(ObjectLinkField<L> linkField, L item, int changeAction) {
        return true;
    }
}