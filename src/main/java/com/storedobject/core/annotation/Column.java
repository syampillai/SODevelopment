package com.storedobject.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the features of a field (attribute of a Data Class).
 *
 * @author Syam
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * Check whether this field is required or not.
     *
     * @return True/false. Default value is <code>true</code>.
     */
    boolean required() default true;

    /**
     * Style of this field.
     *
     * @return Style details. Default value is a <code>null</code> string.
     */
    String style() default "";

    /**
     * Caption of this field.
     *
     * @return Caption. Default value is a <code>null</code> string.
     */
    String caption() default "";

    /**
     * Display order of this field.
     *
     * @return Order. Default value is zero.
     */
    int order() default 0;

    /**
     * Name of the tab on which this field is displayed.
     *
     * @return Tab name. Default value is a <code>null</code> string.
     */
    String tab() default "";
}