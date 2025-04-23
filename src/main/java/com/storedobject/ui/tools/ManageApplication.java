package com.storedobject.ui.tools;

import com.storedobject.common.Executable;
import com.storedobject.ui.Application;

public class ManageApplication implements Executable {

    @Override
    public void execute() {
        try {
            new ApplicationManager().execute();
        } catch(Throwable t) {
            Application.warning("Not running in a SO Platform environment!");
        }
    }
}