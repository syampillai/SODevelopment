package com.storedobject.ui.iot;

import com.storedobject.iot.Block;
import com.storedobject.iot.Resource;

public class ComputeConsumption extends AbstractConsumptionSelector {

    public ComputeConsumption() {
        super("Compute Consumption", null, null);
    }


    @Override
    protected void accept(Resource resource, Block block) throws Exception {
        block.computeConsumption(getTransactionManager(), resource);
        message(resource.getName() + " - Consumption computed for " + block.toDisplay());
    }
}
