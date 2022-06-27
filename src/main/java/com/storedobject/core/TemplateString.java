package com.storedobject.core;

import java.util.function.Function;

/**
 * Template string that can be evaluated. Values are defined using variable names: ${name}. If you want to use
 * $ symbol as a literal (escaping), use $$.
 *
 * @param <T> Type of object to apply on the template.
 * @author Syam
 */
public class TemplateString<T> implements Function<T, String> {

    /**
     * Constructor.
     *
     * @param objectClass Type of object to apply the template.
     * @param template Template string.
     */
    public TemplateString(Class<T> objectClass, String template) {
        this(objectClass, null, template);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type of object to apply the template.
     * @param device Device on which the template to be applied.
     * @param template Template string.
     */
    public TemplateString(Class<T> objectClass, Device device, String template) {
    }

    /**
     * Set the device to be used while evaluating the template.
     *
     * @param device Device on which the template to be applied.
     */
    public void setDevice(Device device) {
    }

    @Override
    public String apply(T object) {
        return "";
    }
}
