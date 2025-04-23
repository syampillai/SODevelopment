package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SVG;
import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.Signature;
import com.storedobject.core.StreamData;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.PaintedImageResource;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.WindowDecorator;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;

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

    private static class NoImageResource extends StreamResource {

        private NoImageResource() {
            super(PaintedImageResource.createBaseFileName() + ".svg",
                    (StreamResourceWriter) (outputStream, vs)
                            -> IO.copy(MediaFile.noImage().getFile().getContent(), outputStream, true));
        }
    }

    public static void setAsBackground(MediaFile mediaFile, HasStyle component) {
        if(component instanceof Dialog dialog) {
            component = dialog.getChildren().filter(c -> !(c instanceof WindowDecorator))
                    .findFirst().orElse(null);
            if(component == null) {
                return;
            }
        }
        component.getStyle()
                .set("background-image", "url('" + resource(mediaFile) + "')")
                .set("background-repeat", "no-repeat")
                .set("background-size", "cover")
                .set("background-position", "center");
    }

    public static void setAsBackground(MediaFile mediaFile, View view) {
        setAsBackground(mediaFile, view.getComponent());
    }
}