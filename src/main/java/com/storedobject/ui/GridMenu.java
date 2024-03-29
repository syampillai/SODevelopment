package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Logic;
import com.storedobject.core.ReportDefinition;
import com.storedobject.core.SingletonLogic;
import com.storedobject.report.ObjectList;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

public class GridMenu extends View implements SingletonLogic, CloseableView {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Menu menu;
    private boolean autoClose = false;

    public GridMenu(String caption) {
        setCaption(caption);
        menu = new Menu(caption);
        setComponent(menu);
        setFirstFocus(menu.searchField);
    }

    public void add(String menuDescription, Runnable menuLogic) {
        menu.add(new MenuItem(menuDescription, menuLogic));
    }

    public void add(Logic logic) {
        add(logic.getTitle(), () -> Application.get().execute(logic));
    }

    public void add(ReportDefinition reportDefinition) {
        //noinspection resource
        add(reportDefinition.getDescription(), () -> new ObjectList<>(getApplication(), reportDefinition).execute());
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(menu.isEmpty()) {
            message("No options to display for '" + getCaption() + "'");
            return;
        }
        super.execute(parent, doNotLock);
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    private class Menu extends ListGrid<MenuItem> {

        private final SearchField searchField;

        public Menu(String caption) {
            super(MenuItem.class, StringList.EMPTY);
            //noinspection unchecked
            createColumn(caption, m -> m.description);
            searchField = new SearchField(text -> setViewFilter(m -> m.description.toLowerCase().contains(text)));
            searchField.toLowerCase();
            addItemClickListener(e -> {
                if(autoClose) {
                    GridMenu.this.close();
                }
                e.getItem().logic.run();
            });
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(searchField, new Button("Exit", e -> GridMenu.this.close()));
        }
    }

    private record MenuItem(String description, Runnable logic) {
    }
}
