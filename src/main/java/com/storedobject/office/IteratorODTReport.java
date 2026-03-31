package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;

/**
 * An iterator-based ODT report. An iterator should be provided via {@link #setIterator(java.util.Iterator)}. The output
 * will be generated based for each element of the iterator.
 *
 * @param <T> The type of the iterator element that is being processed.
 *
 * @author Syam
 */
public class IteratorODTReport<T> extends AbstractODTReport<T> {

    /**
     * Constructs an IteratorODTReport instance that generates a report based on the elements of an iterator.
     * This uses the specified device for viewing or executing the report.
     *
     * @param device The device associated with the report, used for viewing or executing the report.
     */
    public IteratorODTReport(Device device) {
        super(device);
    }

    /**
     * Constructs an instance of IteratorODTReport with the specified device and stream data ID.
     * This constructor ties the report to a specific device for handling output-related functionalities and
     * associates it with a particular template identified by the given stream data ID.
     *
     * @param device The device associated with the report, typically responsible for viewing or printing the report.
     * @param streamDataId The unique ID of the stream data used as the template for generating the report content.
     */
    public IteratorODTReport(Device device, Id streamDataId) {
        super(device, streamDataId);
    }

    /**
     * Constructs an IteratorODTReport instance with the specified device, stream data identifier,
     * and filler object. This constructor initializes the report by setting the device, template,
     * and filler, allowing further data iteration and output generation.
     *
     * @param device The device associated with this report, defining the context in which the report
     *               will be viewed or printed.
     * @param streamDataId The identifier of the stream data template to be used for the report.
     *                     This identifier is used to fetch the corresponding template.
     * @param filler The filler object used to populate the data in the report. It can be null if
     *               no specific data is required during initialization.
     */
    public IteratorODTReport(Device device, Id streamDataId, Object filler) {
        super(device, streamDataId, filler);
    }

    /**
     * Creates an instance of IteratorODTReport that generates reports based on the specified device and stream data.
     * The report output is structured iteratively by processing each element of an Iterator that must be set via
     * {@link #setIterator(java.util.Iterator)}.
     *
     * @param device The device with which the report will be associated. This typically represents the output
     *               destination, such as a printer or a viewer.
     * @param streamData The stream data containing the pre-defined content (usually in ODT format) for the report.
     *                   This serves as a template or source content for report generation.
     */
    public IteratorODTReport(Device device, StreamData streamData) {
        super(device, streamData);
    }

    /**
     * Constructs an {@code IteratorODTReport} instance with a specific device, stream data,
     * and filler object. This report generates output based on the elements provided by an
     * iterator and employs the given stream data and filler for content configuration.
     *
     * @param device The device with which this report is associated for viewing or printing.
     * @param streamData The stream data containing the template for the ODT report.
     *                   It provides the structure and content for the report.
     *                   Can be {@code null}.
     * @param filler An optional object used to provide additional data or configurations
     *               required for generating the report.
     */
    public IteratorODTReport(Device device, StreamData streamData, Object filler) {
        super(device, streamData, filler);
    }
}