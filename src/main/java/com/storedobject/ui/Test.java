package com.storedobject.ui;

import com.storedobject.core.InventoryFitmentPosition;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionControl;

public class Test extends TextView implements Transactional {

    public Test() {
        super("Data Correction: Fitment");
        setProcessor(this::doUpdate);
    }

    private void doUpdate() {
        blackMessage("Processing...");
        TransactionControl tc = new TransactionControl(getTransactionManager());
        int count = 0;
        try(ObjectIterator<InventoryFitmentPosition> ps = StoredObject.list(InventoryFitmentPosition.class)) {
            for(InventoryFitmentPosition p: ps) {
                if(p.dataCorrection(tc)) {
                    if(tc.isActive()) {
                        if(!tc.commit()) {
                            tc.throwError();
                        }
                        if((++count % 10) == 0) {
                            blueMessage("Updated: " + count);
                        }
                    }
                } else {
                    tc.throwError();
                }
            }
        } catch(Throwable e) {
            log(e);
            redMessage(e);
        }
        blueMessage("Updated: " + count);
        blueMessage("Completed!");
    }
}
