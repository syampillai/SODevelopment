package com.storedobject.office;

import com.storedobject.core.*;

/**
 * Represents a report based on the ODT format, allowing integration with devices for viewing or printing.
 * This class provides various constructors to configure the report with a device, template, or filler data,
 * and ensures that the report is executed and viewed as per the requirements.
 *
 * @author Syam
 */
public class ODTReport extends AbstractODTReport<Object> {

    /**
     * Constructs an ODTReport instance that generates a report based on the provided device, stream data,
     * and filler object. The constructor initializes the report with the given parameters.
     *
     * @param device The device associated with the report, used for viewing or executing the report.
     */
    public ODTReport(Device device) {
        super(device);
    }

    /**
     * Constructs an ODTReport instance with the specified device, template ID, and optional filler data.
     * The report is tied to a particular device for viewing or printing functionalities.
     * The specified template ID is used to set the report's template.
     *
     * @param device The device associated with the report, typically responsible for viewing or printing the report.
     * @param templateId The unique ID of the template used for generating the report content.
     */
    public ODTReport(Device device, Id templateId) {
        super(device, templateId);
    }

    /**
     * Constructs an ODTReport instance with the specified device, template identifier, and filler object.
     * This constructor initializes the instance by setting the device and the filler, and sets the
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
    public ODTReport(Device device, Id templateId, Object filler) {
        super(device, templateId, filler);
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
    public ODTReport(Device device, StreamData streamData) {
        super(device, streamData);
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
    public ODTReport(Device device, StreamData streamData, Object filler) {
        super(device, streamData, filler);
    }
}