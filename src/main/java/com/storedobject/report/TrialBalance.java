package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.SystemEntity;
import com.storedobject.pdf.PDFReport;

import java.util.Date;

/**
 * Trial Balance
 *
 * @author Syam
 */
public class TrialBalance extends PDFReport {

    public TrialBalance(Device device, SystemEntity entity, Date date) {
        super(device);
    }

	@Override
	public void generateContent() {
    }
}