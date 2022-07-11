package com.storedobject.ui.common;

import com.storedobject.core.MemoType;
import com.storedobject.ui.ObjectEditor;
import com.vaadin.flow.component.Component;

public class MemoTypeEditor extends ObjectEditor<MemoType> {

    public MemoTypeEditor() {
        super(MemoType.class);
        addConstructedListener(f -> setColumnSpan((Component) getField("ContentTemplate"), 2));
    }

    public MemoTypeEditor(String className) {
        this();
    }
}
