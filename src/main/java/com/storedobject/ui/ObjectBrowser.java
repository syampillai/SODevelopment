package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.report.ObjectList;
import com.storedobject.ui.accounts.JournalVoucherBrowser;
import com.storedobject.ui.inventory.POBrowser;
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

/**
 * A specialized grid-based component designed for browsing, editing, and managing objects of a specified class.
 * This class provides a rich set of features for handling object data such as searching, filtering, editing,
 * and various actions. It is highly customizable and allows integration with custom editors and logic.
 *
 * @param <T> The type of the object that this browser will handle.
 *
 * @author Syam
 */
public class ObjectBrowser<T extends StoredObject> extends ObjectGrid<T>
        implements EditableDataGrid<T>, ObjectEditorListener {

    private LoadFilterButtons<T> loadFilterButtons;
    /**
     * Represents a layout container for managing and organizing button components within the ObjectBrowser UI.
     * This variable is a final instance ensuring consistency throughout the lifecycle of the ObjectBrowser.
     * It provides a structured mechanism to handle button-related components efficiently.
     */
    protected final ButtonLayout buttonPanel = new ButtonLayout();
    /**
     * Represents a button used to trigger a print action within the ObjectBrowser.
     * This button is used to facilitate printing of data or output managed by the ObjectBrowser component.
     */
    protected PrintButton print;
    /**
     * The "Add" button associated with this instance of the {@code ObjectBrowser}.
     * This button is typically used to initiate an "add" operation, allowing users
     * to add a new object or item to the data displayed or managed by the browser.
     */
    protected Button add, /**
     * Represents the "Edit" action in the object browser. This button or functionality is used
     * to facilitate editing of an item or object within the browser grid or context.
     */
    edit, /**
     * The delete action represented by this variable provides the functionality
     * to delete a selected object or record within the ObjectBrowser.
     * This action is typically part of the set of actions available to manage
     * the objects displayed in the browser.
     */
    delete, /**
     * Represents a variable used for search functionality in the context of an object browser.
     * It could be used for defining, initiating, or invoking search-related operations within the
     * browsing or filtering mechanisms provided by the {@code ObjectBrowser} class.
     */
    search, /**
     * Represents a filter used to determine or refine a subset of data based on specific conditions.
     * This variable is typically used in operations where data needs to be included or excluded
     * according to defined criteria.
     */
    filter, /**
     * Represents a variable or field that is responsible for loading or initiating
     * specific operations or data within the context of the ObjectBrowser class.
     * Its behavior and purpose may vary depending on how it is used within the methods
     * and logic of the class.
     */
    load, /**
     * A variable representing a view component associated with this object browser.
     * This component is used to present a specific view.
     */
    view, /**
     * This variable represents a report object that contains data or information intended for processing, storage, or display.
     * It is typically used to compile, summarize, and organize information for a specific purpose or analysis within an application.
     */
    report, /**
     * Represents the "Excel" feature within the ObjectBrowser. This variable is typically
     * associated with functionalities related to exporting data or interacting with Excel files.
     * Its utilization depends on the specific actions and logic implemented in the ObjectBrowser
     * class.
     */
    excel, /**
     * Represents the audit information for a specific entity or transaction.
     * This variable is typically used to track or log detailed information
     * such as changes, timestamps, or user actions for accountability and
     * historical reference.
     */
    audit, /**
     * Represents a flag or condition indicating whether a process, program, or operation
     * should terminate or conclude. This variable is typically used to control exit
     * behavior in a loop or a program's execution flow.
     */
    exit, /**
     * A variable representing an action, state, or mechanism for storing or preserving data,
     * objects, or some form of information for future use.
     * The specific functionality and purpose of this variable depend on the context
     * within which it is defined and used in the code.
     */
    save, /**
     * A variable that represents the action or state of canceling an operation or process.
     * It is typically used to abort or terminate tasks in progress.
     */
    cancel, /**
     * Represents the ledger used for recording financial transactions
     * or other data entries in chronological order.
     * This variable is typically used to store and manage an
     * organized collection of records.
     */
    ledger;
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
    private Runnable exitAction;

    /**
     * Creates an instance of the ObjectBrowser with a specified object class.
     *
     * @param objectClass The class of the objects that this ObjectBrowser will manage.
     */
    public ObjectBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    /**
     * Creates an ObjectBrowser instance with the specified object class and a caption.
     * This constructor assumes all actions are allowed.
     *
     * @param objectClass The class of the objects to be browsed.
     * @param caption The caption to display for this browser.
     */
    public ObjectBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    /**
     * Constructs an ObjectBrowser instance for the specified object class and columns to browse.
     *
     * @param objectClass The class of the objects to be displayed in the browser.
     * @param browseColumns An iterable containing the column names to be included in the browsing view.
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    /**
     * Constructs an ObjectBrowser instance with the specified object class, browse columns, and filter columns,
     * applying a default action set.
     *
     * @param objectClass The class of the objects to be browsed.
     * @param browseColumns An iterable collection of column names to be displayed in the browser.
     * @param filterColumns An iterable collection of column names to be used as filters in the browser.
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    /**
     * Constructs an ObjectBrowser instance with the given object class, browse columns, and search builder.
     *
     * @param objectClass The class type of the objects to be displayed and managed by this ObjectBrowser.
     * @param browseColumns An iterable list of column names to be included for browsing in this ObjectBrowser.
     * @param searchBuilder A SearchBuilder implementation that will be used for creating object search functionality.
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, SearchBuilder<T> searchBuilder) {
        this(objectClass, browseColumns, ALL, searchBuilder);
    }

    /**
     * Constructs an ObjectBrowser instance with the specified object type and defined actions.
     * Delegates to another constructor with a null caption parameter.
     *
     * @param objectClass The class type of the objects to be browsed.
     * @param actions An integer representing the set of actions allowed on the objects.
     */
    public ObjectBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, (String) null);
    }

    /**
     * Constructs an ObjectBrowser with the specified object class, actions, and search builder.
     *
     * @param objectClass The class type of the object to be managed within the browser.
     * @param actions The action flags defining the allowed operations in the browser.
     * @param searchBuilder The search builder to use for creating custom search functionalities.
     */
    public ObjectBrowser(Class<T> objectClass, int actions, SearchBuilder<T> searchBuilder) {
        this(objectClass, null, actions, searchBuilder, null);
    }

    /**
     * Constructs an ObjectBrowser for a specific object type with the specified actions and a custom caption.
     *
     * @param objectClass The class of the objects to be browsed.
     * @param actions Bitmask representing the actions allowed (e.g., add, edit, delete, view, etc.).
     * @param caption The title or label to be displayed for the ObjectBrowser.
     */
    public ObjectBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null, actions, caption);
    }

    /**
     * Constructs an ObjectBrowser for browsing objects of the specified class.
     *
     * @param objectClass   the class of objects to be browsed
     * @param browseColumns the collection of column names to be used for browsing
     * @param actions       the flags representing the actions to be performed on the browsed objects
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, (Iterable<String>) null, null);
    }

    /**
     * Constructs an ObjectBrowser instance to facilitate browsing, filtering, and performing actions on a set of objects.
     *
     * @param objectClass the class type of the objects to be browsed
     * @param browseColumns an iterable set of column names to be displayed while browsing
     * @param actions an integer representing the actions that can be performed on the objects
     * @param filterColumns an iterable set of column names that can be used for filtering
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    /**
     * Constructs an instance of {@code ObjectBrowser} with the specified parameters.
     *
     * @param objectClass The class of objects to be displayed in the browser.
     * @param browseColumns A collection of column names to be displayed in the browser.
     * @param actions An integer representing the actions allowed on the objects (e.g., add, edit, delete).
     * @param searchBuilder A {@link SearchBuilder} instance that defines the logic for creating a search builder.
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, SearchBuilder<T> searchBuilder) {
        this(objectClass, browseColumns, actions, searchBuilder, null);
    }

    /**
     * Constructs an ObjectBrowser instance with the specified parameters.
     *
     * @param objectClass   the class type of the object to be browsed
     * @param browseColumns an iterable collection of column names to be displayed in the browser
     * @param actions       an integer representing the actions available in the browser
     * @param caption       a string representing the caption or title for the browser
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, (Iterable<String>) null, caption);
    }

    /**
     * Constructs an ObjectBrowser instance with the provided parameters.
     *
     * @param objectClass  The class type of the objects being browsed.
     * @param browseColumns  The columns to be displayed for browsing purposes.
     * @param actions  The actions allowed for objects being browsed.
     * @param filterColumns  The columns to be used for filtering purposes.
     * @param caption  The caption or title of the ObjectBrowser.
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                         Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    /**
     * Constructs an ObjectBrowser instance with specified parameters.
     *
     * @param objectClass   the class type of the objects to browse
     * @param browseColumns the columns to be displayed for browsing
     * @param actions       the action flags to be used for object operations
     * @param searchBuilder the search builder responsible for creating search queries
     * @param caption       the caption or title of the browser
     */
    public ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                         SearchBuilder<T> searchBuilder, String caption) {
        this(objectClass, browseColumns, actions, searchBuilder, caption, null);
    }

    /**
     * Creates an ObjectBrowser for managing objects of a specific class with defined browsing columns, actions,
     * filterable columns, caption, and allowed actions.
     *
     * @param objectClass The class type of the objects to be managed and browsed.
     * @param browseColumns An iterable collection of column names to be displayed in the browser.
     * @param actions Integer representing the actions available for the browser.
     * @param filterColumns An iterable collection of column names used for filtering in the browser.
     * @param caption The caption or title for the browser interface.
     * @param allowedActions A comma-separated string representing the actions that are allowed.
     */
    protected ObjectBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                            Iterable<String> filterColumns, String caption, String allowedActions) {
        this(objectClass, browseColumns, actions, filterColumns, null, caption, allowedActions);
    }

    /**
     * Constructs an instance of ObjectBrowser with specified parameters.
     *
     * @param objectClass    the class type of objects to be browsed
     * @param browseColumns  an iterable containing column names to be displayed during object browsing
     * @param actions        the integer representing action flags associated with the browser
     * @param searchBuilder  the SearchBuilder instance used for implementing search functionality
     * @param caption        the string representing the title or caption of the object browser
     * @param allowedActions a string defining the actions allowed for the object browser
     */
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

    /**
     * Constructs an ObjectBrowser instance for the specified class name.
     *
     * @param className the name of the class to be used for creating the ObjectBrowser instance
     * @throws Exception if there is an error resolving the specified class or during initialization
     */
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

    /**
     * Creates an {@code ObjectBrowser} instance for the specified class type.
     *
     * @param <O> the type of the objects being handled, extending {@code StoredObject}
     * @param objectClass the {@code Class} object of the specific type to be managed by the browser
     * @return an {@code ObjectBrowser} instance for the specified class type
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass) {
        return create(objectClass, ALL);
    }

    /**
     * Creates and returns an instance of ObjectBrowser for the specified object class and actions.
     *
     * @param objectClass the class type of the objects to be managed by the ObjectBrowser
     * @param actions the integer value representing available actions for the ObjectBrowser
     * @return an instance of ObjectBrowser configured for the specified object class and actions
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions) {
        return create(objectClass, actions, null);
    }

    /**
     * Creates an instance of ObjectBrowser for the given object class, browse columns, and actions.
     *
     * @param <O>           The type of the objects stored and managed by the ObjectBrowser.
     * @param objectClass   The class of the object to be browsed.
     * @param browseColumns An iterable of strings representing the columns to display in the browser.
     * @param actions       The action flags that determine the operations allowed in the browser.
     * @return An instance of ObjectBrowser configured with the specified class, columns, and actions.
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions) {
        return create(objectClass, browseColumns, actions, (String) null);
    }

    /**
     * Creates an instance of ObjectBrowser for the specified object class with the given configuration.
     *
     * @param <O>            The type of the objects that the ObjectBrowser will handle. Must extend StoredObject.
     * @param objectClass    The class type of the objects for the browser.
     * @param browseColumns  An iterable containing the column names that should be displayed in the browser.
     * @param actions        Integer flag indicating the actions that can be performed with the browser.
     * @param filterColumns  An iterable containing the column names that should be used for filtering.
     * @return               A new ObjectBrowser instance configured with the given parameters.
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions, Iterable<String> filterColumns) {
        return create(objectClass, browseColumns, actions, filterColumns, null);
    }

    /**
     * Creates a new instance of ObjectBrowser for the specified object class with the given actions and title.
     *
     * @param <O>         the type of objects that the ObjectBrowser will handle, which extends StoredObject
     * @param objectClass the class of the objects that the ObjectBrowser will manage
     * @param actions     the actions to be associated with the ObjectBrowser
     * @param title       the title of the ObjectBrowser
     * @return an instance of ObjectBrowser configured for the specified object class, actions, and title
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, int actions, String title) {
        return create(objectClass, null, actions, title);
    }

    /**
     * Creates an instance of ObjectBrowser for the specified object class.
     *
     * @param objectClass   the class of objects to be browsed
     * @param browseColumns an iterable collection of column names to be displayed in the browser
     * @param actions       an integer representing the actions available in the browser
     * @param title         the title of the browser
     * @return an instance of ObjectBrowser configured with the specified parameters
     */
    public static <O extends StoredObject> ObjectBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns,
                                                                   int actions, String title) {
        return create(objectClass, browseColumns, actions, null, title);
    }

    /**
     * Creates an instance of {@code ObjectBrowser<O>} based on the provided parameters and available constructors for the corresponding logic class.
     * This method attempts to dynamically create the browser by invoking suitable constructors from the associated logic class.
     *
     * @param objectClass the class type of the stored object for which the browser is being created
     * @param browseColumns an iterable of column names that should be used for browsing
     * @param actions the action flags specifying the actions available in the browser
     * @param filterColumns an iterable of column names that can be used for filtering purposes
     * @param title the title of the browser window
     * @return an instance of {@code ObjectBrowser<O>} configured based on the input parameters or a default internal browser if no suitable constructor is found
     */
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

    /**
     * Sets the provided Logic instance if it has not been set already. If an editor is available,
     * it will also set the provided Logic instance to the editor.
     *
     * @param logic the Logic instance to be set. It is applied only if the current logic is null.
     */
    @Override
    public final void setLogic(Logic logic) {
        if(this.logic == null) {
            this.logic = logic;
            if(editor != null) {
                editor.setLogic(logic);
            }
        }
    }

    /**
     * Retrieves the logic instance associated with this object.
     *
     * @return the logic instance of type {@code Logic}
     */
    @Override
    public final Logic getLogic() {
        return logic;
    }

    /**
     * Sets the caption for an object.
     *
     * @param caption The text to be used as the caption.
     */
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

    /**
     * Creates and returns the header component of the user interface.
     * This method constructs the header by combining the provided button panel
     * and filter buttons into a single component.
     *
     * @return a {@code Component} representing the constructed header.
     */
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

    /**
     * Filters the provided set of actions and returns the result.
     *
     * @param actions the input set of actions to be filtered
     * @return the filtered set of actions
     */
    protected int filterActions(int actions) {
        return actions;
    }

    /**
     * A protected method designed to create and initialize additional buttons
     * that might be required for enhancing the user interface functionality.
     * This method can be overridden by subclasses to provide specific
     * implementations for creating extra buttons as needed.
     */
    protected void createExtraButtons() {
    }

    /**
     * Configures and adds additional buttons to the user interface.
     * This method is intended to be overridden by subclasses to provide
     * specific behavior or additional UI elements.
     * Subclasses can use this method to enhance the default layout
     * with custom buttons based on their requirements. The base
     * implementation does not add any buttons.
     */
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

    /**
     * Determines if an item can be added based on the current logic or conditions.
     *
     * @return true if the item can be added, false otherwise
     */
    protected boolean canAdd() {
        return true;
    }

    /**
     * Closes the current instance and performs cleanup operations if necessary.
     * If the editor associated with this instance is open, it will cancel any
     * ongoing row edits before invoking the superclass's close method.
     * This ensures that any incomplete or pending edits are discarded
     * and do not affect the consistency of the program's state.
     */
    @Override
    public void close() {
        if(getEditor().isOpen()) {
            cancelRowEdit();
        }
        super.close();
    }

    /**
     * Determines the currently selected item. If no item is selected, it attempts to
     * retrieve the item being edited. If neither exists, it handles specific cases:
     *  - If the list is empty, it logs a warning and returns null.
     *  - If the list contains one item, it selects that item and returns it.
     * If none of the above conditions are met, it logs a warning and returns null.
     *
     * @return the currently selected item, the item being edited if no item is selected,
     *         the only item present if the list contains one item, or null if no item
     *         is selected and the list is empty or in an ambiguous state.
     */
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

    /**
     * Handles the event of a row being double-clicked.
     * The behavior varies depending on the state of the application, such as
     * whether a search dialog is visible and enabled, or whether editing is allowed
     * for the given object.
     *
     * @param object The object associated with the double-clicked row.
     */
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

    /**
     * Handles the clicked event for a given component. Depending on the component clicked,
     * it triggers specific actions such as add, edit, delete, view, audit, ledger, or search,
     * and provides appropriate feedback or behavior based on the current state and selection.
     *
     * @param c the component that was clicked. This determines the action to be executed,
     *          such as exiting the application, adding a new item, generating a report,
     *          performing CRUD operations on selected objects, or handling search functionality.
     */
    @Override
    public void clicked(Component c) {
        clearAlerts();
        if(c == exit) {
            Application a = Application.get();
            close();
            if(exitAction != null) {
                a.access(() -> exitAction.run());
            }
            return;
        }
        if(c == add) {
            doAdd();
            return;
        }
        if(c == report) {
            doReport();
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

    /**
     * Loads data based on the specified parameters.
     *
     * @param linkType the type of link to be used for loading the data
     * @param master the master object used as a reference for loading
     * @param condition the condition to filter the data during loading
     * @param orderedBy the order by which the data should be sorted
     * @param any a flag indicating whether to include any matching data from subclasses
     */
    @Override
    public void load(int linkType, StoredObject master, String condition, String orderedBy, boolean any) {
        getDataProvider().load(linkType, master, condition, orderedBy, any);
    }

    /**
     * Loads data based on the specified condition, ordering, and flag.
     *
     * @param condition the condition to filter the data
     * @param orderedBy the field by which the data should be ordered
     * @param any a flag indicating whether to include any matching data from subclasses
     */
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
     * This method is invoked when anchor values are set via the anchor form, and if any exception is
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

    /**
     * Executes the add operation by checking whether the operation is allowed
     * using the canAdd method. If canAdd returns true, it retrieves the object editor
     * and performs the addObject operation with the current view.
     * This method is typically used to manage the addition of objects in the specified context.
     * The addObject operation is delegated to the object editor,
     * and the view serves as the object representation to be added.
     */
    public void doAdd() {
        if(canAdd()) {
            getObjectEditor().addObject(getView());
        }
    }

    /**
     * Edits the specified object if it is editable.
     *
     * @param object the object to be edited, must not be null and must be editable
     */
    public void doEdit(T object) {
        if(object != null && canEdit(object)) {
            getObjectEditor().editObject(object, getView(), true);
        }
    }

    /**
     * Deletes the specified object if it is not null. After deletion, checks whether the object has been successfully removed
     * and displays a confirmation message if deleted.
     *
     * @param object the object to be deleted; if null, no action is taken
     */
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

    /**
     * Executes a reporting operation by creating and managing an instance of ObjectList.
     * This method initializes the ObjectList with the necessary parameters such as application context,
     * object class, column names, and filter conditions. It then executes the list under these configured settings.
     * This method suppresses resource warnings for the ObjectList initialization.
     */
    public void doReport() {
        @SuppressWarnings("resource") ObjectList<T> list = new ObjectList<>(getApplication(), getObjectClass(),
                isAllowAny(), getRenderedColumnNames().toList());
        list.setExtraCondition(getFilterCondition());
        list.execute();
    }

    /**
     * Posts the given object to the ledger if it meets the required conditions.
     * The method checks whether the object is an instance of a financial entry,
     * whether it has already been posted to the ledger, and whether the current
     * user has the necessary permissions to view or post the ledger.
     *
     * @param object the object to be processed and potentially posted to the ledger.
     *               Must be an instance of a financial entry to proceed.
     */
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

    /**
     * Handles the view operation for the given object. If the provided object
     * is not null, it uses the object editor to display the object in
     * the specified view.
     *
     * @param object the object to be viewed; must not be null to trigger the view operation
     */
    public void doView(T object) {
        if(object != null) {
            getObjectEditor().viewObject(object, getView(), true);
        }
    }

    /**
     * Returns the primary view component to be used for rendering.
     * If a custom layout is defined, it will return the layout;
     * otherwise, it falls back to the default component from the superclass.
     *
     * @return the view component, either the custom layout or the default superclass component
     */
    @Override
    public Component getViewComponent() {
        return layout == null ? super.getViewComponent() : layout;
    }

    /**
     * Configures and sets up a split view layout for the current component.
     * The method performs the following actions:
     * - Checks if the view is already initialized using the `getView(false)` method.
     *   If a view is already present, the method will return immediately without further processing.
     * - Assigns a new layout to the component by invoking the `splitLayout` method with
     *   the current object as an argument.
     * - Registers an item selected listener to handle selection events within a grid. When
     *   an item is selected, the `itemSelected()` method is triggered.
     * - If a previous view exists, it removes the view from the `buttonPanel` and clears the `view` reference.
     */
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

    /**
     * Retrieves the row editor for the current object type.
     * If the editor has not been constructed yet, it initializes the editor.
     * Ensures that the editor is configured in row mode, updating field labels and layouts as needed.
     *
     * @return the ObjectEditor instance for the current object type
     */
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

    /**
     * Sets the ObjectEditor for managing the editing process of the object.
     * If an existing editor is assigned and is currently executing, it will be aborted,
     * and its associated component will be removed from the layout if applicable.
     * The new editor will then be initialized and constructed.
     *
     * @param editor the ObjectEditor instance to be set. Can be null, which will
     *               remove the current editor and its associated behavior.
     */
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

    /**
     * Retrieves the ObjectEditor instance. If the instance is null, it initializes the editor.
     * Adjusts the editor's field positions and configuration when in row editing mode.
     *
     * @return the ObjectEditor instance of type T, appropriately configured based on the current state.
     */
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

    /**
     * Creates and returns an instance of ObjectEditor for managing
     * the editing of objects of type T. This method can be overridden
     * by subclasses to provide specific editor implementations.
     *
     * @return an instance of ObjectEditor for the generic type T, or null
     *         if no implementation is provided.
     */
    protected ObjectEditor<T> createObjectEditor() {
        return null;
    }

    /**
     * Constructs and returns an instance of ObjectEditor for managing and editing objects
     * of type T. This method is intended to be overridden to provide specific
     * implementations of the ObjectEditor as needed.
     *
     * @return an ObjectEditor instance for managing objects of type T, or null if no
     *         editor is constructed.
     */
    protected ObjectEditor<T> constructObjectEditor() {
        return null;
    }

    /**
     * Determines if a column with the specified name is editable.
     *
     * @param columnName the name of the column to check for editability
     * @return true if the column is editable, otherwise false
     */
    @Override
    public boolean isColumnEditable(String columnName) {
        return true;
    }

    /**
     * Streams all editable fields in the current context.
     *
     * @return a stream of fields that implement the {@code HasValue<?, ?>} interface,
     *         representing the editable fields available.
     */
    @Override
    public Stream<HasValue<?, ?>> streamEditableFields() {
        return fields.values().stream();
    }

    /**
     * Determines if the object is in search mode.
     * Search mode is active when the search property is not null.
     *
     * @return true if the search mode is active, false otherwise
     */
    @Override
    public boolean isSearchMode() {
        return search != null;
    }

    /**
     * Retrieves the search builder associated with the load filter buttons.
     *
     * @return an instance of ObjectSearchBuilder<T> if loadFilterButtons is not null, otherwise returns null
     */
    @Override
    public ObjectSearchBuilder<T> getSearchBuilder() {
        return loadFilterButtons == null ? null : loadFilterButtons.getSearchBuilder();
    }

    /**
     * Sets the read-only state for the current object.
     * When set to true, row editing will be canceled if it is in progress.
     *
     * @param readOnly a boolean value specifying the read-only state.
     *                 If true, the object is marked as read-only and any row edit is canceled.
     *                 If false, the object allows modifications.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            cancelRowEdit();
        }
    }

    /**
     * Checks if the current object is in a read-only state.
     *
     * @return true if the object is read-only; false otherwise.
     */
    public final boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Creates and returns a new editor instance. The editor is initialized to be buffered,
     * allowing changes to be committed explicitly rather than immediately upon modification.
     *
     * @return the newly created and buffered editor instance
     */
    @Override
    protected final Editor<T> createEditor() {
        Editor<T> editor = super.createEditor();
        editor.setBuffered(true);
        return editor;
    }

    /**
     * Edits the specified row in the data set.
     * This method checks if the provided item can be edited
     * before proceeding with the edit operation.
     *
     * @param item the item representing the row to be edited
     */
    public void editRow(T item) {
        if(canEdit(item)) {
            editRowInt(item);
        }
    }

    /**
     * Determines if the specified row can be edited based on the given item.
     *
     * @param item the object representing the row to check for edit capability
     * @return true if the row can be edited, false otherwise
     */
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

    /**
     * Cancels the editing process for the currently edited row in the table.
     * If an editor is currently active, this method will invoke its cancel operation,
     * ensure that the editing state for the table row is reset, and hide the save
     * and cancel buttons associated with the operation.
     * The method performs the following steps:
     * 1. Checks if the editor is non-null and retrieves the editor instance.
     * 2. If the editor is open, invokes the cancel operation on the editor.
     * 3. Resets the editing state of the row by calling the row editor's cancel operation.
     * 4. Clears the `editingItem` reference.
     * 5. Hides the associated save and cancel buttons.
     */
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

    /**
     * Saves the currently edited row if an editor is active and there is an item being edited.
     * This method checks the editor and the item being edited for validity, saves the edited data
     * using the row editor's save method, and then finalizes the editing process.
     * It resets the editing state and updates the visibility of the save
     * and cancel controls associated with the editor.
     */
    public void saveEditedRow() {
        if(editor != null && editingItem != null && getRowEditor().saveEdited()) {
            T item = editingItem;
            editingItem = null;
            select(item);
            save.setVisible(false);
            cancel.setVisible(false);
        }
    }

    /**
     * Retrieves the item currently being edited.
     *
     * @return the item of type T that is currently being edited
     */
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

    /**
     * Determines whether the specified action is allowed.
     *
     * @param action the action to be checked for permission
     * @return true if the action is allowed, false otherwise
     */
    @Override
    public boolean actionAllowed(String action) {
        return (allowedActions == null || allowedActions.contains(action)) && super.actionAllowed(action);
    }

    /**
     * Sets the action to be executed when an exit event is triggered.
     *
     * @param exitAction a {@code Runnable} that defines the action to be performed on exit
     */
    public void setExitAction(Runnable exitAction) {
        this.exitAction = exitAction;
    }
}
