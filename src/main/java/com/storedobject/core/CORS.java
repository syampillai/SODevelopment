package com.storedobject.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * The CORS class manages Cross-Origin Resource Sharing (CORS) validation and handling
 * for HTTP requests. It ensures that the origin of a request is allowed based on
 * predefined rules and provides options to respond appropriately for valid and invalid origins.
 * This class supports setting custom reject handling, processing allowed origins dynamically,
 * and responding to CORS preflight and simple requests.
 * The CORS class is tightly coupled with the Device interface, which represents the
 * origin of the request context and utilizes configurations for dynamic CORS policies.
 *
 * @author Syam
 */
public final class CORS {

    private static final ArrayList<String> allowedOrigins = new ArrayList<>();

    private CORS() {
    }

    private static boolean isAllowedRequestOrigin(String origin) {
        if("null".equals(origin)) {
            return true;
        }
        try {
            if(allowedOrigins.isEmpty()) {
                Arrays.stream(GlobalProperty.get("SECURITY-CORS").split(","))
                        .map(String::trim).filter(c -> !c.isBlank()).forEach(allowedOrigins::add);
                if(allowedOrigins.isEmpty()) {
                    allowedOrigins.add(".*");
                }
            }
            if(allowedOrigins.stream().anyMatch(origin::matches)) {
                return true;
            }
        } catch(Throwable error) {
            allowedOrigins.add("_");
            return false;
        }
        ApplicationServer.log("CORS rejected: " + origin);
        return false;
    }

    /**
     * Reject the request with a default error message.
     *
     * @param response Response instance.
     * @throws IOException If an error occurs.
     */
    public static void reject(HttpServletResponse response) throws IOException {
        String html = """
            <!DOCTYPE html>
            <html>
                <head><title>Error %d</title></head>
                <body>
                </body>
            </html>
            """.formatted(HttpServletResponse.SC_FORBIDDEN);

        response.setHeader("content-type", "text/html; charset=utf-8");
        response.getWriter().write(html);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * Handles a cross-origin resource sharing (CORS) request and determines whether it should be rejected.
     * Updates the response headers to allow or forbid origin-based access depending on the origin and request method.
     *
     * @param request  The HTTP request object containing the CORS request details.
     * @param response The HTTP response object to be configured based on CORS policies.
     * @return true if the request is rejected; false if the request is accepted.
     * @throws IOException If an error occurs while writing to the response.
     */
    public static boolean rejected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return rejected(request, response, null);
    }

    /**
     * Handles a cross-origin resource sharing (CORS) request and determines whether it should be rejected.
     * Updates the response headers to allow or forbid origin-based access depending on the origin and request method.
     *
     * @param request  The HTTP request object containing the CORS request details.
     * @param response The HTTP response object to be configured based on CORS policies.
     * @param reject   A custom handler to be invoked if the request is rejected.
     * @return true if the request is rejected; false if the request is accepted.
     * @throws IOException If an error occurs while writing to the response.
     */
    public static boolean rejected(HttpServletRequest request, HttpServletResponse response,
                            Consumer<HttpServletResponse> reject) throws IOException {
        String origin = request.getHeader("Origin");
        if(origin == null) {
            return false;
        }
        if ("null".equals(origin)) {
            origin = "*";
        }
        if (isAllowedRequestOrigin(origin)) { // Allowed
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
                    return true;
                }
                case "post", "get" -> {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.addHeader("Access-Control-Allow-Credentials", "true");
                    return false;
                }
            }
        }
        // Not allowed
        if(reject != null) {
            reject.accept(response);
            return true;
        }
        reject(response);
        return true;
    }

    /**
     * Clear the allowed origins list so that it will be re-populated on the next request.
     */
    public static void clear() {
        allowedOrigins.clear();
    }
}
