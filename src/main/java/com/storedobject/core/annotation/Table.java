package com.storedobject.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the features of a Data Class.
 *
 * @author Syam
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * Check if this is a base class or not.
     *
     * @return True/false. Default is <code>false</code>.
     */
    boolean base() default false;

    /**
     * Title value.
     *
     * @return Title value. Default is <code>null</code> string.
     */
    String title() default "";

    /**
     * Form style (Number of field columns).
     *
     * @return Form style. Default is 0 meaning 2 columns.
     */
    int formStyle() default 0;

    /**
     * Anchor fields of this data class (comma-separated list of attributes).
     *
     * @return Anchors. Default is <code>null</code> string (meaning no anchors).
     */
    String anchors() default "";

    /**
     * Name of the tab on which fields (attributes) are displayed.
     *
     * @return Tab name. Default value is a <code>null</code> string.
     */
    String tab() default "";
}