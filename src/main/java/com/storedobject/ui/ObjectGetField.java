package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.ImageButton;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ObjectGetField<T extends StoredObject> extends AbstractObjectField<T> {

    private GetProvider<T> getProvider;
    private ImageButton search = new ImageButton("Search", e -> doSearch());
    private TextField searchField;

    public ObjectGetField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectGetField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectGetField(Class<T> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny, null);
    }

    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny, null);
    }

    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, GetProvider<T> getProvider) {
        super(objectClass, allowAny);
        this.getProvider = getProvider;
        setLabel(label);
        setPlaceholder("Search");
    }

    GetProvider<T> createGetProvider() {
        return new GetSupplier<>(getObjectClass());
    }

    private GetProvider<T> getProvider() {
        return getProvider == null ? (getProvider = createGetProvider()) : getProvider;
    }

    @Override
    protected Component createPrefixComponent() {
        if(searchField != null) {
            return searchField;
        }
        TextField tf = new TextField();
        tf.setPrefixComponent(search);
        searchField = tf;
        return searchField;
    }

    @Override
    public void setPlaceholder(String placeholder) {
        getSearchField().setPlaceholder(placeholder);
    }

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
                        s.load(filteredList(text));
                        if(s.size() == 1) {
                            value = s.getItem(0);
                        } else {
                            s.search(this);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                value = null;
            }
        }
        setPresentationValue(value);
        return value;
    }

    private ObjectIterator<T> filteredList(String text) {
        try {
            return filteredList(getProvider().listTextObjects(Application.get().getTransactionManager().getEntity(), text));
        } catch (Exception e) {
            return ObjectIterator.create();
        }
    }

    @Override
    protected void doSearch() {
        String text = getSearchField().getValue().trim();
        if(text.length() > 0) {
            getSearcher().setObjects(filteredList(text));
        } else {
            if(doSearchLoadAll()) {
                return;
            }
        }
        if(getSearcher().size() == 1) {
            T object = getSearcher().getItem(0);
            T current = getValue();
            if(!object.equals(current)) {
                setPresentationValue(object);
                setModelValue(object, true);
                return;
            }
        }
        super.doSearch();
    }

    boolean doSearchLoadAll() {
        return false;
    }

    @Override
    protected void setPresentationValue(T value) {
        super.setPresentationValue(value);
        getSearchField().setValue("");
    }

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

    public interface GetProvider<O extends StoredObject> {
        O getTextObject(SystemEntity systemEntity, String value) throws Exception;
        ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception;
    }

    public static class GetSupplier<O extends StoredObject> implements GetProvider<O> {

        protected Method getMethod, listMethod;
        protected Class<O> objectClass;

        public GetSupplier(Class<O> objectClass) {
            this(objectClass, "get", "list");
        }

        public GetSupplier(Class<O> objectClass, String getMethodName, String listMethodName) {
            this.objectClass = objectClass;
            init(getMethodName, listMethodName);
        }

        protected GetSupplier() {
        }

        protected void init(String getMethodName, String listMethodName) {
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

        protected boolean isSE() {
            return OfEntity.class.isAssignableFrom(objectClass);
        }

        protected Class<?>[] getParamClasses() {
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

    public static class GetTypedSupplier<O extends StoredObject> extends GetSupplier<O> {

        private ObjectProvider typeObjectProvider;

        public GetTypedSupplier(ObjectProvider typeObjectProvider, Class<O> objectClass) {
            this(typeObjectProvider, objectClass, "get", "list");
        }

        public GetTypedSupplier(ObjectProvider typeObjectProvider, Class<O> objectClass, String getMethodName, String listMethodName) {
            super();
            this.objectClass = objectClass;
            this.typeObjectProvider = typeObjectProvider;
            init(getMethodName, listMethodName);
        }

        @Override
        protected Class<?>[] getParamClasses() {
            if(isSE()) {
                return new Class<?>[] { SystemEntity.class, String.class, typeObjectProvider.getObjectClass() };
            }
            return new Class<?>[] { String.class, typeObjectProvider.getObjectClass() };
        }

        @SuppressWarnings("unchecked")
        @Override
        public O getTextObject(SystemEntity systemEntity, String value) throws Exception {
            if(isSE()) {
                return (O) getMethod.invoke(null, systemEntity, value, typeObjectProvider.getObject());
            }
            return (O) getMethod.invoke(null, value, typeObjectProvider.getObject());
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            if(isSE()) {
                return (ObjectIterator<O>)listMethod.invoke(null, systemEntity, value, typeObjectProvider.getObject());
            }
            return (ObjectIterator<O>)listMethod.invoke(null, value, typeObjectProvider.getObject());
        }
    }
}