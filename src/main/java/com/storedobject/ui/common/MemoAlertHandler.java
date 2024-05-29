package com.storedobject.ui.common;

import com.storedobject.core.Memo;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.AlertHandler;

public class MemoAlertHandler implements AlertHandler {

    @Override
    public void handleAlert(StoredObject so) {
        if(so instanceof Memo m) {
            new MemoSystem(m.getType()).handleAlert(m);
        }
    }
}
