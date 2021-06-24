package com.storedobject.pdf;

@SuppressWarnings("serial")
public class PDFException extends Exception {
	
    public PDFException(Exception exception) {
        super(exception);
    }
    
    public PDFException() {
        super();
    }
    
    public PDFException(String message) {
        super(message);
    }
}
