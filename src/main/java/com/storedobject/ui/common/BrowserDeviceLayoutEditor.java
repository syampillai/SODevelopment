package com.storedobject.ui.common;

import com.storedobject.core.BrowserDeviceLayout;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.util.SOServlet;

public class BrowserDeviceLayoutEditor extends ObjectEditor<BrowserDeviceLayout> {

    public BrowserDeviceLayoutEditor() {
        super(BrowserDeviceLayout.class);
    }

    public BrowserDeviceLayoutEditor(int actions) {
        super(BrowserDeviceLayout.class, actions);
    }

    public BrowserDeviceLayoutEditor(int actions, String caption) {
        super(BrowserDeviceLayout.class, actions, caption);
    }

    public BrowserDeviceLayoutEditor(String className) throws Exception {
        super(className);
    }

    @Override
    public void saved(BrowserDeviceLayout object) {
        super.saved(object);
        String name = object.getLoginImageName();
        if(!name.isEmpty()) {
            SOServlet.removeCache(name);
        }
    }
}
