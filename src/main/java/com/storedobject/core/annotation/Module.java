package com.storedobject.core.annotation;

@java.lang.annotation.Documented

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.PACKAGE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Module {
    int release() default 0;
    java.lang.String moduleName();
    java.lang.String description() default "";
    java.lang.String version() default "";
    int build() default 0;
    java.lang.String vendor() default "www.storedobject.com";
    java.lang.String jarName() default "";
    int year() default 0;
}
