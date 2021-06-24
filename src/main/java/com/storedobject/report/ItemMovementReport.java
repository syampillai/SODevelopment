package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.InventoryItem;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;

public class ItemMovementReport extends PDFReport {

    private String caption = "Item Movement";
    private final InventoryItem item;

    public ItemMovementReport(Device device, InventoryItem item) {
        super(device);
        this.item = item;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public Object getTitleText() {
        Text t = new Text();
        t.append(getFontSize() + 6, PDFFont.BOLD).append(caption);
        t.newLine().append(getFontSize() + 4).append("Movement of Item: ").append(item.toDisplay());
        return t;
    }

    @Override
    public void generateContent() {
    }
}