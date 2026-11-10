package com.storedobject.report;

import com.storedobject.common.Executable;
import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.SystemEntity;

/**
 * Represents an executable report for a {@link SystemEntity}.
 * <p>
 * The report definition is resolved from the available special print logic
 * definitions for {@link SystemEntity} using the supplied report name. If no
 * system entity is provided, the current transaction manager entity associated
 * with the device server is used.
 * </p><p>
 * When a matching report definition is found, the report execution is delegated
 * to an {@link ObjectReport}. If no matching definition can be found, the issue
 * is logged through the supplied device and execution performs no action.
 * </p>
 *
 * @see Executable
 * @see ObjectReport
 * @see SystemEntity
 * @see PrintLogicDefinition
 *
 * @author Syam
 */
public class EntityReport implements Executable {

    /**
     * Task that performs the resolved report execution.
     * May be {@code null} when no matching report definition is available, in which
     * case execution performs no action.
     */
    protected final Runnable runnable;

    /**
     * Creates an entity report for the current transaction manager entity associated
     * with the device server, using the report definition identified by the given
     * report name.
     *
     * @param device the device used to resolve the current entity and log/report execution issues
     * @param reportName the name of the report definition to resolve
     */
    public EntityReport(Device device, String reportName) {
        this(device, reportName, null);
    }

    /**
     * Creates an executable entity report for the specified device, report name, and system entity.
     * If the supplied system entity is {@code null}, the current entity from the device server's
     * transaction manager is used. The report definition is resolved from special print logic
     * definitions for {@link SystemEntity} by matching the supplied report name. If no matching
     * definition is found, the issue is logged and no runnable report task is created.
     *
     * @param device the device used to access server context, log resolution failures, and execute the report
     * @param reportName the label of the special report definition to resolve
     * @param systemEntity the system entity to report on, or {@code null} to use the current transaction entity
     */
    public EntityReport(Device device, String reportName, SystemEntity systemEntity) {
        if(systemEntity == null) {
            systemEntity = device.getServer().getTransactionManager().getEntity();
        }
        PrintLogicDefinition def = PrintLogicDefinition.listFor(SystemEntity.class, true)
                .filter(PrintLogicDefinition::getSpecial).filter(d -> d.getLabel().equals(reportName)).findFirst();
        if(def == null) {
            device.log("Unable to find report definition - " + reportName);
            runnable = null;
            return;
        }
        runnable = new ObjectReport(device, def, systemEntity, false).getRunnable();
    }

    /**
     * Executes the resolved report task, if one is available.
     * If no matching report definition was resolved during construction, this method
     * performs no action.
     */
    @Override
    public void execute() {
        if(runnable != null) runnable.run();
    }
}
