package com.storedobject.ui.util;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectHint;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectGridData;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ThemeStyle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

public final class LoadFilterButtons<T extends StoredObject> {

    private final ObjectGridData<T, ?> grid;
    private final Button load, filter;
    private final ObjectSearchBuilder<T> searchBuilder;
    private boolean loadPending, filterMode, canFilter;

    public LoadFilterButtons(ObjectGridData<T, ?> grid, Iterable<String> filterColumns) {
        this.grid = grid;
        grid.addDataLoadedListener(this::loaded);
        boolean smallList = filterColumns == null && ObjectHint.isSmallList(grid.getObjectClass(), grid.isAllowAny());
        Button f = null;
        if(!smallList) {
            f = new com.storedobject.vaadin.Button("Apply Filter", "Filter", e -> filter());
        }
        if(smallList) {
            loadPending = true;
        }
        StringList filters = null;
        if(filterColumns != null) {
            if(filterColumns instanceof StringList) {
                filters = (StringList)filterColumns;
            } else {
                filters = StringList.create(filterColumns);
            }
        }
        ObjectSearchBuilder<T> sb = null;
        if(!smallList && grid.canSearch()) {
            sb = grid.createSearchBuilder(filters, x -> filterChanged());
        }
        if(sb != null && sb.getSearchFieldCount() == 0) {
            searchBuilder = null;
        } else {
            searchBuilder = sb;
        }
        if(searchBuilder == null) {
            f = null;
        }
        filter = f;
        if(filter != null) {
            filter.setVisible(false);
        }
        load = new com.storedobject.vaadin.Button(searchBuilder == null ? "Reload" : "Load", e -> load());
        load.addTheme(ThemeStyle.PRIMARY);
        if(loadPending && grid instanceof Component c) {
            c.addAttachListener(e -> {
               if(loadPending) {
                   loadPending = false;
                   load();
               }
            });
        }
    }

    private void filter() {
        filterMode = true;
        filter.setVisible(false);
        grid.setViewFilter(searchBuilder.getFilterPredicate());
    }

    private void load() {
        filterMode = false;
        load.removeTheme(ThemeStyle.PRIMARY);
        grid.setViewFilter(null);
        if(searchBuilder == null) {
            grid.load();
        } else {
            grid.load(searchBuilder.getFilterText());
        }
    }

    private void loaded() {
        if(filterMode || filter == null) {
            return;
        }
        load.removeTheme(ThemeStyle.PRIMARY);
        canFilter = grid.size() > 0 && grid.getCacheLevel() > 80;
        if(!filter.isVisible()) {
            return;
        }
        Application a = Application.get();
        if(a == null) {
            filter.setVisible(false);
        } else {
            a.access(() -> filter.setVisible(false));
        }
    }

    private void filterChanged() {
        if(filterMode) {
            filter.setVisible(true);
        } else {
            if(filter != null) {
                filter.setVisible(canFilter);
            }
        }
        load.addTheme(ThemeStyle.PRIMARY);
    }

    public Button getLoadButton() {
        return load;
    }

    public Button getFilterButton() {
        return filter;
    }

    public void addTo(HasComponents layout) {
        if(filter != null) {
            layout.add(filter);
        }
        layout.add(load);
    }

    public ObjectSearchBuilder<T> getSearchBuilder() {
        return searchBuilder;
    }
}
