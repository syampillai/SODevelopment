package com.storedobject.ui;

import com.storedobject.helper.ID;

/**
 * A {@link com.vaadin.flow.component.Component} based on a template. The template can contain HTML tags (and thus,
 * Vaadin component tags too) and if any image resources are referenced, respective
 * {@link com.storedobject.core.MediaFile}s must be used just like in {@link HTMLView}. For CSS styling the content,
 * style tag should be used and if used, it should be used only once. If style tags are specified more than once,
 * only the first one is considered and the rest are ignored.
 * <p>Note: You can use {@link com.vaadin.flow.component.template.Id} mapping as used with {@link TemplateLayout}
 * to map components inside this class to their HTML counterparts.</p>
 *
 * @author Syam
 */
public class TemplateComponent extends TemplateLayout {

    /**
     * Constructor.
     *
     * @param templateCode Template containing HTML and CSS (within style tag).
     */
    public TemplateComponent(String templateCode) {
        super(() -> templateCode);
        setId("soTC" + ID.newID());
    }
}
