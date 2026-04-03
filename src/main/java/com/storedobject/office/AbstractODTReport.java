package com.storedobject.office;

import com.storedobject.core.*;

/**
 * Represents a report based on the ODT format, allowing integration with devices for viewing or printing.
 * This class provides various constructors to configure the report with a device, template, or filler data,
 * and ensures that the report is executed and viewed as per the requirements.
 *
 * @author Syam
 */
public class AbstractODTReport<T> extends ODT<T> {

    private final Device device;
    private boolean executed = false;
    private Entity entity;

    /**
     * Constructs an ODTReport instance that generates a report based on the provided device, stream data,
     * and filler object. The constructor initializes the report with the given parameters.
     *
     * @param device The device associated with the report, used for viewing or executing the report.
     */
    public AbstractODTReport(Device device) {
        this(device, (StreamData)null, null);
    }

    /**
     * Constructs an ODTReport instance with the specified device, template ID, and optional filler data.
     * The report is tied to a particular device for viewing or printing functionalities.
     * The specified template ID is used to set the report's template.
     *
     * @param device The device associated with the report, typically responsible for viewing or printing the report.
     * @param templateId The unique ID of the template used for generating the report content.
     */
    public AbstractODTReport(Device device, Id templateId) {
        this(device, templateId, null);
    }

    /**
     * Constructs an ODTReport instance with the specified device, template identifier, and filler object.
     * This constructor initializes the instance by setting the device and the filler and sets the
     * template using the provided template identifier.
     *
     * @param device The device associated with this report. It defines the context in which the
     *               report will be viewed or printed.
     * @param templateId The identifier of the template (StreamData) to be used for the report.
     *                   This identifier is used to fetch the corresponding template from the stored
     *                   object system.
     * @param filler The filler object used to populate the data in the report. It can be null
     *               if no specific data is required during initialization.
     */
    public AbstractODTReport(Device device, Id templateId, Object filler) {
        this(device, (StreamData)null, filler);
        setTemplate(templateId);
    }

    /**
     * Constructor to initialize an ODTReport instance with the given device, stream data, and filler object.
     * This allows creating a report instance that integrates with a specific device while populating
     * content from the provided stream data and an optional filler object.
     *
     * @param device The device with which the report will be associated. This is typically the output destination
     *               such as a printer or a viewer.
     * @param streamData The stream data containing the pre-defined content (in ODT format) for the report.
     *                   This can be used as a template or a source of data.
     */
    public AbstractODTReport(Device device, StreamData streamData) {
        this(device, streamData, null);
    }

    /**
     * Constructs an {@code ODTReport} instance with a specific device, stream data, and filler object.
     *
     * @param device The device with which this report is associated for viewing or printing.
     * @param streamData The stream data containing the template for the ODT report.
     *                   It provides the structure and content for the report.
     *                   Can be {@code null}.
     * @param filler An optional object to supply additional data or configurations needed
     *               for generating the report.
     */
    public AbstractODTReport(Device device, StreamData streamData, Object filler) {
        super(streamData, filler);
        this.device = device;
    }

    @Override
    public void execute() {
        if(!executed) {
            executed = true;
            getDevice().view(this);
            return;
        }
        super.execute();
    }

    /**
     * Triggers the viewing process for the report associated with this instance.
     * This method executes the report, ensuring that it is prepared for viewing
     * by interacting with the associated device. If the report is already executed,
     * it delegates further execution to the superclass implementation.
     * <p></p>
     * The viewing process involves verifying permissions and interacting with the
     * device to present the report to the user.
     * <p></p>
     * Note: This method relies on the {@code execute()} method to handle the core
     * execution logic, including permission checks and ensuring the report is opened
     * through the device.
     */
    public void view() {
        execute();
    }

    /**
     * Sets the entity associated with the report.
     * The entity is used to provide specific context or data that the report operates on.
     *
     * @param entity The entity to be associated with this report. This parameter
     *               can represent any business object or data entity that the report uses.
     */
    public final void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        if(entity != null) {
            return entity;
        }
        TransactionManager tm = getTransactionManager();
        return tm == null ? null : tm.getEntity().getEntity();
    }

    /**
     * Retrieves the report format associated with the current context.
     * If a {@code TransactionManager} is available, the report format is fetched based on it.
     * Otherwise, the report format associated with the entity is returned.
     *
     * @return The {@link ReportFormat} object that defines the format for the report.
     *         This could be determined by the {@code TransactionManager} or the associated entity.
     */
    public ReportFormat getReportFormat() {
        TransactionManager tm = getTransactionManager();
        if(tm != null) {
            return ReportFormat.get(tm);
        }
        return ReportFormat.get(entity);
    }

    @Override
    public Device getDevice() {
        return device;
    }
}