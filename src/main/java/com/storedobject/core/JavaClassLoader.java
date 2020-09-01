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
}
