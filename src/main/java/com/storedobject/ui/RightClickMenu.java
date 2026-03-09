package com.storedobject.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.ArrayList;
import java.util.List;

public class RightClickMenu<T> extends GridContextMenu<T> {

    private final List<Button<T>> buttons = new ArrayList<>();

    public RightClickMenu() {
        super();
        init();
    }

    public RightClickMenu(Grid<T> grid) {
        super(grid);
        init();
    }

    private void init() {
        super.setDynamicContentHandler(i -> {
            boolean any = false;
            for(Button<T> button: buttons) {
                String label = button.menuItem.getText();
                if(i != null && button.button.test(i)) {
                    button.menuItem.setVisible(true);
                    any = true;
                } else {
                    button.menuItem.setVisible(false);
                }
                String changedLabel = button.button.getLabel();
                if(!label.equals(changedLabel)) {
                    button.menuItem.setText(changedLabel);
                }
            }
            return any;
        });
    }

    @Override
    public void setDynamicContentHandler(SerializablePredicate<T> dynamicContentHandler) {
    }

    public void add(RightClickButton<T> button) {
        buttons.add(new Button<>(button, addItem(button.getLabel(), e ->
                e.getItem().ifPresent(t -> {
                    String label = e.getSource().getText();
                    button.accept(t);
                    String changedLabel = button.getLabel();
                    if(!label.equals(changedLabel)) {
                        e.getSource().setText(changedLabel);
                    }
                }))));
    }

    public void remove(RightClickButton<T> button) {
        buttons.removeIf(b -> {
            if(b.button == button) {
                remove(b.menuItem);
                return true;
            }
            return false;
        });
    }

    private record Button<T>(RightClickButton<T> button, GridMenuItem<T> menuItem) {
    }
}
