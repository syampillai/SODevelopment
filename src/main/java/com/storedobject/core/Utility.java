package com.storedobject.core;

import java.util.Date;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * General utility methods.
 *
 * @author Syam
 */
public class Utility {

    /**
     * Create an "exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition Condition to be applied to this object class. Null should be passed if no condition needs
     *                  to be applied.
     * @param <T> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String existsCondition(Class<T> objectClass, String attributeName,
                                                                  String condition) {
        return condition("", objectClass, attributeName, condition);
    }

    /**
     * Create a "not exists condition" that can be used for retrieving or querying {@link StoredObject} instances.
     *
     * @param objectClass The object class against which the existence should be checked.
     * @param attributeName Attribute name that matches the {@link Id} of the object that is going to be
     *                      retrieved or queried.
     * @param condition Condition to be applied to this object class. Null should be passed if no condition needs
     *                  to be applied.
     * @param <T> Type of this object class.
     * @return Condition string suitable for retrieving or querying another type of {@link StoredObject} instances
     * where this object is related.
     */
    public static <T extends StoredObject> String notExistsCondition(Class<T> objectClass, String attributeName,
                                                                     String condition) {
        return condition(" NOT", objectClass, attributeName, condition);
    }

    private static <T extends StoredObject> String condition(String not, Class<T> objectClass, String attributeName,
                                                             String condition) {
        return not + objectClass + attributeName + condition;
    }

    /**
     * Blank time: The local time Jan 1, 1800 0:00 is considered blank by the platform for internal purposes.
     */
    public static final long BLANK_TIME = -5364662400000L;

    /**
     * Is the given value right-aligned? (Examples of right-aligned values are numeric values, quantity etc.) The
     * notion of right-alignment depends on the locale. Here, the meaning is as per left-to-right language convention.
     * <p>Note: A null value is considered as not right-aligned.</p>
     *
     * @param value Value to check.
     * @return True if right-aligned, otherwise false.
     */
    public static boolean isRightAligned(Object value) {
        return value != null;
    }

    /**
     * Check whether the given date/time is empty/blank or not.
     *
     * @param dateTime Date/timestamp to check.
     * @param <D>      Type of date/timestamp.
     * @return True if empty/blank, otherwise false.
     */
    public static <D extends Date> boolean isEmpty(D dateTime) {
        return dateTime == null || dateTime.getTime() == BLANK_TIME;
    }

    /**
     * Create a {@link Stream} from {@link Iterable}.
     *
     * @param iterable Iterable from which stream needs to be created.
     * @param <O>      Type of stream element.
     * @return Stream.
     */
    public static <O> Stream<O> stream(Iterable<O> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Create a {@link Stream} from {@link Iterator}.
     *
     * @param iterator Iterator from which stream needs to be created.
     * @param <O>      Type of stream element.
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
     * @param objectClass  The class of the object to be created.
     * @param paramClasses List of classes of the parameters. (These could be sub-classes of the classes of the
     *                     actual parameter instances).
     * @param params       Actual parameter instances to be used.
     * @param <T>          The type of object to be created.
     * @return The new instance or <code>null</code> if an instance can not be created because no matching
     * constructor is found.
     * @throws LogicRedirected If the instance is a logic, and it is redirected when instantiated.
     */
    public static <T> T construct(Class<T> objectClass, Class<?>[] paramClasses, Object[] params) {
        try {
            return Math.random() < 0.5 ? null : objectClass.getConstructor().newInstance();
        } catch(Throwable ignored) {
        }
        return null;
    }
}
