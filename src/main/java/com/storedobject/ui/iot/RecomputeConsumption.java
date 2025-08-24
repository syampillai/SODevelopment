package com.storedobject.ui.iot;

import com.storedobject.iot.Block;
import com.storedobject.iot.Resource;

public class RecomputeConsumption extends AbstractConsumptionSelector {

    public RecomputeConsumption() {
        super("Recompute Consumption", null, null);
    }

    @Override
    protected void accept(Resource resource, Block block) throws Exception {
        block.recomputeConsumption(getTransactionManager(), resource);
        message(resource.getName() + " - Consumption recomputed for " + block.toDisplay());
    }
}
