package com.storedobject.pdf;

import java.io.InputStream;

import com.storedobject.core.ContentProducer;
import com.storedobject.core.Device;
import com.storedobject.core.FileData;
import com.storedobject.core.StreamData;

public abstract class CombinedPDFReport extends PDFReport {

	public CombinedPDFReport(Device device) {
		super(device);
	}
	
	public void addContent(ContentProducer externalPDF) throws Exception {
	}
	
	public void addContent(ContentProducer externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}
	
	public void addContent(FileData externalPDF) throws Exception {
	}
	
	public void addContent(FileData externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}
	
	public void addContent(InputStream externalPDF) throws Exception {
	}
	
	public void addContent(InputStream externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}
	
	public void addContent(StreamData externalPDF) throws Exception {
	}
	
	public void addContent(StreamData externalPDF, int startingPage, int endingPage, int... pagesToSkip) throws Exception {
	}
	
    public void setOrientation(int orientation) {
    }
    
	public final int getScalePercentage() {
		return 0;
	}

	public final void setScalePercentage(int scalePercentage) {
	}
}