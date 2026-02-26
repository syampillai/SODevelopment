package com.storedobject.ui;

import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A custom field implementation for managing and editing text comments.
 * The field includes a popover for editing values, with an icon that indicates
 * the state of the field and buttons for saving or cancelling changes.
 *
 * @author Syam
 */
public class CommentField extends CustomField<String> {

    /**
     * Represents the layout container for managing and organizing buttons within the CommentField component.
     * This layout is used to house and structure interactive elements like "ok" and "cancel" buttons.
     * It ensures a consistent arrangement of buttons when interacting with the component's UI.
     * The layout is immutable and is initialized when the CommentField is instantiated.
     */
    protected final ButtonLayout buttonLayout = new ButtonLayout();
    /**
     * The "OK" button used within the CommentField component.
     * This button is typically associated with a confirmation or save action
     * for the input provided in the component.
     */
    protected Button ok;
    /**
     * Represents the cancel button in the CommentField component.
     * This variable is used to handle the cancellation events within the
     * comment editing functionality. It allows users to discard any
     * unsaved changes they make to the comment field.
     */
    protected Button cancel;
    private final TextArea textArea = createTextArea();
    private final Icon icon = VaadinIcon.FILE_TEXT.create();
    private final Popover popover = new Pop();
    private List<Consumer<CommentField>> savedListeners, cancelledListeners;

    /**
     * Constructs a new instance of the CommentField with no initial label or value.
     * This constructor initializes the field with a null label and an empty value.
     */
    public CommentField() {
        this(null);
    }

    /**
     * Constructs a CommentField with a specified label.
     *
     * @param label the label to set for the comment field; a null value will not set a label
     */
    public CommentField(String label) {
        this(label, "");
    }

    /**
     * Constructs a CommentField instance with a specified label and initial value.
     *
     * @param label the label to display for the CommentField. If null, no label is set.
     * @param value the initial value of the CommentField. If blank or null, the icon color will be grey.
     */
    public CommentField(String label, String value) {
        super("");
        setValue(value);
        if (label != null) {
            setLabel(label);
        }
        icon.setColor("grey");
        icon.getStyle().set("cursor", "pointer");
        add(icon);
        configurePopover();
        addValueChangeListener(e -> icon.setColor(e.getValue().isBlank() ? "grey" : Application.COLOR_SUCCESS));
    }

    @Override
    protected String generateModelValue() {
        return textArea.getValue();
    }

    @Override
    protected void setPresentationValue(String s) {
        textArea.setValue(s);
    }

    private TextArea createTextArea() {
        TextArea area = new TextArea();
        area.setWidthFull();
        area.setHeight("150px");
        return area;
    }

    private VerticalLayout createPopoverContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidth("320px");
        layout.add(textArea, buttonLayout);
        return layout;
    }

    /**
     * Builds and initializes the "OK" and "Cancel" buttons for the CommentField.
     * <pre>
     * The "OK" button is configured to:
     * - Close the associated popover when clicked.
     * - Trigger the `updateValue` method to update the field's value.
     * - Notify all registered saved listeners by invoking their `accept` method with the current instance.
     *
     * The "Cancel" button is configured to:
     * - Close the associated popover when clicked.
     * - Notify all registered cancelled listeners by invoking their `accept` method with the current instance.
     *</pre>
     * Both buttons are styled, with the "OK" button being a primary button and both buttons rendered in a small size.
     * The buttons are added to the `buttonLayout` of the field.
     */
    protected void buildButtons() {
        ok = new Button("OK", e -> {
            popover.close();
            updateValue();
            if(savedListeners != null) {
                savedListeners.forEach(l -> l.accept(this));
            }
        });
        ok.asPrimary().asSmall();
        cancel = new Button("Cancel", e -> {
            popover.close();
            if(cancelledListeners != null) {
                cancelledListeners.forEach(l -> l.accept(this));
            }
        }).asSmall();
        buttonLayout.add(ok, cancel);
    }

    private void configurePopover() {
        icon.getElement().executeJs(
                "this.addEventListener('click', function(e) { e.stopPropagation(); });"
        );
        popover.setTarget(icon);
        popover.setOpenOnClick(true);
        popover.setCloseOnOutsideClick(true);
        popover.setCloseOnEsc(true);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.addThemeVariants(PopoverVariant.ARROW);
        popover.setModal(false);
        popover.setAutofocus(true);
        popover.add(createPopoverContent());
        popover.addOpenedChangeListener(e -> {
            if(e.isOpened()) {
                textArea.setValue(getValue());
                textArea.focus();
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        textArea.setReadOnly(readOnly || !isEnabled());
        cancel.setVisible(isEnabled() && !readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textArea.setEnabled(isReadOnly() || enabled);
        cancel.setVisible(enabled && !isReadOnly());
    }

    /**
     * Registers a listener that will be invoked when a comment is saved.
     * The provided listener is triggered whenever the "save" action is performed
     * on this comment field. The listener consumes the current instance of
     * {@code CommentField}.
     *
     * @param listener the {@code Consumer} to be invoked when the comment is saved;
     *                 must not be null
     * @return a {@code Registration} object that can be used to remove the listener
     */
    public Registration addSavedListener(Consumer<CommentField> listener) {
        if(savedListeners == null) {
            savedListeners = new ArrayList<>();
        }
        savedListeners.add(listener);
        return () -> savedListeners.remove(listener);
    }

    /**
     * Registers a listener to be notified when the "Cancel" button is clicked in the CommentField.
     *
     * @param listener a {@link Consumer} that accepts the current instance of {@code CommentField},
     *                 invoked when the "Cancel" button is clicked.
     * @return a {@link Registration} object that can be used to remove the registered listener.
     */
    public Registration addCancelledListener(Consumer<CommentField> listener) {
        if(cancelledListeners == null) {
            cancelledListeners = new ArrayList<>();
        }
        cancelledListeners.add(listener);
        return () -> cancelledListeners.remove(listener);
    }

    private class Pop extends Popover {

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            if(ok == null && cancel == null) {
                buildButtons();
            }
            super.onAttach(attachEvent);
        }
    }
}
