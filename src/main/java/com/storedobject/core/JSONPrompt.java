package com.storedobject.core;

import java.lang.reflect.Method;
import java.util.Map;

public class JSONPrompt implements JSONService {

    private JSONMap.Array prompts;
    private String template;
    private Map<String, Method> methods;

    @Override
    public final void execute(Device device, JSON json, JSONMap result) {
        prompts = result.map("result").array("prompts");
        process(device, json);
    }

    protected void process(Device device, JSON parameters) {
        String template = getTemplate();
        if(template == null || template.isBlank()) {
            StringBuilder sb = new StringBuilder();
            for(String p: parameters.keys()) {
                sb.append("{{").append(p).append("}}");
            }
            template = sb.toString();
        }
        if(template.isBlank()) {
            addPrompt("text", "Do idea!");
            return;
        }
        for(String p: parameters.keys()) {
            template = template.replace("{{" + p + "}}", StringUtility.toString(getValue(parameters, p)));
        }
    }

    protected Object getValue(JSON parameters, String parameter) {
        Method m = methods.get(parameter);
        if(m == null) {
            JSON json = parameters.get(parameter);
            return switch (json.getType()) {
                case NULL -> "<No data>";
                case STRING -> json.getString();
                case NUMBER -> json.getDecimal();
                case BOOLEAN -> json.getBoolean();
                case null -> "<No value>";
                default -> json.toString();
            };
        }
        try {
            return m.invoke(parameters, parameter);
        } catch (Exception e) {
            return "<Error value>";
        }
    }

    public final void addPrompt(String prompt) {
        addPrompt("text", prompt);
    }

    public final void addPrompt(String type, String prompt) {
        JSONMap m = prompts.map();
        m.put("type", type);
        m.put("prompt", prompt);
    }

    public void set(String template, Map<String, Method> methods) {
        this.template = template;
        this.methods = methods;
    }

    /**
     * Retrieves the template string associated with the current MCP request. Overriding classes
     * may return a custom template string based on the business logic.
     *
     * @return the template string from the request, or null if no template is defined.
     */
    protected String getTemplate() {
        return template;
    }
}