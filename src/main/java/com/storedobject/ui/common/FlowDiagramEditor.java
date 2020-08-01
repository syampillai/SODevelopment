package com.storedobject.ui.common;

public class FlowDiagramEditor extends TextContentEditor {

    public FlowDiagramEditor() {
        super();
    }

    public FlowDiagramEditor(int actions) {
        super(actions);
    }

    public FlowDiagramEditor(int actions, String caption) {
        super(actions, caption);
    }

    public FlowDiagramEditor(String className) throws Exception {
        super(className);
    }
}