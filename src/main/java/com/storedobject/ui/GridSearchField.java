package com.storedobject.ui;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.SearchField;

import java.util.function.BiFunction;

public class GridSearchField<T extends StoredObject> extends SearchField {

    private final ObjectGrid<T> grid;

    public GridSearchField(ObjectGrid<T> grid) {
        super(grid::filterView);
        this.grid = grid;
    }

    public void filter(String filters) {
        grid.filterView(filters);
    }

    public void configure(BiFunction<T, String[], Boolean> matchFunction) {
        grid.configureFilterView(matchFunction);
    }

    public void configure(ObjectToString<T> objectToString) {
        grid.configureFilterView(objectToString);
    }

    public void configure(String... attributes) {
        grid.configureFilterView(attributes);
    }

    public void configure(LogicalOperator logicalOperator) {
        grid.configureFilterView(logicalOperator);
    }
}
