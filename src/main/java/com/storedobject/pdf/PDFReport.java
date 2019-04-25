package com.storedobject.pdf;

import java.io.InputStream;

import com.storedobject.core.Device;

public abstract class PDFReport extends PDF {

	public PDFReport(Device device) {
		this(device, false, false);
	}

	public PDFReport(Device device, boolean letterHead) {
		this(device, letterHead, false);
	}

	public PDFReport(Device device, boolean letterHead, boolean printLogo) {
		super(letterHead);
	}
	
    public InputStream extractContent() throws Exception {
    	return null;
    }

	public void view() {
	}

	public Device getDevice() {
    	return null;
	}

	public PDFImage getLogo() {
    	return null;
	}

	public PDFImage getProductLogo() {
    	return null;
	}
	
	public int getLogoPosition() {
    	return 0;
	}

	@Override
	public Object getTitle() {
    	return null;
	}

	public void setTitleText(Object title) {
	}

	public Object getTitleText() {
    	return null;
	}

	public PDFTable getTitleTable() {
    	return null;
	}
}
