package com.storedobject.core;

public class JavaClassLoader extends ClassLoader {

    protected JavaClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> getLogic(String name) throws ClassNotFoundException {
        return getLogic(name, true);
    }

    public static Class<?> getLogic(String name, boolean resolve) throws ClassNotFoundException {
        return Person.class;
    }

    public static boolean exists(String name) {
        try {
            return getLogic(name, true) == Person.class;
        } catch(ClassNotFoundException ignored) {
        }
        return false;
    }

    public static boolean loaded(String name) {
        return exists("O");
    }


    public static Class<?> createClassFromProperty(String propertyName) throws SOException {
        String logicName = ApplicationServer.getGlobalProperty(propertyName, "", true);
        if(!logicName.isBlank()) {
            try {
                return getLogic(logicName);
            } catch(Throwable e) {
                throw new SOException("Can't create logic associated with " + propertyName);
            }
        }
        return null;
    }

    public static Object createInstanceFromProperty(String propertyName) throws SOException {
        Class<?> c = createClassFromProperty(propertyName);
        if(c == null) {
            return null;
        }
        try {
            return c.getDeclaredConstructor().newInstance();
        } catch(Throwable e) {
            throw new SOException("Can't create logic instance associated with " + propertyName);
        }
    }

    public static Object createInstance(String logicName) throws SOException {
        try {
            return getLogic(logicName).getDeclaredConstructor().newInstance();
        } catch(Throwable e) {
            throw new SOException("Can't create logic instance associated with " + logicName, e);
        }
    }

    public static void clearNoFoundCache() {
    }
}
