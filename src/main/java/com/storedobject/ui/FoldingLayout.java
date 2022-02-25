package com.storedobject.ui;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;

/**
 * A responsive layout that can contain only sizeable components ({@link HasSize}). On a screen with its width less than
 * its height or its width less than {@link #getFoldingPoint()}, it displays its components horizontally. Otherwise,
 * it displays the components vertically.
 *
 * @author Syam
 */
public class FoldingLayout extends Composite<Component> {

    private final FlexLayout content = new FlexLayout();
    private final List<Component> components = new ArrayList<>();
    private final List<Integer> widths = new ArrayList<>();
    private int totalWidth;
    private final List<Integer> heights = new ArrayList<>();
    private int totalHeight;
    private int gap = 1;
    private boolean folded = false;
    private Registration resizer;
    private int foldingPoint;
    private int width = -1, height = -1;
    private boolean sizeFixed = false;

    /**
     * Constructor.
     *
     * @param components Components to add.
     */
    public FoldingLayout(HasSize... components) {
        this(0, 0, components);
    }

    /**
     * Constructor with fixed view-width and view-height. This is useful only if you are adding
     * this to a {@link com.storedobject.vaadin.View} with {@link com.storedobject.vaadin.View#setWindowMode(boolean)}
     * set to true (For example: {@link com.storedobject.vaadin.DataForm}).
     *
     * @param viewWidth View  width (typically between 1 and 100).
     * @param viewHeight View height (typically between 1 and 100).
     * @param components Components to add.
     */
    public FoldingLayout(int viewWidth, int viewHeight, HasSize... components) {
        content.getStyle().set("flex-wrap", "wrap").set("align-items", "start").set("align-content", "stretch");
        content.setSizeFull();
        if(viewWidth > 0) {
            content.setWidth(viewWidth + "vw");
            sizeFixed = true;
        }
        if(viewHeight > 0) {
            content.setHeight(viewHeight + "vh");
            sizeFixed = true;
        }
        add(components);
    }

