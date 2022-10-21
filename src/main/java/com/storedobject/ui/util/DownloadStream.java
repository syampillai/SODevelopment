package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DownloadStream implements Serializable {

    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final long DEFAULT_CACHETIME = 1000 * 60 * 60 * 24;
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;
    private static final int MAX_BUFFER_SIZE = 64 * 1024;
    private InputStream stream;
    private String contentType;
    private String fileName;
    private Map<String, String> params;
    private long cacheTime = DEFAULT_CACHETIME;
    private int bufferSize = 0;

    /**
     * Creates a new instance of DownloadStream.
     *
     * @param stream Input stream
     * @param contentType Mime type of the content
     * @param fileName Name of the file (for the browser to save the content)
     */
    public DownloadStream(InputStream stream, String contentType, String fileName) {
        setStream(stream);
        setContentType(contentType);
        setFileName(fileName);
    }

    /**
     * Gets downloadable stream.
     *
     * @return output stream.
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Sets the stream.
     *
     * @param stream The stream to set
     */
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Gets stream content type.
     *
     * @return type of the stream content.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets stream content type.
     *
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the file name.
     *
     * @return the name of the file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the file name to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets a parameter for download stream. Parameters are optional information
     * about the downloadable stream and their meaning depends on the used
     * adapter. For example in WebAdapter they are interpreted as HTTP response
     * headers.
     * <p>If the parameters by this name exists, the old value is replaced.</p>
     *
     * @param name the Name of the parameter to set.
     * @param value the Value of the parameter to set.
     */
    public void setParameter(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name, value);
    }

    /**
     * Gets a parameter for download stream. Parameters are optional information
     * about the downloadable stream and their meaning depends on the used
     * adapter. For example in WebAdapter they are interpreted as HTTP response
     * headers.
     *
     * @param name the Name of the parameter to set.
     * @return Value of the parameter or null if the parameter does not exist.
     */
    public String getParameter(String name) {
        if (params != null) {
            return params.get(name);
        }
        return null;
    }

    /**
     * Gets the names of the parameters.
     *
     * @return Iterator of names or null if no parameters are set.
     */
    public Iterator<String> getParameterNames() {
        if (params != null) {
            return params.keySet().iterator();
        }
        return null;
    }

    /**
     * Gets length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Default is
     * <code>DEFAULT_CACHETIME</code>.
     *
     * @return Cache time in milliseconds
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * Sets length of cache expiration time. This gives the adapter the
     * possibility cache streams sent to the client. The caching may be made in
     * adapter or at the client if the client supports caching. Zero or negavive
     * value disables the caching of this stream.
     *
     * @param cacheTime the cache time in milliseconds.
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    /**
     * Gets the size of the download buffer.
     *
     * @return int The size of the buffer in bytes.
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets the size of the download buffer.
     *
     * @param bufferSize the size of the buffer in bytes.
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Writes this download stream to a Vaadin response. This takes care of
     * setting response headers according to what is defined in this download
     * stream ({@link #getContentType()}, {@link #getCacheTime()},
     * {@link #getFileName()}) and transferring the data from the stream (
     * {@link #getStream()}) to the response. Defined parameters (
     * {@link #getParameterNames()}) are also included as headers in the
     * response. If there is a parameter named <code>Location</code>, a
     * redirect (302 Moved temporarily) is sent instead of the contents of this
     * stream.
     *
     * @param request the request for which the response should be written
     * @param response the Vaadin response to write this download stream to
     * @throws IOException Passed through from the Vaadin response
     */
    public void writeResponse(@SuppressWarnings("unused") VaadinRequest request, VaadinResponse response)
            throws IOException {
        if (getParameter("Location") != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", getParameter("Location"));
            return;
        }

        // Download from given stream
        final InputStream data = getStream();
        if (data == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        OutputStream out = null;
        try {
            // Sets content type
            response.setContentType(getContentType());
            // Sets cache headers
            response.setCacheTime(getCacheTime());
            // Copy download stream parameters directly to HTTP headers.
            final Iterator<String> parameterNames = getParameterNames();
            if (parameterNames != null) {
                while (parameterNames.hasNext()) {
                    final String param = parameterNames.next();
                    response.setHeader(param, getParameter(param));
                }
            }
            // Content-Disposition: attachment generally forces download
            String contentDisposition = getParameter(CONTENT_DISPOSITION);
            if (contentDisposition == null) {
                contentDisposition = getContentDispositionFilename(getFileName());
            }
            response.setHeader(CONTENT_DISPOSITION, contentDisposition);
            int bufferSize = getBufferSize();
            if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE) {
                bufferSize = DEFAULT_BUFFER_SIZE;
            }
            final byte[] buffer = new byte[bufferSize];
            int bytesRead;
            out = response.getOutputStream();
            long totalWritten = 0;
            while ((bytesRead = data.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                totalWritten += bytesRead;
                if (totalWritten >= buffer.length) {
                    // Avoid chunked encoding for small resources
                    out.flush();
                }
            }
        } finally {
            IO.close(out, data);
        }
    }

    /**
     * Returns the filename formatted for inclusion in a Content-Disposition
     * header. Includes both a plain version of the name and a UTF-8 version
     *
     * @param filename The filename to include
     * @return A value for inclusion in a Content-Disposition header
     */
    public static String getContentDispositionFilename(String filename) {
        String encodedFilename = rfc5987Encode(filename);
        return String.format("filename=\"%s\"; filename*=utf-8''%s", encodedFilename, encodedFilename);
    }

    /**
     * Encodes the given string to UTF-8 <code>value-chars</code> as defined in
     * RFC5987 for use in e.g. the <code>Content-Disposition</code> HTTP header.
     *
     * @param value the string to encode, not <code>null</code>
     * @return the encoded string
     */
    public static String rfc5987Encode(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length();) {
            int cp = value.codePointAt(i);
            if (cp < 127 && (Character.isLetterOrDigit(cp) || cp == '.')) {
                builder.append((char) cp);
            } else {
                // Create string from a single code point
                String cpAsString = new String(new int[] { cp }, 0, 1);
                appendHexBytes(builder, cpAsString.getBytes(StandardCharsets.UTF_8));
            }
            // Advance to the next code point
            i += Character.charCount(cp);
        }
        return builder.toString();
    }

    private static void appendHexBytes(StringBuilder builder, byte[] bytes) {
        for (byte byteValue : bytes) {
            // mask with 0xFF to compensate for "negative" values
            int intValue = byteValue & 0xFF;
            String hexCode = Integer.toString(intValue, 16);
            builder.append('%').append(hexCode);
        }
    }
}