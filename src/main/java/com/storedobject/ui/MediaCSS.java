package com.storedobject.ui;

import com.storedobject.core.MediaFile;
import com.storedobject.ui.util.SOServlet;

/**
 * Utility methods related to handling media access from within CSS styles.
 *
 * @author Syam
 */
public class MediaCSS {

    /**
     * Create a URL for the given media. The resulting URL can be used to access the media from CSS, HTML etc.
     *
     * @param mediaName Name of the media content to access.
     * @return URL.
     */
    public static String mediaURL(String mediaName) {
        return mediaURL(SOServlet.getMedia(mediaName));
    }

    /**
     * Create a URL for the given media. The resulting URL can be used to access the media from CSS, HTML etc.
     *
     * @param mediaFile Media content to access.
     * @return URL.
     */
    public static String mediaURL(MediaFile mediaFile) {
        return mediaFile == null ? "" : ("media/" + mediaFile.getFileName());
    }

    /**
     * Parse the HTML/CSS string and replace all occurrences of ${MediaName} with respective media URLs.
     *
     * @param line Line to parse.
     * @return Parsed output with occurrences of ${MediaName} replaced with corresponding URLs.
     */
    public static String parse(String line) {
        MediaFile m;
        int p1, p2 = 0;
        while((p1 = line.indexOf("${", p2)) >= 0) {
            p2 = line.indexOf('}', p1);
            if(p2 < 0) {
                p2 = p1 + 2;
                continue;
            }
            m = SOServlet.getMedia(stripMedia(line.substring(p1 + 2, p2)));
            if(m == null) {
                p2 = p1 + 2;
                continue;
            }
            line = line.substring(0, p1) + mediaURL(m) + line.substring(p2 + 1);
            ++p2;
        }
        return line;
    }

    private static String stripMedia(String media) {
        if(media.startsWith("media:")) {
            media = media.substring("media:".length());
        }
        return media;
    }
}
