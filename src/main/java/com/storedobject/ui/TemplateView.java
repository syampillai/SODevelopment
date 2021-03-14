package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.WrappedView;

import java.util.function.Supplier;

/**
 * An {@link ExecutableView} that uses a {@link TemplateLayout} as its sole component.
 *
 * @author Syam
 */
public class TemplateView extends TemplateLayout implements ExecutableView {

    private String caption;
    private View view;

    /**
     * Constructor. This will look for the HTML content in a {@link TextContent} that has the same name as this class.
     */
    public TemplateView() {
        super();
        setCaption(Application.getLogicCaption(null));
    }

    /**
     * Constructor.
     *
     * @param caption Caption to set.
     * @param textContentName Name of the text content that provides the HTML content.
     */
    public TemplateView(String caption, String textContentName) {
        super(textContentName);
        setCaption(caption);
    }

    /**
     * Constructor.
     *
     * @param caption Caption to set.
     * @param textContent Text content that provides the HTML content.
     */
    public TemplateView(String caption, TextContent textContent) {
        super(textContent);
        setCaption(caption);
    }


    /**
     * Constructor.
     *
     * @param contentSupplier Supplier of the HTML content.
     */
    public TemplateView(Supplier<String> contentSupplier) {
        this(null, contentSupplier);
    }

    /**
     * Constructor.
     *
     * @param caption Caption to set.
     * @param contentSupplier Supplier of the HTML content.
     */
    public TemplateView(String caption, Supplier<String> contentSupplier) {
        super(contentSupplier);
        setCaption(caption);
    }

    @Override
    public View getView(boolean create) {
        if(view == null && create) {
            view = new WrappedView(this, caption);
        }
        return view;
    }

    @Override
    public void setCaption(String caption) {
        if(view == null) {
            this.caption = caption;
        } else {
            view.setCaption(caption);
        }
    }
}
