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

/**
 * GUI helper class. It provides various functionalities such as managing IoT sites, blocks, units, and resources.
 * The GUI also contains features to display different views, charts, dashboards, and the ability to execute
 * specific commands and operations.
 *
 * @author Syam
 */
public class GUI implements Executable {

    private final Map<Id, ConsumptionDashboard> dashboardMap = new HashMap<>();
    private SendCommand sendCommand;
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
    private String siteViewLabel = "Site View";
    BlockView blockView;
    boolean allowCommand = true;
    boolean allowDownload = true;

    /**
     * Default constructor for the GUI class.
     * Initializes the GUI with default settings and without enabling development mode.
     */
    public GUI() {
        this(false);
    }

    /**
     * Constructs a GUI instance with the specified development mode setting.
     *
     * @param devMode a boolean indicating whether the GUI should operate in development mode.
     *                If true, the GUI will run in development mode; otherwise, it will operate in production mode.
     */
    public GUI(boolean devMode) {
        this(null, devMode);
    }

    /**
     * Constructs a GUI instance associated with a specific site.
     *
     * @param site The site to associate with the GUI.
     */
    public GUI(Site site) {
        this(site,false);
    }

    /**
     * Constructs a GUI object with the specified site and development mode flag.
     *
     * @param site The site associated with this GUI. This determines the fixed site status.
     * @param devMode A flag indicating whether the GUI is in development mode.
     */
    public GUI(Site site, boolean devMode) {
        this.devMode = devMode;
        this.site = site;
        this.fixedSite = site != null;
        this.application = Application.get();
    }

    /**
     * Checks whether the site is fixed.
     *
     * @return true if the site is a fixed site, false otherwise.
     */
    public boolean isFixedSite() {
        return fixedSite;
    }

    /**
     * Executes the default action for the GUI by displaying the site view.
     * This method overrides the execute() method of the parent class and ensures that the appropriate
     * site-related view is initialized and displayed. It delegates the task to the {@link #showSiteView()} method.
     */
    @Override
    public void execute() {
        showSiteView();
    }

    /**
     * Loads the list of units associated with the currently selected block.
     * <ul>
     * - Clears the existing list of units.
     * - Retrieves the active units linked to the current block from storage.
     * - Populates the units list with the retrieved data.
     */
    private void loadUnits() {
        units.clear();
        StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active", true).collectAll(units);
    }

    /**
     * Retrieves the currently set unit.
     *
     * @return The current {@link Unit} instance associated with this object.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the current unit for the GUI instance. If the specified unit is null,
     * or it is the same as the current unit (based on unit ID), the method will
     * return without making any changes. If the specified unit belongs to the
     * list of available units, it updates the current unit and its associated site.
     *
     * @param unit The unit to set as the current unit. It must not be null and should
     *             differ from the currently set unit. The unit is set only if it
     *             exists in the list of available units.
     */
    public void setUnit(Unit unit) {
        if(unit == null || (this.unit != null && this.unit.getId().equals(unit.getId()))) {
            return;
        }
        setSite(unit.getSite());
        if(units.contains(unit)) {
            this.unit = unit;
        }
    }

