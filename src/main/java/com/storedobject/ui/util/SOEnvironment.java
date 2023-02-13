package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;

import java.util.HashMap;
import java.util.Map;

public class SOEnvironment implements ApplicationEnvironment {

    private ObjectFieldCreator<?> fieldCreator;
    private ObjectColumnCreator<?> columnCreator;

    @Override
    public ObjectFieldCreator<?> getObjectFieldCreator() {
        if(fieldCreator == null) {
            fieldCreator = new SOFieldCreator<>();
        }
        return fieldCreator;
    }

    @Override
    public ObjectColumnCreator<?> getObjectColumnCreator() {
        if(columnCreator == null) {
            columnCreator = new SOColumnCreator<>();
        }
        return columnCreator;
    }

    @Override
    public String toString(Object any) {
        return StringUtility.toString(any);
    }

    @Override
    public String toDisplay(Object message) {
        return StoredObjectUtility.toDisplay(Application.get(), message);
    }

    @Override
    public String createLabel(String attributeName) {
        if(attributeName.endsWith(".l") || attributeName.endsWith(".e")) {
            attributeName = attributeName.substring(0, attributeName.length() - 2);
        }
        return StringUtility.makeLabel(attributeName);
    }

    @Override
    public String createLabel(Class<?> aClass) {
        return StringUtility.makeLabel(aClass);
    }

    private static final Map<String, String> iconNameMap = new HashMap<>();
    static {
        iconNameMap.put("acknowledge", "check");
        iconNameMap.put("aircraft", "plane");
        iconNameMap.put("email", "envelope_o");
        iconNameMap.put("mail", "envelope_o");
        iconNameMap.put("message", "envelope_o");
        iconNameMap.put("error", "times");
        iconNameMap.put("quit", "angle_double_right");
        iconNameMap.put("collapse", "compress");
        iconNameMap.put("cancel", "close");
        iconNameMap.put("add", "plus");
        iconNameMap.put("go", "chevron_circle_right");
        iconNameMap.put("proceed", "play");
        iconNameMap.put("execute", "play");
        iconNameMap.put("process", "start_cog");
        iconNameMap.put("query", "question_circle");
        iconNameMap.put("help", "question_circle");
        iconNameMap.put("children", "sitemap");
        iconNameMap.put("tree", "sitemap");
        iconNameMap.put("excel", "table");
        iconNameMap.put("browse", "table");
        iconNameMap.put("load_", "load");
        iconNameMap.put("view", "eye");
        iconNameMap.put("view_", "eye");
        iconNameMap.put("compile_", "java");
        iconNameMap.put("compile", "java");
        iconNameMap.put("activate", "load");
        iconNameMap.put("disable", "close_menu");
        iconNameMap.put("deactivate", "close_menu");
        iconNameMap.put("system", "wrench");
        iconNameMap.put("up", "arrow_up");
        iconNameMap.put("down", "arrow_down");
        iconNameMap.put("attach", "paperclip");
        iconNameMap.put("clip", "paperclip");
        iconNameMap.put("detach", "chain_broken");
        iconNameMap.put("information", "info");
        iconNameMap.put("format", "bars");
        iconNameMap.put("list", "list_ol");
        iconNameMap.put("chart", "bar_chart");
        iconNameMap.put("new", "plus");
        iconNameMap.put("delete", "trash");
        iconNameMap.put("dashboard", "tachometer");
        iconNameMap.put("load", "play");
        iconNameMap.put("play", "caret_right");
        iconNameMap.put("run", "cogs");
        iconNameMap.put("current", "circle_thin");
    }

    @Override
    public String getIconName(String label) {
        if(label == null || label.contains(":")) {
            return label;
        }
        label = label.toLowerCase();
        switch(label) {
            case "sign in", "sign out" -> label = label.replace(" ", "_");
            default -> {
                int p = label.indexOf(' ');
                if(p > 0) {
                    label = label.substring(0, p).trim();
                }
            }
        }
        String icon = switch(label) {
            case "confirm", "ok", "yes", "save", "set", "finish", "check", "approve", "authorize" -> "check";
            case "no", "cancel", "close", "remove", "clear" -> "close";
            case "compute" -> "plus-minus";
            case "clone", "copy" -> "copy-o";
            case "next" -> "angle-double-right";
            case "previous", "back" -> "angle-double-left";
            case "refresh", "undo", "undelete", "reload", "reset" -> "refresh";
            case "report", "pdf" -> "image:picture-as-pdf";
            case "compile" -> "places:free-breakfast";
            case "format" -> "align-justify";
            case "menu" -> "list-ol";
            case "debug" -> "bug-o";
            case "test" -> "flask";
            default -> null;
        };
        if(icon == null) {
            String mapped = iconNameMap.get(label);
            return mapped == null ? label : mapped;
        }
        return icon;
    }
}
