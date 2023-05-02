package com.storedobject.ui;

import com.storedobject.core.ApplicationModule;

public class Test extends ModuleMenu {

    public Test(Application application) {
        super(application);
    }

    public Test(Application application, String moduleName) {
        super(application, moduleName);
    }

    public Test(Application application, ApplicationModule module) {
        super(application, module);
    }

    @Override
    public int getSize() {
        return 200;
    }

    @Override
    public String getFontSize() {
        return "smaller";
    }
}