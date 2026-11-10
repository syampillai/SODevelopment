package com.storedobject.ui.report;

import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;

/**
 * Application-specific entity report that delegates report generation to the
 * StoredObject reporting framework using the current application context.
 * This class simplifies report creation by automatically supplying
 * {@link Application#get()} to the superclass constructors.
 *
 * @author Syam
 */
public class EntityReport extends com.storedobject.report.EntityReport {

    /**
     * Creates an application-specific entity report with the specified report name,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     */
    public EntityReport(String reportName) {
        super(Application.get(), reportName);
    }

    /**
     * Creates an entity report with the specified report name and system entity,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     * @param systemEntity the system entity associated with the report
     */
    public EntityReport(String reportName, SystemEntity systemEntity) {
        super(Application.get(), reportName, systemEntity);
    }
}
