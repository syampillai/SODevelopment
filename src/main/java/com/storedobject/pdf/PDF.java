package com.storedobject.pdf;

import com.storedobject.common.*;
import com.storedobject.core.StringUtility;
import com.storedobject.core.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * This class is used to create PDF output. The only abstract method in this class is generateContent()
 * and is the one where you really print the report content. In most cases, you may use
 * {@link PDFReport} class instead of this class directly.
 *
 * @author Syam
 */
public abstract class PDF implements java.io.Closeable, com.storedobject.core.ContentProducer {

    public static final String[] pageSizes = new String[] {
            "Default",
            "A4 Portrait",
            "A4 Landscape",
            "A3 Portrait",
            "A3 Landscape",
            "A2 Portrait",
            "A2 Landscape",
            "A1 Portrait",
            "A1 Landscape",
            "A0 Portrait",
            "A0 Landscape",
            "Letter Portrait",
            "Letter Landscape",
    };
    public final static int ORIENTATION_PORTRAIT = 0;
    public final static int ORIENTATION_LANDSCAPE = 1;

    /**
     * Constructor.
     * This will generate a PDF with letterhead.
     */
    public PDF() {
    }

    /**
     * Constructor.
     * This will generate a PDF with letterhead.
     * @param logo Logo
     */
    public PDF(PDFImage logo) {
        this();
    }

    /**
     * Constructor.
     *
     * @param letterhead Whether letterhead needs to be printed or not.
     */
    public PDF(boolean letterhead) {
        this();
    }

    /**
     * Constructor.
     *
     * @param letterhead Whether letterhead needs to be printed or not.
     * @param logo Logo
     */
    public PDF(boolean letterhead, PDFImage logo) {
        this();
    }

    /**
     * Constructor.
     * This will generate a PDF with letterhead.
     *
     * @param out Output stream to which content should be streamed out. Useful for storing the generated PDF to the backend.
     */
    public PDF(OutputStream out) {
        this();
    }

    /**
     * Constructor.
     * This will generate a PDF with letterhead.
     *
     * @param out Output stream to which content should be streamed out. Useful for storing the generated PDF to the backend.
     * @param logo Logo
     */
    public PDF(OutputStream out, PDFImage logo) {
        this();
    }

    /**
     * Constructor.
     *
     * @param out Output stream to which content should be streamed out.
     * @param letterhead Whether letterhead needs to be printed or not.
     */
    public PDF(OutputStream out, boolean letterhead) {
        this();
    }

    /**
     * Constructor.
     *
     * @param out Output stream to which content should be streamed out.
     * @param letterhead Whether letterhead needs to be printed or not.
     * @param logo Logo
     */
    public PDF(OutputStream out, boolean letterhead, PDFImage logo) {
        this();
    }

    /**
     * Open the PDF for creating the content. This is automatically invoked by the framework.
     */
    public void open() {
    }

    /**
     * Return the width in number of characters for drawing the given value.
     *
     * @param any anything.
     * @return Width in number of characters.
     */
    protected final int toCharCount(Object any) {
        return 0;
    }

    public static PDFRectangle getPageSize(int paperSizeIndex) {
        return null;
    }

    public int getPageSizeIndex() {
        return 0;
    }

    public void setPageSizeIndex(int pageSizeIndex) {
    }

    /**
     * Set a password for the PDF. If set, this password will be required to open the generated PDF. This method
     * should be invoked before the actual content generation starts (so, the password must be set before
     * {@link #generateContent()} and that means, the constructor is the ideal place to set the password).
     *
     * @param password Password to set.
     */
    public void setPassword(String password) {
    }

    /**
     * Sets the default font size.
     *
     * @param fontSize Font size
     */
    public final void setFontSize(int fontSize) {
    }

    /** Gets default font size.
     *
     * @return Default font size.
     */
    public final int getFontSize() {
        return 0;
    }

    /**
     * Sets the font language for this PDF.
     *
     * @param language Font language.
     */
    public void setLanguage(PDFFont.Language language) {
    }

    /**
     * Sets the font language for this PDF.
     *
     * @param language Font language.
     * @param fontSize Font size
     */
    public void setLanguage(PDFFont.Language language, int fontSize) {
    }

    /**
     * Adds an object to the document. Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param any Any object
     */
    public void add(Object any) {
    }

    /**
     * Add an object to the document. Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param any Any object
     * @param rightAligned Right alignment
     */
    public void add(Object any, boolean rightAligned) {
    }

    /**
     * Add an object to the document. Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param any Any object
     * @param horizontalAlignment Alignment value from PDFElement.
     */
    public void add(Object any, int horizontalAlignment) {
    }

