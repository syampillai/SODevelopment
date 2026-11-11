package com.storedobject.ui.report;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;

/**
 * Application-specific entity report that delegates report generation to the
 * StoredObject reporting framework using the current application context.
 * This class simplifies report creation by automatically supplying
 * {@link Application#get()} to the superclass constructors.
 * <p>This can be used for reports that requires a date-period.</p>
 *
 * @author Syam
 */
public class EntityReportForPeriod extends AbstractEntityReport {

    private final DatePeriodField dpField = new DatePeriodField("Period");

    /**
     * Creates an application-specific entity report with the specified report name,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     */
    public EntityReportForPeriod(String reportName) {
        this(reportName, null);
    }

    /**
     * Creates an entity report with the specified report name and system entity,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     * @param systemEntity the system entity associated with the report
     */
    public EntityReportForPeriod(String reportName, SystemEntity systemEntity) {
        super(reportName, systemEntity);
        addField(dpField);
        setRequired(dpField);
        dpField.setValue(DatePeriod.yearTillYesterday());
    }

    @Override
    protected boolean process() {
        close();
        new com.storedobject.report.EntityReport(Application.get(), getCaption(), entityField.getObject(), dpField.getValue()).execute();
        return true;
    }
}
