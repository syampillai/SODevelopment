package com.storedobject.ui;

import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.report.ObjectReport;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.PopupButton;
import com.vaadin.flow.component.Composite;

import java.util.List;
import java.util.function.Supplier;

public final class PrintButton extends Composite<Button> {

    private final Supplier<StoredObject> objectSupplier;
    private final Button button;

    private PrintButton(Supplier<StoredObject> objectSupplier, List<PrintLogicDefinition> logics) {
        this.objectSupplier = objectSupplier;
        if(logics.size() == 1) {
            button = new PButton(logics.get(0));
        } else {
            button = new PopupButton("Print");
            logics.forEach(d -> ((PopupButton) button).add(new PButton(d)));
        }
    }

    @Override
    protected Button initContent() {
        return button;
    }

    public static PrintButton create(ObjectEditor<?> objectEditor) {
        return create(objectEditor.getObjectClass(), objectEditor::getObject);
    }

    public static PrintButton create(ObjectGrid<?> objectGrid) {
        return create(objectGrid.getObjectClass(), objectGrid::selected);
    }

    public static PrintButton create(Class<? extends StoredObject> objectClass, Supplier<StoredObject> objectSupplier) {
        List<PrintLogicDefinition> list = PrintLogicDefinition.listFor(objectClass).toList();
        if(list.isEmpty()) {
            return null;
        }
        return new PrintButton(objectSupplier, list);
    }

    private static String iconName(PrintLogicDefinition printLogicDefinition) {
        String iconName = printLogicDefinition.getIconName();
        return iconName.isEmpty() ? printLogicDefinition.getLabel() : iconName;
    }

    private void clicked(PrintLogicDefinition printLogicDefinition) {
        StoredObject so = objectSupplier.get();
        Application a = Application.get();
        if(so != null && a != null) {
            new ObjectReport(a, printLogicDefinition, so);
        }
    }

    private class PButton extends Button {

        public PButton(PrintLogicDefinition printLogicDefinition) {
            super(printLogicDefinition.getLabel(), iconName(printLogicDefinition), e -> clicked(printLogicDefinition));
        }
    }
}
