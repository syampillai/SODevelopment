package com.storedobject.pdf;

import com.storedobject.core.Device;
import com.storedobject.core.HasContacts;
import com.storedobject.core.MediaFile;
import com.storedobject.core.SystemUser;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

/**
 * A PDF class used for generating reports.
 *
 * @author Syam
 */
public abstract class PDFReport extends PDF {

	private static final Map<String, PDFImage> imageCache = new HashMap<>();
	private final Device device;
	private Object titleText;
	private final boolean printLogo;

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 */
	public PDFReport(Device device) {
		this(device, false, true);
	}

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 * @param letterhead Whether default letterhead needs to be printed or not.
	 */
	public PDFReport(Device device, boolean letterhead) {
		this(device, letterhead, true);
	}

	/**
	 * Constructor.
	 *
	 * @param device Device on which the output will be rendered when viewing.
	 * @param letterhead Whether default letterhead needs to be printed or not.
	 * @param printLogo Whether default logo needs to be printed or not.
	 */
	public PDFReport(Device device, boolean letterhead, boolean printLogo) {
		super(letterhead, null);
		this.device = device;
		setTransactionManager(device.getServer().getTransactionManager());
		if(printLogo) {
			setLogo(getLogo());
		}
		this.printLogo = printLogo;
	}

	@Override
	public InputStream extractContent() throws Exception {
		produce();
		return getContent();
	}

	@Override
	public void execute() {
		device.view(this);
	}

	/**
	 * Generate the content and view it on the device with a caption.
	 *
	 * @param caption Caption.
	 */
	public void execute(String caption) {
		device.view(caption, this);
	}

	/**
	 * View the content on the device.
	 */
	public void view() {
		execute();
	}

	@Override
	public SystemUser getUser() {
		SystemUser su = super.getUser();
		return su == null ? device.getServer().getTransactionManager().getUser() : su;
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
		return logo(getLogoName());
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
		return logo(getProductLogoName());
	}

	/**
	 * Get the name of the configured product logo of the organization.
	 *
	 * @return Name of the product logo. You may override this to print another logo.
	 */
	public String getProductLogoName() {
		return device.getDeviceLayout().getProductLogoName();
	}

	private PDFImage logo(String name) {
		if(name == null || name.isEmpty()) {
			return null;
		}
		MediaFile mf = MediaFile.get(name);
		if(mf == null || !mf.isImage()) {
			return null;
		}
		String key = mf.getId() + "/" + mf.getTransactionId();
		PDFImage image = imageCache.get(key);
		if(image == null) {
			try {
				image = createImage(mf.getFile().getContent());
			} catch (Throwable e) {
				return null;
			}
			imageCache.put(key, image);
		}
		return image;
	}

	/**
	 * Get the position of the logo in this report. By default, it looks for the "report format" configuration
	 * from which this value can be obtained. See {@link com.storedobject.core.ReportFormat}.
	 *
	 * @return Position.
	 */
	public int getLogoPosition() {
		return getReportFormat().getLogoPosition();
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
		if(printLetterhead()) {
			return super.getTitle();
		}
		PDFImage left = null, center = null, right = null;
		PDFTable table;
		if(printLogo) {
			switch(getLogoPosition()) {
				case 0 -> {
					left = getLogo();
					right = getProductLogo();
				}
				case 1 -> left = getLogo();
				case 2 -> {
					left = getProductLogo();
					right = getLogo();
				}
				case 3 -> right = getLogo();
				case 4 -> center = getLogo();
			}
		}
		PDFCell c;
		float m = getPageSize().getWidth();
		if(left != null && right != null) {
			table = createTable(3, m < 800 ? 20 : 30, 3);
		} else if(left != null) {
			table = createTable(3, m < 800 ? 23 : 33);
		} else if(right != null) {
			table = createTable(m < 800 ? 23 : 33, 3);
		} else {
			table = createTable(1);
		}
		if(center != null) {
			c = createCenteredCell(center);
			c.setBorder(0);
			table.addCell(c);
		}
		if(left != null) {
			c = createCenteredCell(left);
			c.setBorder(0);
			c.setPaddingRight(3);
			table.addCell(c);
		}
		PDFTable tt = getTitleTable();
		c = createCenteredCell(Objects.requireNonNullElse(tt, ""));
		c.setBorder(0);
		table.addCell(c);
		if(right != null) {
			c = createCenteredCell(right);
			c.setBorder(0);
			c.setPaddingLeft(3);
			table.addCell(c);
		}
		addBlankRow(table);
		return table;
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
		titleText = title;
	}

	/**
	 * Get the "title text" part that was set by {@link #setTitleText(Object)}. (If nothing was ever set,
	 * <code>null</code> will be returned.
	 *
	 * @return "Title text" part of the report. (This is used by {@link #getTitleTable()} method).
	 */
	public Object getTitleText() {
		return titleText;
	}

	/**
	 * Get the "title table" part of the report. It builds a {@link PDFTable} from the "title text" part returned
	 * by the {@link #getTitleText()} method. The {@link #getTitle()} method uses the return value of this
	 * to build the "title" part of the report.
	 *
	 * @return "Title table" part of the report.
	 */
	public PDFTable getTitleTable() {
		Object title = getTitleText();
		if(title == null) {
			return null;
		}
		if(title instanceof PDFTable) {
			return (PDFTable)title;
		}
		PDFCell c;
		if(title instanceof String) {
			c = createCenteredCell(createTitleText((String)title, 14));
		} else {
			c = createCell(title);
			c.setHorizontalAlignment(PDFElement.ALIGN_CENTER);
			c.setVerticalAlignment(PDFElement.ALIGN_MIDDLE);
		}
		c.setPaddingBottom(5);
		PDFTable t = createTable(1);
		t.addCell(c);
		return t;
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
		PDFTable table = createTable(40, 60);
		table.setBorderWidth(0);
		Text text;
		if(hasContacts != null) {
			text = new Text(hasContacts.getName(), 14, PDFFont.BOLD);
			StringBuilder a = new StringBuilder();
			String s = hasContacts.getContact("Address");
			if(s != null) {
				a.append(s);
			}
			s = hasContacts.getContact("Phone");
			boolean phone = s != null;
			if(phone) {
				a.append("\nPhone: ").append(s);
			}
			s = hasContacts.getContact("Email");
			if(s != null) {
				a.append(phone ? ", " : "\n").append("Email: ").append(s);
			}
			text.newLine().append(a, 6, PDFFont.BOLD);
		} else {
			text = new Text();
		}
		table.addCell(createCell(text, nb));
		if(captions == null || captions.length == 0) {
			table.addCell(createCell(""), nb);
		} else {
			text = new Text(captions[0], 12, PDFFont.BOLD).newLine();
			for(int i = 1; i < captions.length; i++) {
				if(captions[i] == null) {
					continue;
				}
				text.newLine(true).append(captions[i], 10, PDFFont.BOLD);
			}
			table.addCell(createCell(text, true), nb);
		}
		return table;
	}

	@Override
	public void dumpError(Throwable error) {
		getDevice().log(error);
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
