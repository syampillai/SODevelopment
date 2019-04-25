package com.storedobject.pdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("serial")
public class PDFPhrase extends ArrayList < PDFElement > {

    public PDFPhrase() {
    }

    public PDFPhrase(PDFPhrase phrase) {
        this();
    }

    public PDFPhrase(PDFChunk chunk) {
        this();
    }

    public PDFPhrase(String text) {
        this();
    }

    public PDFPhrase(String text, PDFFont font) {
        this();
    }

    @Override
	public void add(int index, PDFElement element) {
    }

    public boolean add(String text) {
        return false;
    }

    @Override
	public boolean add(PDFElement element) {
        return false;
    }

    @Override
	public boolean isEmpty() {
        return false;
    }

    @Override
	public boolean addAll(Collection <? extends PDFElement > list) {
        return false;
    }

    public static final PDFPhrase getInstance(String text) {
        return null;
    }

    public List < PDFChunk > getChunks() {
        return null;
    }

    public void setFont(PDFFont font) {
    }

    public PDFFont getFont() {
        return null;
    }
    
    public void setLeading(final float leading) {
    }
    
    public float getLeading() {
    	return 0;
    }
}
