package com.storedobject.ui.tools;

import com.storedobject.common.Executable;
import com.storedobject.ui.Application;

public class Filler implements Executable {

    @Override
    public void execute() {
        Application.message("Not installed!");
    }
}