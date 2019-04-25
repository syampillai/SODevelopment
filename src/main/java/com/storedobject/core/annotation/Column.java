package com.storedobject.core.annotation;

@java.lang.annotation.Documented

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Column {
    int order() default 0;
    java.lang.String caption() default "";
    boolean required() default true;
    java.lang.String style() default "";
}
