package com.storedobject.ui;

import com.storedobject.common.Barcode;
import com.storedobject.core.ApplicationServer;
import com.storedobject.vaadin.PaintedImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BarcodeImage extends PaintedImage {

    private static final String NO_DATA = " ";
    private final Barcode barcode;

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
        setHeightFull();
        setWidthFull();
        barcode = new Barcode(format, value == null ? NO_DATA : value);
    }

    @Override
    public void setImageWidth(int width) {
        super.setImageWidth(width);
        if(barcode.getFormat() == Barcode.Format.QR_CODE) {
            //noinspection SuspiciousNameCombination
            super.setImageHeight(width);
        }
    }

    @Override
    public void setImageHeight(int height) {
        super.setImageHeight(height);
        if(barcode.getFormat() == Barcode.Format.QR_CODE) {
            //noinspection SuspiciousNameCombination
            super.setImageWidth(height);
        }
    }

    public String getValue() {
        return barcode.getData();
    }

    public void setValue(String value) {
        if(value == null) {
            value = NO_DATA;
        }
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
        Rectangle r = graphics.getClipBounds();
        if(getValue() == null || getFormat() == null) {
            graphics.clearRect(0, 0, (int)r.getWidth(), (int)r.getHeight());
            return;
        }
        barcode.setWidth((int)r.getWidth());
        barcode.setHeight((int)r.getHeight());
        try {
            BufferedImage image = barcode.getImage();
            graphics.drawImage(image, 0, 0, null);
            setMinWidth((int)r.getWidth() + "px");
            setMinHeight((int)r.getHeight() + "px");
        } catch (Exception e) {
            Application a = Application.get();
            if(a == null) {
                ApplicationServer.log(a, e);
            } else {
                a.log(e);
            }
        }
    }

    public boolean isPrintText() {
        return barcode.isPrintText();
    }

    public void setPrintText(boolean printText) {
        barcode.setPrintText(printText);
    }
}
