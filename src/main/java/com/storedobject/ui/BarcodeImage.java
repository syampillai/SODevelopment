package com.storedobject.ui;

import com.storedobject.common.Barcode;
import com.storedobject.vaadin.PaintedImage;

import java.awt.*;

public class BarcodeImage extends PaintedImage {

    public BarcodeImage() {
    }

    public BarcodeImage(String value) {
    }

    public BarcodeImage(Barcode.Format format) {
    }

    public BarcodeImage(Barcode.Format format, String value) {
    }

    public String getValue() {
        return null;
    }

    public void setValue(String value) {
    }

    public Barcode.Format getFormat() {
        return null;
    }

    public void setFormat(Barcode.Format format) {
    }

    @Override
    public void paint(Graphics2D graphics) {
    }

    public boolean isPrintText() {
        return false;
    }

    public void setPrintText(boolean printText) {
    }
}
