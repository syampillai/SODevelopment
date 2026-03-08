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
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
 * <p>Yet another feature is to define a class extending {@link ObjectLogicButton} where
 * the desired logic is implemented in the {@link ObjectLogicButton#accept(StoredObject, Object)} method.</p>
 * <p>In {@link ObjectEditor}, by default, the {@link PrintButton} will be hidden if the object instance is null.
 * However, you can control this behavior and control the visibility of it or its individual buttons by overriding
 * the {@link ObjectEditor#enablePrintButtons(boolean)} method.</p>
 *
 * @author Syam
 */
public final class PrintButton<T extends StoredObject> extends Composite<Button> {

    private final Supplier<T> objectSupplier;
    private final Object objectSource;
    private final Button button;
    private final Map<String, Button> buttons = new HashMap<>();

    private PrintButton(Class<T> objectClass, Object objectSource, Supplier<T> objectSupplier, List<PrintLogicDefinition> logics, List<Component> extras) {
        this.objectSupplier = objectSupplier;
        this.objectSource = objectSource;
        if(logics.size() == 1 && extras == null) {
            button = createButton(objectClass, logics.getFirst());
        } else {
            button = new PopupButton("More", VaadinIcon.LINES);
            logics.forEach(d -> ((PopupButton) button).add(createButton(objectClass, d)));
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
     * Create a "print button" for the object editor.
     *
     * @param objectEditor Object editor for which the button to be created.
     * @return Print button.
     */
    public static <O extends StoredObject> PrintButton<O> create(ObjectEditor<O> objectEditor) {
        return create(objectEditor.getObjectClass(), objectEditor, objectEditor::getObject, objectEditor.getClass().getName());
    }

    /**
     * Create a "print button" for the object grid.
     *
     * @param objectGrid Object grid for which the button to be created.
     * @return Print button.
     */
    public static <O extends StoredObject> PrintButton<O> create(ObjectGrid<O> objectGrid) {
        return create(objectGrid.getObjectClass(), objectGrid, objectGrid::selected, objectGrid.getClass().getName());
    }

    /**
     * Create a "print button" for the object supplier.
     *
     * @param objectClass Object class of the object supplier.
     * @param objectSupplier Object supplier for which the button to be created.
     * @return Print button.
     */
    public static <O extends StoredObject> PrintButton<O> create(Class<O> objectClass, Supplier<O> objectSupplier) {
        return create(objectClass, null, objectSupplier, null);
    }

    private static <O extends StoredObject> PrintButton<O> create(Class<O> objectClass, Object objectSource, Supplier<O> objectSupplier,
                                      String dataLogicName) {
        List<Component> extras = objectSupplier instanceof ObjectBrowser<?> b ? b.listMoreButtons() : null;
        if(extras != null && extras.isEmpty()) {
            extras = null;
        }
        List<PrintLogicDefinition> list = PrintLogicDefinition.listFor(objectClass, dataLogicName).toList();
        if(list.isEmpty() && extras == null) {
            return null;
        }
        return new PrintButton<>(objectClass, objectSource, objectSupplier, list, extras);
    }

    private static String iconName(PrintLogicDefinition printLogicDefinition) {
        String iconName = printLogicDefinition.getIconName();
        return iconName.isEmpty() ? printLogicDefinition.getLabel() : iconName;
    }

    private void clicked(PrintLogicDefinition printLogicDefinition) {
        Application a = Application.get();
        if(a != null) {
            if(new ObjectReport(a, printLogicDefinition, objectSource, objectSupplier.get()).getRunnable() == null) {
                Application.error("An error has occurred, please contact the Technical Support");
            }
        }
    }

    /**
     * Get the button corresponding to the label passed. This could be used from
     * {@link ObjectEditor#enablePrintButtons(boolean)} to selectively hide buttons.
     *
     * @param label Label of the button.
     * @return Button.
     */
    public Button getButton(String label) {
        return buttons.get(label);
    }

    public Stream<PrintLogicDefinition> definitions() {
        return buttons.values().stream().map(this::def);
    }

    private PrintLogicDefinition def(Button b) {
        return (b instanceof PButton pb) ? pb.definition : ((ObjectLogicButton<?>)b).definition;
    }

    private static class PButton extends Button {

        private final PrintLogicDefinition definition;

        public PButton(PrintLogicDefinition printLogicDefinition) {
            super(printLogicDefinition.getLabel(), iconName(printLogicDefinition), null);
            definition = printLogicDefinition;
        }
    }

    private <O extends StoredObject> Button createButton(Class<T> objectClass, PrintLogicDefinition printLogicDefinition) {
        Class<?> lc = printLogicDefinition.getLogicClass();
        if(lc == null) {
            return errorButton(printLogicDefinition, null);
        }
        if(!ObjectLogicButton.class.isAssignableFrom(lc)) {
            Button b = new PButton(printLogicDefinition);
            b.addClickListener(e -> clicked(printLogicDefinition));
            buttons.put(printLogicDefinition.getLabel(), b);
            return b;
        }
        ObjectLogicButton<?> ob = createLogicButton(objectClass, lc);
        if(ob == null) {
            return errorButton(printLogicDefinition, null);
        }
        if(!ob.getObjectClass().isAssignableFrom(objectClass)) {
            return errorButton(printLogicDefinition, "Logic class " + lc.getName() + " does not support "
                    + objectClass.getName());
        }
        @SuppressWarnings("unchecked") ObjectLogicButton<O> finalOb = (ObjectLogicButton<O>) ob;
        ob.listem(e -> {
            @SuppressWarnings("unchecked") O object = (O)objectSupplier.get();
            if(object != null) {
                finalOb.accept(object, objectSource);
            }
        });
        String label = printLogicDefinition.getLabel();
        ob.setText(label);
        label = printLogicDefinition.getIconName();
        if(!label.isEmpty()) {
            ob.setIcon(label);
        }
        ob.definition = printLogicDefinition;
        buttons.put(label, ob);
        return ob;
    }

    private static <T extends StoredObject> @Nullable ObjectLogicButton<?> createLogicButton(Class<T> objectClass, Class<?> lc) {
        ObjectLogicButton<?> ob = null;
        Class<?> oc = objectClass;
        Constructor<?> c = null;
        while(true) {
            try {
                c = lc.getConstructor(oc);
                ob = (ObjectLogicButton<?>) c.newInstance(oc);
                break;
            } catch (NoSuchMethodException
                     | InstantiationException
                     | IllegalAccessException
                     | InvocationTargetException ignored) {
            }
            if(oc == StoredObject.class) break;
            oc = oc.getSuperclass();
        }
        if(c == null) {
            try {
                c = lc.getConstructor();
                ob = (ObjectLogicButton<?>) c.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return ob;
    }

    private ObjectLogicButton<StoredObject> errorButton(PrintLogicDefinition printLogicDefinition, String message) {
        if(message == null) {
            message = "Unable to create logic " + printLogicDefinition.getPrintLogicClassName();
        }
        String finalMessage = message;
        var b = new ObjectLogicButton<>(StoredObject.class) {

            @Override
            public void accept(StoredObject object, Object source) {
                Application.warning(finalMessage);
            }
        };
        String label = printLogicDefinition.getLabel();
        b.setText(label);
        buttons.put(label, b);
        b.definition = printLogicDefinition;
        return b;
    }
}
