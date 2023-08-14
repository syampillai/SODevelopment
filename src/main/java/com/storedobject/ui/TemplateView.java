package com.storedobject.ui;

import com.storedobject.core.StringUtility;
import com.storedobject.core.TextContent;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.WrappedView;
import com.vaadin.flow.component.Component;

import java.util.function.Supplier;

/**
 * An {@link ExecutableView} that uses a {@link TemplateLayout} as its sole component.
 *
 * @author Syam
 */
public class TemplateView extends TemplateLayout implements ExecutableView {

    private String caption;
    View view;

    /**
     * Constructor. This will look for the HTML content in a {@link TextContent} that has the same name as this class.
     */
    public TemplateView() {
        super();
        setCaption(Application.getLogicCaption(StringUtility.makeLabel(getClass())));
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
            view = new WrappedView(this, caption) {
                @Override
                public void decorateComponent() {
                    super.decorateComponent();
                    TemplateView.this.decorateComponent();
                }
            };
            build();
            viewConstructed(view);
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

    /**
     * This will be invoked when the {@link View} is constructed for the first time.
     *
     * @param view View that is constructed now.
     */
    public void viewConstructed(View view) {
    }

    /**
     * Decorate the outermost component if required. (This will be invoked after applying the
     * {@link View#decorateComponent()}.
     */
    public void decorateComponent() {
    }

    /**
     * Get the component that represents this template view. (This will be the outermost component).
     * This is equivalent to {@link View#getComponent()}.
     *
     * @return The outermost component.
     */
    public Component getComponent() {
        return getView(true).getComponent();
    }
}
