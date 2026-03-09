package com.storedobject.ui.inventory;

import com.storedobject.core.HasInventoryItemType;
import com.storedobject.core.InventoryItemType;
import com.storedobject.ui.Application;
import com.storedobject.ui.RightClickMenu;
import com.storedobject.ui.Transactional;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;

public class ItemTypeContextMenu<T extends HasInventoryItemType> extends RightClickMenu<T> {

    private GridMenuItem<T> pnDetails, viewStock;
    private final ItemContext context = new ItemContext();
    private final boolean isAdmin;

    public ItemTypeContextMenu(Grid<T> itemTypeGrid) {
        super(itemTypeGrid);
        isAdmin = itemTypeGrid instanceof Transactional transactional
                && transactional.getTransactionManager().getUser().isAdmin();
        addCustomContentHandler(hit -> {
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
                return false;
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
}
