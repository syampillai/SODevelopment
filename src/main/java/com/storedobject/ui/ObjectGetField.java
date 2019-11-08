package com.storedobject.ui;

import com.storedobject.core.ObjectIterator;
import com.storedobject.core.OfEntity;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemEntity;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ObjectGetField<T extends StoredObject> extends AbstractObjectField<T> {

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

    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        this(label, objectClass, allowAny, allowAdd,null);
    }

    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, GetProvider<T> getProvider) {
        this(label, objectClass, allowAny, false, getProvider);
    }

    public ObjectGetField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd, GetProvider<T> getProvider) {
        super(objectClass, allowAny);
    }

    GetProvider<T> createGetProvider() {
        return new GetSupplier<>(getObjectClass());
    }

    @Override
    protected Component createPrefixComponent() {
        return null;
    }

    @Override
    public void setPlaceholder(String placeholder) {
        getSearchField().setPlaceholder(placeholder);
    }

    public TextField getSearchField() {
        return null;
    }

    @Override
    protected T generateModelValue() {
        return null;
    }

    private ObjectIterator<T> filteredList(String text) {
        return null;
    }

    @Override
    protected void doSearch() {
    }

    boolean doSearchLoadAll() {
        return false;
    }

    @Override
    protected void setPresentationValue(T value) {
    }

    public void setNotFoundTacker(Consumer<String> notFound) {
    }

    public static <O extends StoredObject> boolean canCreate(Class<O> objectClass) {
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
        }

        protected GetSupplier() {
        }

        protected void init(String getMethodName, String listMethodName) {
        }

        protected boolean isSE() {
            return OfEntity.class.isAssignableFrom(objectClass);
        }

        protected Class<?>[] getParamClasses() {
            return null;
        }

        @Override
        public O getTextObject(SystemEntity systemEntity, String value) throws Exception {
            return null;
        }

        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            return null;
        }
    }

    public static class GetTypedSupplier<O extends StoredObject> extends GetSupplier<O> {

        public GetTypedSupplier(ObjectProvider typeObjectProvider, Class<O> objectClass) {
            this(typeObjectProvider, objectClass, "get", "list");
        }

        public GetTypedSupplier(ObjectProvider typeObjectProvider, Class<O> objectClass, String getMethodName, String listMethodName) {
            super();
        }

        @Override
        protected Class<?>[] getParamClasses() {
            return null;
        }

        @Override
        public O getTextObject(SystemEntity systemEntity, String value) throws Exception {
            return null;
        }

        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String value) throws Exception {
            return null;
        }
    }
}