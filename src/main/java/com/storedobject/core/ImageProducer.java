package com.storedobject.core;

import com.storedobject.common.ImageGenerator;

import java.io.OutputStream;

public class ImageProducer extends StreamContentProducer {

    private ImageGenerator imageGenerator;

    public ImageProducer() {
        this(null, null);
    }

    public ImageProducer(OutputStream out) {
        super(out);
    }

    public ImageProducer(ImageGenerator imageGenerator) {
        this(imageGenerator, null);
    }

    public ImageProducer(ImageGenerator imageGenerator, OutputStream out) {
        super(out);
        setContentGenerator(imageGenerator);
    }

    public void setContentGenerator(ImageGenerator imageGenerator) {
        this.imageGenerator = imageGenerator;
    }

    @Override
    public String getContentType() {
        return imageGenerator == null ? "image/png" : imageGenerator.getContentType();
    }

    @Override
    public String getFileExtension() {
        return imageGenerator == null ? "png" : imageGenerator.getFileExtension();
    }

    @Override
    public void generateContent() throws Exception {
        imageGenerator.generateContent(out);
    }
}
