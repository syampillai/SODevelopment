package com.storedobject.pdf;

import java.io.InputStream;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.Device;
import com.storedobject.core.FileData;
import com.storedobject.core.StreamData;

/**
 * A class that allows you to combine multiple other reports into a single report. Any number of other contents can
 * be added using one of the "addContent" methods.
 *
 * @author Syam
 */
public abstract class CombinedPDFReport extends PDFReport {

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 */
	public CombinedPDFReport(Device device) {
		super(device);
	}

	/**
	 * Add content from a "content producer".
	 *
	 * @param externalPDF Another content producer. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(ContentProducer externalPDF) throws Exception {
	}

	/**
	 * Add content from a "content producer".
	 *
	 * @param externalPDF Another content producer. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @param startingPage Starting page from which content will be added. (Page numbering starts from 1).
	 * @param endingPage Ending page up to which content will be added (Passing -1 will add up to the last page).
	 * @param pagesToSkip Pages to skip. These pages will not be added.
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(ContentProducer externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}

	/**
	 * Add content from "file data".
	 *
	 * @param externalPDF Content from "file data". (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(FileData externalPDF) throws Exception {
	}

	/**
	 * Add content from "file data".
	 *
	 * @param externalPDF Content from "file data". (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @param startingPage Starting page from which content will be added. (Page numbering starts from 1).
	 * @param endingPage Ending page up to which content will be added (Passing -1 will add up to the last page).
	 * @param pagesToSkip Pages to skip. These pages will not be added.
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(FileData externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}

	/**
	 * Add content from an "input stream".
	 *
	 * @param externalPDF Content from a stream. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(InputStream externalPDF) throws Exception {
	}

	/**
	 * Add content from an "input stream".
	 *
	 * @param externalPDF Content from a stream. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @param startingPage Starting page from which content will be added. (Page numbering starts from 1).
	 * @param endingPage Ending page up to which content will be added (Passing -1 will add up to the last page).
	 * @param pagesToSkip Pages to skip. These pages will not be added.
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(InputStream externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}

	/**
	 * Add content from "stream data".
	 *
	 * @param externalPDF Stream-data content. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(StreamData externalPDF) throws Exception {
	}

	/**
	 * Add content from "stream data".
	 *
	 * @param externalPDF Stream-data content. (If if the content-type is not PDF, it will be converted to PDF
	 *                    if possible).
	 * @param startingPage Starting page from which content will be added. (Page numbering starts from 1).
	 * @param endingPage Ending page up to which content will be added (Passing -1 will add up to the last page).
	 * @param pagesToSkip Pages to skip. These pages will not be added.
	 * @throws Exception Raises if any error occurs.
	 */
	public void addContent(StreamData externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}

	/**
	 * Set the orientation to be used when the next content is added.
	 *
	 * @param orientation Orientation value. ({@link #ORIENTATION_PORTRAIT} or {@link #ORIENTATION_LANDSCAPE}).
	 */
    public void setOrientation(int orientation) {
    }

	/**
	 * Get the current value of the scale percentage (See {@link #setScalePercentage(int)}).
	 *
	 * @return Current value of the scale percentage.
	 */
	public final int getScalePercentage() {
		return 0;
	}

	/**
	 * Set the percentage scaling to be done when the next content is added.
	 *
	 * @param scalePercentage Percentage value (1 to 100).
	 */
	public final void setScalePercentage(int scalePercentage) {
	}
}