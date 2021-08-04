package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.MediaFile;
import com.storedobject.core.StreamData;
import com.storedobject.ui.util.SOServlet;
import com.vaadin.flow.server.StreamResource;

public class Audio extends com.storedobject.vaadin.Audio {

    /**
     * Constructor.
     */
    public Audio() {
    }

    /**
     * Constructor.
     * @param streamData Stream data from which media streamAll to be pulled
     */
    public Audio(StreamData streamData) {
        super(new DBResource(streamData));
    }

    /**
     * Constructor.
     * @param streamDataId Id of the streamAll data from which media streamAll to be obtained
     */
    public Audio(Id streamDataId) {
        super(new DBResource(streamDataId));
    }

    /**
     * Constructor.
     * @param resources Sources to be set
     */
    public Audio(StreamResource... resources) {
        super(resources);
    }

    /**
     * Constructor.
     * @param uri URI source
     * @param type Content type of the media
     */
    public Audio(String uri, String type) {
        super(uri, type);
    }

    /**
     * Constructor.
     *
     * @param mediaFile Media file containing the audio
     */
    public Audio(MediaFile mediaFile) {
        this(mediaFile != null && mediaFile.isAudio() ? ("media/" + mediaFile.getFileName()) : "",
                mediaFile != null && mediaFile.isAudio() ? mediaFile.getMimeType() : "");
    }

    /**
     * Create an audio from the media file.
     *
     * @param name Name of the media file
     * @return Audio if exists.
     */
    public static Audio createFromMedia(String name) {
        MediaFile mf = SOServlet.getAudio(name);
        return mf == null ? null : new Audio(mf);
    }
}
