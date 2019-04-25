package com.storedobject.core;

import java.awt.image.BufferedImage;

public class Barcode {

	public enum Format {

		/** Aztec 2D barcode format. */
		AZTEC,

		/** CODABAR 1D format. */
		CODABAR,

		/** Code 39 1D format. */
		CODE_39,

		/** Code 93 1D format. */
		CODE_93,

		/** Code 128 1D format. */
		CODE_128,

		/** Data Matrix 2D barcode format. */
		DATA_MATRIX,

		/** EAN-8 1D format. */
		EAN_8,

		/** EAN-13 1D format. */
		EAN_13,

		/** ITF (Interleaved Two of Five) 1D format. */
		ITF,

		/** MaxiCode 2D barcode format. */
		MAXICODE,

		/** PDF417 format. */
		PDF_417,

		/** QR Code 2D barcode format. */
		QR_CODE,

		/** RSS 14 */
		RSS_14,

		/** RSS EXPANDED */
		RSS_EXPANDED,

		/** UPC-A 1D format. */
		UPC_A,

		/** UPC-E 1D format. */
		UPC_E,

		/** UPC/EAN extension format. Not a stand-alone format. */
		UPC_EAN_EXTENSION

	}

	public Barcode() {
		this(null, null);
	}

	public Barcode(Format format, String data) {
		this(format, data, 100, 100);
	}

	public Barcode(Format format, String data, int width, int height) {
	}

	public String getData() {
		return null;
	}

	public void setData(String data) {
	}

	public final Format getFormat() {
		return null;
	}

	public final void setFormat(Format format) {
	}

	public int getWidth() {
		return 0;
	}

	public void setWidth(int width) {
	}

	public int getHeight() {
		return 0;
	}

	public void setHeight(int height) {
	}

	public BufferedImage getImage() throws Exception {
		return null;
	}

	public boolean isPrintText() {
		return false;
	}

	public void setPrintText(boolean printText) {
	}
}
