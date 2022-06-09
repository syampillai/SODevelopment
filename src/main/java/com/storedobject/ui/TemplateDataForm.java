package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.StringUtility;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.template.Id;

import java.util.function.Supplier;

/**
 * This is equivalent of the {@link DataForm} class except that the content is based on HTML-template. However,
 * it doesn't support "window mode" because the whole UI part is controlled by the HTML template. Two "Id" values
 * ("Ok" and "Cancel" buttons) must be mapped and must use the vaadin-button tags. Also, there is no concept of
 * "button panel" as in {@link DataForm}. All required fields should be defined using "Id" annotations and
 * {@link #createComponentForId(String)} because methods
 * {@link #addField(String, HasValue)} and {@link #addField(HasValue[])} do not work.
 *
 * @author Syam
 */
public abstract class TemplateDataForm extends AbstractDataForm<Object> {

    /**
     * The Ok button.
     */
    @Id
    protected Button ok;
    /**
     * The Cancel button.
     */
    @Id
    protected Button cancel;
    private final TemplateLayout templateLayout;

    public TemplateDataForm() {
        this(null, (TemplateLayout) null);
    }

    public TemplateDataForm(String caption, String textContentName) {
        this(caption, new TemplateLayout(textContentName));
    }

    public TemplateDataForm(String caption, TextContent textContent) {
        this(caption, new TemplateLayout(textContent));
    }

    public TemplateDataForm(Supplier<String> contentSupplier) {
        this(null, contentSupplier);
    }

    public TemplateDataForm(String caption, Supplier<String> contentSupplier) {
        this(caption, new TemplateLayout(contentSupplier));
    }

    private TemplateDataForm(String caption, TemplateLayout templateLayout) {
        if(templateLayout == null) {
            String name = getClass().getName();
            TextContent tc = SOServlet.getTextContent(name);
            if(tc == null) {
                throw new SORuntimeException(name + " - Template not found!");
            }
            templateLayout = new TemplateLayout(tc);
        }
        this.templateLayout = templateLayout;
        templateLayout.setView(this);
        templateLayout.setComponentCreator(this::componentForId);
        this.form = new Form();
        this.form.setView(this);
        setErrorDisplay(null);
        if(caption == null) {
            caption = Application.getLogicCaption(StringUtility.makeLabel(getClass()));
        }
        setCaption(caption);
    }

    private Component componentForId(String id) {
        return switch(id) {
            case "ok" -> new Button(null, (String) null, e -> clicked(ok));
            case "cancel" -> new Button(null, (String) null, e -> clicked(cancel));
            default -> forId(id);
        };
    }

    private Component forId(String id) {
        Component c = createComponentForId(id);
        if(c instanceof HasValue<?,?> field) {
            super.addField(getFieldNameForId(id), field);
        }
        return c;
    }

    /**
     * Create a component for the given Id value.
     *
     * @param id Id for which a mapping component needs to be added.
     * @return Mapped component.
     */
    protected Component createComponentForId(String id) {
        return null;
    }

    /**
     * Get the name of the field. Name of the field is used when displaying error/informational messages.
     *
     * @param id Id for which the component was mapped.
     * @return Name.
     */
    protected String getFieldNameForId(String id) {
        return id;
    }

    @Override
    protected final void initUI() {
        setComponent(templateLayout);
        templateLayout.build();
    }

    @Override
    public final void setWindowMode(boolean windowOn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean skipFirstFocus(Focusable<?> skipFocus) {
        return skipFocus == ok || skipFocus == cancel;
    }

    /**
     * This method is invoked if the "Cancel" button is pressed. Default action closes the "data entry screen".
     */
    protected void cancel() {
        abort();
    }

    /**
     * This method is invoked if the "Ok" button is pressed and there is no error raised by {@link #validateData()}
     * method. This method has to return <code>true</code> in order to close the "data entry screen".
     * @return True or false.
     */
    protected abstract boolean process();

    @Override
    public void clicked(Component c) {
        if(c == cancel) {
            cancel();
            return;
        }
        if(c == ok) {
            if(commit()) {
                try {
                    validateData();
                    if(process()) {
                        close();
                    }
                } catch (Exception e) {
                    warning(e);
                }
            }
            ok.setEnabled(true);
            ok.setDisableOnClick(true);
        }
    }

    /**
     * Validate data. This method is invoked when the "Ok" button is pressed. {@link #process()} will be invoked only if this
     * method did not raise any exception.
     * @throws Exception Exception raised will be displayed as a warning message.
     */
    @SuppressWarnings("RedundantThrows")
    protected void validateData() throws Exception {
    }

    @Override
    protected final void attachField(String fieldName, HasValue<?, ?> field) {
    }

    @Override
    public final void addField(HasValue<?, ?>... fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void addField(String fieldName, HasValue<?, ?> field) {
        throw new UnsupportedOperationException();
    }

    /**
     * Center this {@link View} on the screen.
     */
    public void center() {
        templateLayout.center();
    }
}
