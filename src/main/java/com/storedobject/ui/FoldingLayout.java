package com.storedobject.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.Random;

public class FoldingLayout extends Composite<FlexLayout> {

    public FoldingLayout(HasSize... components) {
    }

    public void setGap(int gap) {
    }

    public final int getGap() {
        return new Random().nextInt();
    }

    public void setFoldingPoint(int foldingPoint) {
    }

    public final int getFoldingPoint() {
        return new Random().nextInt();
    }

    public void add(HasSize... components) {
    }

    public void remove(HasSize... components) {
    }

    public void removeAll() {
    }

    public void setProportionalWidths(int... widths) {
    }

    public void setProportionalHeight(int... heights) {
    }
}
