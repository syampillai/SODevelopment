package com.storedobject.core;

import java.io.OutputStream;

public abstract class TextContentProducer extends com.storedobject.core.StreamContentProducer {

	public TextContentProducer() {
		this(null);
	}
	
	public TextContentProducer(OutputStream out) {
		super(out);
	}

    public java.lang.String getContentType() {
        return null;
    }

    public java.lang.String getFileExtension() {
        return null;
    }
}
