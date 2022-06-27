package com.storedobject.ui;

import com.storedobject.core.TemplateString;

/**
 * Template string that can be evaluated. Values are defined using variable names: ${name}. If you want to use
 * $ symbol as a literal (escaping), use $$.
 *
 * @param <T> Type of object to apply on the template.
 * @author Syam
 */
public class TemplateText<T> extends TemplateString<T> {

    /**
     * Constructor.
     *
     * @param objectClass Type of object to apply the template.
     * @param template Template string.
     */
    public TemplateText(Class<T> objectClass, String template) {
        super(objectClass, Application.get(), template);
    }
}
