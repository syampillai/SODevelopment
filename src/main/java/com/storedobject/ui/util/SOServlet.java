package com.storedobject.ui.util;

import com.storedobject.common.IO;
import com.storedobject.common.JSON;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.MediaCSS;
import com.vaadin.flow.server.VaadinServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@WebServlet(urlPatterns = "/*", name = "SOServlet", asyncSupported = true, loadOnStartup = 0, initParams = {
    @WebInitParam(name = "closeIdleSessions", value = "true")
})
@MultipartConfig(maxFileSize = Integer.MAX_VALUE, maxRequestSize = Integer.MAX_VALUE)
public class SOServlet extends VaadinServlet {

    private static String url, folder;
    private static final WeakHashMap<String, MediaFile> mediaCache = new WeakHashMap<>();
    private static final WeakHashMap<String, TextContent> tcCache = new WeakHashMap<>();
    private static final long CACHE_TIME_IN_MILLIS = TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS);
    private static final long CACHE_TIME_IN_SECONDS = CACHE_TIME_IN_MILLIS / 1000;
    private static final List<String> CORS = new ArrayList<>();
    private static String headerKey = null, headerValue = null;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        folder = context.getRealPath("/");
        String link = context.getContextPath();
        if (link != null && link.length() > 1 && link.startsWith("/")) {
            link = link.substring(1);
        } else {
            link = null;
        }
        ApplicationServer.initialize(null, link);
        if(headerKey == null) {
            headerKey = ApplicationServer.getGlobalProperty("application.request.header.key", "");
        }
        if(headerValue == null) {
            headerValue = ApplicationServer.getGlobalProperty("application.request.header.value", "");
        }
    }

    private boolean isAllowedRequestOrigin(String origin) {
        if("null".equals(origin)) {
            return true;
        }
        try {
            if(CORS.isEmpty()) {
                Arrays.stream(GlobalProperty.get("SECURITY-CORS").split(","))
                        .map(String::trim).filter(c -> !c.isBlank()).forEach(CORS::add);
                if(CORS.isEmpty()) {
                    CORS.add(".*");
                }
            }
            if(CORS.stream().anyMatch(origin::matches)) {
                return true;
            }
        } catch(Throwable error) {
            CORS.add("_");
            return false;
        }
        ApplicationServer.log(Application.get(), "CORS rejected: " + origin);
        return false;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Match header key/value if configured
        if(!headerKey.isEmpty()) {
            String hv = request.getHeader(headerKey);
            if(!headerValue.equals(hv)) {
                ApplicationServer.log("Illegal request header specified - " + headerKey + " = " + hv);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // Capture url from the first request that came in
        if(url == null) {
            url = request.getRequestURL().toString().replace("//" , "\n");
            int p = url.indexOf('/');
            if(p > 0) {
                url = url.substring(0, p);
            }
            url = url.replace("\n", "//");
        }

        // Check for CORS requests
        String origin = request.getHeader("Origin");
        if (origin != null) {
            if("null".equals(origin)) {
                origin = "*";
            }
            if(isAllowedRequestOrigin(origin)) { // Allowed
                switch (request.getMethod().toLowerCase()) {
                    case "options" -> {
                        response.addHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");

                        // Allow the requested method
                        String method = request.getHeader("Access-Control-Request-Method");
                        response.addHeader("Access-Control-Allow-Methods", method);

                        // Allow the requested headers
                        String headers = request.getHeader("Access-Control-Request-Headers");
                        response.addHeader("Access-Control-Allow-Headers", headers);

                        response.addHeader("Access-Control-Allow-Credentials", "true");
                        response.setContentType("text/plain");
                        response.setCharacterEncoding("utf-8");
                        response.getWriter().flush();
                        return;
                    }
                    case "post", "get" -> {
                        response.addHeader("Access-Control-Allow-Origin", origin);
                        response.addHeader("Access-Control-Allow-Credentials", "true");
                    }
                }
            } else { // Not allowed
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }

        // Check for controlled caches like media, text content etc.
        if(isMedia(request, response) || isTextContent(request, response)) {
            return;
        }

        // Over to logic handlers
        super.service(request, response);
    }

    private boolean isMedia(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String what = request.getPathInfo() == null ? request.getServletPath() : request.getServletPath() + request.getPathInfo();
        if(what.equals("/manifest.webmanifest")) {
            serveWebManifest(response);
            return true;
        }
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

    private void serveWebManifest(HttpServletResponse response) throws IOException {
        JSON mf;
        TextContent tc = getTextContent("webmanifest");
        if(tc == null) {
            mf = new JSON("{\"background_color\":\"#f2f2f2\",\"theme_color\":\"#ffffff\"," +
                    "\"icons\":[{\"src\":\"icons/icon-144x144.png\",\"sizes\":\"144x144\",\"type\":\"image/png\"}," +
                    "{\"src\":\"icons/icon-192x192.png\",\"sizes\":\"192x192\",\"type\":\"image/png\"}," +
                    "{\"src\":\"icons/icon-512x512.png\",\"sizes\":\"512x512\",\"type\":\"image/png\"}]}");
        } else {
            mf = new JSON(tc.getContent());
        }
        Map<String, Object> mfMap = mf.toMap();
        mfMap.put("start_url", ".");
        mfMap.put("display", "standalone");
        mfMap.putIfAbsent("name", ApplicationServer.getApplicationName());
        mfMap.putIfAbsent("short_name", ApplicationServer.getApplicationName());
        mf = new JSON(mfMap);
        respond(response, null, new StringReader(mf.toPrettyString()),
                "application/manifest+json", false, null);
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
        switch(ext) {
            case "css", "html" -> mimeType = "text/" + ext;
            case "js" -> mimeType = "text/javascript";
            default -> {
                mimeType = "text/plain";
                fileName = what + "." + ext;
            }
        }
        respond(response, null, new StringReader(tc.getContent()), mimeType, false, fileName);
        return true;
    }

    public static TextContent getTextContent(String... ids) {
        TextContent tc;
        for(String id: ids) {
            tc = getTextContent(id);
            if(tc != null) {
                return tc;
            }
        }
        return null;
    }

    public static TextContent getTextContent(String id) {
        id = id.trim().toLowerCase();
        TextContent tc = null;
        boolean isId = StringUtility.isDigit(id);
        if(isId) {
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

    public static MediaFile getMedia(String... names) {
        MediaFile media;
        for(String name: names) {
            media = getMedia(name);
            if(media != null) {
                return media;
            }
        }
        return null;
    }

    public static MediaFile getMedia(String name) {
        if(name == null || name.isBlank()) {
            return null;
        }
        name = name.trim().toLowerCase();
        MediaFile mf = null;
        boolean isId = StringUtility.isDigit(name);
        if(isId) {
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

    public static MediaFile getImage(String... names) {
        MediaFile image;
        for(String name: names) {
            image = getImage(name);
            if(image != null) {
                return image;
            }
        }
        return null;
    }

    public static MediaFile getImage(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isImage()) ? mf : null;
    }

    public static MediaFile getAudio(String... names) {
        MediaFile audio;
        for(String name: names) {
            audio = getAudio(name);
            if(audio != null) {
                return audio;
            }
        }
        return null;
    }

    public static MediaFile getAudio(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isAudio()) ? mf : null;
    }

    public static MediaFile getVideo(String... names) {
        MediaFile video;
        for(String name: names) {
            video = getVideo(name);
            if(video != null) {
                return video;
            }
        }
        return null;
    }

    public static MediaFile getVideo(String name) {
        MediaFile mf = getMedia(name);
        return (mf != null && mf.isVideo()) ? mf : null;
    }

    public static String getFolder() {
        return folder;
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

    public static void reloadCORS() {
        CORS.clear();
    }
}