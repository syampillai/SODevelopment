package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SVG;
import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import java.io.*;

public class SVGResource extends StreamResource {

    public SVGResource(SVG svg) {
        super(fileName(), new SVGWriter(svg));
        setContentType(svg.getContentType());
    }

    private static String fileName() {
        return PaintedImageResource.createBaseFileName() + ".svg";
    }

    private record SVGWriter(SVG svg) implements StreamResourceWriter {

        @Override
            public void accept(OutputStream outputStream, VaadinSession vaadinSession) throws IOException {
                try {
                    Writer w = IO.getWriter(outputStream);
                    svg.generateContent(w);
                    w.close();
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
}
