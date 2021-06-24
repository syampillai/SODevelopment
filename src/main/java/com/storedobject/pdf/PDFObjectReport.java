package com.storedobject.pdf;

import com.storedobject.core.Device;
import com.storedobject.core.PrintLogicDefinition;
import com.storedobject.core.StoredObject;

/**
 * {@link PDFObjectReport} and {@link com.storedobject.office.ODTObjectReport} are used to define report logic to be
 * used in {@link com.storedobject.core.PrintLogicDefinition}. The
 * {@link PrintLogicDefinition#getPrintLogicClassName()} should return the name of the logic derived from either
 * {@link PDFObjectReport} or {@link com.storedobject.office.ODTObjectReport}.
 *
 * @param <T> Type of {@link StringBuilder} class.
 * @author Syam
 */
public abstract class PDFObjectReport<T extends StoredObject> extends PDFReport {

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
    public PDFObjectReport(Device device, T object) {
        super(device);
        this.object = object;
    }

    /**
     * Constructor.
     *
     * @param device Device.
     * @param object Object instance to report.
     * @param letterHead Whether to print in letterhead mode or not.
     */
    public PDFObjectReport(Device device, T object, boolean letterHead) {
        super(device, letterHead);
        this.object = object;
    }

    /**
     * Constructor.
     *
     * @param device Device.
     * @param object Object instance to report.
     * @param letterHead Whether to print in letterhead mode or not.
     * @param printLogo Whether to print logo or not.
     */
    public PDFObjectReport(Device device, T object, boolean letterHead, boolean printLogo) {
        super(device, letterHead, printLogo);
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
}
