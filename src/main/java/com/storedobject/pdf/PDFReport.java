package com.storedobject.pdf;

import com.storedobject.core.Device;

/**
 * A PDF class used for generating reports.
 *
 * @author Syam
 */
public abstract class PDFReport extends PDF {

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 */
	public PDFReport(Device device) {
		this(device, false, false);
	}

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 * @param letterhead Whether default letterhead needs to be printed or not.
	 */
	public PDFReport(Device device, boolean letterhead) {
		this(device, letterhead, false);
	}

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 * @param letterhead Whether default letterhead needs to be printed or not.
	 * @param printLogo Whether default logo needs to be printed or not.
	 */
	public PDFReport(Device device, boolean letterhead, boolean printLogo) {
		super(letterhead);
	}

	/**
	 * View the content on the device.
	 */
	public void view() {
	}

	/**
	 * Generate the content and view it on the device with a caption.
	 *
	 * @param caption Caption.
	 */
	public void execute(String caption) {
	}

	/**
	 * Get the device on which content will be rendered on.
	 *
	 * @return The device.
	 */
	public Device getDevice() {
    	return null;
	}

	/**
	 * Get the configured logo of the organization (could be <code>null</code>).
	 *
	 * @return The configured logo.
	 */
	public PDFImage getLogo() {
    	return null;
	}

	/**
	 * Get the configured logo of the product (could be <code>null</code>).
	 *
	 * @return The configured product logo.
	 */
	public PDFImage getProductLogo() {
    	return null;
	}

	/**
	 * Get the position of the logo in this report. By default, it looks for the "report format" configuration
	 * from which this value can be obtained. See {@link com.storedobject.core.ReportFormat}.
	 *
	 * @return Position.
	 */
	public int getLogoPosition() {
    	return 0;
	}

	/**
	 * Get the "title" part of the report. By default, title part is a {@link PDFTable} created from the
	 * configured logos (customer logo and product logo) and the "title table" part returned by
	 * the {@link #getTitleTable()} method.
	 *
	 * @return You can return anything and that will be converted appropriately to create the title part of the
	 * report.
	 */
	@Override
	public Object getTitle() {
    	return null;
	}

	/**
	 * Set the "title text" part of the report. The value set by this method is returned by the
	 * {@link #getTitleText()} method and used by the {@link #getTitleTable()} method.
	 *
	 * @param title Title text part of the report. Can be anything and will be converted appropriately.
	 */
	public void setTitleText(Object title) {
	}

	/**
	 * Get the "title text" part that was set by {@link #setTitleText(Object)}. (If nothing was ever set,
	 * <code>null</code> will be returned.
	 *
	 * @return "Title text" part of the report. (This is used by {@link #getTitleTable()} method).
	 */
	public Object getTitleText() {
    	return null;
	}

	/**
	 * Get the "title table" part of the report. It builds a {@link PDFTable} from the "title text" part returned
	 * by the {@link #getTitleText()} method. The {@link #getTitle()} method uses the return value of this
	 * to build the "title" part of the report.
	 *
	 * @return "Title table" part of the report.
	 */
	public PDFTable getTitleTable() {
    	return null;
	}
}
