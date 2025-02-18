package com.storedobject.ui.iot;

import com.storedobject.common.Executable;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.QueryBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.PopupButton;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.*;
import java.util.function.Consumer;

public class GUI implements Executable {

    private Dashboard dashboard;
    private final Map<Id, ConsumptionDashboard> dashboardMap = new HashMap<>();
    private ValueChart valueChart;
    private SiteView siteView;
    private StatusGrid statusGrid;
    private final boolean devMode;
    private final List<Unit> units = new ArrayList<>();
    private Block block;
    private Unit unit;
    private final boolean fixedSite;
    private Site site;
    private List<Resource> resources;
    final Application application;

    public GUI() {
        this(false);
    }

    public GUI(boolean devMode) {
        this(null, devMode);
    }

    public GUI(Site site) {
        this(site,false);
    }

    public GUI(Site site, boolean devMode) {
        this.devMode = devMode;
        this.site = site;
        this.fixedSite = site != null;
        this.application = Application.get();
    }

    public boolean isFixedSite() {
        return fixedSite;
    }

    @Override
    public void execute() {
        showSiteView();
    }

    private void loadUnits() {
        units.clear();
        StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active", true).collectAll(units);
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        if(unit == null || (this.unit != null && this.unit.getId().equals(unit.getId()))) {
            return;
        }
        setSite(unit.getSite());
        if(units.contains(unit)) {
            this.unit = unit;
        }
    }

    public void setSite(Site site) {
        if(site == null || (this.site != null && this.site.getId().equals(site.getId()))) {
            return;
        }
        this.site = site;
        block = QueryBuilder.from(Block.class).where("Active AND Site=" + site.getId()).limit(1).get();
        if(block == null) {
            units.clear();
        } else {
            loadUnits();
        }
        unit = units.isEmpty() ? null : units.get(0);
    }

    public Site getSite() {
        return site;
    }

    Id siteId() {
        return site == null ? null : site.getId();
    }

    public void showDashboard() {
        if(dashboard == null) {
            dashboard = new Dashboard(this);
        }
        //dashboard.execute();
        Application.message("Dashboard display is undergoing development changes.");
    }

    public void showChart() {
        if(site == null) {
            Application.message("No site selected or set");
            return;
        }
        if(valueChart == null) {
            valueChart = new ValueChart(this);
        }
        valueChart.execute();
    }

    public void showSiteView() {
        if(siteView == null) {
            siteView = new SiteView(this, devMode);
        }
        siteView.execute();
    }

    public void showStatusGrid() {
        if(statusGrid == null) {
            statusGrid = new StatusGrid(this);
        }
        statusGrid.execute();
    }

    public void sendCommand() {
        Block block = getBlock();
        if(block == null) {
            Application.warning("No block selected or set");
            return;
        }
        new SendCommand(block).execute();
    }

    public void downloadData() {
        new DownloadData(getBlock()).execute();
    }

    public void viewData() {
        new ViewData(getBlock()).execute();
    }

    public boolean contains(Unit unit) {
        return units.contains(unit);
    }

    public boolean isEmpty() {
        return units.isEmpty();
    }

    public void setBlock(Block block) {
        if(block == null) {
            units.clear();
            this.block = null;
            unit = null;
            return;
        }
        Block b = getBlock();
        if(b != null && b.getId().equals(block.getId())) {
            return;
        }
        this.block = block;
        loadUnits();
        unit = units.isEmpty() ? null : units.get(0);
        site = block.getSite();
    }

    public Block getBlock() {
        if(block == null && !units.isEmpty()) {
            block = units.get(0).getBlock();
        }
        return block;
    }

    Id blockId() {
        Block block = getBlock();
        return block == null ? null : block.getId();
    }

    Button consumptionButton() {
        if(resources == null) {
            resources = StoredObject.list(Resource.class).toList();
        }
        if(resources.isEmpty()) {
            return null;
        }
        if(resources.size() == 1) {
            Resource r = resources.get(0);
            return new Button(r.getName() + " Consumption", VaadinIcon.CONTROLLER, e -> resource(r));
        }
        PopupButton popupButton = new PopupButton("Consumption", VaadinIcon.CONTROLLER);
        for(Resource r: resources) {
            popupButton.add(new Button(r.getName(), (String) null, e -> resource(r)));
        }
        return popupButton;
    }

    Button dataButton() {
        PopupButton popupButton = new PopupButton("Data", VaadinIcon.TABLE);
        popupButton.add(new Button("View", e -> viewData()));
        popupButton.add(new Button("Download", e -> downloadData()));
        return popupButton;
    }

    private void resource(Resource resource) {
        ConsumptionDashboard consumptionDashboard = dashboardMap.get(resource.getId());
        if(consumptionDashboard == null) {
            consumptionDashboard = new ConsumptionDashboard(this, resource);
            dashboardMap.put(resource.getId(), consumptionDashboard);
        }
        consumptionDashboard.setBlock(getBlock());
        consumptionDashboard.execute();
    }

    void lastUpdate(ELabel lastUpdate) {
        lastUpdate.clearContent().append("Last update at: ");
        if (DataSet.getTime() == 0) {
            lastUpdate.append("UNKNOWN", Application.COLOR_ERROR);
        } else {
            Date date = new Date();
            date.setTime(DataSet.getTime());
            date = application.getTransactionManager().date(date);
            lastUpdate.append(DateUtility.formatWithTimeHHMM(date));
        }
        lastUpdate.update();
    }

    public void selectBlock(Consumer<Block> blockConsumer) {
        new BS(blockConsumer, this).execute();
    }

    private static class BS extends BlockSelector {

        private final GUI gui;
        private final Consumer<Block> blockConsumer;

        private BS(Consumer<Block> blockConsumer, GUI gui) {
            super("Select", gui.getBlock());
            this.gui = gui;
            this.blockConsumer = blockConsumer;
            if(gui.fixedSite) {
                setSite(gui.getSite());
            }
        }

        @Override
        protected boolean accept(Block block) {
            close();
            gui.setBlock(block);
            gui.application.access(() -> blockConsumer.accept(gui.getBlock()));
            return true;
        }
    }
}
