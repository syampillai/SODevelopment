package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialReturned;
import com.storedobject.core.MaterialReturnedItem;
import com.vaadin.flow.component.checkbox.Checkbox;

public abstract class AbstractReturnMaterial extends
        AbstractSendAndReceiveMaterial<MaterialReturned, MaterialReturnedItem> {

    public AbstractReturnMaterial(String from) {
        super(MaterialReturned.class, MaterialReturnedItem.class, from, false);
    }

    public AbstractReturnMaterial(InventoryLocation from) {
        this(from, null);
    }

    public AbstractReturnMaterial(InventoryLocation from, InventoryLocation otherLocation) {
        super(MaterialReturned.class, MaterialReturnedItem.class, from, false, otherLocation);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setFixedFilter("Status<2");
    }
}
