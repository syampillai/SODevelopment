package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class ScrollingContent extends Div {

    private final Div headerContainer = new Div();
    private final Div container = new Div();
    private int margin;

    public ScrollingContent() {
        this(null, null);
    }

    public ScrollingContent(Component content) {
        this(null, content);
    }

    public ScrollingContent(Component header, Component content) {
        setMargin(12);
        container.getStyle()
                .set("flex", "1")
                .set("overflow", "auto");
        headerContainer.getStyle().set("display", "flex");
        headerContainer.setWidthFull();
        setHeaderHeight(64);
        container.add(content);
        getStyle()
                .set("background", "#F0EFED")
                .set("height", "100%")
                .set("min-height", "0")
                .set("min-width", "0")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("overflow", "hidden");
        setHeader(header);
        add(headerContainer, container);
    }

    public Component getContent() {
        return container.getComponentCount() > 0 ? container.getComponentAt(0) : null;
    }

    public void setContent(Component content) {
        container.removeAll();
        if(content != null) {
            container.add(content);
        }
    }

    public Component getHeader() {
        return headerContainer.getComponentCount() > 0 ? headerContainer.getComponentAt(0) : null;
    }

    public void setHeader(Component header) {
        headerContainer.removeAll();
        if(header != null) {
            headerContainer.add(header);
        }
    }

    public void setHeaderHeight(int headerHeight) {
        headerContainer.getStyle().set("flex", "0 0 " + headerHeight + "px");
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        margin = Math.max(0, margin);
        this.margin = margin;
        container.getStyle().set("margin", margin + "px");
        headerContainer.getStyle().set("margin", margin + "px " + margin + "px 0px " + margin + "px");
    }
}
