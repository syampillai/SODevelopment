package com.storedobject.ui.iot;

import com.storedobject.common.Executable;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
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
    private SendCommand sendCommand;
    private final boolean devMode;
    final List<Unit> units = new ArrayList<>();
    private static boolean fixedSite = false;
    private static Site site;
    private List<Resource> resources;
    final Application application;

    public GUI() {
        this(false);
    }

    public GUI(boolean devMode) {
        if(fixedSite) {
            throw new SORuntimeException("Illegal access");
        }
        this.devMode = devMode;
        this.application = Application.get();
    }

    public GUI(Site site) {
        this(site,false);
    }

    public GUI(Site site, boolean devMode) {
        this.devMode = devMode;
        GUI.site = site;
        fixedSite = true;
        this.application = Application.get();
    }

    @Override
    public void execute() {
        siteView();
    }

    public static boolean isFixedSite() {
        return fixedSite;
    }

    static Site site() {
        return site;
    }

    static void setSite(Site site) {
        if(GUI.site != null && site != null && GUI.site.getId().equals(site.getId())) {
            return;
        }
        if(fixedSite) {
            throw new SORuntimeException("Illegal access");
        }
        GUI.site = site;
    }

    static void fixSite(Site site) {
        setSite(site);
        fixedSite = true;
    }

    void dashboard() {
        Application.message("Dashboard display is undergoing development changes.");
        /*
        if(dashboard == null) {
            dashboard = new Dashboard(this);
        }
        dashboard.execute();
        */
    }

    void chart() {
        if(valueChart == null) {
            valueChart = new ValueChart(this);
        }
        valueChart.execute();
    }

    void siteView() {
        if(siteView == null) {
            siteView = new SiteView(this, devMode);
        }
        siteView.execute();
    }

    void statusGrid() {
        if(statusGrid == null) {
            statusGrid = new StatusGrid(this);
        }
        statusGrid.execute();
    }

    void sendCommand() {
        if(sendCommand == null) {
            sendCommand = new SendCommand(this);
        } else {
            sendCommand.setBlock(block());
        }
        sendCommand.execute();
    }

    void downloadData() {
        new DownloadData(block()).execute();
    }

    void viewData() {
        new ViewData(block()).execute();
    }

    Block block() {
        return units.isEmpty() ? null : units.get(0).getBlock();
    }

    Id blockId() {
        Block block = block();
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
        consumptionDashboard.setBlock(block());
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
        System.err.println("SELECTing BLOCK");
        new BS(blockConsumer).execute();
    }

    private static class BS extends BlockSelector {

        private final Consumer<Block> blockConsumer;

        private BS(Consumer<Block> blockConsumer) {
            super("Select");
            this.blockConsumer = blockConsumer;
            setSite(GUI.site);
        }

        @Override
        protected boolean accept(Block block) {
            blockConsumer.accept(block);
            return true;
        }
    }
}
