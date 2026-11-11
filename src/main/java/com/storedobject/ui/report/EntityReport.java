package com.storedobject.ui.report;

import com.storedobject.core.DateUtility;
import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.DateField;

/**
 * Application-specific entity report that delegates report generation to the
 * StoredObject reporting framework using the current application context.
 * This class simplifies report creation by automatically supplying
 * {@link Application#get()} to the superclass constructors.
 * <p>This can be used for reports that requires a date.</p>
 *
 * @author Syam
 */
public class EntityReport extends AbstractEntityReport {

    private final DateField dateField = new DateField("Date");

    /**
     * Creates an application-specific entity report with the specified report name,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     */
    public EntityReport(String reportName) {
        this(reportName, null);
    }

    /**
     * Creates an entity report with the specified report name and system entity,
     * using the current application context.
     *
     * @param reportName the name of the report to create
     * @param systemEntity the system entity associated with the report
     */
    public EntityReport(String reportName, SystemEntity systemEntity) {
        super(reportName, systemEntity);
        addField(dateField);
        setRequired(dateField);
        dateField.setValue(DateUtility.endOfMonth(-1));
    }

    @Override
    protected boolean process() {
        close();
        new com.storedobject.report.EntityReport(Application.get(), getCaption(), entityField.getObject(),dateField.getValue()).execute();
        return true;
    }
}
