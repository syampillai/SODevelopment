package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.Signature;
import com.storedobject.core.StreamData;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.PaintedImageResource;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Image extends com.storedobject.vaadin.Image {

    private static final NoImageResource NO_IMAGE_RESOURCE = new NoImageResource();

    public Image() {
        this(NO_IMAGE_RESOURCE);
    }

    public Image(String url) {
        super(url);
    }

    public Image(StreamData streamData) {
        this(new DBResource(streamData));
    }

    public Image(Id streamDataId) {
        this(new DBResource(streamDataId));
    }

    public Image(SVG svg) {
        this(new SVGResource(svg));
    }

    public Image(AbstractStreamResource resource) {
        super(resource);
    }

    public Image(MediaFile mediaFile) {
        this(resource(mediaFile));
    }

    public Image(Signature signature) {
        this(signature.getSignature());
    }

    private static String resource(MediaFile mediaFile) {
        return mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : null;
    }

    public void setSource(MediaFile mediaFile) {
        super.setSource(resource(mediaFile));
    }

    public void setSource(Signature signature) {
        super.setSource(signature.getSignature());
    }

    /**
     * Create an image from the media file.
     *
     * @param names Name of the media file. The first one found is returned.
     * @return Image if exists.
     */
    public static Image createFromMedia(String... names) {
        MediaFile mf = SOServlet.getImage(names);
        return mf == null ? null : new Image(mf);
    }

    private static InputStream noSVGStream() {
        return new ByteArrayInputStream(
                """
                <svg xmlns="http://www.w3.org/2000/svg" width="0" height="0"/>
                """.getBytes(StandardCharsets.UTF_8));
    }

    private static class NoImageResource extends StreamResource {

        private NoImageResource() {
            super(PaintedImageResource.createBaseFileName() + ".svg",
                    (StreamResourceWriter) (outputStream, vs)
                            -> IO.copy(noSVGStream(), outputStream, true));
        }
    }
}