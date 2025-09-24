package com.storedobject.office;

import com.storedobject.core.*;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ODS extends AbstractSpreadSheet {

    /**
     * Constructor.
     * This will create a blank ODS file and the content may be manipulated in the generateContent() method.
     */
    public ODS() {
        this((InputStream)null, null);
    }

    /**
     * Constructor.
     *
     * @param out Output is written to this stream.
     */
    public ODS(OutputStream out) {
        this((InputStream)null, out);
    }

    /**
     * Constructor.
     *
     * @param in Input stream containing an ODS file with some content.
     */
    public ODS(InputStream in) {
        this(in, null);
    }

    /**
     * Constructor.
     *
     * @param databaseFileName Database file name (FileData) containing ODS file with some content.
     */
    public ODS(String databaseFileName) {
        this(Objects.requireNonNull(FileData.get(databaseFileName)));
    }

    /**
     * Constructor.
     *
     * @param fileData Database file containing ODS file with some content.
     */
    public ODS(FileData fileData) {
        this(fileData.getFile().getContent(), null);
    }

    /**
     * Constructor
     *
     * @param templateId Id of the stream data containing ODS file with some content.
     */
    public ODS(Id templateId) {
        this(templateId, null);
    }

    /**
     * Constructor
     *
     * @param templateId Id of the stream data containing ODS file with some content.
     * @param out Output is written to this stream.
     */
    public ODS(Id templateId, OutputStream out) {
        this(StoredObject.get(StreamData.class, templateId), out);
    }

    /**
     * Constructor
     *
     * @param streamData Stream data containing ODS file with some content.
     */
    public ODS(StreamData streamData) {
        this(streamData, null);
    }

    /**
     * Constructor
     *
     * @param streamData Stream data containing ODS file with some content.
     * @param out Output is written to this stream.
     */
    public ODS(StreamData streamData, OutputStream out) {
        this(streamData.getContent(), out);
    }

    /**
     * Constructor.
     *
     * @param in Input stream containing an ODS file with some content.
     * @param out Output is written to this stream.
     */
    public ODS(InputStream in, OutputStream out) {
        super(in, out);
    }

    @Override
    protected Workbook createWorkbook(InputStream in) throws Exception {
        return null;
    }

    public final void setRawOutput(boolean raw) {
    }

    public final boolean isRawOutput() {
        return false;
    }

    public final PDFProperties getPDFProperties() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getFileExtension() {
        return null;
    }

    /**
     * Re-evaluate all formula in the worksheet.
     */
    public void recompute() {
    }
}