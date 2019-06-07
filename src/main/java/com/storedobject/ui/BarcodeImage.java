package com.storedobject.ui;

import com.storedobject.common.Barcode;
import com.storedobject.vaadin.PaintedImage;

import java.awt.*;

public class BarcodeImage extends PaintedImage {

    private Barcode barcode;

    public BarcodeImage() {
        this(Barcode.Format.QR_CODE, null);
    }

    public BarcodeImage(String value) {
        this(Barcode.Format.QR_CODE, value);
    }

    public BarcodeImage(Barcode.Format format) {
        this(format, null);
    }

    public BarcodeImage(Barcode.Format format, String value) {
        barcode = new Barcode(format, value);
    }

    public String getValue() {
        return barcode.getData();
    }

    public void setValue(String value) {
        barcode.setData(value);
        redraw();
    }

    public Barcode.Format getFormat() {
        return barcode.getFormat();
    }

    public void setFormat(Barcode.Format format) {
        barcode.setFormat(format);
    }

    @Override
    public void paint(Graphics2D graphics) {
    }

    public boolean isPrintText() {
        return barcode.isPrintText();
    }

    public void setPrintText(boolean printText) {
        barcode.setPrintText(printText);
    }
}
