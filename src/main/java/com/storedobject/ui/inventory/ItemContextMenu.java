package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DetailLinkGrid;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ExecutableView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

public class ItemContextMenu<T extends HasInventoryItem> extends GridContextMenu<T> {

    private final ItemContext context = new ItemContext();
    private boolean allowInspection, allowBreaking, allowAssemble, allowEditCost, hideGRNDetails, hideViewStock, hideMovementDetails;
    private GridMenuItem<T> itemAssembly, parentAssembly, viewFitment, viewFitmentLocations, inspect, split, assemble, inspectAssembly,
            breakAssembly, movementDetails, grnDetails, editCost, itemDetails, costDetails, pnDetails, viewStock, viewSource;
    private SerializablePredicate<T> dynamicContentHandler;

    public ItemContextMenu(Grid<T> itemGrid) {
        this(itemGrid, null);
    }

    public ItemContextMenu(Grid<T> itemGrid, Runnable refresher) {
        this(itemGrid, false, false, false, refresher);
    }

    public ItemContextMenu(Grid<T> itemGrid, boolean canInspect, boolean allowBreaking, boolean allowEditCost) {
        this(itemGrid, canInspect, allowBreaking, allowEditCost, null);
    }

    public ItemContextMenu(Grid<T> itemGrid, boolean canInspect, boolean allowBreaking,
                           boolean allowEditCost, Runnable refresher) {
        super(itemGrid);
        if(itemGrid instanceof DetailLinkGrid<?> linkGrid && linkGrid.getDataClass() == InventoryGRNItem.class) {
            setHideGRNDetails(true);
        }
        boolean isAdmin = itemGrid instanceof Transactional transactional
                && transactional.getTransactionManager().getUser().isAdmin();
        this.allowInspection = canInspect;
        this.allowBreaking = allowBreaking;
        this.allowEditCost = allowEditCost;
        context.setRefresher(refresher);
        context.setView((ExecutableView) itemGrid);
        super.setDynamicContentHandler(hi -> {
            itemGrid.deselectAll();
            if(hi == null) {
                return false;
            }
            if(itemAssembly == null) {
                build(isAdmin);
            }
            InventoryItem ii = hi.getInventoryItem();
            if(ii == null) {
                hide(itemAssembly, parentAssembly, viewFitment, viewFitmentLocations, inspect, split, assemble,
                        inspectAssembly,
                        breakAssembly, movementDetails, grnDetails, viewSource, editCost, itemDetails, costDetails,
                        pnDetails, viewStock);
                InventoryItemType iit = hi.getInventoryItemType();
                if(iit == null) {
                    return dynamicContentHandler != null && dynamicContentHandler.test(hi);
                }
                pnDetails.setVisible(true);
                pnDetails.setText("P/N Details: " + iit.getPartNumber());
                if(!hideViewStock) {
                    viewStock.setVisible(true);
                    viewStock.setText("View Stock: " + iit.getPartNumber());
                }
                if(dynamicContentHandler != null) {
                    dynamicContentHandler.test(hi);
                }
                return true;
            }
            pnDetails.setVisible(true);
            itemDetails.setVisible(true);
            costDetails.setVisible(true);
            grnDetails.setVisible(!hideGRNDetails);
            viewSource.setVisible(!hideGRNDetails);
            viewStock.setVisible(!hideViewStock);
            itemGrid.select(hi);
            InventoryLocation loc = ii.getLocation();
            boolean a = ii.getPartNumber().isAssembly();
            assemble.setVisible(a && (this.allowAssemble || this.allowBreaking));
            inspectAssembly.setVisible(a && (this.allowInspection || this.allowBreaking || this.allowAssemble));
            itemAssembly.setVisible(a);
            parentAssembly.setVisible(loc instanceof InventoryFitmentPosition);
            inspect.setVisible((this.allowInspection || this.allowBreaking || this.allowAssemble) && !(itemGrid instanceof ReceiveAndBin));
            split.setVisible(this.allowInspection && !ii.isSerialized() && ii.getQuantity().isPositive()
                    && !ii.getQuantity().equals(Count.ONE));
            editCost.setVisible(this.allowEditCost);
            viewFitment.setVisible(loc instanceof InventoryFitmentPosition);
            viewFitmentLocations.setVisible(loc instanceof InventoryFitmentPosition);
            breakAssembly.setVisible((this.allowInspection || this.allowBreaking) && loc instanceof InventoryFitmentPosition);
            movementDetails.setVisible(!hideMovementDetails && ii.isSerialized());
            InventoryItemType itemType = ii.getPartNumber();
            String pnLabel = ii.getPartNumber().getPartNumber();
            String itemLabel = pnLabel + " " + itemType.getSerialNumberShortName() + ": " + ii.getSerialNumber();
            getItems().forEach(mi -> {
                String label = mi.getText();
                int p = label.indexOf('-');
                if(p > 0) {
                    if(mi == pnDetails || mi == viewStock) {
                        mi.setText(label.substring(0, p) + "- " + pnLabel);
                    } else {
                        mi.setText(label.substring(0, p) + "- " + itemLabel);
                    }
                }
            });
            if(dynamicContentHandler != null) {
                dynamicContentHandler.test(hi);
            }
            return true;
        });
    }

