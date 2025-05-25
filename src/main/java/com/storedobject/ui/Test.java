package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

public class Test extends View implements CloseableView {

    public Test() {
        super("Test");
        ModelViewer modelViewer = new ModelViewer(MediaCSS.mediaURL("test-model"));
        setComponent(modelViewer);
    }
}