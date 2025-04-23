package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.TemplateView;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * BlockView represents a view specifically designed to handle block entities and relevant updates in the IoT system.
 * It extends TemplateView to provide an interface for block-related operations and manages the state and updates
 * on block entities, their associated units, and various data statuses such as limits and alarms.
 * <br/>
 * Implements both Transactional and CloseableView interfaces to ensure proper state management and transactional
 * handling of its operations.
 *
 * @author Syam
 */
public class BlockView extends TemplateView implements Transactional, CloseableView {

    private final Object lock = new Object();
    private Consumer<Id> refresher;
    private final Application application;
    private final List<Unit> units = new ArrayList<>();
    private Block block;
    private Date lastUpdateTime;
    private boolean repainting = false;

    @com.vaadin.flow.component.template.Id
    private Span lastUpdate;
    @com.vaadin.flow.component.template.Id("block")
    private BlockComboField blockField;

    /**
     * Constructor for the BlockView class.
     * <br/>
     * Initializes the BlockView instance by performing the following tasks:
     * <pre>
     * - Sets the caption for the view as "Block View".
     * - Closes the menu of the current application instance.
     * - Registers a content resize listener to detect changes in content size
     *   and triggers a reload of the view upon size changes.
     * </pre>
     */
    public BlockView() {
        super();
        setCaption("Block View");
        Application.get().closeMenu();
        application = Application.get();
    }

    /**
     * Cleans up resources and releases references related to the BlockView instance.
     * This method removes the associated size attribute, unregisters any linked data refresher,
     * stops polling from the associated application, and invokes the superclasses clean method.
     * If a refresher is currently active, it will be unregistered before being set to null.
     * <p>Note: Do not call this method anywhere from your logic.</p>
     */
    @Override
    public void clean() {
        if(refresher != null) {
            DataSet.unregister(refresher);
            refresher = null;
        }
        application.stopPolling(this);
        super.clean();
    }

    /**
     * Executes the operation for the current block view. This method ensures the
     * proper setup of a refresher mechanism and block association prior to execution.
     * It also invokes the parent class's execute method to perform any additional
     * actions required.
     *
     * @param lock A {@link View} object used to control the execution context
     *             and ensure thread safety.
     */
    @Override
    public void execute(View lock) {
        if(refresher == null) {
            lastUpdateTime = null;
            application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
        if(block == null) {
            findBlock();
        }
        super.execute(lock);
    }

    /**
     * Retrieves the Block object associated with this instance.
     *
     * @return the Block object currently set in this instance
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Sets the specified {@link Block} to this view.
     * If the block is null or the block is already set with the same ID, the method returns without any action.
     * Clears all existing units, retrieves the new block's units, and adds them to the unit collection.
     * If the view is in a created state, the block is painted. Finally, reloads the view.
     *
     * @param block The {@link Block} to be set.
     */
    public void setBlock(Block block) {
        if(block == null || (this.block != null && this.block.getId().equals(block.getId()))) {
            return;
        }
        this.block = block;
        units.clear();
        block.listUnits().collectAll(units);
        if(isCreated()) {
            application.access(() -> paint(block));
        }
    }

    /**
     * Updates the associated site for the block field and reloads the view if necessary.
     * The method ensures that the new site is compatible with the block's current site ID
     * before proceeding with the update.
     *
     * @param site The site to be set. If null or not matching the conditions,
     *             the operation is ignored.
     */
    public void setSite(Site site) {
        if(blockField == null || site == null || (this.block != null && this.block.getSiteId().equals(site.getId()))) {
            return;
        }
        blockField.setSite(site);
    }

    /**
     * Retrieves the Site associated with this BlockView instance.
     * If a block is set, it will return the site associated with the block.
     * If no block is set, it will return the site associated with the blockField,
     * or null if blockField is not available.
     *
     * @return The associated Site object, or null if no site is available.
     */
    public Site getSite() {
        if(block != null) {
            return block.getSite();
        }
        return blockField == null ? null : blockField.getSite();
    }

    /**
     * Reloads the view. This is a manual call and "last update" time is not updated.
     * <p>Note: The {@link #reloading()} method will not be invoked.</p>
     */
    public final void reload() {
        if(block != null) {
            buildTree();
        }
    }

    /**
     * Performs the operation necessary for updating or refreshing internal
     * states related to the BlockView instance.
     * <p>
     * The method is designed for subclass usage or internal logic flow,
     * ensuring that specific elements or data related to the view can be
     * re-initialized or recalculated. Its exact behavior is expected to be
     * defined or overridden in subclasses or through internal calls in the
     * containing class.</p>
     */
    protected void reloading() {
    }

    /**
     * Retrieves the last update time of the block view.
     *
     * @return the date and time of the last update as a {@code Date} object
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Retrieves the formatted timestamp for the last update of the block view.
     * If no update has been recorded, it returns "UNKNOWN".
     *
     * @return A formatted string representing the last update timestamp, or "UNKNOWN" if no update exists.
     */
    public String getLastUpdate() {
        if(lastUpdateTime == null) {
            return "UNKNOWN";
        }
        return DateUtility.formatWithTimeHHMM(lastUpdateTime);
    }

    private void findBlock() {
        Block block = this.block;
        if(block != null) {
            setBlock(block);
            return;
        }
        if(blockField != null) {
            block = blockField.getBlock();
        }
        if(block != null) {
            setBlock(block);
            return;
        }
        block = StoredObject.get(Block.class, "Active");
        if(block != null) {
            if(blockField != null) {
                blockField.setValue(block);
            } else {
                setBlock(block);
            }
        }
    }

    private void refreshStatus(Id blockId) {
        lastUpdateTime = application.getTransactionManager().date(new Date());
        if(lastUpdate != null) {
            application.access(() -> lastUpdate.setText(getLastUpdate()));
        }
        if(block == null || !blockId.equals(this.block.getId())) {
            return;
        }
        reload();
    }

    private void buildTree() {
        if(repainting) {
            return;
        }
        synchronized (lock) {
            if(repainting) {
                return;
            }
            repainting = true;
            application.access(this::reloading);
            Id siteId = block.getSiteId();
            List<Unit> unprocessed = new ArrayList<>(units);
            DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(siteId))
                    .forEach(s -> buildBranches(s, unprocessed));
            repainting = false;
        }
    }

