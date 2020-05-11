package com.storedobject.ui;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.SearchField;

import java.util.function.BiFunction;

public class GridSearchField<T extends StoredObject> extends SearchField {

    public GridSearchField(ObjectGrid<T> grid) {
        super(grid::filterView);
    }

    public void filter(String filters) {
    }

    public void configure(BiFunction<T, String[], Boolean> matchFunction) {
    }

    public void configure(ObjectToString<T> objectToString) {
    }

    public void configure(String... attributes) {
    }

    public void configure(LogicalOperator logicalOperator) {
    }
}