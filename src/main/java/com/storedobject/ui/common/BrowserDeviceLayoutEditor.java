package com.storedobject.ui.common;

import com.storedobject.core.BrowserDeviceLayout;
import com.storedobject.ui.ObjectEditor;

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
}
