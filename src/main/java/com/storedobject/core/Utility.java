package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.SORuntimeException;
import org.postgresql.util.PGobject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * General utility methods.
 *
 * @author Syam
 */
public class Utility {

    /**
     * Blank time: The local time Jan 1, 1800 0:00 is considered blank by the platform for internal purposes.
     */
    public static final long BLANK_TIME = -5364662400000L;

    /**
     * Blank date: The local date Jan 1, 1800 is considered blank by the platform for internal purposes.
     */
    public static final java.sql.Date BLANK_DATE = new java.sql.Date(BLANK_TIME);

    /**
     * Get the size of a sub-list. Index values will be re-adjusted to the valid range automatically to eliminate
     * any exceptions.
     *
     * @param list List from which sub-list to be obtained.
     * @param startingIndex Starting index.
     * @param endingIndex Ending index.
     * @return Size.
     */
    public static int size(List<?> list, int startingIndex, int endingIndex) {
        if(endingIndex < 0) {
            endingIndex = Integer.MAX_VALUE;
        }
        endingIndex = Math.min(endingIndex, list.size());
        startingIndex = Math.max(startingIndex, 0);
        startingIndex = Math.min(startingIndex, endingIndex);
        return endingIndex - startingIndex;
    }

    /**
     * Get the stream of a sub-list. Index values will be re-adjusted to the valid range automatically to eliminate
     * any exceptions.
     *
     * @param list List from which sub-list to be obtained.
     * @param startingIndex Starting index.
     * @param endingIndex Ending index.
     * @return Stream.
     */
    public static <T> Stream<T> stream(List<T> list, int startingIndex, int endingIndex) {
        if(endingIndex < 0) {
            endingIndex = Integer.MAX_VALUE;
        }
        endingIndex = Math.min(endingIndex, list.size());
        startingIndex = Math.max(startingIndex, 0);
        startingIndex = Math.min(startingIndex, endingIndex);
        return IntStream.range(startingIndex, endingIndex).mapToObj(list::get);
    }

    /**
     * Is the given value right-aligned? (Examples of right-aligned values are numeric values, quantity etc.) The
     * notion of right-alignment depends on the locale. Here, the meaning is as per left-to-right language convention.
     * <p>Note: A null value is considered as not right-aligned.</p>
     * @param value Value to check. This could be {@link Class} of the value too.
     * @return True if right-aligned, otherwise false.
     */
    public static boolean isRightAligned(Object value) {
        if(value == null) {
            return false;
        }
        if(value instanceof PGobject pgo) {
            return switch(pgo.getType()) {
                case "mv", "qty", "cminute", "cinteger", "cdouble", "clong" -> true;
                default -> false;
            };
        }
        Class<?> valueType = value instanceof Class ? (Class<?>) value : value.getClass();
        return valueType == int.class || valueType == long.class || valueType == double.class ||
                valueType == float.class || valueType == byte.class || valueType == short.class ||
                Number.class.isAssignableFrom(valueType) ||
                Rate.class.isAssignableFrom(valueType) ||
                Money.class.isAssignableFrom(valueType) ||
                Quantity.class.isAssignableFrom(valueType) ||
                DecimalNumber.class.isAssignableFrom(valueType) ||
                (ComputedValue.class.isAssignableFrom(valueType) && valueType != ComputedDate.class);
    }

    /**
     * Check whether the given date/time is empty/blank or not.
     *
     * @param dateTime Date/timestamp to check.
     * @param <D> Type of date/timestamp.
     * @return True if empty/blank, otherwise false.
     */
    public static <D extends Date> boolean isEmpty(D dateTime) {
        return dateTime == null || dateTime.getTime() == BLANK_TIME;
    }

