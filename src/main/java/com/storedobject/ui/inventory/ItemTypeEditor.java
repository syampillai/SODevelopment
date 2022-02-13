package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.Quantity;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.QuantityField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;

public class ItemTypeEditor<T extends InventoryItemType> extends ObjectEditor<T> {

    private final List<HasValue<?, Quantity>> qFields = new ArrayList<>();
    private UOMField uomField;
    private Registration fc;

    public ItemTypeEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public ItemTypeEditor(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public ItemTypeEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public ItemTypeEditor(String className) throws Exception {
        super(className);
    }

    protected ItemTypeEditor(Class<T> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if(fc == null) {
            fc = addConstructedListener(f -> uomSetup());
        }
        if("UnitOfMeasurement".equals(fieldName)) {
            if(field instanceof UOMField) {
                uomField = (UOMField) field;
            }
        } else {
            if(field instanceof UOMField || field instanceof QuantityField) {
                //noinspection unchecked
                qFields.add((HasValue<?, Quantity>) field);
            }
        }
        super.customizeField(fieldName, field);
    }

    private void uomSetup() {
        if(uomField == null) {
            return;
        }
        qFields.forEach(f -> f.addValueChangeListener(e -> adjustUOM(f)));
        uomField.addValueChangeListener(e -> adjustUOM());
    }

    private void adjustUOM(HasValue<?, Quantity> field) {
        adjustUOM(field, uomField.getValue());
    }

    private void adjustUOM(HasValue<?, Quantity> field, Quantity u) {
        Quantity q = field.getValue();
        if(!q.isCompatible(u) || (q.isZero() && !(field instanceof UOMField))) {
            field.setValue(Quantity.create(q.getValue(), u.getUnit()));
        }
    }

    private void adjustUOM() {
        Quantity u = uomField.getValue();
        qFields.forEach(f -> adjustUOM(f, u));
    }
}
