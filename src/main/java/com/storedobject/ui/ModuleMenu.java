package com.storedobject.ui;

import com.storedobject.core.ApplicationModule;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

public class ModuleMenu extends View implements CloseableView {

    public ModuleMenu(Application application) {
        this(application, application.getLogicTitle(null));
    }

    public ModuleMenu(Application application, String moduleName) {
        this(application, (ApplicationModule)null);
    }

    public ModuleMenu(Application application, ApplicationModule module) {
    }
}
