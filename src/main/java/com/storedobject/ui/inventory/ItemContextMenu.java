package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataGrid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

public class ItemContextMenu<T extends HasInventoryItem> extends GridContextMenu<T> {

    private final ItemContext context = new ItemContext();
    private boolean allowInspection, allowBreaking, allowEditCost, hideGRNDetails, hideViewStock;
    private GridMenuItem<T> itemAssembly, parentAssembly, viewFitment, viewFitmentLocations, inspect, split,
            breakAssembly, movementDetails, grnDetails, editCost, itemDetails, costDetails, pnDetails, viewStock;
    private SerializablePredicate<T> dynamicContentHandler;

    public ItemContextMenu(DataGrid<T> itemGrid) {
        this(itemGrid, null);
    }

    public ItemContextMenu(DataGrid<T> itemGrid, Runnable refresher) {
        this(itemGrid, false, false, false, refresher);
    }

    public ItemContextMenu(DataGrid<T> itemGrid, boolean canInspect, boolean allowBreaking,
                           boolean allowEditCost) {
        this(itemGrid, canInspect, allowBreaking, allowEditCost, null);
    }

    public ItemContextMenu(DataGrid<T> itemGrid, boolean canInspect, boolean allowBreaking,
                           boolean allowEditCost, Runnable refresher) {
        super(itemGrid);
        this.allowInspection = canInspect;
        this.allowBreaking = allowBreaking;
        this.allowEditCost = allowEditCost;
        context.setRefresher(refresher);
        context.setView(itemGrid);
        super.setDynamicContentHandler(hi -> {
            itemGrid.deselectAll();
            if(hi == null) {
                return false;
            }
            if(itemAssembly == null) {
                build();
            }
            InventoryItem ii = hi.getItem();
            if(ii == null) {
                hide(itemAssembly, parentAssembly, viewFitment, viewFitmentLocations, inspect, split,
                        breakAssembly, movementDetails, grnDetails, editCost, itemDetails, costDetails, pnDetails,
                        viewStock);
                return dynamicContentHandler != null && dynamicContentHandler.test(hi);
            }
            pnDetails.setVisible(true);
            itemDetails.setVisible(true);
            costDetails.setVisible(true);
            grnDetails.setVisible(!hideGRNDetails);
            viewStock.setVisible(!hideViewStock);
            itemGrid.select(hi);
            InventoryLocation loc = ii.getLocation();
            itemAssembly.setVisible(ii.getPartNumber().isAssembly());
            parentAssembly.setVisible(loc instanceof InventoryFitmentPosition);
            inspect.setVisible(this.allowInspection || this.allowBreaking);
            split.setVisible(this.allowInspection && !ii.isSerialized() && ii.getQuantity().isPositive()
                    && !ii.getQuantity().equals(Count.ONE));
            editCost.setVisible(this.allowEditCost);
            viewFitment.setVisible(loc instanceof InventoryFitmentPosition);
            viewFitmentLocations.setVisible(loc instanceof InventoryFitmentPosition);
            breakAssembly.setVisible((this.allowInspection || this.allowBreaking) && loc instanceof InventoryFitmentPosition);
            movementDetails.setVisible(ii.isSerialized());
            InventoryItemType itemType = ii.getPartNumber();
            String item = ii.getPartNumber().getPartNumber() + " " + itemType.getSerialNumberShortName() + ": "
                    + ii.getSerialNumber();
            getItems().forEach(mi -> {
                String label = mi.getText();
                int p = label.indexOf('-');
                if(p > 0) {
                    mi.setText(label.substring(0, p) + "- " + item);
                }
            });
            if(dynamicContentHandler != null) {
                dynamicContentHandler.test(hi);
            }
            return true;
        });
    }

    private void build() {
        itemDetails = addItem("Item Details -", e -> e.getItem().ifPresent(context::view));
        itemAssembly = addItem("Item Assembly -", e -> e.getItem().ifPresent(context::viewAssembly));
        parentAssembly = addItem("Parent Assembly -", e -> e.getItem().ifPresent(context::viewParentAssembly));
        viewFitment = addItem("Fitment Details -", e -> e.getItem().ifPresent(context::viewFitment));
        viewFitmentLocations = addItem("Fitment Locations -",
                e -> e.getItem().ifPresent(context::viewFitmentLocations));
        inspect = addItem("Inspect & Bin -", e -> e.getItem().ifPresent(context::inspect));
        split = addItem("Split Quantity -", e -> e.getItem().ifPresent(context::split));
        breakAssembly = addItem("Break from Assembly -", e -> e.getItem().ifPresent(context::breakAssembly));
        movementDetails = addItem("Movement Details -", e -> e.getItem().ifPresent(context::viewMovements));
        grnDetails = addItem("GRN Details -", e -> e.getItem().ifPresent(context::viewGRN));
        costDetails = addItem("Cost Details -", e -> e.getItem().ifPresent(context::viewCost));
        editCost = addItem("Edit Cost -", e -> e.getItem().ifPresent(item -> {
            close();
            context.editCost(item);
        }));
        pnDetails = addItem("P/N Details -", e -> e.getItem()
                .ifPresent(i -> context.view(i.getItem().getPartNumber())));
        viewStock = addItem("View Stock -", e -> e.getItem().ifPresent(context::viewStock));
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

    public void setAllowBreaking(boolean allowBreaking) {
        this.allowBreaking = allowBreaking;
    }

    public void setAllowEditCost(boolean allowEditCost) {
        this.allowEditCost = allowEditCost;
    }

    public void setAllowInspection(boolean allowInspection) {
        this.allowInspection = allowInspection;
    }

    public void setHideGRNDetails(boolean hideGRNDetails) {
        this.hideGRNDetails = hideGRNDetails;
    }

    public void setHideViewStock(boolean hideViewStock) {
        this.hideViewStock = hideViewStock;
    }

    public ItemContext getContext() {
        return context;
    }
}