    private void buildBranches(DataSet.AbstractData parent, List<Unit> unprocessed) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(units.contains(ud.getUnit())) {
                    ud.getDataStatus().forEach(ds -> process(ds, unprocessed));
                }
            }
            buildBranches(row, unprocessed);
        });
    }

    private void process(DataSet.DataStatus<?> ds, List<Unit> unprocessed) {
        if(!isCreated()) {
            return;
        }
        Unit unit = ds.getUnit();
        if(unprocessed.contains(unit)) {
            unprocessed.remove(unit);
            application.access(() -> paint(unit));
        }
        if(ds instanceof DataSet.LimitStatus ls) {
            application.access(() -> paint(ls));
        } else if(ds instanceof DataSet.AlarmStatus as) {
            application.access(() -> paint(as));
        }
    }

    /**
     * Paints the specified Block in the view or graphical component.
     *
     * @param block The Block object to be painted.
     */
    protected void paint(Block block) {
    }

    /**
     * Renders or updates the visual representation of the given unit.
     *
     * @param unit the unit object to paint or render within the context of this view
     */
    protected void paint(Unit unit) {
    }

    /**
     * Paints the visual representation related to the provided limit status.
     * This method customizes and updates the display based on the given limit status,
     * ensuring consistency with the dataset constraints or thresholds.
     *
     * @param limitStatus the limit status to be reflected in the visual display
     */
    protected void paint(DataSet.LimitStatus limitStatus) {
    }

    /**
     * Paints the visual representation of the specified alarm status.
     *
     * @param alarmStatus The alarm status to be painted, providing information
     *                    about the current state of the alarm for visualization.
     */
    protected void paint(DataSet.AlarmStatus alarmStatus) {
    }


    /**
     * This method is invoked when a {@link SiteView} is active and someone clicked on an item that is an alarm switch.
     *
     * @param alarmStatus The respective alarm status.
     */
    protected void clicked(DataSet.AlarmStatus alarmStatus) {
    }

    /**
     * This method is invoked when a {@link SiteView} is active and someone clicked on an item that is a limit value.
     *
     * @param limitStatus The respective limit status.
     */
    protected void clicked(DataSet.LimitStatus limitStatus) {
    }

    /**
     * Creates and returns a component based on the specified id. If the id equals "block",
     * a {@link BlockComboField} is created and configured with a value change listener
     * to update the block. For other ids, the method delegates the component creation
     * to the superclass implementation.
     *
     * @param id the identifier used to determine the type of component to create
     * @return the created {@link Component} based on the id
     */
    @Override
    protected Component createComponentForId(String id) {
        return switch (id) {
            case "lastUpdate" -> new Span();
            case "block" -> {
                BlockComboField blockField = new BlockComboField();
                blockField.setMinWidth("400px");
                blockField.addValueChangeListener(e -> setBlock(e.getValue()));
                yield blockField;
            }
            default -> super.createComponentForId(id);
        };
    }
}
