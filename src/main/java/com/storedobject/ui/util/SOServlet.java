package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.core.*;
import com.storedobject.ui.MediaCSS;
import com.vaadin.flow.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

@WebServlet(urlPatterns = "/*", name = "SOServlet", asyncSupported = true, loadOnStartup = 0, initParams = {
    @WebInitParam(name = "closeIdleSessions", value = "true")
})
public class SOServlet extends VaadinServlet {

    private static String url;
    private static final WeakHashMap<String, MediaFile> mediaCache = new WeakHashMap<>();
    private static final WeakHashMap<String, TextContent> tcCache = new WeakHashMap<>();
    private static final long CACHE_TIME_IN_MILLIS = TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS);
    private static final long CACHE_TIME_IN_SECONDS = CACHE_TIME_IN_MILLIS / 1000;

    @Override
    public void init() throws ServletException {
        super.init();
        String link = getServletConfig().getServletContext().getContextPath();
        if (link != null && link.length() > 1 && link.startsWith("/")) {
            link = link.substring(1);
        } else {
            link = null;
        }
        ApplicationServer.initialize(null, link);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(url == null) {
            url = request.getRequestURL().toString().replace("//" , "\n");
            int p = url.indexOf('/');
            if(p > 0) {
                url = url.substring(0, p);
            }
            url = url.replace("\n", "//");
        }
        if(isMedia(request, response) || isTextContent(request, response)) {
            return;
        }
        super.service(request, response);
    }

    private boolean isMedia(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String what = request.getPathInfo() == null ? request.getServletPath() : request.getServletPath() + request.getPathInfo();
        if(!what.startsWith("/media/")) {
            return false;
        }
        what = what.substring(7);
        int p = what.indexOf('/');
        if(p <= 0) {
            return false;
        }
        what = what.substring(p + 1);
        String fileName = what;
        p = what.lastIndexOf('.');
        if(p <= 0) {
            return false;
        }
        what = what.substring(0, p);
        MediaFile mf = getMedia(what);
        if(mf == null) {
            return false;
        }
        final InputStream data = mf.getFile().getContent();
        if(data == null) {
            return false;
        }
        respond(response, data,null, mf.getMimeType(),true, fileName);
        return true;
    }

    private void respond(HttpServletResponse response, InputStream dataStream, Reader dataReader,
                         String mimeType, boolean cached, String fileName) throws IOException {
        OutputStream out = response.getOutputStream();
        response.setContentType(mimeType);
        if(cached) {
            response.setHeader("Cache-Control", "max-age=" + CACHE_TIME_IN_SECONDS);
            response.setDateHeader("Expires", System.currentTimeMillis() + CACHE_TIME_IN_MILLIS);
            response.setHeader("Pragma", "cache");
        } else {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        if(fileName != null) {
            response.setHeader(DownloadStream.CONTENT_DISPOSITION, DownloadStream.getContentDispositionFilename(fileName));
        }
        try {
            if(dataReader != null || mimeType.startsWith("text/")) {
                BufferedReader bin = null;
                BufferedWriter bout = null;
                try {
                    if(dataReader == null) {
                        bin = IO.getReader(dataStream);
                    } else {
                        bin = IO.get(dataReader);
                    }
                    bout = IO.getWriter(out);
                    String line;
                    while ((line = bin.readLine()) != null) {
                        bout.write(MediaCSS.parse(line));
                        bout.newLine();
                    }
                } finally {
                    IO.close(bin, bout);
                }
            } else {
                IO.copy(dataStream, out, DownloadStream.DEFAULT_BUFFER_SIZE);
            }
        } finally {
            IO.close(out, dataStream, dataReader);
        }
    }

    private boolean isTextContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String what = request.getPathInfo() == null ? request.getServletPath() : request.getServletPath() + request.getPathInfo();
        if(!what.startsWith("/tc/")) {
            return false;
        }
        what = what.substring(4);
        TextContent tc = getTextContent(what);
        if(tc == null) {
            return false;
        }
        int p = what.lastIndexOf('.');
        String ext = p <= 0 ? "" : what.substring(p + 1);
        ext = ext.toLowerCase();
        String fileName = null, mimeType;
        switch (ext) {
            case "css":
            case "html":
                mimeType = "text/" + ext;
                break;
            case "js":
                mimeType = "text/javascript";
                break;
            default:
                mimeType = "text/plain";
                fileName = what + "." + ext;
                break;
        }
        respond(response, null, new StringReader(tc.getContent()), mimeType, false, fileName);
        return true;
    }

    public static TextContent getTextContent(String id) {
        id = id.trim().toLowerCase();
        TextContent tc = null;
        boolean isId;
        if(isId = StringUtility.isDigit(id)) {
            tc = tcCache.get("id:" + id);
        }
        if(tc == null) {
            tc = tcCache.get(id);
        }
        if(tc == null) {
            if(isId) {
                tc = StoredObject.get(TextContent.class, "Id=" + id, true);
            }
            if(tc == null) {
                tc = TextContent.get(id);
                if(tc != null && !tc.getName().equalsIgnoreCase(id)) {
                    tc = null;
                }
            }
            if(tc != null) {
                tcCache.put("id:" + tc.getId(), tc);
                tcCache.put(tc.getName().toLowerCase(), tc);
            }
        }
        return tc;
    }

    public static void cacheTextContent(TextContent tc) {
        if(tc == null || tc.created()) {
            return;
        }
        tcCache.put("id:" + tc.getId(), tc);
        tcCache.put(tc.getName().toLowerCase(), tc);
    }

    public static void removeCache(TextContent textContent) {
        if(textContent != null) {
            TextContent tc = tcCache.remove("id:" + textContent.getId());
            if(tc != null) {
                tcCache.remove(tc.getName().toLowerCase());
            }
            tcCache.remove(textContent.getName().toLowerCase());
        }
    }

    public static void removeCache(String name) {
        name = name.trim().toLowerCase();
        MediaFile mf = mediaCache.remove(name);
        if(mf != null) {
            mediaCache.remove("id:" + mf.getId());
        }
        TextContent tc = tcCache.remove(name);
        if(tc != null) {
            tcCache.remove("id:" + tc.getId());
        }
    }

    public static MediaFile getMedia(String name) {
        name = name.trim().toLowerCase();
        MediaFile mf = null;
        boolean isId;
        if(isId = StringUtility.isDigit(name)) {
            mf = mediaCache.get("id:" + name);
        }
        if(mf == null) {
            mf = mediaCache.get(name);
        }
        if(mf == null) {
            if(isId) {
                mf = StoredObject.get(MediaFile.class, "Id=" + name, true);
            }
            if(mf == null) {
                mf = MediaFile.get(name);
                if(mf != null && !name.equalsIgnoreCase(mf.getName())) {
                    mf = null;
                }
            }
            if(mf != null) {
                mediaCache.put(mf.getName().toLowerCase(), mf);
                mediaCache.put("id:" + mf.getId(), mf);
            }
        }
        return mf;
    }

    public static void removeCache(MediaFile mediaFile) {
        if(mediaFile != null) {
            MediaFile mf = mediaCache.remove("id:" + mediaFile.getId());
            if(mf != null) {
                mediaCache.remove(mf.getName().toLowerCase());
            }
            mediaCache.remove(mediaFile.getName().toLowerCase());
        }
    }

    public static MediaFile getImage(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isImage()) ? mf : null;
    }

    public static MediaFile getAudio(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isAudio()) ? mf : null;
    }

    public static MediaFile getVideo(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isVideo()) ? mf : null;
    }

    public static String getURL() {
        return url;
    }

    public static String getServer() {
        String s = url.substring(url.indexOf("://") + 3);
        int p = s.indexOf(':');
        if(p > 0) {
            s = s.substring(0, p);
        }
        return s;
    }

    public static String getDomain() {
        String s = getServer();
        return s.substring(s.indexOf('.') + 1);
    }

    public static String getSubdomain() {
        String s = getServer();
        return s.substring(0, s.indexOf('.'));
    }

    public static String getProtocol() {
        return url.substring(0, url.indexOf(':'));
    }
}