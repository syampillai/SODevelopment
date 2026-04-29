package com.storedobject.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Svg;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for creating dashlet components.
 *
 * @author Syam
 */
public class Dashlet {

    private Dashlet() {}

    public static class Plot extends Composite<Svg> {

        private final Svg svg = new Svg();
        private final int xGap = 35;

        public Plot(int xCount, int yCount) {
        }

        @Override
        protected Svg initContent() {
            return svg;
        }
    }

    protected abstract static class SVGElement {

        final Map<String, String> attributes = new HashMap<>();
        final String tag;

        protected SVGElement(String tag) {
            this.tag = tag;
        }

        protected String getContent() {
            return null;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("<").append(tag);
            for(Map.Entry<String, String> e: attributes.entrySet()) {
                s.append(" ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
            }
            String content = getContent();
            if(content == null) {
                s.append(" />");
            } else {
                s.append(">");
                s.append(content);
                s.append("</").append(tag).append(">");
            }
            return s.toString();
        }
    }

    protected static class Line extends SVGElement {

        private Line(int x1, int y1, int x2, int y2, String stroke) {
            super("line");
            attributes.put("x1", String.valueOf(x1));
            attributes.put("y1", String.valueOf(y1));
            attributes.put("x2", String.valueOf(x2));
            attributes.put("y2", String.valueOf(y2));
            attributes.put("stroke", stroke);
        }
    }

    protected static class Circle extends SVGElement {

        private Circle(int cx, int cy, int r, String stroke) {
            super("circle");
            attributes.put("cx", String.valueOf(cx));
            attributes.put("cy", String.valueOf(cy));
            attributes.put("r", String.valueOf(r));
            attributes.put("stroke", stroke);
        }
    }

    protected static class CirclePoint extends Circle {

        private CirclePoint(int cx, int cy, String stroke) {
            super(cx, cy, 3, stroke);
        }
    }

    protected static class Text extends SVGElement {

        private final String text;

        private Text(int x, int c, String text) {
            super("text");
            attributes.put("x", String.valueOf(x));
            attributes.put("y", String.valueOf(c));
            this.text = text;
        }

        @Override
        protected String getContent() {
            return text;
        }
    }

    protected static class Polyline extends SVGElement {

        protected Polyline(String stroke) {
            this(stroke, 2);
        }

        protected Polyline(String stroke, int strokeWidth) {
            this(stroke, strokeWidth, "none");
        }

        protected Polyline(String stroke, int strokeWidth, String fill) {
            super("polyline");
            attributes.put("stroke", stroke);
            attributes.put("stroke-width", String.valueOf(strokeWidth));
            attributes.put("fill", fill);
        }

        public void addPoint(int x, int y) {
            String points = attributes.get("points");
            if(points != null) {
                points += " ";
            }
            points += x + "," + y;
            attributes.put("points", points);
        }

        @Override
        public String toString() {
            if(attributes.get("points") == null) {
                return "";
            }
            return super.toString();
        }
    }
}
