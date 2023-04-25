package com.storedobject.ui;

import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFObjectReport;
import com.storedobject.report.ObjectReport;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.PopupButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A "print button" is created automatically by {@link ObjectEditor}s and
 * {@link ObjectBrowser}s if one or more {@link PrintLogicDefinition}s exist for the {@link StoredObject} class. The
 * "print button" may be a single button if there is only one {@link PrintLogicDefinition} defined, or it can contain
 * multiple buttons.
 * <p>The most common use-case of {@link PrintButton} is to define logic for printing. For defining such logic, either
 * the {@link com.storedobject.pdf.PDFObjectReport} or the {@link com.storedobject.office.ODTObjectReport} may be
 * extended. If the {@link com.storedobject.office.ODTObjectReport} is extended, the ODT template may be specified
 * in the {@link PrintLogicDefinition}.</p>
 * <p>The "printing logic" that is defined in the {@link PrintLogicDefinition} should have a constructor that
 * takes a {@link com.storedobject.core.Device} and a {@link StoredObject} instance as its parameters. (Have a look at
 * the constructors - {@link PDFObjectReport#PDFObjectReport(Device, StoredObject)} and
 * {@link com.storedobject.office.ODTObjectReport#ODTObjectReport(Device, StoredObject)}</p>
 * <p><b>Usage as a generic button:</b> {@link PrintButton} may be used to invoke non-printing logic too by defining
 * any generic logic in the {@link PrintLogicDefinition}. The logic must implement
 * {@link Runnable} and must have a constructor that takes a
 * {@link com.storedobject.core.Device} and a {@link StoredObject} instance as its parameters.</p>
 * <p>In {@link ObjectEditor}, by default, the {@link PrintButton} will be hidden if the object instance is null.
 * However, you can control this behaviour and control the visibility of it or its individual buttons by overriding
 * the {@link ObjectEditor#enablePrintButtons(boolean)} method.</p>
 *
 * @author Syam
 */
public final class PrintButton extends Composite<Button> {

    private final Supplier<StoredObject> objectSupplier;
    private final Button button;
    private final Map<String, PButton> buttons = new HashMap<>();

    private PrintButton(Supplier<StoredObject> objectSupplier, List<PrintLogicDefinition> logics, List<Component> extras) {
        this.objectSupplier = objectSupplier;
        if(logics.size() == 1 && extras == null) {
            button = new PButton(logics.get(0));
        } else {
            button = new PopupButton("More", VaadinIcon.LINES);
            logics.forEach(d -> ((PopupButton) button).add(new PButton(d)));
            if(extras != null) {
                extras.forEach(ec -> ((PopupButton) button).add(ec));
            }
        }
    }

    @Override
    protected Button initContent() {
        return button;
    }

    /**
     * Create a "print button" for object editor.
     *
     * @param objectEditor Object editor for which the button to be created.
     * @return Print button.
     */
    public static PrintButton create(ObjectEditor<?> objectEditor) {
        return create(objectEditor.getObjectClass(), objectEditor::getObject, objectEditor.getClass().getName());
    }

    /**
     * Create a "print button" for object grid.
     *
     * @param objectGrid Object grid for which the button to be created.
     * @return Print button.
     */
    public static PrintButton create(ObjectGrid<?> objectGrid) {
        return create(objectGrid.getObjectClass(), objectGrid::selected, objectGrid.getClass().getName());
    }

    /**
     * Create a "print button" for object supplier.
     *
     * @param objectClass Object class of the object supplier.
     * @param objectSupplier Object supplier for which the button to be created.
     * @return Print button.
     */
    public static PrintButton create(Class<? extends StoredObject> objectClass, Supplier<StoredObject> objectSupplier) {
        return create(objectClass, objectSupplier, null);
    }

    private static PrintButton create(Class<? extends StoredObject> objectClass, Supplier<StoredObject> objectSupplier,
                                      String dataLogicName) {
        List<Component> extras = objectSupplier instanceof ObjectBrowser<?> b ? b.listMoreButtons() : null;
        if(extras != null && extras.isEmpty()) {
            extras = null;
        }
        List<PrintLogicDefinition> list = PrintLogicDefinition.listFor(objectClass, dataLogicName).toList();
        if(list.isEmpty() && extras == null) {
            return null;
        }
        return new PrintButton(objectSupplier, list, extras);
    }

    private static String iconName(PrintLogicDefinition printLogicDefinition) {
        String iconName = printLogicDefinition.getIconName();
        return iconName.isEmpty() ? printLogicDefinition.getLabel() : iconName;
    }

    private void clicked(PrintLogicDefinition printLogicDefinition) {
        Application a = Application.get();
        if(a != null) {
            StoredObject so = objectSupplier.get();
            if(so != null) {
                if(new ObjectReport(a, printLogicDefinition, so).getRunnable() == null) {
                    Application.error("An error has occurred, please contact the Technical Support");
                }
            }
        }
    }

    /**
     * Get the button corresponding to the label passed. (This could be used from
     * {@link ObjectEditor#enablePrintButtons(boolean)} to selectively hide buttons.
     *
     * @param label Label of the button.
     * @return Button.
     */
    public Button getButton(String label) {
        return buttons.get(label);
    }

    public Stream<PrintLogicDefinition> definitions() {
        return buttons.values().stream().map(pb -> pb.definition);
    }

    private class PButton extends Button {

        private final PrintLogicDefinition definition;

        public PButton(PrintLogicDefinition printLogicDefinition) {
            super(printLogicDefinition.getLabel(), iconName(printLogicDefinition), e -> clicked(printLogicDefinition));
            definition = printLogicDefinition;
            buttons.put(printLogicDefinition.getLabel(), this);
        }
    }
}
