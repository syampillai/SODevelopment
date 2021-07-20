package com.storedobject.pdf;

import com.storedobject.core.Device;
import com.storedobject.core.HasContacts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A PDF class used for generating reports.
 *
 * @author Syam
 */
public abstract class PDFReport extends PDF {

	private Device device;

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 */
	public PDFReport(Device device) {
		this(device, false, false);
		this.device = device;
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
    	return device;
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
	 * Get the name of the configured logo of the organization.
	 *
	 * @return Name of the logo. You may override this to print another logo.
	 */
	public String getLogoName() {
		return device.getDeviceLayout().getLogoName(getTransactionManager());
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
	 * Get the name of the configured product logo of the organization.
	 *
	 * @return Name of the product logo. You may override this to print another logo.
	 */
	public String getProductLogoName() {
		return device.getDeviceLayout().getProductLogoName();
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
	 * the {@link #getTitleTable()} method. However, this method is usually not overridden unless you want to
	 * create a report that doesn't include printing of logos etc. So, if you override this method, then, the
	 * {@link #getTitleTable()} is never used (unless you use it within your overridden method).
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
	 * <p>Note: Please make sure that this method is invoked from within the constructor itself because it will
	 * not have any effect if invoked from any other position.</p>
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

	/**
	 * This is a helper method to create a "title table" from a {@link HasContacts} instance. The
	 * {@link #getTitleTable()} can use this to create a "title table" quickly.
	 *
	 * @param hasContacts Contact instance. The name and address will be printed from this.
	 * @param captions Caption to be printed. The fist caption will be more highlighted than the subsequent ones.
	 * @return A table instance that can be used as a "title table".
	 */
	public PDFTable createTitleTable(HasContacts hasContacts, Iterable<String> captions) {
		if(captions == null) {
			return createTitleTable(hasContacts);
		}
		List<String> list;
		if(captions instanceof List) {
			list = (List<String>) captions;
		} else {
			list = new ArrayList<>();
			for(String c: captions) {
				list.add(c);
			}
		}
		String[] cs = new String[list.size()];
		int i = 0;
		for(String c: captions) {
			cs[i++] = c;
		}
		return createTitleTable(hasContacts, cs);
	}

	/**
	 * This is a helper method to create a "title table" from a {@link HasContacts} instance. The
	 * {@link #getTitleTable()} can use this to create a "title table" quickly.
	 *
	 * @param hasContacts Contact instance. The name and address will be printed from this.
	 * @param captions Caption to be printed. The fist caption will be more highlighted than the subsequent ones.
	 * @return A table instance that can be used as a "title table".
	 */
	public PDFTable createTitleTable(HasContacts hasContacts, String... captions) {
		Function<PDFCell, PDFCell> nb =
				c -> {
					c.setBorder(0);
					return c;
				};
		PDFTable table = createTable(60, 40);
		table.setBorderWidth(0);
		Text text = new Text(hasContacts.getName(), 14, PDFFont.BOLD);
		StringBuilder a = new StringBuilder();
		String s = hasContacts.getContact("Address");
		if(s != null) {
			a.append(s);
		}
		s = hasContacts.getContact("Phone");
		boolean phone;
		if(phone = s != null) {
			a.append("\nPhone: ").append(s);
		}
		s = hasContacts.getContact("Email");
		if(s != null) {
			a.append(phone ? ", " : "\n").append("Email: ").append(s);
		}
		text.newLine().append(a, 6, PDFFont.BOLD);
		table.addCell(createCell(text, nb));
		if(captions == null || captions.length == 0) {
			table.addCell(createCell(""), nb);
		} else {
			text = new Text(captions[0], 12, PDFFont.BOLD).newLine();
			for(int i = 1; i < captions.length; i++) {
				text.newLine(true).append(captions[i], 10, PDFFont.BOLD);
			}
			table.addCell(createCell(text, true), nb);
		}
		return table;
	}

	/**
	 * Log something via the logger associated with this report.
	 *
	 * @param anything Anything to log.
	 */
	public void log(Object anything) {
		getDevice().log(anything);
	}
}
