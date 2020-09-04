package com.storedobject.core;

import com.storedobject.common.TextContentGenerator;

import java.io.OutputStream;

public class TextContentProducer extends StreamContentProducer {

    private TextContentGenerator textContentGenerator;

    public TextContentProducer() {
        this(null, null);
    }

    public TextContentProducer(OutputStream out) {
        super(out);
    }

    public TextContentProducer(TextContentGenerator textContentGenerator) {
        this(textContentGenerator, null);
    }

    public TextContentProducer(TextContentGenerator textContentGenerator, OutputStream out) {
        super(out);
        setContentGenerator(textContentGenerator);
    }

    public void setContentGenerator(TextContentGenerator textContentGenerator) {
        this.textContentGenerator = textContentGenerator;
    }

    @Override
    public String getContentType() {
        return textContentGenerator == null ? "text/plain" : textContentGenerator.getContentType();
    }

    @Override
    public String getFileExtension() {
        return textContentGenerator == null ? "txt" : textContentGenerator.getFileExtension();
    }

    @Override
    public void generateContent() throws Exception {
        textContentGenerator.generateContent(getWriter());
    }
}