    private void build(boolean isAdmin) {
        itemDetails = addItem("Item Details -", e -> e.getItem().ifPresent(context::view));
        itemAssembly = addItem("Item Assembly -", e -> e.getItem().ifPresent(context::viewAssembly));
        parentAssembly = addItem("Parent Assembly -", e -> e.getItem().ifPresent(context::viewParentAssembly));
        viewFitment = addItem("Fitment Details -", e -> e.getItem().ifPresent(context::viewFitment));
        viewFitmentLocations = addItem("Fitment Locations -",
                e -> e.getItem().ifPresent(context::viewFitmentLocations));
        inspect = addItem("Inspect & Bin -", e -> e.getItem().ifPresent(context::inspect));
        split = addItem("Split Quantity -", e -> e.getItem().ifPresent(context::split));
        assemble = addItem("Assemble -", e -> e.getItem().ifPresent(context::assemble));
        inspectAssembly = addItem("Inspect Assembly -", e -> e.getItem().ifPresent(context::inspectAssembly));
        breakAssembly = addItem("Break from Assembly -", e -> e.getItem().ifPresent(context::breakAssembly));
        movementDetails = addItem("Movement Details -", e -> e.getItem().ifPresent(context::viewMovements));
        grnDetails = addItem("GRN Details -", e -> e.getItem().ifPresent(context::viewGRN));
        viewSource = addItem("GRN & Source Details -", e -> e.getItem()
                .ifPresent(i -> context.viewGRN(i, true)));
        costDetails = addItem("Cost Details -", e -> e.getItem().ifPresent(context::viewCost));
        editCost = addItem("Edit Cost -", e -> e.getItem().ifPresent(item -> {
            close();
            context.editCost(item);
        }));
        pnDetails = addItem("P/N Details -", e -> e.getItem()
                .ifPresent(i -> context.view(i.getInventoryItemType())));
        viewStock = addItem("View Stock -", e -> e.getItem()
                .ifPresent(i -> context.viewStock(i.getInventoryItemType())));
        if(!isAdmin) return;
        addItem("View System IDs", e -> e.getItem().ifPresent(i -> {
            InventoryItem ii = i.getInventoryItem();
            InventoryItemType iit = i.getInventoryItemType();
            String m;
            if(ii == null && iit == null) {
                m = "No item or item type!";
            } else {
                m = iit.getId() + " : " + iit.getClass().getName();
                if(ii != null) {
                    m += "\n" + ii.getId() + " : " + ii.getClass().getName();
                }
            }
            Application.error(m);
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

    public void setAllowBreaking(boolean allowBreaking) {
        this.allowBreaking = allowBreaking;
    }

    public void setAllowEditCost(boolean allowEditCost) {
        this.allowEditCost = allowEditCost;
    }

    public void setAllowInspection(boolean allowInspection) {
        this.allowInspection = allowInspection;
    }

    public void setAllowAssemble(boolean allowAssemble) {
        this.allowAssemble = allowAssemble;
    }

    public void setHideGRNDetails(boolean hideGRNDetails) {
        this.hideGRNDetails = hideGRNDetails;
    }

    public void setHideViewStock(boolean hideViewStock) {
        this.hideViewStock = hideViewStock;
    }

    public void setHideMovementDetails(boolean hideMovementDetails) {
        this.hideMovementDetails = hideMovementDetails;
    }

    public ItemContext getContext() {
        return context;
    }
}
