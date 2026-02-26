package com.storedobject.ui;

import com.vaadin.flow.component.html.Div;

public class CardGrid extends Div {

    private CardDashboard dashboard;
    private int cardWidth, gap;

    CardGrid() {
        getStyle()
                .set("display", "grid")
                .set("justify-content", "stretch")
                .set("align-items", "start")
                .set("grid-auto-rows", "minmax(50px, auto)")
                .set("width", "100%");
        setGap(16);
        setCardWidth(350);
    }

    public void setGap(int gap) {
        this.gap = Math.max(0, gap);
        getStyle().set("gap", this.gap + "px");
    }

    public int getGap() {
        return gap;
    }

    public void setCardWidth(int cardWidth) {
        if(cardWidth < 1) cardWidth = 350;
        this.cardWidth = cardWidth;
        getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(0," + cardWidth + "px))");
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public void setDashboard(CardDashboard dashboard) {
        this.dashboard = dashboard;
    }

    public CardDashboard getDashboard() {
        return dashboard;
    }
}
