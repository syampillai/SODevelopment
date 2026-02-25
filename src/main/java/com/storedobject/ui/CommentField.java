package com.storedobject.ui;

import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;

public class CommentField extends CustomField<String> {

    private final TextArea textArea = createTextArea();
    private final Icon icon = VaadinIcon.FILE_TEXT.create();
    private final Popover popover = new Popover();
    private Button cancel;

    public CommentField() {
        this(null);
    }

    public CommentField(String label) {
        this(label, "");
    }

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
        layout.add(textArea, createButtonLayout());
        return layout;
    }

    private ButtonLayout createButtonLayout() {
        Button ok = new Button("OK", e -> {
            popover.close();
            updateValue();
        });
        ok.asPrimary().asSmall();
        cancel = new Button("Cancel", e -> popover.close()).asSmall();
        return new ButtonLayout(ok, cancel);
    }

    private void configurePopover() {
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
}
