package com.storedobject.ui;

import com.storedobject.core.HasStreamData;
import com.storedobject.core.MediaFile;
import com.storedobject.vaadin.AbstractResourcedComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.server.AbstractStreamResource;

/**
 * A component for rendering 3D models using the `@google/model-viewer` library. It supports various
 * media sources and enables camera controls by default for model interaction.
 * This class extends {@code AbstractResourcedComponent} and allows sources to be specified via URLs,
 * media files, or streamable data resources.
 * <b>Features:</b>
 * - Renders 3D models using a browser-supported viewer.
 * - Provides camera controls by default for interactive model viewing.
 * - Supports multiple constructors for various source types including media files and stream data.
 */
@NpmPackage(value = "@google/model-viewer", version = "v4.1.0")
@JsModule("@google/model-viewer/dist/model-viewer.min.js")
@Tag("model-viewer")
public class ModelViewer extends AbstractResourcedComponent {

    /**
     * Default constructor for the ModelViewer component.
     * <p>This constructor initializes a basic instance of the ModelViewer without setting any
     * specific source or attributes. It is primarily intended for use cases where the source or
     * content is configured after instantiation.</p>
     */
    public ModelViewer() {
    }

    /**
     * Constructs a {@code ModelViewer} using a given {@link HasStreamData}.
     * It initializes the model viewer with the stream data source and enables camera controls by default.
     *
     * @param streamData The streamable data source that provides the content to be rendered by the ModelViewer.
     *                   If {@code null}, the initialization will rely on default or fallback behavior.
     */
    public ModelViewer(HasStreamData streamData) {
        this(new DBResource(streamData));
    }

    /**
     * Constructs a {@code ModelViewer} instance with the specified media source URL.
     * This constructor initializes the viewer to render 3D models from the given URL
     * and enables camera controls by default for interactive model viewing.
     *
     * @param url The URL pointing to the media source (e.g., a 3D model file) to be rendered by the viewer.
     */
    public ModelViewer(String url) {
        super(url);
        getElement().setAttribute("camera-controls", true);
    }

    /**
     * Constructs a ModelViewer component using a media file as the source.
     * The media file is processed to generate a URL compatible with the viewer.
     * If the media file is null or not an image, the source will not be set.
     *
     * @param mediaFile The media file to be used as the source for the model viewer.
     */
    public ModelViewer(MediaFile mediaFile) {
        this(resource(mediaFile));
    }

    /**
     * Creates a new {@code ModelViewer} instance with the specified stream resource.
     * This constructor allows initializing the 3D model viewer with a given {@code AbstractStreamResource},
     * enabling interaction with the model through camera controls by default.
     *
     * @param resource The {@code AbstractStreamResource} from which the 3D model will be loaded. This resource
     *                 can represent a streamable data source, such as a file, network stream, or in-memory data stream.
     */
    public ModelViewer(AbstractStreamResource resource) {
        super(resource);
        getElement().setAttribute("camera-controls", true);
    }

    private static String resource(MediaFile mediaFile) {
        return mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : null;
    }

    /**
     * Sets the source of the {@code ModelViewer} component using the specified {@link MediaFile}.
     * If the {@code MediaFile} is valid and represents an image, its path will be processed
     * and used as the source for the component.
     *
     * @param mediaFile The {@code MediaFile} to be used as the source for the {@code ModelViewer}.
     *                  If the {@code MediaFile} is null or not an image, the source will not be set.
     */
    public void setSource(MediaFile mediaFile) {
        super.setSource(resource(mediaFile));
    }

    @Override
    protected String getURIAttributeName() {
        return "src";
    }
}
