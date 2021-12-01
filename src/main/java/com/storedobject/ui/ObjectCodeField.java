package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectLoadFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.CustomTextField;
import com.vaadin.flow.component.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * In many situations, a {@link StoredObject} instance may contain a code attribute (a String attribute that looks
 * like a code - part number of an inventory item for example) and this field accept such codes for inputting the
 * object instance.
 *
 * @param <T> Type of object instance.
 * @author Syam
 */
public class ObjectCodeField<T extends StoredObject> extends CustomTextField<T> implements ObjectInput<T> {

    private T cached;
    private final Class<T> objectClass;
    private final Method codeMethod;
    private final String code;

    /**
     * Constructor. ("Code" will be used as the code attribute).
     *
     * @param objectClass Object class.
     */
    public ObjectCodeField(Class<T> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor. ("Code" will be used as the code attribute).
     *
     * @param label Label.
     * @param objectClass Object class.
     */
    public ObjectCodeField(String label, Class<T> objectClass) {
        this(label, objectClass, "Code", 0);
    }

    /**
     * Constructor. ("Code" will be used as the code attribute).
     *
     * @param objectClass Object class.
     * @param maxLength Maximum length allowed in the code.
     */
    public ObjectCodeField(Class<T> objectClass, int maxLength) {
        this(null, objectClass, maxLength);
    }

    /**
     * Constructor. ("Code" will be used as the code attribute).
     *
     * @param label Label.
     * @param objectClass Object class.
     * @param maxLength Maximum length allowed in the code.
     */
    public ObjectCodeField(String label, Class<T> objectClass, int maxLength) {
        this(label, objectClass, "Code", maxLength);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param codeAttribute Code attribute.
     */
    public ObjectCodeField(Class<T> objectClass, String codeAttribute) {
        this(null, objectClass, codeAttribute, 0);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param objectClass Object class.
     * @param codeAttribute Code attribute.
     */
    public ObjectCodeField(String label, Class<T> objectClass, String codeAttribute) {
        this(label, objectClass, codeAttribute, 0);
    }

    /**
     * Constructor.
     *
     * @param objectClass Object class.
     * @param codeAttribute Code attribute.
     * @param maxLength Maximum length allowed in the code.
     */
    public ObjectCodeField(Class<T> objectClass, String codeAttribute, int maxLength) {
        this(null, objectClass, codeAttribute, maxLength);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param objectClass Object class.
     * @param codeAttribute Code attribute.
     * @param maxLength Maximum length allowed in the code.
     */
    public ObjectCodeField(String label, Class<T> objectClass, String codeAttribute, int maxLength) {
        super(null);
        if(maxLength > 0) {
            getField().setMaxLength(maxLength);
        }
        if(label != null) {
            setLabel(label);
        }
        this.objectClass = objectClass;
        codeMethod = ClassAttribute.get(objectClass).getMethod(codeAttribute);
        if(codeMethod == null || codeMethod.getReturnType() != String.class
                || Modifier.isStatic(codeMethod.getModifiers())) {
            throw new SORuntimeException(objectClass.getName() + " doesn't have => public String get"
                    + codeAttribute + "()");
        }
        code = "lower(" + codeAttribute + ")";
    }

    @Override
    public void setInternalLabel(String label) {
    }

    @Override
    public String getInternalLabel() {
        return null;
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public T getCached() {
        return cached;
    }

    @Override
    public void setCached(T cached) {
        this.cached = cached;
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
    }

    @Override
    public Component getDetailComponent() {
        return null;
    }

    @Override
    public void setDisplayDetail(Consumer<T> displayDetail) {
    }

    @Override
    public Consumer<T> getDisplayDetail() {
        return null;
    }

    @Override
    public void setPrefixFieldControl(boolean prefixFieldControl) {
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        throw new SORuntimeException();
    }

    @Override
    public void applyFilter() {
    }

    @Override
    protected T getModelValue(String string) {
        if(string == null) {
            return null;
        }
        string = string.trim().toLowerCase();
        T v = StoredObject.get(objectClass, code + "='" + string + "'", isAllowAny());
        if(v == null) {
            v = StoredObject.list(objectClass, code + " LIKE '" + string + "%'", isAllowAny())
                    .single(false);
        }
        return v;
    }

    @Override
    protected String format(T value) {
        try {
            return (String) codeMethod.invoke(value);
        } catch(Throwable ignored) {
        }
        return "";
    }

    /**
     * This method does nothing in this field.
     *
     * @param objects Objects to load.
     */
    @Override
    public void load(ObjectIterator<T> objects) {
    }
}