    /**
     * Sets the site for the current instance. This method updates the associated
     * block and units based on the provided site. If the site is null or matches
     * the currently set site (based on the site ID), the operation is ignored.
     * If a corresponding active block for the given site exists, the units are loaded;
     * otherwise, the units are cleared.
     *
     * @param site The new site to set. If it differs from the current site, the associated
     *             block, units, and the selected unit are updated accordingly.
     */
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
        unit = units.isEmpty() ? null : units.getFirst();
    }

    /**
     * Retrieves the current site associated with this instance.
     *
     * @return The site object currently set for this instance.
     */
    public Site getSite() {
        return site;
    }

    /**
     * Retrieves the unique identifier of the current site.
     * <br/>
     * If the site is not set, this method will return null.
     *
     * @return The identifier of the site if available, or null if the site is not set.
     */
    Id siteId() {
        return site == null ? null : site.getId();
    }

    /**
     * Displays the dashboard interface for the application.
     * <br/>
     * If the dashboard instance has not been initialized, this method will
     * create and instantiate a new Dashboard object using the current GUI context.
     * <br/>
     * Currently, the method displays a message indicating that the dashboard
     * functionality is under development, as the dashboard execution logic has
     * been commented out.
     */
    public void showDashboard() {
        if(blockView != null) {
            blockView.execute();
            return;
        }
        Application.message("Dashboard is not available or configured yet.");
    }

    /**
     * Displays a chart for the currently selected site.
     * <br/>
     * If no site is selected or set, a message will be displayed
     * indicating that there is no site selected. If the chart
     * has not been created yet, it initializes a new instance
     * of the {@code ValueChart} class.
     * <br/>
     * If the chart is ready or created successfully, it will
     * execute the display of the chart.
     */
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

    /**
     * Displays the site view within the graphical user interface (GUI).
     * Initializes the site view if it has not been created yet.
     * The site view is constructed using the current instance and the
     * developer mode flag. Once initialized or retrieved, it executes
     * the logic associated with the site view.
     */
    public void showSiteView() {
        if(siteView == null) {
            siteView = new SiteView(this, devMode);
        }
        if(devMode) {
            selectBlock(b -> siteView.execute());
        } else {
            siteView.execute();
        }
    }

    /**
     * Displays the status grid within the GUI. If the status grid is not initialized, the method will
     * create a new instance and execute it.
     * <br/>
     * This method ensures the status grid is properly initialized and executed, providing an
     * interface for monitoring or interacting with the current system status.
     */
    public void showStatusGrid() {
        Block block = anyBlock();
        if(block == null) {
            return;
        }
        if(statusGrid == null) {
            statusGrid = new StatusGrid(this);
        }
        statusGrid.execute();
        statusGrid.setBlock(block);
    }

    private Block anyBlock() {
        Block block = getBlock();
        if(block == null) {
            Application.message("No block selected or set");
            return null;
        }
        return block;
    }

    /**
     * Sends a command to the currently selected block.
     * <br/>
     * If no block is selected or set, a warning is displayed, and the operation is aborted.
     * Otherwise, a new command execution is initiated for the selected block.
     */
    public void sendCommand() {
        if(!allowCommand) {
            Application.warning("Not allowed");
            return;
        }
        Block block = anyBlock();
        if(block == null) {
            return;
        }
        if(sendCommand == null) {
            sendCommand = new SendCommand(block);
        } else {
            sendCommand.setBlock(block);
        }
        sendCommand.execute();
    }

    /**
     * Initiates the process to download data for the block associated with the current context.
     * This method triggers the execution of the {@link DownloadData} class, which handles
     * the data retrieval and processing tasks.
     * <br/>
     * The block to download data for is retrieved using the {@code getBlock()} method.
     * This method provides a high-level entry point for initiating data download operations.
     */
    public void downloadData() {
        if(!allowDownload) {
            Application.message("Not allowed");
            return;
        }
        new DownloadData(getBlock()).execute();
    }

    /**
     * Triggers the viewing of data associated with a specific block.
     * <br/>
     * This method executes a {@link ViewData} operation for the block currently associated with the instance.
     * If no block is explicitly set, it attempts to retrieve the block through the {@link #getBlock()} method,
     * which provides the default block based on the available units. The {@code ViewData} operation further
     * initiates data processing and viewing within the associated framework.
     */
    public void viewData() {
        new ViewData(getBlock(), allowDownload).execute();
    }

    /**
     * Checks if the specified unit is present in the collection of units.
     *
     * @param unit the unit to be checked for presence in the collection
     * @return true if the unit is present in the collection, false otherwise
     */
    public boolean contains(Unit unit) {
        return units.contains(unit);
    }

    /**
     * Checks if the collection of units is empty.
     *
     * @return {@code true} if the collection of units is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return units.isEmpty();
    }

    /**
     * Sets the block for the GUI instance. Depending on the provided block, it may update the associated units, site,
     * and selected unit of the GUI. If the block is null, all associated units and related properties are cleared.
     *
     * @param block The block to set. A null value clears the current block and associated properties.
     *              If the block is the same as the current block, no action is performed.
     */
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
        unit = units.isEmpty() ? null : units.getFirst();
        site = block.getSite();
    }

    /**
     * Retrieves the current block. If the block is not already set and the list of units
     * is not empty, the block is initialized using the block from the first unit in the list.
     *
     * @return The current block, or the block from the first unit if not already set and available.
     */
    public Block getBlock() {
        if(block == null && !units.isEmpty()) {
            block = units.getFirst().getBlock();
        }
        return block;
    }

    /**
     * Retrieves the unique identifier (Id) of the current Block.
     * This method fetches the Id from the current Block object associated with the GUI.
     * If no Block is set or available, this method returns null.
     *
     * @return The unique identifier (Id) of the current Block, or null if no Block is present.
     */
    Id blockId() {
        Block block = getBlock();
        return block == null ? null : block.getId();
    }

    /**
     * Creates and returns a Button or a PopupButton configured for resource consumption.
     * If the list of resources is null, it initializes the list by retrieving available resources.
     * If no resources are found, the method returns null.
     * If a single resource is found, a button for its consumption is returned.
     * Otherwise, a PopupButton is created, and multiple buttons for different resources
     * are added to it, each invoking a specific consumption action for the associated resource.
     *
     * @return The generated Button or PopupButton for resource consumption,
     *         or null if no resources are available.
     */
    Button consumptionButton() {
        if(resources == null) {
            resources = StoredObject.list(Resource.class).toList();
        }
        if(resources.isEmpty()) {
            return null;
        }
        if(resources.size() == 1) {
            Resource r = resources.getFirst();
            return new Button(r.getName() + " Consumption", VaadinIcon.CONTROLLER, e -> resource(r));
        }
        PopupButton popupButton = new PopupButton("Consumption", VaadinIcon.CONTROLLER);
        for(Resource r: resources) {
            popupButton.add(new Button(r.getName(), (String) null, e -> resource(r)));
        }
        return popupButton;
    }

    /**
     * Creates a data button with associated actions for viewing and downloading data.
     * The button is represented as a {@link PopupButton} with sub-options for "View" and "Download".
     *
     * @return A {@link Button} object that provides a popup menu for accessing data-related actions.
     */
    Button dataButton() {
        if(!allowDownload) {
            return null;
        }
        PopupButton popupButton = new PopupButton("Data", VaadinIcon.TABLE);
        popupButton.add(new Button("View", e -> viewData()));
        popupButton.add(new Button("Download", e -> downloadData()));
        return popupButton;
    }

    /**
     * Creates and returns a Button component that, when triggered, executes a command action.
     * The button is labeled "Send Control Command" and uses a controller icon.
     * If the command action is not allowed, this method returns null.
     *
     * @return a Button for sending a control command, or null if the command action is not allowed.
     */
    Button commandButton() {
        if(!allowCommand) {
            return null;
        }
        return new Button("Send Control Command", VaadinIcon.CONTROLLER, e -> sendCommand());
    }

    /**
     * Creates and returns a dashboard button. The button is labeled "Dashboard"
     * and uses the Vaadin DASHBOARD icon. When clicked, it triggers the
     * {@code showDashboard()} method.
     *
     * @return A button configured to display the dashboard if the {@code blockView}
     *         is not null; otherwise, returns null.
     */
    Button dashboardButton() {
        return blockView == null ? null
                : new Button("Dashboard", VaadinIcon.DASHBOARD, e -> showDashboard());
    }

    /**
     * Creates and returns a Button component configured for navigating to the site view.
     *
     * @return a Button with the site view label, icon, and a click listener that triggers the site view display.
     */
    Button siteViewButton() {
        return new Button(getSiteViewLabel(), VaadinIcon.FACTORY, e -> showSiteView());
    }

    /**
     * Creates and returns a button labeled "Status" with a dashboard icon.
     * When clicked, it triggers the display of the status grid.
     *
     * @return a Button instance configured with a label, icon, and click listener
     */
    Button statusGridButton() {
        return new Button("Status", VaadinIcon.DASHBOARD, e -> showStatusGrid());
    }

    /**
     * Creates and returns a button configured for displaying charts.
     * The button is labeled "Value Charts", uses a chart icon, and is set with an event listener that triggers the chart display action.
     *
     * @return a Button instance configured for showing charts
     */
    Button chartButton() {
        return new Button("Value Charts", VaadinIcon.CHART, e -> showChart());
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

    /**
     * Updates the given label with the last update time information.
     *
     * @param lastUpdate the label to be updated with the last update time. If the time is unknown,
     *                   it will display "UNKNOWN" with an error color. If the time is available,
     *                   it formats and appends the time to the label.
     */
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

    /**
     * Selects a block and performs an action defined by the provided Consumer.
     *
     * @param blockConsumer a Consumer that specifies the action to be performed on the selected Block
     */
    public void selectBlock(Consumer<Block> blockConsumer) {
        new BS(blockConsumer, this).execute();
    }

    /**
     * Sets the label for the site view.
     *
     * @param siteViewLabel the label to assign to the site view
     */
    public void setSiteViewLabel(String siteViewLabel) {
        if(siteViewLabel != null && !siteViewLabel.trim().isEmpty()) {
            this.siteViewLabel = siteViewLabel;
        }
    }

    /**
     * Retrieves the label associated with the site view.
     *
     * @return the site view label as a String
     */
    public String getSiteViewLabel() {
        return siteViewLabel;
    }

    /**
     * Sets the active block view instance.
     *
     * @param blockView The BlockView instance to be set.
     */
    public void setBlockView(BlockView blockView) {
        this.blockView = blockView;
    }

    /**
     * Set whether commands are allowed or not.
     *
     * @param allowCommand True/false.
     */
    public void setAllowCommand(boolean allowCommand) {
        this.allowCommand = allowCommand;
    }

    /**
     * Set whether data download is allowed or not.
     *
     * @param allowDownload True/false.
     */
    public void setAllowDownload(boolean allowDownload) {
        this.allowDownload = allowDownload;
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
