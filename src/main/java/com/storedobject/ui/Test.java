package com.storedobject.ui;

import com.storedobject.core.InventoryItem;
import com.storedobject.ui.inventory.AssemblyItemField;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements CloseableView {

    private final AssemblyItemField<InventoryItem> aiField;
    private final ObjectField<InventoryItem> aField;

    public Test() {
        super("Test", false);
        aiField = new AssemblyItemField<>("Item", InventoryItem.class, true);
        aField = new ObjectField<>("Parent Assembly", InventoryItem.class, true);
        aField.addValueChangeListener(e -> aiField.setAssembly(aField.getObject()));
        addField(aField, aiField);
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        buttonPanel.add(new Button("Test R/O", e -> {
            aField.setReadOnly(!aField.isReadOnly());
            aiField.setReadOnly(!aiField.isReadOnly());
        }));
        buttonPanel.add(new Button("Test E/D", e -> {
            aField.setEnabled(!aField.isEnabled());
            aiField.setEnabled(!aiField.isEnabled());
        }));
    }

    @Override
    protected boolean process() {
        return false;
    }
}
