package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

/**
 * A grid-view that is typically used for searching and setting a particular instance of a {@link StoredObject}.
 * Instead of using the constructors directly, static create methods are available for creating an instance of this
 * view - {@link #create(Class, String, Consumer)}, {@link #create(Class, Iterable, String, Consumer)}.
 *
 * @param <T> Type of object to search for.
 * @author Syam
 */
public class ObjectSearchBrowser<T extends StoredObject> extends ObjectBrowser<T> {

    private ObjectSearcherField<T> searcherField;

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     */
    public ObjectSearchBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param caption Caption of the view.
     */
    public ObjectSearchBrowser(Class<T> objectClass, String caption) {
        this(objectClass, null, caption);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     */
    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, (String)null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     * @param filterColumns Filter columns.
     */
    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, filterColumns, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     * @param caption Caption of the view.
     */
    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, String caption) {
        this(objectClass, browseColumns, null, caption);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     * @param filterColumns Filter columns.
     * @param caption Caption of the view.
     */
    public ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, filterColumns, caption, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     * @param filterColumns Filter columns
     * @param caption Caption of the view.
     * @param allowedActions Allowed actions. Example: (EDIT, DELETE)
     */
    ObjectSearchBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, EditorAction.SEARCH | EditorAction.RELOAD, filterColumns, caption, allowedActions);
        if(ObjectSearcherField.canCreate(objectClass)) {
            searcherField = new ObjectSearcherField<>(objectClass, o -> {
                if(objectSetter != null) {
                    objectSetter.setObject(o);
                }
                close();
            }) {
                @Override
                protected ObjectBrowser<T> createSearcher() {
                    return ObjectSearchBrowser.this;
                }
            };
            searcherField.getSearchField().setPrefixComponent(null);
        }
    }

    /**
     * Constructor.
     *
     * @param className Name of the object class.
     */
    @SuppressWarnings("unchecked")
    public ObjectSearchBrowser(String className) throws Exception {
        this((Class<T>) JavaClassLoader.getLogic(ObjectEditor.sanitize(className)), null, null,
                Application.get().getRunningLogic().getTitle(), null);
    }

    /**
     * Create a searcher from the given parameters. The returned class may be a customized version of the searcher
     * if one exists.
     *
     * @param objectClass Class of the object type.
     * @param caption Caption of the view.
     * @param <O> Type of the object class.
     * @param objectConsumer Consumer of the search result. (Could be null and can be set later
     *                       via {@link #setObjectConsumer(Consumer)}).
     * @return An instance of the searcher.
     */
    public static <O extends StoredObject> ObjectSearchBrowser<O> create(Class<O> objectClass, String caption, Consumer<O> objectConsumer) {
        return create(objectClass, StoredObjectUtility.browseColumns(objectClass), caption, objectConsumer);
    }

    /**
     * Create a searcher from the given parameters. The returned class may be a customized version of the searcher
     * if one exists.
     *
     * @param objectClass Class of the object type.
     * @param browseColumns Browse columns.
     * @param caption Caption of the view.
     * @param <O> Type of the object class.
     * @param objectConsumer Consumer of the search result. (Could be null and can be set later
     *                       via {@link #setObjectConsumer(Consumer)}).
     * @return An instance of the searcher.
     */
    public static <O extends StoredObject> ObjectSearchBrowser<O> create(Class<O> objectClass, Iterable<String> browseColumns, String caption, Consumer<O> objectConsumer) {
        ObjectSearchBrowser<O> b = createInt(objectClass, browseColumns, caption);
        if(objectConsumer != null) {
            b.setObjectConsumer(objectConsumer);
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    private static <O extends StoredObject> ObjectSearchBrowser<O> createInt(Class<O> objectClass,
                                                                             Iterable<String> browseColumns,
                                                                             String caption) {
        try {
            Class<?> logic = JavaClassLoader.getLogic(LogicParser.createLogicName(objectClass, "SearchBrowser"));
            Constructor<?> c = null;
            try {
                c = logic.getConstructor(Iterable.class, String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectSearchBrowser<O>) c.newInstance(browseColumns, caption);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectSearchBrowser<O>) c.newInstance(browseColumns);
            }
            try {
                c = logic.getConstructor(Iterable.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null) {
                return (ObjectSearchBrowser<O>) c.newInstance(browseColumns);
            }
            try {
                c = logic.getConstructor(String.class);
            } catch(NoSuchMethodException ignored) {
            }
            if(c != null && c.getDeclaringClass() != ObjectSearchBrowser.class) {
                return (ObjectSearchBrowser<O>) c.newInstance(caption);
            }
            try {
                c = logic.getConstructor();
            } catch(NoSuchMethodException ignored) {
                c = null;
            }
            if(c != null) {
                return (ObjectSearchBrowser<O>) c.newInstance();
            }
        } catch (Throwable t) {
            Application.get().log(t);
        }
        return new ObjectSearchBrowser<>(objectClass, browseColumns, caption);
    }

    @Override
    public View getView(boolean create) {
        View v = super.getView(create);
        if(v != null && searcherField != null) {
            v.setFirstFocus(searcherField);
        }
        return v;
    }

    @Override
    public Component createHeader() {
        if(searcherField != null) {
            buttonPanel.add(searcherField);
        }
        return super.createHeader();
    }
}
