package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a {@link StoredObject} class type. This is used for selecting the class in a user-friendly way in
 * some configuration options.
 *
 * @author Syam
 */
public final class SOClass extends Name {

    private static final Map<Integer, SOClass> cache = new HashMap<>();
    private String className;
    private ClassAttribute<?> ca;

    public SOClass() {}

    public static void columns(Columns columns) {
        columns.add("ClassName", "text");
    }

    public static void indices(Indices indices) {
        indices.add("ClassName", true);
    }

    @Override
    public String getUniqueCondition() {
        return "ClassName='" + getClassName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static SOClass get(String name) {
        return StoredObjectUtility.get(SOClass.class, "Name", name, false);
    }

    public static ObjectIterator<SOClass> list(String name) {
        return StoredObjectUtility.list(SOClass.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setClassName(String className) {
        if (!loading()) {
            throw new Set_Not_Allowed("Class Name");
        }
        this.className = className;
    }

    @SetNotAllowed
    @Column(order = 200)
    public String getClassName() {
        return className;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (!deleted() && getSOClass() == null) {
            throw new Invalid_Value("Class Name");
        }
        super.validateData(tm);
    }

    public Class<? extends StoredObject> getSOClass() {
        if(ca != null) {
            return ca.getObjectClass();
        }
        if(className == null || className.isBlank() || className.equals(StoredObject.class.getName())) {
            return null;
        }
        try {
            Class<?> klass = JavaClassLoader.getLogic(className, false);
            if(StoredObject.class.isAssignableFrom(klass)) {
                //noinspection unchecked
                ca = ClassAttribute.get((Class<? extends StoredObject>) klass);
                return ca.getObjectClass();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    public ClassAttribute<?> getClassAttribute() {
        if(ca == null) {
            getSOClass();
        }
        return ca;
    }

    public static ObjectIterator<SOClass> listAll(Class<? extends StoredObject> baseClass) {
        return listAll(baseClass, true, false);
    }

    public static ObjectIterator<SOClass> listAll(Class<? extends StoredObject> baseClass, boolean includeBaseClass,
                                                  boolean includeAbstract) {
        return list(baseClass, includeBaseClass, includeAbstract, null);
    }

    public static ObjectIterator<SOClass> listAllActive(Class<? extends StoredObject> baseClass) {
        return listAllActive(baseClass, true, false);
    }

    public static ObjectIterator<SOClass> listAllActive(Class<? extends StoredObject> baseClass, boolean includeBaseClass,
                                                        boolean includeAbstract) {
        return list(baseClass, includeBaseClass, includeAbstract, "Active");
    }

    private static ObjectIterator<SOClass> list(Class<? extends StoredObject> baseClass, boolean includeBaseClass,
                                                        boolean includeAbstract, String condition) {
        ObjectIterator<SOClass> classes = list(SOClass.class, condition)
                .filter(soc -> soc.isOf(baseClass));
        if(!includeAbstract) {
            classes = classes.filter(soc -> !soc.isAbstract());
        }
        if(!includeBaseClass) {
            classes = classes.filter(soc -> soc.getSOClass() != baseClass);
        }
        return classes;
    }

    public boolean isOf(Class<? extends StoredObject> baseClass) {
        //noinspection DataFlowIssue
        return baseClass.isAssignableFrom(getSOClass());
    }

    public boolean isAbstract() {
        //noinspection DataFlowIssue
        return Modifier.isAbstract(getSOClass().getModifiers());
    }

    public static SOClass getSOClass(Class<? extends StoredObject> soClass) {
        if(soClass == StoredObject.class) {
            return null;
        }
        ClassAttribute<?> ca = ClassAttribute.get(soClass);
        SOClass soc = cache.get(ca.getFamily());
        if(soc == null) {
            soc = get(SOClass.class, "ClassName='" + soClass.getName() + "'");
            if(soc != null) {
                cache.put(ca.getFamily(), soc);
                return soc;
            }
        }
        //noinspection unchecked
        return soc == null ? getSOClass((Class<? extends StoredObject>) soClass.getSuperclass()) : soc;
    }
}
