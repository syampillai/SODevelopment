package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ImageButton;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * A field to accept instances of {@link StoredObject}s that are searchable using some keywords.
 * <p>In order to make a {@link StoredObject} searchable using keywords, it should at least implement
 * a static get(String keywords) method that returns a matching instance.
 * Example: {@link com.storedobject.core.Person#get(String)}.</p>
 * <p>Also, it is recommended to implement a static list(String keywords) method that returns an
 * iterator of all matching instances. Example: {@link com.storedobject.core.Person#list(String)}.</p>
 * <p>Some classes are specific to a particular {@link SystemEntity} instance and such classes should implement
 * static get and list methods that take a {@link SystemEntity} as the first parameter and
 * keywords ad the second parameter. Example: {@link InventoryStore#get(SystemEntity, String)},
 * {@link InventoryStore#list(SystemEntity, String)}</p>
 * <p>Even if a {@link StoredObject} class is not providing the required "get" and "list" methods for
 * supporting this field, it is still possible to create one by passing an implementation for the
 * {@link GetProvider} interface as the parameter.</p>
 *
 * @param <T> Type of object instance accepted.
 * @author Syam
 */
public class ObjectGetField<T extends StoredObject> extends AbstractObjectField<T> {

    private GetProvider<T> getProvider;
    private TextField searchField;
    private Consumer<String> notFound;
    private ImageButton addButton;

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     */
    public ObjectGetField(Class<T> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     */
    public ObjectGetField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ObjectGetField(Class<T> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny, null);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny, null);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     * @param allowAdd Whether new object instances can be added via this field or not.
     */
    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        this(label, objectClass, allowAny, allowAdd,null);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     * @param getProvider "Get" provider for searching the object instances.
     */
    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, GetProvider<T> getProvider) {
        this(label, objectClass, allowAny, false, getProvider);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     * @param allowAdd Whether new object instances can be added via this field or not.
     * @param getProvider "Get" provider for searching the object instances.
     */
    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd, GetProvider<T> getProvider) {
        super(objectClass, allowAny);
        this.getProvider = getProvider;
        setLabel(label);
        if(allowAdd && !isAllowAny()) {
            ObjectField.checkDetailClass(objectClass, label);
            addButton = new ImageButton("Add new", VaadinIcon.PLUS, e -> addNew()).withBox();
        }
        setPlaceholder("Search");
    }

    /**
     * Create the "get" provider for this field.
     *
     * @return By default, it will create an instance of {@link GetSupplier}.
     */
    protected GetProvider<T> createGetProvider() {
        return new GetSupplier<>(getObjectClass());
    }

    private GetProvider<T> getProvider() {
        return getProvider == null ? (getProvider = createGetProvider()) : getProvider;
    }

    private int w() {
        if(InventoryItemType.class.isAssignableFrom(getObjectClass())) {
            return 300;
        }
        if(Account.class.isAssignableFrom(getObjectClass())) {
            return 300;
        }
        return 200;
    }

    @Override
    protected Component createPrefixComponent() {
        if(searchField != null) {
            return searchField;
        }
        searchField = new TextField();
        searchField.setWidth(w() + "px");
        ButtonLayout buttonLayout = new ButtonLayout();
        ImageButton search = new ImageButton("Search", e -> doSearch()).withBox();
        if(addButton != null) {
            buttonLayout.add(addButton);
        }
        ImageButton b = new ImageButton("Cancel", e -> {
            setValue((Id)null);
            searchField.focus();
        }).withBox();
        buttonLayout.add(b);
        buttonLayout.add(search);
        searchField.setPrefixComponent(buttonLayout);
        return searchField;
    }

    @Override
    public void focus() {
        searchField.focus();
    }

    @Override
    public void setPlaceholder(String placeholder) {
        getSearchField().setPlaceholder(placeholder);
    }

    /**
     * Get the search field for this.
     *
     * @return The search field. (The default implementation invokes {@link #createPrefixComponent()} to create
     * a text field decorated with search and clear buttons).
     */
    public TextField getSearchField() {
        if(searchField == null) {
            createPrefixComponent();
        }
        return searchField;
    }

    @Override
    protected T generateModelValue() {
        T value;
        String text = getSearchField().getValue().trim();
        if(text.isEmpty()) {
            value = null;
        } else {
            try {
                value = getProvider().getTextObject(Application.get().getTransactionManager().getEntity(), text);
                if(value != null) {
                    value = filter(value);
                } else {
                    ObjectBrowser<T> s = getSearcher();
                    if(s != null) {
                        if(s.objectSetter == null) {
                            s.setObjectSetter(this);
                        }
                        if(s.filter == null) {
                            s.load();
                        } else {
                            s.load(filteredList(text));
                        }
                        if(s.size() == 1) {
                            value = s.getItem(0);
                        } else {
                            if(notFound != null) {
                                notFound.accept(text);
                            }
                            s.search();
                        }
                    }
                }
            } catch (Exception e) {
                ApplicationServer.log(Application.get(), e);
                value = null;
            }
        }
        setPresentationValue(value);
        return value;
    }

    /**
     * Set a "not found" consumer. Whenever no matching item is found for a particular search text, this consumer
     * is asked to accept that search text.
     *
     * @param notFound Consumer for accepting "not found" search text.
     */
    public void setNotFoundTacker(Consumer<String> notFound) {
        this.notFound = notFound;
    }

    private ObjectIterator<T> filteredList(String text) {
        try {
            return filteredList(getProvider().listTextObjects(Application.get()
                    .getTransactionManager().getEntity(), text));
        } catch (Exception e) {
            return ObjectIterator.create();
        }
    }

    @Override
    protected void doSearch() {
        String text = getSearchField().getValue().trim();
        if(text.isEmpty()) {
            if(doSearchLoadAll()) {
                return;
            }
        } else {
            getSearcher().setObjects(filteredList(text));
        }
        if(getSearcher().size() == 1) {
            T object = getSearcher().getItem(0);
            T current = previousValue();
            if(!object.equals(current)) {
                setPresentationValue(object);
                setModelValue(object, true);
                return;
            }
            if(text.isEmpty()) {
                getSearcher().resetSearch();
            }
        }
        if(notFound != null) {
            notFound.accept(text);
        }
        super.doSearch();
    }

    T previousValue() {
        return getValue();
    }

    /**
     * Load the searcher with all the available instances.
     *
     * @return True if loaded. Default implementation does nothing and returns <code>false</code>.
     */
    protected boolean doSearchLoadAll() {
        return false;
    }

    @Override
    protected void setPresentationValue(T value) {
        super.setPresentationValue(value);
        getSearchField().setValue("");
    }

    /**
     * Check whether an instance of the {@link ObjectGetField} can be created for a given object class or not.
     *
     * @param objectClass Class of the object.
     * @param <O> Class type.
     * @return True if appropriate "get" method exists. (Please see the read the documentation of this class).
     */
    public static <O extends StoredObject> boolean canCreate(Class<O> objectClass) {
        Method m;
        try {
            m = objectClass.getMethod("get", String.class);
            if(Modifier.isStatic(m.getModifiers()) && objectClass.isAssignableFrom(m.getReturnType())) {
                return true;
            }
        } catch (Exception ignored) {
        }
        try {
            m = objectClass.getMethod("get", SystemEntity.class, String.class);
            if(Modifier.isStatic(m.getModifiers()) && objectClass.isAssignableFrom(m.getReturnType())) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * The {@link ObjectGetField} requires a "get provider" to provide objects or list of objects based on
     * a search text. This interface defines the required methods for that.
     *
     * @param <O> Type of object class for the "get".
     * @author Syam
     */
    public interface GetProvider<O extends StoredObject> {

        /**
         * Get an object instance for the search text specified.
         * @param systemEntity System entity for which this search should be conducted.
         * @param searchText Search text for which object instance needs to be returned.
         * @return Object instance matching the search text.
         * @throws Exception If any error occurs while retrieving the object instance.
         */
        O getTextObject(SystemEntity systemEntity, String searchText) throws Exception;

        /**
         * Get a list of object instances for the search text specified.
         * @param systemEntity System entity for which this search should be conducted.
         * @param searchText Search text for which object instances need to be returned.
         * @return Object instances matching the search text as an iterator.
         * @throws Exception If any error occurs while retrieving the object instances.
         */
        ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String searchText) throws Exception;
    }

    /**
     * Implementation of {@link GetProvider} for normal {@link StoredObject} classes.
     *
     * @param <O> Type of class.
     * @author Syam
     */
    public static class GetSupplier<O extends StoredObject> implements GetProvider<O> {

        Method getMethod, listMethod;
        Class<O> objectClass;

        /**
         * Constructor.
         *
         * @param objectClass Class of the objects for which this needs to be created.
         */
        public GetSupplier(Class<O> objectClass) {
            this(objectClass, "get", "list");
        }

        /**
         * Constructor.
         *
         * @param objectClass Class of the objects for which this needs to be created.
         * @param getMethodName Name of the "get" method.
         * @param listMethodName Name of the "list" method.
         */
        public GetSupplier(Class<O> objectClass, String getMethodName, String listMethodName) {
            this.objectClass = objectClass;
            init(getMethodName, listMethodName);
        }

        public GetSupplier() {
        }

        void init(String getMethodName, String listMethodName) {
            Class<?>[] params = getParamClasses();
            while(true) {
                try {
                    getMethod = objectClass.getMethod(getMethodName, params);
                    if(!objectClass.isAssignableFrom(getMethod.getReturnType())) {
                        getMethod = null;
                    }
                    if(getMethod != null && !Modifier.isStatic(getMethod.getModifiers())) {
                        getMethod = null;
                    }
                } catch (Exception ignored) {
                }
                if(getMethod != null) {
                    break;
                }
                if(params[params.length - 1] == String.class || params[params.length - 1] == Object.class) {
                    break;
                }
                params[params.length - 1] = params[params.length - 1].getSuperclass();
            }
            if(getMethod == null) {
                StringBuilder s = new StringBuilder("Method not found: ");
                s.append(objectClass.getName()).append('.').append(getMethodName).append("(");
                for(Class<?> c: params) {
                    s.append(' ').append(c);
                }
                s.append(" )");
                throw new SORuntimeException(s.toString());
            }
            try {
                listMethod = objectClass.getMethod(listMethodName, params);
                if(!ObjectIterator.class.isAssignableFrom(listMethod.getReturnType())) {
                    listMethod = null;
                }
                if(listMethod != null && !Modifier.isStatic(listMethod.getModifiers())) {
                    listMethod = null;
                }
            } catch (Exception ignored) {
            }
        }

        boolean isSE() {
            return OfEntity.class.isAssignableFrom(objectClass);
        }

        Class<?>[] getParamClasses() {
            if(isSE()) {
                return new Class<?>[] { SystemEntity.class, String.class };
            }
            return new Class<?>[] { String.class };
        }

        @SuppressWarnings("unchecked")
        @Override
        public O getTextObject(SystemEntity systemEntity, String value) throws Exception {
            if(isSE()) {
                return (O) getMethod.invoke(null, systemEntity, value);
            }
            return (O) getMethod.invoke(null, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            if(listMethod == null) {
                return ObjectIterator.create();
            }
            if(isSE()) {
                return (ObjectIterator<O>)listMethod.invoke(null, systemEntity, value);
            }
            return (ObjectIterator<O>)listMethod.invoke(null, value);
        }
    }

    /**
     * Implementation of {@link GetProvider} for {@link StoredObject} classes where additional type information is
     * required (For example, {@link InventoryItem} requires an instance of the {@link InventoryItemType} while
     * searching in order to narrow down the search).
     *
     * @param <O> Type of class.
     * @author Syam
     */
    public static class GetTypedSupplier<O extends StoredObject> extends GetSupplier<O> {

        private final ObjectProvider<?> typeObjectProvider;

        /**
         * Constructor.
         *
         * @param typeObjectProvider Object provider for the type part,
         * @param objectClass Class of the objects for which this needs to be created.
         */
        public GetTypedSupplier(ObjectProvider<?> typeObjectProvider, Class<O> objectClass) {
            this(typeObjectProvider, objectClass, "get", "list");
        }

        /**
         * Constructor.
         *
         * @param typeObjectProvider Object provider for the type part,
         * @param objectClass Class of the objects for which this needs to be created.
         * @param getMethodName Name of the "get" method.
         * @param listMethodName Name of the "list" method.
         */
        public GetTypedSupplier(ObjectProvider<?> typeObjectProvider, Class<O> objectClass, String getMethodName,
                                String listMethodName) {
            super();
            this.objectClass = objectClass;
            this.typeObjectProvider = typeObjectProvider;
            init(getMethodName, listMethodName);
        }

        @Override
        Class<?>[] getParamClasses() {
            if(isSE()) {
                return new Class<?>[] { SystemEntity.class, String.class, typeObjectProvider.getObjectClass() };
            }
            return new Class<?>[] { String.class, typeObjectProvider.getObjectClass() };
        }

        @SuppressWarnings("unchecked")
        @Override
        public O getTextObject(SystemEntity systemEntity, String value) throws Exception {
            if(isSE()) {
                return (O) getMethod.invoke(null, systemEntity, value, getTypeObject());
            }
            return (O) getMethod.invoke(null, value, getTypeObject());
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            if(isSE()) {
                return (ObjectIterator<O>)listMethod.invoke(null, systemEntity, value, getTypeObject());
            }
            return (ObjectIterator<O>)listMethod.invoke(null, value, getTypeObject());
        }

        public StoredObject getTypeObject() {
            return typeObjectProvider.getObject();
        }
    }
}