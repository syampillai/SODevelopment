package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;

public class Card extends Div {

    public Card() {
        getStyle()
                .set("border-radius", "12px")
                .set("padding", "16px")
                .set("background", "white")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("margin", "0")
                .set("align-self", "stretch")
                .set("gap", "8px");
    }

    public void alignTop() {
        getStyle().set("align-items", "start");
    }

    public void alignBottom() {
        getStyle().set("align-items", "end");
    }

    public void alignCenter() {
        getStyle().set("align-items", "center");
    }

    public void justifyLeft() {
        getStyle().set("justify-items", "start");
    }

    public void justifyRight() {
        getStyle().set("justify-items", "end");
    }

    public void justifyCenter() {
        getStyle().set("justify-items", "center");
    }

    public void setColumnSpan(int span) {
        getStyle().set("grid-column", "span " + span);
    }

    public void setRowSpan(int span) {
        getStyle().set("grid-row", "span " + span);
    }

    public static class Cell extends Div {

        public Cell(Component... components) {
            this(-1, components);
        }

        public Cell(int gap, Component... components) {
            this(gap, true, components);
        }

        Cell(int gap, boolean column, Component... components) {
            super(components);
            getStyle().set("display", "flex").set("justify-content", "space-between");
            getStyle().set("flex-direction", column ? "column" : "row");
            setGap(gap);
        }

        public void setGap(int gap) {
            getStyle().set("gap", (gap < 0 ? 4 : gap) + "px");
        }


        public void alignTop() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "start");
        }

        public void alignBottom() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "end");
        }

        public void alignCenter() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "center");
        }

        public void justifyLeft() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "start");
        }

        public void justifyRight() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "end");
        }

        public void justifyCenter() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "center");
        }
    }

    public static class Column extends Cell {

        public Column(Component... components) {
            this(-1, components);
        }

        public Column(int gap, Component... components) {
            super(gap, true, components);
        }
    }

    public static class Row extends Cell {

        public Row(Component... components) {
            this(-1, components);
        }

        public Row(int gap, Component... components) {
            super(gap, false, components);
        }
    }

    public static class Line extends Hr {

        public Line() {
            getStyle()
                    .set("border", "none")
                    .set("border-top", "1px solid #e0e0e0")
                    .set("height", "3px")
                    .set("margin", "8px 0");
        }

        public void setColor(String color) {
            getStyle().set("background-color", color);
        }
    }
}