    @Override
    protected Component initContent() {
        return sizeFixed ? new Div(content) : content;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(resizer == null) {
            Application a = Application.get();
            if(a != null) {
                resizer = a.addContentResizedListener((this::resized));
            }
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if(resizer != null) {
            resizer.remove();
            resizer = null;
        }
    }

    private void resized(int w, int h) {
        this.width = w;
        this.height = h;
        if(w < h || w < foldingPoint) {
            fold();
        } else {
            unfold();
        }
    }

    private void fold() {
        if(folded) {
            return;
        }
        folded = true;
        content.getStyle().set("align-items", "stretch").set("align-content", "start").set("flex-direction", "column");
        content.getChildren().filter(c -> c instanceof Filler).forEach(c -> ((Filler)c).fold());
        size();
    }

    private void unfold() {
        if(!folded) {
            return;
        }
        folded = false;
        content.getStyle().set("align-items", "start").set("align-content", "stretch").set("flex-direction", "row");
        content.getChildren().filter(c -> c instanceof Filler).forEach(c -> ((Filler)c).unfold());
        size();
    }

    /**
     * Set the gap between components. (Default gap is 1%).
     * @param gap Gap in percentage.
     */
    public void setGap(int gap) {
        this.gap = gap;
        normalizeSize();
    }

    /**
     * Get the gap between components.
     * @return Gap in percentage.
     */
    public final int getGap() {
        return gap;
    }

    /**
     * Set the folding point (Point at which folding is initiated).
     *
     * @param foldingPoint Folding point. (Default is 800).
     */
    public void setFoldingPoint(int foldingPoint) {
        this.foldingPoint = foldingPoint;
        resized(width, height);
    }

    /**
     * Set the folding point (Point at which folding is initiated).
     *
     * @return  Folding point. (Default is 800).
     */
    public final int getFoldingPoint() {
        return foldingPoint;
    }

    /**
     * Add components.
     *
     * @param components Components to add.
     */
    public void add(HasSize... components) {
        if(components != null) {
            for(HasSize hs: components) {
                if(hs instanceof Component c) {
                    if(!this.components.isEmpty()) {
                        content.add(new Filler());
                    }
                    content.add(c);
                    this.components.add(c);
                }
            }
            normalizeSize();
        }
    }

    /**
     * Remove components.
     *
     * @param components Components to remove.
     */
    public void remove(HasSize... components) {
        if(components != null) {
            for(HasSize hs: components) {
                if(hs instanceof Component) {
                    discard((Component) hs);
                }
            }
            normalizeSize();
        }
    }

    private void discard(Component c) {
        int i = components.indexOf(c);
        components.remove(c);
        if(i >= 0 && i < widths.size()) {
            widths.remove(i);
        }
        if(i >= 0 && i < heights.size()) {
            heights.remove(i);
        }
        content.removeAll();
        if(components.isEmpty()) {
            return;
        }
        Component last = components.get(components.size() - 1);
        components.forEach(component -> {
            content.add(component);
            if(component != last) {
                content.add(new Filler());
            }
        });
    }

    /**
     * Remove all components.
     */
    public void removeAll() {
        content.removeAll();
        components.clear();
        widths.clear();
        heights.clear();
    }

    /**
     * Set proportional widths for the components. These values are used to spread the components horizontally.
     *
     * @param widths Widths.
     */
    public void setProportionalWidths(int... widths) {
        this.widths.clear();
        if(widths != null) {
            for(int w: widths) {
                this.widths.add(w);
            }
        }
        normalizeSize();
    }

    /**
     * Set proportional widths for the components. These values are used to spread the components vertically.
     *
     * @param heights Heights.
     */
    public void setProportionalHeight(int... heights) {
        this.heights.clear();
        if(heights != null) {
            for(int h: heights) {
                this.heights.add(h);
            }
        }
        normalizeSize();
    }

    private void size() {
        if(components.isEmpty()) {
            return;
        }
        for(int i = 0; i < components.size(); i++) {
            if(folded) {
                h(components.get(i), i);
            } else {
                w(components.get(i), i);
            }
        }
    }

    private void w(Component c, int i) {
        int width = 100 * (widths.size() <= i ? 100 : widths.get(i)) / totalWidth;
        width -= (components.size() - 1) * gap;
        if(c instanceof HasSize) {
            ((HasSize) c).setWidth(width + "%");
            ((HasSize) c).setHeightFull();
        } else {
            c.getElement().getStyle().set("width", width + "%").set("height", "100%");
        }
    }

    private void h(Component c, int i) {
        int height = 100 * (heights.size() <= i ? 100 : heights.get(i)) / totalHeight;
        height -= (components.size() - 1) * gap;
        if(c instanceof HasSize) {
            ((HasSize) c).setWidthFull();
            ((HasSize) c).setHeight(height + "%");
        } else {
            c.getElement().getStyle().set("width", "100%").set("height", height + "%");
        }
    }

    private void normalizeSize() {
        int count = components.size();
        if(count == 0) {
            return;
        }
        totalWidth = normalize(widths, count);
        totalHeight = normalize(heights, count);
        size();
    }

    private static int normalize(List<Integer> sizes, int count) {
        if(count > sizes.size()) {
            int a = (int) sizes.stream().mapToInt(s -> s).average().orElse(100);
            while(count > sizes.size()) {
                sizes.add(a);
            }
        }
        return sizes.stream().limit(count).mapToInt(s -> s).sum();
    }

    private class Filler extends Span {

        private Filler() {
            if(folded) {
                fold();
            } else {
                unfold();
            }
        }

        private void fold() {
            setVisible(gap > 0);
            Style s = getStyle();
            s.set("width", "100%");
            s.set("height", gap + "%");
        }

        private void unfold() {
            setVisible(gap > 0);
            Style s = getStyle();
            s.set("width", gap + "%");
            s.set("height", "100%");
        }
    }
}
