package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.storedobject.ui.util.HtmlTemplate;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasSize;

import java.util.function.Supplier;

/**
 * A layout {@link com.vaadin.flow.component.Component} based on a template stored in the DB as an HTML like file -
 * It should be stored as {@link com.storedobject.core.TextContent}. The template can contain HTML tags (and thus,
 * Vaadin component tags too) and if any image resources are referenced, respective
 * {@link com.storedobject.core.MediaFile}s must be used just like in {@link HTMLView}. For CSS styling the content,
 * style tag should be used and if used, it should be used only once. If style tags are specified more than once,
 *  * only the first one is considered and the rest are ignored.
 * <p>A separate constructor is available if you want to provide the content directly. See
 * {@link #TemplateLayout(Supplier)}.</p>
 *
 * @author Syam
 */
public class TemplateLayout extends HtmlTemplate implements HasSize {

    /**
     * Constructor. The template content will be determined from the class name.
     */
    public TemplateLayout() {
        super();
    }

    /**
     * Constructor.
     *
     * @param textContentName Name of the template content.
     */
    public TemplateLayout(String textContentName) {
        super(textContentName);
    }

    /**
     * Constructor.
     *
     * @param tc Template content.
     */
    public TemplateLayout(TextContent tc) {
        super(tc);
    }

    /**
     * Constructor.
     *
     * @param contentSupplier Content supplier.
     */
    public TemplateLayout(Supplier<String> contentSupplier) {
        super(contentSupplier);
    }

    /**
     * Center this {@link View} on the screen.
     */
    public void center() {
        getElement().getStyle().
                set("display", "flex").
                set("flex-wrap", "wrap").
                set("align-items", "center").
                set("align-content", "space-around").
                set("justify-content", "space-evenly").
                set("box-sizing", "border-box");
    }
}