    /**
     * Add an object to the document. Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param any Any object
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param verticalAlignment Alignment value from PDFElement.
     */
    public void add(Object any, int horizontalAlignment, int verticalAlignment) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     */
    public void addToTable(PDFTable table, Object any) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addToTable(PDFTable table, Object any, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param rightAligned Right alignment.
     */
    public void addToTable(PDFTable table, Object any, boolean rightAligned) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param rightAligned Right alignment.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addToTable(PDFTable table, Object any, boolean rightAligned, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     */
    public void addToTableCentered(PDFTable table, Object any) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addToTableCentered(PDFTable table, Object any, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param horizontalAlignment Alignment value from PDFElement.
     */
    public void addToTable(PDFTable table, Object any, int horizontalAlignment) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addToTable(PDFTable table, Object any, int horizontalAlignment, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param verticalAlignment Alignment value from PDFElement.
     */
    public void addToTable(PDFTable table, Object any, int horizontalAlignment, int verticalAlignment) {
    }

    /**
     * Adds an object to the given PDFTable as a PDFCell.
     * Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param table Table to which the object needs to be added after converting into a PDFCell.
     * @param any Any object to convert to a PDFCell.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param verticalAlignment Alignment value from PDFElement.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell will be passed through this for further customization.
     */
    public void addToTable(PDFTable table, Object any, int horizontalAlignment, int verticalAlignment, Function<PDFCell, PDFCell> cellCustomizer) {
    }

    /**
     * Closes the PDF for content generation process. This method is generally invoked by the framework automatically.
     */
    @Override
    public void close() {
    }

    /**
     * Chain to another PDF. Content from the PDF will be appended to the current one.
     *
     * @param pdf PDF that needs to be appended to this content.
     */
    public void chainTo(PDF pdf) {
    }

    /**
     * Gets the name of the file for this PDF
     *
     * @return The PDF file name
     */
    @Override
    public String getFileName() {
        return null;
    }

    /**
     * Gets the content as a stream.
     *
     * @return Content as a stream from the generated file.
     */
    @Override
    public final InputStream getContent() throws java.lang.Exception {
        return null;
    }

    /**
     * Gets the content type for this.
     *
     * @return "application/pdf"
     */
    @Override
    public final String getContentType() {
        return null;
    }

    /**
     * This is the method to be invoked in order to do the content generation. This method is generally not overridden but
     * if it is overridden, make sure that the "super" is invoked.
     */
    public void produce() {
    }

    /**
     * Starts a new report stage. Methods like getTitle(), getFooter(), getPageSize() etc. will be invoked again. A new page will
     * be created with new page size, title and footer. Report Stage will be incremented by one and it can be obtained by
     * calling getReportStage().
     */
    public void newReportStage() {
    }

    /**
     * Starts a new report stage. Methods like getTitle(), getFooter(), getPageSize() etc. will be invoked again. A new page will
     * be created with new page size, title and footer. Report Stage will be set to the value passed as parameter so that the next call
     * to getReportStage() returns that value.
     *
     * @param reportStage Next report stage
     */
    public void newReportStage(int reportStage) {
    }

    /**
     * Stage of the report. The first stage is zero and every call to newReportStage() increments the stage.
     *
     * @return Report stage.
     */
    public int getReportStage() {
        return 0;
    }

    /**
     * Title to be displayed on each page. This method is invoked only once and the same title is drawn on all pages unless newReportStage()
     * is called. This could be a String, a PDFCell or any other object from which a PDFCell can be created, including a PDFTable.
     * @return Default return value is null, meaning nothing will be displayed as page title.
     */
    public Object getTitle() {
        return null;
    }

    /**
     * Sets the Entity so that the "letter head" can be printed. If this method or setTransaction() is not invoked in the letter head mode,
     * letter head will not be printed. If both this method and setTransactionManager() are invoked, setTransactionManager() method will be
     * used only for printing water marks and this method will be used for printing letter head.
     *
     * @param entity Entity to be set.
     */
    public final void setEntity(Entity entity) {
    }

    /**
     * Get the 'Report Format Definition' for this report.
     * It will be determined by looking at the value of 'Transaction Manager' or 'Entity'.
     *
     * @return Report Format Definition for this report.
     */
    public ReportFormat getReportFormat() {
        return new ReportFormat();
    }

    /**
     * Set the 'Report Format Definition' for this report.
     * This will override the default one.
     *
     * @param reportFormat Report Format Definition for this report.
     */
    public void setReportFormat(ReportFormat reportFormat) {
    }

    /**
     * Get the name of the entity (If the entity can not be identified from "Transaction Manager" or "Entity", then, a blank String
     * is returned.
     *
     * @return Name of the entity.
     */
    public String getEntityName() {
        return null;
    }

    /**
     * Get the location of the entity (If the entity can not be identified from "Transaction Manager" or "Entity", then, a blank String
     * is returned.
     *
     * @return Location of the entity.
     */
    public String getEntityLocation() {
        return null;
    }

    /**
     * Get the entity (If the entity can not be identified from "Transaction Manager" or "Entity", then, null is returned.
     *
     * @return The entity.
     */
    public Entity getEntity() {
        return null;
    }

    /**
     * Generate the content by writing stuff to the PDF.
     * @throws Exception Any exception.
     */
    public abstract void generateContent() throws java.lang.Exception;


    /**
     * Add an image to the next page as the content. The image will fill the page 100%.
     * @param image Image to be added.
     */
    public void addContent(PDFImage image) {
    }

    /**
     * Add an image to the next page as the content. The image will be added at the center of the page.
     * @param image Image to be added.
     * @param scalePercentage Scale percentage within the page. Use 100 for filling 100%.
     */
    public void addContent(PDFImage image, float scalePercentage) {
    }

    /**
     * Add content from another PDF content producer. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @throws Exception Any exception.
     */
    public void addContent(ContentProducer externalPDF, float scalePercentage) throws Exception {
    }

    /**
     * Add content from another PDF content producer. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @param startingPage Pages will be inserted from the starting page (Page number starts from 1).
     * @param endingPage Pages will be inserted till this page. If 0 is specified, it will be interpreted as the last page of the content.
     * Negative numbers will be subtracted from last page. So, from the content, if last 3 pages to be dropped,
     * just pass -3 as the endingPage.
     * @param pagesToSkip Pages to skip.
     * @throws Exception Any exception.
     */
    public void addContent(ContentProducer externalPDF, float scalePercentage, int startingPage, int endingPage, int... pagesToSkip)
            throws Exception {
    }

    /**
     * Add content from a PDF file. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @throws Exception Any exception.
     */
    public void addContent(FileData externalPDF, float scalePercentage) throws Exception {
    }

    /**
     * Add content from a PDF file. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @param startingPage Pages will be inserted from the starting page (Page number starts from 1).
     * @param endingPage Pages will be inserted till this page. If 0 is specified, it will be interpreted as the last page of the content.
     * Negative numbers will be subtracted from last page. So, from the content, if last 3 pages to be dropped,
     * just pass -3 as the endingPage.
     * @param pagesToSkip Pages to skip.
     * @throws Exception Any exception.
     */
    public void addContent(FileData externalPDF, float scalePercentage, int startingPage, int endingPage, int... pagesToSkip)
            throws Exception {
    }

    /**
     * Add content from a PDF stream. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @throws Exception Any exception.
     */
    public void addContent(StreamData externalPDF, float scalePercentage) throws Exception {
    }

    /**
     * Add content from a PDF stream. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @param startingPage Pages will be inserted from the starting page (Page number starts from 1).
     * @param endingPage Pages will be inserted till this page. If 0 is specified, it will be interpreted as the last page of the content.
     * Negative numbers will be subtracted from last page. So, from the content, if last 3 pages to be dropped,
     * just pass -3 as the endingPage.
     * @param pagesToSkip Pages to skip.
     * @throws Exception Any exception.
     */
    public void addContent(StreamData externalPDF, float scalePercentage, int startingPage, int endingPage, int... pagesToSkip)
            throws Exception {
    }

    /**
     * Add content from a PDF stream. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @throws Exception Any exception.
     */
    public void addContent(InputStream externalPDF, float scalePercentage) throws Exception {
    }

    /**
     * Add content from a PDF stream. The method addingExternalContent will be invoked before adding each page image.
     * @param externalPDF External PDF to add.
     * @param scalePercentage Scale percentage of each page.
     * @param startingPage Pages will be inserted from the starting page (Page number starts from 1).
     * @param endingPage Pages will be inserted till this page. If 0 is specified, it will be interpreted as the last page of the content.
     * Negative numbers will be subtracted from last page. So, from the content, if last 3 pages to be dropped,
     * just pass -3 as the endingPage.
     * @param pagesToSkip Pages to skip.
     * @throws Exception Any exception.
     */
    public void addContent(InputStream externalPDF, float scalePercentage, int startingPage, int endingPage, int... pagesToSkip)
            throws Exception {
    }

    protected void aboutToAddExternalContent(int page, PDFImage pageImage) {
    }

    public void addingExternalContent(int page, PDFImage content) {
    }

    /**
     * Creates a table that can fit 100% to document's width.
     *
     * @param columnCount Number of columns in the table.
     * @return The table
     */
    public static PDFTable createTable(int columnCount) {
        return new PDFTable(columnCount);
    }

    /**
     * Creates a table that can fit 100% to document's width.
     *
     * @param relativeWidths Relative (or percentage) widths of the columns to be created.
     * @return The table
     */
    public static PDFTable createTable(int... relativeWidths) {
        return new PDFTable(1);
    }

    /**
     * Set a cell customizer that will be called whenever a table cell is created.
     * The default customizer draws a border around the cell. So, if you don't want that, set this to null or to
     * your own custom one.
     *
     * @param defaultCellCustomizer Default cell customizer. Could be null.
     */
    public void setDefaultCellCustomizer(Consumer<PDFCell> defaultCellCustomizer) {
    }

    /**
     * Creates a centered cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase or PDFChunk.
     * @return The cell that is created.
     */
    public PDFCell createCenteredCell(Object object) {
        return new PDFCell();
    }

    /**
     * Creates a centered cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase, PDFChunk,
     * Image, StreamData or Barcode.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell created will be passed through this for further customization.
     * @return The cell that is created.
     */
    public PDFCell createCenteredCell(Object object, Function<PDFCell, PDFCell> cellCustomizer) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase or PDFChunk.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase, PDFChunk,
     * Image, StreamData or Barcode.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell created will be passed through this for further customization.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, Function<PDFCell, PDFCell> cellCustomizer) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase or PDFChunk.
     * @param rightAligned Whether the cell is right aligned or not.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, boolean rightAligned) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase, PDFChunk,
     * Image, StreamData or Barcode.
     * @param rightAligned Whether the cell is right aligned or not.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell created will be passed through this for further customization.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, boolean rightAligned, Function<PDFCell, PDFCell> cellCustomizer) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase or PDFChunk.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, int horizontalAlignment) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase, PDFChunk,
     * Image, StreamData or Barcode.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell created will be passed through this for further customization.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, int horizontalAlignment, Function<PDFCell, PDFCell> cellCustomizer) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase or PDFChunk.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param verticalAlignment Alignment value from PDFElement.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, int horizontalAlignment, int verticalAlignment) {
        return new PDFCell();
    }

    /**
     * Creates a cell that can be added to a table.
     *
     * @param object Object that provides the content of the cell - object.toString(). Object can also be either a PDFImage, PDFCell, PDFTable, PDFPhrase, PDFChunk,
     * Image, StreamData or Barcode.
     * @param horizontalAlignment Alignment value from PDFElement.
     * @param verticalAlignment Alignment value from PDFElement.
     * @param cellCustomizer Cell customizer. If non-null value is passed, the cell created will be passed through this for further customization.
     * @return The cell that is created.
     */
    public PDFCell createCell(Object object, int horizontalAlignment, int verticalAlignment, Function<PDFCell, PDFCell> cellCustomizer) {
        return new PDFCell();
    }

    /**
     * Print page number or not.
     * @param print True or false
     */
    public void printPageNumber(boolean print) {
    }

    /**
     * Print page number or not.
     * @return True or false
     */
    public boolean printPageNumber() {
        return false;
    }

    /**
     * Print total page number or not.
     * @param print True or false
     */
    public void printTotalPageNumber(boolean print) {
    }

    /**
     * Print total page number or not.
     * @return True or false
     */
    public boolean printTotalPageNumber() {
        return false;
    }

    /**
     * Print audit trail at the bottom of the page
     * @return True or false
     */
    public boolean printAuditTrail() {
        return true;
    }

    /*
     * Sets the logo for the letterhead
     *
     * @param logo Logo image
     */
    public void setLogo(PDFImage image) {
    }

    /*
     * Sets the logo for the letterhead
     *
     * @param logo Logo image
     */
    public void setLogo(java.awt.Image image) {
    }

    /**
     * Check whether the letterhead needs to be printed or not.
     *
     * @return True/false.
     */
    final boolean printLetterhead() {
        return Math.random() > 0.5;
    }

    /**
     * Document margin. This is invoked only once to create the document unless newReportStage() is called.
     * Override this to set a different margin.
     *
     * @return Margin. Default is 36 = 0.5 inch.
     */
    public float getLeftMargin() {
        return 0;
    }

    /**
     * Document margin. This is invoked only once to create the document unless newReportStage() is called.
     * Override this to set a different margin.
     *
     * @return Margin. Default is 36 = 0.5 inch.
     */
    public float getRightMargin() {
        return 0;
    }

    /**
     * Document margin. This is invoked only once to create the document unless newReportStage() is called.
     * Override this to set a different margin.
     *
     * @return Margin. Default is 72 = 1 inch.
     */
    public float getTopMargin() {
        return 0;
    }

    /**
     * Document margin. This is invoked only once to create the document unless newReportStage() is called.
     * Override this to set a different margin.
     *
     * @return Margin. Default is 72 = 1 inch.
     */
    public float getBottomMargin() {
        return 0;
    }

    /**
     * Gets the printable width.
     * @return Width
     */
    public final float getWidth() {
        return 0;
    }

    /**
     * Gets the printable height.
     *
     * @return Height
     */
    public final float getHeight() {
        return 0;
    }

    /**
     * Page size. This is invoked only once to create the document unless newReportStage() is called. This is used to control the size of the page.
     * For example, if you want landscape A4 size, simply override this method to return super.getPageSize().rotate().
     *
     * @return PDFRectangle representing page size. Default is A4 size.
     */
    public PDFRectangle getPageSize() {
        return null;
    }

    /**
     * Page orientation - Portrait or Landscape
     *
     * @return ORIENTATION_PORTRAIT or ORIENTATION_LANDSCAPE. Default is ORIENTATION_PORTRAIT
     */
    public int getPageOrientation() {
        return 0;
    }

    /**
     * Create title Phrase. It will be created with (getFontSize() + 2) Point, Black, Bold, Helvetica font.
     * @param title Title
     * @return A Phrase that is suitable for drawing titles.
     */
    public PDFPhrase createTitleText(String title) {
        return null;
    }

    /**
     * Create title Phrase. It will be created with in Black, Bold, Helvetica font.
     * @param title Title
     * @param pointSize Font size
     * @return A Phrase that is suitable for drawing titles.
     */
    public PDFPhrase createTitleText(String title, int pointSize) {
        return null;
    }

    /**
     * Create title Phrase. It will be created with in Bold, Helvetica font.
     * @param title Title
     * @param pointSize Font size
     * @param color Color
     * @return A Phrase that is suitable for drawing titles.
     */
    public PDFPhrase createTitleText(String title, int pointSize, PDFColor color) {
        return null;
    }

    /**
     * Footer to be displayed on each page. This method is invoked only once and the same footer is drawn on all pages unless newReportStage()
     * is called. This could be a String, a PDFCell or any other object from which a PDFCell can be created, including a PDFTable.
     * @return Default return value is null, meaning nothing will be displayed as page footer.
     */
    public Object getFooter() {
        return null;
    }

    /**
     * Print letter head on every page or not.
     * @param print True or false
     */
    public void printLetterHeadOnEveryPage(boolean print) {
    }

    /**
     * Print letter head on every page or not.
     * @return True or false
     */
    public boolean printLetterHeadOnEveryPage() {
        return false;
    }

    /**
     * Create text Chunk. It will be created with in Helvetica font.
     * @param text Text
     * @return Chunk containing the text.
     */
    public PDFChunk createText(String text) {
        return null;
    }

    /**
     * Create text Chunk. It will be created with in Helvetica font.
     * @param text Text
     * @param pointSize Font size
     * @return Chunk containing the text.
     */
    public PDFChunk createText(String text, int pointSize) {
        return null;
    }

    /**
     * Create text Chunk. It will be created with in Helvetica font.
     * @param text Text
     * @param pointSize Font size
     * @param color Color
     * @return Chunk containing the text.
     */
    public PDFChunk createText(String text, int pointSize, PDFColor color) {
        return null;
    }

    /**
     * Sets the Transaction Manager so that the "letter head" can be printed. If this method or setEntity() is not invoked in the letter head mode,
     * letter head will not be printed. Also, this is needed to print water marks on the generated output.
     *
     * @param tm The Transaction Manager to be set.
     */
    @Override
    public final void setTransactionManager(com.storedobject.core.TransactionManager tm) {
    }

    /**
     * Gets the Transaction Manager
     * @return Transaction Manager
     */
    public final TransactionManager getTransactionManager() {
        return new TransactionManager(null, null);
    }

    /**
     * Gets the file name extension.
     *
     * @return "pdf"
     */
    @Override
    public final String getFileExtension() {
        return null;
    }


    /**
     * Set the page number to a new value.
     *
     * @param pageNumber Page number to be set.
     */
    public void setPageNumber(int pageNumber) {
    }

    /**
     * Get the current page number.
     *
     * @return Current page number. Returns 0 if nothing started yet.
     */
    public int getPageNumber() {
        return 0;
    }

    public void addTitles(PDFTable table, Stream<String> titles) {
    }

    /**
     * Add title texts to table.
     * @param table Table to which title texts to be added.
     * @param titles Titles
     */
    public void addTitles(PDFTable table, String... titles) {
    }

    /**
     * Add title texts to table.
     * @param table Table to which title texts to be added.
     * @param titles Titles
     */
    public void addTitles(PDFTable table, StringList titles) {
    }

    /**
     * Add title texts to table.
     * @param table Table to which title texts to be added.
     * @param pointSize Point size of the title.
     * @param titles Titles
     */
    public void addTitles(PDFTable table, int pointSize, Stream<String> titles) {
    }

    /**
     * Add title texts to table.
     * @param table Table to which title texts to be added.
     * @param pointSize Point size of the title.
     * @param titles Titles
     */
    public void addTitles(PDFTable table, int pointSize, String... titles) {
    }

    /**
     * Add title texts to table.
     * @param table Table to which title texts to be added.
     * @param pointSize Point size of the title.
     * @param titles Titles
     */
    public void addTitles(PDFTable table, int pointSize, StringList titles) {
    }

    /**
     * Adds a blank row to the table.
     *
     * @param table Table to which row to be added.
     */
    public void addBlankRow(PDFTable table) {
    }

    /**
     * Adds a blank row to the table.
     *
     * @param table Table to which row to be added.
     * @param fromColumn Column from which blank cells to be added.
     */
    public void addBlankRow(PDFTable table, int fromColumn) {
    }

    /**
     * Adds a blank row to the table.
     *
     * @param table Table to which row to be added.
     * @param fromColumn Column from which blank cells to be added.
     * @param toColumn Column up to which blank cells to be added.
     */
    public void addBlankRow(PDFTable table, int fromColumn, int toColumn) {
    }

    /**
     * Add an image at position (0, 0) in the current page.
     *
     * @param image Image to add.
     * @throws PDFException Exception if not successful.
     */
    public void addImage(PDFImage image) throws PDFException {
    }

    /**
     * Add an image at some absolute position in the current page.
     *
     * @param image Image to add.
     * @param positionX X position (from the left).
     * @param positionY Y position (from the bottom).
     * @throws PDFException Exception if not successful.
     */
    public void addImage(PDFImage image, float positionX, float positionY) throws PDFException {
    }

    /**
     * Add an image at some absolute position in the current page.
     *
     * @param image Image to add.
     * @param positionX X position (from the left).
     * @param positionY Y position (from the bottom).
     * @param width Width to which image to be fit.
     * @param height Height to which image to be fit.
     * @throws PDFException Exception if not successful.
     */
    public void addImage(PDFImage image, float positionX, float positionY, float width, float height) throws PDFException {
    }

    /**
     * Add an object to the document. Object can be 'text', a PDFImage, a PDFCell, a PDFTable or anything else.
     *
     * @param any Any object
     */
    public void addCentered(Object any) {
    }

    /**
     * Add some vertical gap (Typically used to add space before adding a table).
     * @param verticalGap Amount of space to be added
     */
    public void addGap(int verticalGap) {
    }

    /**
     * Get the current Y position of the PDF
     * @return Current Y
     */
    public float getY() {
        return 0;
    }

    /**
     * Get the current Y position of the PDF
     * @param ensureNewLine See whether line feed needs to be considered when computing this.
     * @return Current Y
     */
    public float getY(boolean ensureNewLine) {
        return 0;
    }

    /**
     * Draw the table at the current position till the end of the page. Table will be shrunk by deleting the rows drawn and the rest of
     * the rows may be drawn again at some other position, may be in another page.
     * @param table Table to be drawn
     * @return True if all rows are drawn in the current page.
     */
    public boolean drawTable(PDFTable table) {
        return false;
    }

    /**
     * Draw the table at the given position till the end of the page. Table will be shrunk by deleting the rows drawn and the rest of
     * the rows may be drawn again at some other position, may be in another page.
     * @param table Table to be drawn
     * @param x X position
     * @param y Y position
     * @return True if all rows are drawn in the current page.
     */
    public boolean drawTable(PDFTable table, float x, float y) {
        return false;
    }

    /**
     * Draw the table within the given rectangular area. Table will be shrunk by deleting the rows drawn and the rest of
     * the rows may be drawn again at some other position, may be in another page.
     * @param table Table to be drawn
     * @param x1 Upper left X position
     * @param y1 Upper left Y position
     * @param x2 Lower right X position
     * @param y2 Lower right Y position
     * @return True if all rows are drawn within the area.
     */
    public boolean drawTable(PDFTable table, float x1, float y1, float x2, float y2) {
        return false;
    }

    /**
     * Create a graphics object to draw directly to the PDF. Once the drawing is completed, invoke the dispose method.
     *
     * @return Graphics2D
     */
    public java.awt.Graphics2D createGraphics() {
        return null;
    }

    /**
     * Create a graphics object to draw directly to the PDF. Once the drawing is completed, invoke the dispose method.
     *
     * @param positionX X position (from the left).
     * @param positionY Y position (from the bottom).
     * @param width Width of the graphics.
     * @param height Height of the graphics.
     * @return Graphics2D
     */
    public java.awt.Graphics2D createGraphics(float positionX, float positionY, float width, float height) {
        return null;
    }

    /**
     * Create image from SVG
     * @param svg SVG source
     * @return Image
     */
    public static PDFImage createImageFromSVG(String svg) {
        return null;
    }

    /**
     * Create image from SVG
     * @param svg SVG source
     * @param width Image width
     * @param height Image height
     * @return Image
     */
    public static PDFImage createImageFromSVG(String svg, int width, int height) {
        return null;
    }

    /**
     * Create a PDF image where a painter decides how to paint the image.
     *
     * @param painter The painter whose accept method is invoked with a {@link Graphics2D} object do the painting
     * @return PDF image that covers the full page.
     */
    public PDFImage createImage(Consumer<Graphics2D> painter) {
        return createImage(painter,0,0);
    }

    /**
     * Create a PDF image where a painter decides how to paint the image.
     *
     * @param painter The painter whose accept method is invoked with a {@link Graphics2D} object do the painting
     * @param width Width of the image
     * @param height Height of the image
     * @return PDF image.
     */
    public PDFImage createImage(Consumer<Graphics2D> painter, int width, int height) {
        return null;
    }

    /**
     * Create image from barcode
     * @param barcode Barcode
     * @return Image
     */
    public static PDFImage createImage(Barcode barcode) {
        return null;
    }

    /**
     * Create image from a signature instance
     * @param signature Signature
     * @return Image
     */
    public static PDFImage createImage(Signature signature) {
        return null;
    }

    /**
     * Create image from stream data
     * @param streamData Stream data
     * @return Image
     */
    public static PDFImage createImage(StreamData streamData) {
        return null;
    }

    /**
     * Create image from an input stream (stream will be closed)
     * @param stream Input stream
     * @return Image
     */
    public static PDFImage createImage(InputStream stream) {
        return null;
    }

    /**
     * Create image from Java AWT image
     * @param image AWT image
     * @return Image
     */
    public static PDFImage createImage(java.awt.Image image) {
        return null;
    }

    /**
     * Add HTML content to the rest of the page
     * @param html HTML content
     */
    public void addHTML(String html) {
        addHTML(html, -1, -1);
    }

    /**
     * Add HTML content to the rest of the page
     * @param filler Filler to fill up the html text
     * @param html HTML content
     */
    public void addHTML(String html, StringFiller filler) {
        addHTML(html, filler, -1, -1);
    }

    /**
     * Add HTML content at the current position with the given width and height
     * @param html HTML content
     * @param width Width
     * @param height Height
     */
    public void addHTML(String html, int width, int height) {
        addHTML(html, null, width, height);
    }

    /**
     * Add HTML content at the given position with the given width and height
     * @param html HTML content
     * @param positionX Position X
     * @param positionY Position Y
     * @param width Width
     * @param height Height
     */
    public void addHTML(String html, int positionX, int positionY, int width, int height) {
        addHTML(html, null, positionX, positionY, width, height);
    }

    /**
     * Add HTML content at the current position with the given width and height
     * @param html HTML content
     * @param filler Filler to fill up the html text
     * @param width Width
     * @param height Height
     */
    public void addHTML(String html, StringFiller filler, int width, int height) {
        addHTML(html, filler, -1, -1, width, height);
    }

    /**
     * Add HTML content at the given position with the given width and height
     * @param html HTML content
     * @param filler Filler to fill up the html text
     * @param positionX Position X
     * @param positionY Position Y
     * @param width Width
     * @param height Height
     */
    public void addHTML(String html, StringFiller filler, int positionX, int positionY, int width, int height) {
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @return Image
     */
    public static PDFImage createImageFromHTML(String html) {
        return null;
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @param filler Filler to fill up the html text
     * @return Image
     */
    public static PDFImage createImageFromHTML(String html, StringFiller filler) {
        return null;
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @param width Image width
     * @param height Image height
     * @return Image
     */
    public static PDFImage createImageFromHTML(String html, int width, int height) {
        return null;
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @param filler Filler to fill up the html text
     * @param width Image width
     * @param height Image height
     * @return Image
     */
    public static PDFImage createImageFromHTML(String html, StringFiller filler, int width, int height) {
        return null;
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @return Image
     */
    public static PDFImage createImage(HTMLText html) {
        return null;
    }

    /**
     * Create image from HTML
     * @param html HTML content
     * @param width Image width
     * @param height Image height
     * @return Image
     */
    public static PDFImage createImage(HTMLText html, int width, int height) {
        return null;
    }

    /**
     * Create and add a new page
     *
     * @return True if the page is created and added
     */
    public boolean newPage() {
        return false;
    }


    /**
     * Get the user for which this PDF is being generated. This information will be used for formatting the
     * money values.
     *
     * @return User.
     */
    public SystemUser getUser() {
        return null;
    }

    /**
     * This method will be called whenever a new page is started. This can be overridden for customizing the page.
     */
    public void pageStarted() {
    }

    /**
     * This method will be called whenever a page is completed. This can be overridden for customizing the page.
     */
    public void pageCompleted() {
    }

    /**
     * Gets the currently set water mark.
     * @return Current water mark if any, otherwise null will be returned.
     */
    public String getWaterMark() {
        return null;
    }

    /**
     * Sets the water mark.
     * @param waterMark Water mark text.
     */
    public void setWaterMark(String waterMark) {
    }

    /**
     * Show text at some specified position.
     * @param text Text to be shown.
     * @param x X position.
     * @param y Y position.
     * @param alignment Alignment - Use PDFElement.ALIGN_CENTER etc.
     * @param rotation Angle of rotation.
     * @param font Font to be used, pass 'null' if base font to be used.
     * @param fontSize Font size in points.
     * @param color Color to be used for the text.
     * @param opacity Opacity to be used. (A value between 0.0 to 1.0).
     */
    public void showText(String text, int x, int y, int alignment, float rotation, PDFFont font, float fontSize, PDFColor color, float opacity) {
        new ObjectTable<>(null);
    }

    /**
     * Set additional error information such that it will be printed in the log when an error happens. Any error
     * thrown when generating reports will be dumped to the report output and system log.
     *
     * @param error Error information.
     */
    public final void setError(String error) {
    }

    /**
     * Dump the error that happened during reporting.
     *
     * @param error Error o dump.
     */
    public final void dumpError(Throwable error) {
    }

    /**
     * Log something via the logger associated with this report.
     *
     * @param anything Anything to log.
     */
    public final void log(Object anything) {
        Device d = getDevice();
        if(d == null) {
            StoredObject.logger.log(Level.WARNING, getClass().getName(), anything);
        } else {
            d.log(anything);
        }
    }

    public Device getDevice() {
        return null;
    }

    /**
     * Define a table where {@link StoredObject} instances can be added as rows.
     *
     * @param <T> Class type of the {@link StoredObject} instance
     */
    public class ObjectTable<T extends StoredObject> extends PDFTable {

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param attributes List of attributes to be reported
         */
        public ObjectTable(Class<T> objectClass, String... attributes) {
            this(objectClass, true, attributes);
        }

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param attributes List of attributes to be reported
         */
        public ObjectTable(Class<T> objectClass, StringList attributes) {
            this(objectClass, true, attributes);
        }

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param withHeadings Whether header columns to be created automatically or not
         * @param attributes List of attributes to be reported
         */
        public ObjectTable(Class<T> objectClass, boolean withHeadings, String... attributes) {
            this(objectClass, StringList.create(attributes), withHeadings);
        }

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param withHeadings Whether header columns to be created automatically or not
         * @param attributes List of attributes to be reported
         */
        public ObjectTable(Class<T> objectClass, boolean withHeadings, StringList attributes) {
            this(objectClass, attributes, withHeadings);
        }

        private ObjectTable(Class<T> objectClass, StringList attributes, boolean withHeadings) {
            super(objectClass == null && withHeadings ? 0 : attributes.size());
        }

        /**
         * Generate the header row.
         */
        public void generateHeaderRow() {
        }

        /**
         * Add an object instance to the table. This will create one row.
         *
         * @param object Object to be added.
         */
        public void addObject(T object) {
        }

        /**
         * Create the header cell. Override this method if you want a customized cell.
         *
         * @param attribute Attribute for which cell to be created
         * @return The cell that is created.
         */
        protected PDFCell createHeaderCell(String attribute) {
            return createCell(createTitleText(StringUtility.makeLabel(attribute)));
        }

        /**
         * Create the value cell. Override this method if you want a customized cell.
         *
         * @param attribute Attribute for which cell to be created
         * @param value Value of the cell
         * @return The cell that is created.
         */
        protected PDFCell createValueCell(String attribute, Object value) {
            return createCell(createCell(value));
        }

        /**
         * By invoking this method, the output can be made available as stringified (decoded) values.
         * <p>Note: This needs to be invoked before adding any objects to the table</p>
         */
        public void stringifyValues() {
        }

        /**
         * Configure the given attribute to use the supplied function for obtaining its values instead of the
         * method of the object's class.
         * <p>Note: This needs to be invoked before adding any objects to the table</p>
         *
         * @param attribute Name of the attribute to configure.
         * @param valueFunction Function to obtain the attribute value.
         */
        public void configureAttribute(String attribute, Function<T, ?> valueFunction) {
        }
    }

    /**
     * Define a table where {@link StoredObject} instances can be added in a "form" style (Each attribute is printed
     * in a separate row).
     *
     * @param <T> Class type of the {@link StoredObject} instance
     */
    public class ObjectFormTable<T extends StoredObject> extends PDFTable {

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param attributes List of attributes to be reported
         */
        public ObjectFormTable(Class<T> objectClass, String... attributes) {
            this(objectClass, StringList.create(attributes));
        }

        /**
         * Create a table for the given type of {@link StoredObject} class.
         *
         * @param objectClass Object class
         * @param attributes List of attributes to be reported
         */
        public ObjectFormTable(Class<T> objectClass, StringList attributes) {
            super(2);
        }

        /**
         * Add an object instance to the table. This will create one row for each attribute.
         *
         * @param object Object to be added.
         */
        public void addObject(T object) {
        }

        /**
         * Create the label cell. Override this method if you want a customized cell.
         *
         * @param attribute Attribute for which cell to be created
         * @param label Label to be printed (Label is automatically generated from the attribute)
         * @return The cell that is created.
         */
        protected PDFCell createLabelCell(String attribute, String label) {
            return createCell(label);
        }

        /**
         * Create the value cell. Override this method if you want a customized cell.
         *
         * @param attribute Attribute for which cell to be created
         * @param value Value of the cell
         * @return The cell that is created.
         */
        protected PDFCell createValueCell(String attribute, Object value) {
            return createCell(createCell(value));
        }

        /**
         * By invoking this method, the output can be made available as stringified (decoded) values.
         * <p>Note: This needs to be invoked before adding any objects to the table</p>
         */
        public void stringifyValues() {
        }

        /**
         * Configure the given attribute to use the supplied function for obtaining its values instead of the
         * method of the object's class.
         * <p>Note: This needs to be invoked before adding any objects to the table</p>
         *
         * @param attribute Name of the attribute to configure.
         * @param valueFunction Function to obtain the attribute value.
         */
        public void configureAttribute(String attribute, Function<T, ?> valueFunction) {
        }
    }

    /**
     * Used to define text to include in the PDF document.
     */
    public class Text extends PDFPhrase implements StyledBuilder {

        /**
         * Creates an empty text string.
         */
        public Text() {
            this("");
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         */
        public Text(Object text) {
            this(text, (PDFFont)null);
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         * @param font Font for this text.
         */
        public Text(Object text, PDFFont font) {
            pageCompleted();
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         * @param fontSize Font size.
         */
        public Text(Object text, int fontSize) {
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         * @param fontSize Font size.
         * @param fontStyle Style of the font to be appended (PDFFont.BOLD, PDFFont.ITALIC etc.)
         */
        public Text(Object text, int fontSize, int fontStyle) {
        }

        /**
         * Creates a text string from the object.
         *
         * @param fontSize Font size.
         */
        public Text(int fontSize) {
            this("", fontSize);
        }

        /**
         * Creates a text string from the object.
         *
         * @param fontSize Font size.
         * @param fontStyle Style of the font to be appended (PDFFont.BOLD, PDFFont.ITALIC etc.)
         */
        public Text(int fontSize, int fontStyle) {
            this("", fontSize, fontStyle);
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         * @param color Color for this text.
         */
        public Text(Object text, Color color) {
        }

        /**
         * Creates a text string from the object.
         *
         * @param text Object to be converted to text.
         * @param color Color for this text.
         */
        public Text(Object text, PDFColor color) {
        }

        /**
         * Create a text string from the chunk.
         *
         * @param chunk Chunk from which the text is created. Font of this chunk is also accepted.
         */
        public Text(PDFChunk chunk) {
        }

        /**
         * Appends another chunk to this text.
         *
         * @param chunk Chunk to be appended
         * @return This
         */
        public Text append(PDFChunk chunk) {
            return null;
        }

        /**
         * Appends a color to this text. This color will be used for further texts added.
         *
         * @param color Color to be appended
         * @return This
         */
        public Text append(Color color) {
            return null;
        }

        /**
         * Appends a color to this text. This color will be used for further texts added.
         *
         * @param color Color to be appended
         * @return This
         */
        public Text append(PDFColor color) {
            return null;
        }

        /**
         * Appends a color to this text. This color will be used for further texts added.
         *
         * @param color Color to be appended
         * @return This
         */
        public Text append(Object object, String color) {
            return null;
        }

        /**
         * Appends a font to this text. This font will be used for further texts added.
         *
         * @param fontSize Point size of the font to be appended
         * @return This
         */
        public Text append(int fontSize) {
            return null;
        }

        /**
         * Appends a font to this text. This font will be used for further texts added.
         *
         * @param fontSize Point size of the font to be appended
         * @param fontStyle Style of the font to be appended (PDFFont.BOLD, PDFFont.ITALIC etc.)
         * @return This
         */
        public Text append(int fontSize, int fontStyle) {
            return null;
        }

        /**
         * Appends a font to this text. This font will be used for further texts added.
         *
         * @param text  Text to be appended.
         * @param fontSize Point size of the font to be appended
         * @return This
         */
        public Text append(Object text, int fontSize) {
            return append(fontSize).append(text);
        }

        /**
         * Appends a font to this text. This font will be used for further texts added.
         *
         * @param text  Text to be appended.
         * @param fontSize Point size of the font to be appended
         * @param fontStyle Style of the font to be appended (PDFFont.BOLD, PDFFont.ITALIC etc.)
         * @return This
         */
        public Text append(Object text, int fontSize, int fontStyle) {
            return append(fontSize, fontStyle).append(text);
        }

        /**
         * Appends a font to this text. This font will be used for further texts added.
         *
         * @param font Font to be appended
         * @return This
         */
        public Text append(PDFFont font) {
            return null;
        }

        /**
         * Appends text from the object
         *
         * @param text Object to be converted to text.
         * @return This
         */
        public Text append(Object text) {
            return null;
        }

        /**
         * Appends text from the object
         *
         * @param text Object to be converted to text.
         * @param font Font to be used.
         * @return This
         */
        public Text append(Object text, PDFFont font) {
            return null;
        }

        /**
         * Appends text from the object
         *
         * @param text Object to be converted to text.
         * @param color Color to be used.
         * @return This
         */
        public Text append(Object text, java.awt.Color color) {
            return null;
        }

        /**
         * Appends text from the object
         *
         * @param text Object to be converted to text.
         * @param color Color to be used.
         * @return This
         */
        public Text append(Object text, PDFColor color) {
            return null;
        }

        /**
         * Append a new line. It will not be forced if we are already on a new line
         *
         * @return This
         */
        public Text newLine() {
            return null;
        }

        /**
         * Append a new line.
         *
         * @param forceIt Force the new line.
         * @return This
         */
        public Text newLine(boolean forceIt) {
            return null;
        }

        @Override
        public boolean isNewLine() {
            return false;
        }

        /**
         * Checks if empty or not.
         *
         * @return True or false.
         */
        public boolean empty() {
            return false;
        }

        /**
         * Resets the font to the default.
         *
         * @return This
         */
        public Text resetFont() {
            return null;
        }

        /**
         * Clear content
         *
         * @return this
         */
        public Text clearContent() {
            return null;
        }
    }

    /**
     * Reads and creates a 'Content' object from the stream containing PDF data.
     *
     * @param content Stream containing PDF. It could be from a PDF file or database.
     *
     * @return This
     * @throws IOException The stream may raise this exception.
     */
    public PDFContent readContent(InputStream content) throws IOException {
        return null;
    }

    /**
     * Representation of 'Content' from another PDF.
     */
    public class PDFContent {

        /**
         * Number of pages in this content.
         *
         * @return Number of pages.
         */
        public int getNumberOfPages() {
            return 0;
        }

        /**
         * Creates an image from a particular page of this 'Content'.
         *
         * @param page Page number (Starting from 1)
         * @return Image created.
         * @throws Exception Any exception.
         */
        public PDFImage getPageImage(int page) throws Exception {
            new Text();
            return null;
        }

        /**
         * Closes this 'Content'.
         */
        public void close() {
        }
    }
}
