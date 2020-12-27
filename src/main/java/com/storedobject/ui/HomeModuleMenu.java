package com.storedobject.ui;

import com.storedobject.core.ApplicationModule;
import com.storedobject.vaadin.HomeView;

public class HomeModuleMenu extends ModuleMenu implements HomeView {

    public HomeModuleMenu(Application application) {
        super(application);
    }

    public HomeModuleMenu(Application application, String moduleName) {
        super(application, moduleName);
    }

    public HomeModuleMenu(Application application, ApplicationModule module) {
        super(application, module);
    }
}
