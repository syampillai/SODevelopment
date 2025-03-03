package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SVGImage extends Image{

    public SVGImage(String svg) {
        super(new SGVResource(svg));
    }

    private static class SGVResource extends StreamResource {

        public SGVResource(String svg) {
            super(PaintedImageResource.createBaseFileName() + ".svg",
                    (StreamResourceWriter) (outputStream, vs)
                            -> IO.copy(svg(svg), outputStream, true));
        }

        private static InputStream svg(String svg) {
            return new java.io.ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        }
    }
}