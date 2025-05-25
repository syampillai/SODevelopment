package com.storedobject.whatsapp;

import com.storedobject.common.HTTP2;
import com.storedobject.common.JSON;
import com.storedobject.core.StoredObject;
import com.storedobject.job.MessageSender;
import com.storedobject.job.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends MessageSender<WhatsAppMessage> {

    private static final Map<String, Object> messageRoot = new HashMap<>();
    private static final Map<String, Object> messageTemplate = new HashMap<>();
    private static final List<Map<String, Object>> parameters = new ArrayList<>();
    static {
        messageRoot.put("messaging_product", "whatsapp");
        messageRoot.put("recipient_type", "individual");
        messageRoot.put("type", "template");
        messageRoot.put("template", messageTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "en_US");
        messageTemplate.put("language", map);
        map = new HashMap<>();
        messageTemplate.put("components", map);
        map.put("type", "body");
        map.put("parameters", parameters);
    }
    private String url, token;

    /**
     * Constructor.
     *
     * @param schedule     Schedule defined for this Job.
     */
    public Server(Schedule schedule) {
        super(schedule, WhatsAppMessage.class);
    }

    @Override
    protected void initialize() {
        if(url == null) {
            Configuration configuration = StoredObject.get(Configuration.class);
            if(configuration == null) {
                return;
            }
            url = configuration.getURL();
            token = configuration.getToken();
        }
    }

    @Override
    public boolean isActive() {
        return url != null;
    }

    @Override
    protected int sendMessage(WhatsAppMessage message) {
        if(url == null) {
            return 1;
        }
        String[] m = message.getMessage().split("\n");
        if(m.length < 2 || m[0].isBlank()) {
            invalidMessage(message, "Template name / message missing");
            return 3; // Unknown error
        }
        HTTP2.Builder builder = HTTP2.builder(url);
        synchronized (messageRoot) {
            parameters.clear();
            messageRoot.put("to", "+" + message.getMobileNumber());
            messageTemplate.put("name", m[0]);
            for(int i = 1; i < m.length; i++) {
                if(m[i].length() > 1024) {
                    invalidMessage(message, "Message part too long (" + i + ")");
                    return 3; // Unknown error
                }
                parameters.add(Map.of("type", "text", "text", m[i]));
            }
            m[0] = new JSON(messageRoot).getString();
        }
        JSON result = builder.contentTypeJSON().bearerToken(token).body(m[0]).json();
        Exception e = builder.getException();
        if(result == null || e != null) {
            int statusCode = builder.statusCode();
            log("WhatsApp Server - HTTP Error - " + statusCode + " (+" + message.getMobileNumber() + ")");
            if(e != null) {
                log(e);
            }
            return 4; // Invalid mobile number
        }
        JSON rr = result.get("messages");
        if(rr != null && rr.getType() == JSON.Type.ARRAY) {
            JSON r = rr.get(0);
            if(r != null && r.getString("id") != null) {
                return 0; // Success
            }
            log("WhatsApp Server - Error - " + rr.toPrettyString() + " (+" + message.getMobileNumber() + ")");
            return 4; // Invalid mobile number
        }
        logResponse(message, result);
        return 1;
    }

    private void invalidMessage(WhatsAppMessage message, String error) {
        log("WhatsApp Server - Error - Invalid message (+" + message.getMobileNumber() + "), Id = " + message.getId() + ", Error = " + error);
    }

    private void logResponse(WhatsAppMessage message, JSON response) {
        StringBuilder sb = new StringBuilder("WhatsApp Server - ");
        JSON r = response.get("error");
        if(r != null) {
            String m = r.getString("message");
            if(m != null) {
                sb.append(m);
            }
            JSON rr = r.get("error_data");
            if(rr != null) {
                m = rr.getString("details");
                if(m != null) {
                    sb.append(" (").append(m).append(")");
                }
            }
            sb.append(", Code: ");
            m = r.getString("code");
            if(m != null) {
                sb.append(m);
            }
            m = r.getString("fbtrace_id");
            if(m != null) {
                sb.append(". ").append(m);
            }
        } else {
            sb.append(response.toPrettyString());
        }
        sb.append("\nMobile Number: +").append(message.getMobileNumber()).append(", Id: ").append(message.getId());
        log(sb.toString());
    }
}
