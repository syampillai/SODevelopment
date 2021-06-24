package com.storedobject.core.annotation;

@java.lang.annotation.Documented

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Table {
    boolean base() default false;
    java.lang.String title() default "";
    int formStyle() default 0;
    java.lang.String anchors() default "";    
}
