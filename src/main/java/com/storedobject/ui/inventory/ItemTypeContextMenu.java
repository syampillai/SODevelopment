package com.storedobject.ui.inventory;

import com.storedobject.core.HasInventoryItemType;
import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.Application;
import com.storedobject.ui.Transactional;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

public class ItemTypeContextMenu<T extends HasInventoryItemType> extends GridContextMenu<T> {

    private SerializablePredicate<T> dynamicContentHandler;
    private GridMenuItem<T> pnDetails, viewStock;
    private final ItemContext context = new ItemContext();

    public ItemTypeContextMenu(Grid<T> itemTypeGrid) {
        super(itemTypeGrid);
        boolean isAdmin = itemTypeGrid instanceof Transactional transactional
                && transactional.getTransactionManager().getUser().isAdmin();
        super.setDynamicContentHandler(hit -> {
            itemTypeGrid.deselectAll();
            if(hit == null) {
                return false;
            }
            if(pnDetails == null) {
                build(isAdmin);
            }
            itemTypeGrid.select(hit);
            InventoryItemType it = hit.getInventoryItemType();
            if(it == null) {
                hide(pnDetails);
                return dynamicContentHandler != null && dynamicContentHandler.test(hit);
            }
            pnDetails.setVisible(true);
            viewStock.setVisible(true);
            String pn = "P/N: " + it.getPartNumber();
            getItems().forEach(mi -> {
                String label = mi.getText();
                int p = label.indexOf('-');
                if(p > 0) {
                    mi.setText(label.substring(0, p) + "- " + pn);
                }
            });
            if(dynamicContentHandler != null) {
                dynamicContentHandler.test(hit);
            }
            return true;
        });
    }

    private void build(boolean isAdmin) {
        pnDetails = addItem("Details -", e -> e.getItem().ifPresent(context::view));
        viewStock = addItem("View Stock -", e -> e.getItem().ifPresent(context::viewStock));
        if(!isAdmin) return;
        addItem("View System ID", e -> e.getItem().ifPresent(i -> {
            InventoryItemType it = i.getInventoryItemType();
            if(it == null) {
                return;
            }
            Application.error(it.getId() + " : " + it.getClass().getName());
        }));
    }

    @SafeVarargs
    private void hide(GridMenuItem<T>... items) {
        for(GridMenuItem<T> item: items) {
            item.setVisible(false);
        }
    }

    @Override
    public void setDynamicContentHandler(SerializablePredicate<T> dynamicContentHandler) {
        this.dynamicContentHandler = dynamicContentHandler;
    }
}
