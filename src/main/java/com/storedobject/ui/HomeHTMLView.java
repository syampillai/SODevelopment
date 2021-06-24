package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.storedobject.vaadin.HomeView;

public class HomeHTMLView extends HTMLView implements HomeView {

    public HomeHTMLView(Application application) {
        super(application);
    }

    public HomeHTMLView(String textContentName) {
        super(textContentName);
    }

    public HomeHTMLView(TextContent textContent) {
        super(textContent);
    }
}