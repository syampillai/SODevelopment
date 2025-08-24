package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.TemplateView;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import java.util.Date;
import java.util.function.Consumer;

/**
 * AbstractBlockView serves as the foundational class for creating specific block
 * views in applications. It is designed to manage and visualize blocks and associated
 * data within a transactional and closeable context. This class also integrates
 * with the application's polling mechanisms, providing periodic updates and relevant
 * functionalities for managing block-related operations.
 *
 * <h2>Key Features:</h2>
 * - Automatically integrates refresher mechanisms to update block data periodically.
 * - Provides a template for rendering blocks, requiring subclasses to implement
 *   specific methods for block representation.
 * - Manages site association for blocks with appropriate checks and updates.
 * - Handles resources and polling cleanup upon view termination.
 *
 * <h2>Core Responsibilities:</h2>
 * - Initiates the application menu closure and polling services.
 * - Refreshes the block's status using a polling mechanism.
 * - Offers utility methods to retrieve or format the last update time.
 * <p>
 * Subclasses are required to implement methods like {@code drawBlocks()} and
 * {@code reloadBlock(Id blockId)} to define specific rendering and reloading logic.
 * </p>
 * Implements:
 * - {@link Transactional} to support transactional operations.
 * - {@link CloseableView} to handle resources and cleanup processes.
 * <p>
 * Extends:
 * - {@link TemplateView} to use templating support for the UI.
 * </p>
 */
public abstract class AbstractBlockView extends TemplateView implements Transactional, CloseableView {

    private Consumer<Id> refresher;
    protected final Application application;
    protected Site site;
    private Date lastUpdateTime;

    @com.vaadin.flow.component.template.Id
    private Span lastUpdate;

    /**
     * Constructs a new instance of AbstractBlockView. This constructor initializes
     * the class by invoking the parent constructor and performs additional setup tasks.
     * Specifically, it gets the current instance of the application and closes any
     * open menus associated with it. This ensures that the view is prepared in a defined
     * state for later operations or interactions.
     */
    public AbstractBlockView() {
        super();
        application = Application.get();
        application.closeMenu();
    }

    /**
     * Cleans up resources used by the view and stops any associated polling operations.
     * This method unregisters the refresher if it was previously set, ensuring it is
     * properly disposed of and set to null. Additionally, it stops the polling operation
     * associated with the current view through the application. Finally, it delegates further
     * cleanup actions to the parent class by invoking the superclass's clean method.
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
     * Executes the operation for the view. This method ensures the
     * proper setup of a refresher mechanism and block association prior to execution.
     * It also invokes the parent class's execute method to perform the additional painting
     * actions required.
     *
     * @param lock A {@link View} object used to control the execution context
     *             and ensure thread safety.
     */
    @Override
    public final void execute(View lock) {
        if(refresher == null) {
            lastUpdateTime = null;
            application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
        drawBlocks();
        super.execute(lock);
    }

    /**
     * An abstract method responsible for drawing initial blocks in the view.
     * Subclasses must provide a concrete implementation of this method to define
     * the specific behavior for rendering or handling block-related functionality.
     * This method is called during the lifecycle of the view to perform
     * block-specific operations.
     */
    protected abstract void drawBlocks();

    /**
     * Sets the site associated with this view.
     * If the provided site is not null, it updates the current site reference.
     *
     * @param site the {@link Site} object to associate with this view. If null, the current site remains unchanged.
     */
    public void setSite(Site site) {
        if(site != null) {
            this.site = site;
        }
    }

    /**
     * Returns the associated {@link Site} instance for this view.
     *
     * @return the {@link Site} associated with this view, or null if none is set.
     */
    public Site getSite() {
        return site;
    }

    /**
     * Retrieves the timestamp of the last update for the view.
     *
     * @return the {@link Date} representing the time of the last update.
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Retrieves the last update of the view formatted as a string.
     * If the last update time is not available, it returns "UNKNOWN".
     *
     * @return A string representing the last update time in "HH:MM" format,
     *         or "UNKNOWN" if the update time is not set.
     */
    public String getLastUpdate() {
        if(lastUpdateTime == null) {
            return "UNKNOWN";
        }
        return DateUtility.formatWithTimeHHMM(lastUpdateTime);
    }

    private void refreshStatus(Id blockId) {
        lastUpdateTime = application.getTransactionManager().date(new Date());
        if(lastUpdate != null) {
            application.access(() -> lastUpdate.setText(getLastUpdate()));
        }
        redrawBlock(blockId);
    }

    /**
     * Redraws the specified block within the view.
     * This method is responsible for updating or re-rendering a specific block
     * identified by the given block ID. It is an abstract method that subclasses must
     * implement to define the exact behavior of block redrawing.
     * This operation is typically invoked when a specific block needs to be refreshed
     * due to changes in its state or external triggers.
     *
     * @param blockId The unique identifier of the block to be redrawn.
     */
    protected abstract void redrawBlock(Id blockId);

    /**
     * Creates a component based on the provided identifier.
     * If the identifier matches "lastUpdate", a new {@link Span} component is created and returned.
     * Otherwise, the method delegates the creation to the superclass implementation.
     *
     * @param id the identifier of the component to be created
     * @return the component associated with the given identifier
     */
    @Override
    protected Component createComponentForId(String id) {
        if("lastUpdate".equals(id)) {
            return new Span();
        }
        return super.createComponentForId(id);
    }
}
