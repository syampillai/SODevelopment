package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.TransactionManager;

public abstract class Executor implements Executable {

    protected final Application application;

    public Executor(Application a) {
        this.application = a;
    }

    public final Application getApplication() {
        return application;
    }

    public final TransactionManager getTransactionManager() {
        return application.getTransactionManager();
    }
}