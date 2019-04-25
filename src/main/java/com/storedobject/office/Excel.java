package com.storedobject.office;

import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Excel extends AbstractSpreadSheet {

    /**
     * Constructor.
     * This will create a blank Excel file and the content may be manipulated in the generateContent() method.
     */
    public Excel() {
        this((InputStream)null, null);
    }

    /**
     * Constructor.
     *
     * @param out Output is written to this stream.
     */
    public Excel(OutputStream out) {
        this((InputStream)null, out);
    }

    /**
     * Constructor.
     *
     * @param in Input stream containing an Excel file with some content.
     */
    public Excel(InputStream in) {
        this(in, null);
    }

    /**
     * Constructor.
     *
     * @param databaseFileName Database file name (FileData) containing Excel file with some content.
     */
    public Excel(String databaseFileName) {
        this(Objects.requireNonNull(FileData.get(databaseFileName)));
    }

    /**
     * Constructor.
     *
     * @param fileData Database file containing Excel file with some content.
     */
    public Excel(FileData fileData) {
        this(fileData.getFile().getContent(), null);
    }

    /**
     * Constructor
     *
     * @param templateId Id of the stream data containing Excel file with some content.
     */
    public Excel(Id templateId) {
        this(templateId, null);
    }

    /**
     * Constructor
     *
     * @param templateId Id of the stream data containing Excel file with some content.
     * @param out Output is written to this stream.
     */
    public Excel(Id templateId, OutputStream out) {
        this(StoredObject.get(StreamData.class, templateId), out);
    }

    /**
     * Constructor
     *
     * @param streamData Stream data containing Excel file with some content.
     */
    public Excel(StreamData streamData) {
        this(streamData, null);
    }

    /**
     * Constructor
     *
     * @param streamData Stream data containing Excel file with some content.
     * @param out Output is written to this stream.
     */
    public Excel(StreamData streamData, OutputStream out) {
        this(streamData.getContent(), out);
    }

    /**
     * Constructor.
     *
     * @param in Input stream containing an Excel file with some content.
     * @param out Output is written to this stream.
     */
    public Excel(InputStream in, OutputStream out) {
        super(in, out);
    }

    @Override
    protected Workbook createWorkbook(InputStream in) throws Exception {
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