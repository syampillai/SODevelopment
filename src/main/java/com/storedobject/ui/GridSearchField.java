package com.storedobject.ui;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ViewFilterSupport;
import com.storedobject.vaadin.SearchField;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.function.BiFunction;
import java.util.function.Function;

public class GridSearchField<T extends StoredObject> extends SearchField implements ViewFilterSupport<T> {

    private final ViewFilterSupport<T> grid;

    /**
     * Constructor.
     *
     * @param grid Grid to search.
     */
    public GridSearchField(ViewFilterSupport<T> grid) {
        super(grid::filterView);
        this.grid = grid;
    }

    /**
     * Constructor.
     *
     * @param grid Grid to search.
     * @deprecated This constructor exists for backward compatibility. To eliminate deprecation warning, you can
     * cast the parameter grid as {@link ViewFilterSupport}.
     */
    @Deprecated
    public GridSearchField(ObjectGrid<T> grid) {
        this((ViewFilterSupport<T>) grid);
    }

    public void filter(String filters) {
        grid.filterView(filters);
    }

    @Override
    public void configureMatch(BiFunction<T, String[], Boolean> matchFunction) {
        grid.configureMatch(matchFunction);
    }

    @Override
    public void configure(Function<T, String> toString) {
        grid.configure(toString);
    }

    @Override
    public void configure(String... attributes) {
        grid.configure(attributes);
    }

    @Override
    public void configure(LogicalOperator logicalOperator) {
        grid.configure(logicalOperator);
    }

    @Override
    public DataProvider<?, ?> getDataProvider() {
        return grid.getDataProvider();
    }

    @Override
    public Class<? extends T> getObjectClass() {
        return grid.getObjectClass();
    }
}
