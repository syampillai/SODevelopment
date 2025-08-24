package com.storedobject.ui.iot;

import com.storedobject.iot.Block;

public class RecomputeStatistics extends BlockSelector {

    public RecomputeStatistics() {
        super("Recompute Statistics");
    }

    @Override
    protected boolean accept(Block block) throws Exception {
        close();
        block.recomputeStatistics(getTransactionManager());
        return true;
    }
}
