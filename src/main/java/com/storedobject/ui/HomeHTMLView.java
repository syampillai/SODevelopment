package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.storedobject.vaadin.HomeView;

public class HomeHTMLView extends HTMLView implements HomeView {

    public HomeHTMLView(Application application) {
        super(application);
    }

    public HomeHTMLView(Application application, String textContentName) {
        super(application, textContentName);
    }

    public HomeHTMLView(Application application, TextContent textContent) {
        super(application, textContent);
    }
}