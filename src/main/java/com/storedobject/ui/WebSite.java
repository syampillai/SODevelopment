package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

public class WebSite extends View implements CloseableView {

    private final IFrame iframe = new IFrame();

    public WebSite() {
        this(null);
    }

    public WebSite(String site) {
        this(null, site);
    }

    public WebSite(String caption, String site) {
        setCaption(caption == null ? "Web Site" : caption);
        setSite(site);
        setComponent(iframe);
    }

    @Override
    public void decorateComponent() {
        super.decorateComponent();
        getComponent().getStyle().set("padding", "0px").set("display", "block");
    }

    public void setSite(String site) {
        if(site != null) {
            iframe.setSource(site);
        }
    }
}
