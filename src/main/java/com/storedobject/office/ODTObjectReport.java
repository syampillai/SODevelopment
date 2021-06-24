package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFObjectReport;

/**
 * {@link PDFObjectReport} and {@link com.storedobject.office.ODTObjectReport} are used to define report logic to be
 * used in {@link com.storedobject.core.PrintLogicDefinition}. The
 * {@link PrintLogicDefinition#getPrintLogicClassName()} should return the name of the logic derived from either
 * {@link PDFObjectReport} or {@link com.storedobject.office.ODTObjectReport}.
 *
 * <p>Please note: The format for the ODT will be read from {@link PrintLogicDefinition#getODTFormat()}.</p>
 *
 * @param <T> Type of {@link StringBuilder} class.
 * @author Syam
 */
public class ODTObjectReport<T extends StoredObject> extends ODTReport {

    /**
     * The current object to report.
     */
    protected final T object;

    /**
     * Constructor. Note: This constructor should be overridden in the extended classes because this is the one
     * that is automatically created by the platform.
     *
     * @param device Device.
     * @param object Object instance to report.
     */
    public ODTObjectReport(Device device, T object) {
        super(device);
        this.object = object;
    }

    /**
     * Get the current object instance to report.
     *
     * @return Current object instance.
     */
    public final T getObject() {
        return object;
    }

    /**
     * The fill method when this class acts as the value filler for this ODT report (by default this class will act as
     * the value filler). The default implementation see if this
     * attribute is available in the object instance and if available, that value is returned.
     *
     * @param name Name of the variable to be filled.
     * @return Value to be filled.
     */
    public Object fill(String name) {
        return "?";
    }
}
