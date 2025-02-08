package com.storedobject.ui.iot;

import com.storedobject.iot.*;
import com.storedobject.ui.ObjectComboField;

public abstract class AbstractConsumptionSelector extends BlockSelector {

    final ObjectComboField<Resource> resourceField = new ObjectComboField<>("Resource", Resource.class);

    public AbstractConsumptionSelector(String caption, Resource resource, Block block) {
        super(caption, block);
        addField(resourceField);
        if(resource != null) {
            resourceField.setValue(resource);
            setFieldReadOnly(resourceField);
        }
    }

    @Override
    protected final boolean accept(Block block) throws Exception {
        Resource resource = resourceField.getValue();
        if(resource == null) {
            warning("Please select a resource");
            return false;
        }
        close();
        accept(resource, block);
        return true;
    }

    protected abstract void accept(Resource resource, Block block) throws Exception;
}