    /**
     * Create a {@link Stream} from {@link Iterable}.
     *
     * @param iterable Iterable from which stream needs to be created.
     * @param <O> Type of stream element.
     * @return Stream.
     */
    public static <O> Stream<O> stream(Iterable<O> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Create a {@link Stream} from {@link Iterator}.
     *
     * @param iterator Iterator from which stream needs to be created.
     * @param <O> Type of stream element.
     * @return Stream.
     */
    public static <O> Stream<O> stream(Iterator<O> iterator) {
        return StreamSupport.stream((new ToIterable<>(iterator)).spliterator(), false);
    }

    private record ToIterable<O>(Iterator<O> iterator) implements Iterable<O> {
    }

    /**
     * Construct an instance of an object by creating a {@link java.lang.reflect.Constructor} that matches best
     * with the parameters passed.
     *
     * @param objectClass The class of the object to be created.
     * @param paramClasses List of classes of the parameters. (These could be subclasses of the classes of the
     *                     actual parameter instances).
     * @param params Actual parameter instances to be used.
     * @param <T> The type of object to be created.
     * @return The new instance or <code>null</code> if an instance can not be created because no matching
     * constructor is found.
     * @throws LogicRedirected If the instance is a logic, and it is redirected when instantiated.
     */
    public static <T> T construct(Class<T> objectClass, Class<?>[] paramClasses, Object[] params) {
        CP cp = new CP(paramClasses, params);
        if(cp.length() == 0) {
            return constructDefault(objectClass);
        }
        T o = construct(objectClass, cp);
        if(objectClass == Object.class) {
            o = null;
        }
        while(o == null && cp != null) {
            cp = cp.ltrim();
            if(cp != null && cp.length() > 0) {
                o = construct(objectClass, cp);
            }
        }
        return o == null ? constructDefault(objectClass) : o;
    }

    private static <T> T construct(Class<T> objectClass, CP cp) {
        if(cp.length() == 0) {
            return null;
        }
        CP u = cp.upper();
        T o;
        do {
            o = constructInt(objectClass, u);
            if(o != null) {
                return o;
            }
            u = u.lower(cp.paramClasses);
        } while(u != null);
        cp = cp.trim();
        if(cp != null) {
            return construct(objectClass, cp);
        }
        return null;
    }

    private static <T> T constructInt(Class<T> objectClass, CP cp) {
        if(cp.length() == 0) {
            return null;
        }
        Constructor<T> constructor;
        try {
            constructor = objectClass.getConstructor(cp.paramClasses);
        } catch(NoSuchMethodException e) {
            return null;
        }
        try {
            return constructor.newInstance(cp.params);
        } catch(Throwable e) {
            Throwable error = e;
            while(error != null) {
                if(error instanceof LogicRedirected ld) {
                    throw ld;
                }
                if(error instanceof SORuntimeException sore) {
                    throw sore;
                }
                error = error.getCause();
            }
            throw new SORuntimeException(e);
        }
    }

    private static <T> T constructDefault(Class<T> objectClass) {
        Constructor<T> constructor;
        try {
            constructor = objectClass.getConstructor();
        } catch(NoSuchMethodException e) {
            return null;
        }
        try {
            return constructor.newInstance();
        } catch(Throwable e) {
            Throwable error = e;
            while(error != null) {
                if(error instanceof LogicRedirected ld) {
                    throw ld;
                }
                if(error instanceof SORuntimeException sore) {
                    throw sore;
                }
                error = error.getCause();
            }
            throw new SORuntimeException(e);
        }
    }

    private record CP(Class<?>[] paramClasses, Object[] params) {

        public CP {
            if(paramClasses == null) {
                paramClasses = new Class[0];
            }
            if(params == null) {
                params = new Object[0];
            }
            if(paramClasses.length != params.length) {
                int len = Math.min(paramClasses.length, params.length);
                if(len < paramClasses.length) {
                    if(len == 0) {
                        paramClasses = new Class[0];
                    } else {
                        Class<?>[] pc = new Class[len];
                        System.arraycopy(paramClasses, 0, pc, 0, len);
                        paramClasses = pc;
                    }
                }
                if(len < params.length) {
                    if(len == 0) {
                        params = new Object[0];
                    } else {
                        Object[] oc = new Object[len];
                        System.arraycopy(params, 0, oc, 0, len);
                        params = oc;
                    }
                }
            }
        }

        int length() {
            return paramClasses.length;
        }

        CP lower(Class<?>[] orgClasses) {
            Class<?>[] classes = cp(paramClasses);
            Class<?> pc, oc;
            boolean changed = false;
            for(int i = params.length - 1; i >= 0 ; i--) {
                pc = paramClasses[i];
                oc = orgClasses[i];
                if(pc == oc) {
                    continue;
                }
                pc = pc.getSuperclass();
                if(!oc.isAssignableFrom(pc)) {
                    if(!changed) {
                        changed = classes[i] != oc;
                    }
                    classes[i] = oc;
                    continue;
                }
                changed = true;
                classes[i] = pc;
                break;
            }
            return changed ? new CP(classes, params) : null;
        }

        CP upper() {
            Class<?>[] c = new Class[params.length];
            for(int i = 0; i < c.length; i++) {
                c[i] = params[i].getClass();
            }
            return new CP(c, params);
        }

        private static Class<?>[] cp(Class<?>[] classes) {
            Class<?>[] c = new Class[classes.length];
            System.arraycopy(classes, 0, c, 0, c.length);
            return c;
        }

        CP trim() {
            return switch(paramClasses.length) {
                case 0 -> null;
                case 1 -> new CP(null, null);
                default -> doTrim();
            };
        }

        private CP doTrim() {
            Class<?>[] pc = new Class[paramClasses.length - 1];
            System.arraycopy(paramClasses, 0, pc, 0, pc.length);
            return new CP(pc, params);
        }

        CP ltrim() {
            if(paramClasses.length > 1) {
                return doLTrim();
            }
            return trim();
        }

        private CP doLTrim() {
            Class<?>[] pc = new Class[paramClasses.length - 1];
            System.arraycopy(paramClasses, 1, pc, 0, pc.length);
            Object[] p = new Object[pc.length];
            System.arraycopy(params, 1, p, 0, p.length);
            return new CP(pc, p);
        }
    }

    /**
     * Get a method of a class. If a method is not found, it will look for methods that take superclasses of the
     * parameter.
     *
     * @param ofClass Of class.
     * @param methodName Method name.
     * @param parameter Method parameter type (Only single parameter is supported).
     * @return Method if found, otherwise, null.
     */
    public static Method getMethod(Class<?> ofClass, String methodName, Class<?> parameter) {
        try {
            return ofClass.getMethod(methodName, parameter);
        } catch(NoSuchMethodException ignored) {
        }
        if(parameter == Object.class) {
            return null;
        }
        return getMethod(ofClass, methodName, parameter.getSuperclass());
    }

    /**
     * Return the stack trace of the current thread at the current line as a string. Useful for logging.
     *
     * @return Stack trace as a string.
     */
    public static String stackTrace() {
        String st = SORuntimeException.getTrace(Thread.currentThread());
        for(int i = 0; i < 4; i++) {
            st = st.substring(st.indexOf('\n') + 1);
        }
        return st;
    }

    /**
     * Return the stack trace of the given {@link Throwable} as a string. Useful for logging.
     *
     * @param error Throwable instance.
     * @return Stack trace as a string.
     */
    public static String stackTrace(Throwable error) {
        return SORuntimeException.getTrace(error, true);
    }

    /**
     * Copies elements from an iterable to an array using a provided converter function.
     *
     * @param <T> Type of elements in the iterable.
     * @param <O> Type of elements in the array.
     * @param iterable The iterable containing elements to be copied.
     * @param array The array to copy elements into. Must be of type O[].
     * @param converter The function that converts elements from type T to type O.
     * @return The array with copied elements. Returns null if either the iterable or the array is null.
     */
    public static <T, O> O[] copyTo(Iterable<T> iterable, O[] array, Function<T, O> converter) {
        if(iterable == null || array == null) {
            return null;
        }
        int i = 0;
        for(T t : iterable) {
            if(i >= array.length) {
                break;
            }
            array[i++] = converter.apply(t);
        }
        return array;
    }
}
