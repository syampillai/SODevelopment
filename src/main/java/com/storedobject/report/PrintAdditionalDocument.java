package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.office.ODTReport;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * This class implements 'Additional Document' printing logic. This may be extended further to add additional 'fill' methods for
 * specific documents.
 */
public class PrintAdditionalDocument extends ODTReport implements AdditionalDocumentPrinter {

    /**
     * The 'Additional Document' to be printed
     */
    protected AdditionalDocument additionalDocument;
    /**
     * Entity object related to this document if available
     */
    protected StoredObject entity;
    /**
     * Values involved in this document
     */
    protected Map<String, Object> values;
    /**
     * One or more root documents related to this document
     */
    protected List<StoredObject> documents;

    public PrintAdditionalDocument(Device device) {
        super(device);
    }

    public PrintAdditionalDocument(Device device, StreamData streamData) {
        super(device, streamData);
    }

    public PrintAdditionalDocument(Device device, AdditionalDocumentDefinition documentDefinition) {
        super(device);
    }

    public PrintAdditionalDocument(Device device, String documentDefinition) {
        this(device);
    }

    /**
     * For Internal purpose only
     *
     * @param additionalDocument Additional document
     * @param documents          List of documents involved
     * @param values             Values contained in this additional document
     * @param entity             Entity involved, could be null
     */
    @Override
    public final void setAdditionalDocument(AdditionalDocument additionalDocument, List<StoredObject> documents,
                                            Map<String, Object> values, StoredObject entity) {
    }

    /**
     * This method is invoked when values are set. One may override this to customize values if required.
     */
    public void valuesSet() {
    }

    /**
     * Fill value for the 'Reference Number' from the document.
     *
     * @return Fill value
     */
    public final String fillReferenceNumber() {
        return null;
    }

    /**
     * Fill value for the 'Date' from the document.
     *
     * @return Fill value
     */
    public final Date fillDate() {
        return null;
    }

    /**
     * Get number of documents involved.
     *
     * @param name Name should be matched with document's name
     * @return Number of documents involved. 0 will be returned if the name matches with document's name
     */
    public int rowCount(String name) {
        return 0;
    }

    /**
     * Fill value for the values involved in the document. Name may contain '.' to invoke method results from the document or entity.
     * For example, use "PurchaseOrder.OrderAmount" to get "Order Amount" from the "Purchase Order".
     *
     * @param name Name of the value to be filled
     * @return Fill value
     */
    public Object fill(String name) {
        return null;
    }

    /**
     * Fill value from the document. Name may contain '.' to invoke method results from the document or entity.
     * For example, use "PurchaseOrder.OrderAmount" to get "Order Amount" from the "Purchase Order".
     *
     * @param name          Name of the value to be filled
     * @param documentIndex Index of the document
     * @return Fill value
     */
    public Object fill(String name, int documentIndex) {
        return null;
    }
}
