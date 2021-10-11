package com.storedobject.ui.util;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectEditor;

import java.util.List;
import java.util.stream.Collectors;

public class LogicParser {

    private static final String SO_DOT = "com.storedobject.";
    private static final String DEVICE_TAG = "ui";

    public static String createLogicName(Class<?> objectClass, String tag) {
        return createLogicName(objectClass.getName(), tag, objectClass);
    }

    private static String createLogicName(String objectName, String tag, Class<?> objectClass) {
        if(objectName.indexOf(".logic.") > 0) {
            return objectName;
        }
        int dot = objectName.lastIndexOf('.');
        String p = objectName.substring(0, dot), c = objectName.substring(dot) + tag;
        if(p.startsWith(SO_DOT)) {
            p = ApplicationServer.getPackageName() + ".logic" + c;
        } else {
            p += ".logic" + c;
        }
        if(JavaClassLoader.exists(p)) {
            return p;
        }
        p = SO_DOT + DEVICE_TAG + ".common" + c;
        if(JavaClassLoader.exists(p)) {
            return p;
        }
        p = SO_DOT + DEVICE_TAG + ".tools" + c;
        if(JavaClassLoader.exists(p)) {
            return p;
        }
        if("Editor".equals(tag)) {
            if(objectClass == null) {
                objectClass = findClass(objectName);
            }
            if(objectClass != null && InventoryItemType.class.isAssignableFrom(objectClass)) {
                return SO_DOT + DEVICE_TAG + ".inventory.ItemType" + tag;
            }
            if(objectClass != null && PersonRole.class.isAssignableFrom(objectClass)) {
                return SO_DOT + DEVICE_TAG + ".common.PersonRole" + tag;
            }
            if(objectClass != null && EntityRole.class.isAssignableFrom(objectClass)) {
                return SO_DOT + DEVICE_TAG + ".common.EntityRole" + tag;
            }
        }
        return SO_DOT + DEVICE_TAG + ".Object" + tag;
    }

    public static void parse(Logic logic) throws SOException {
        String cn = logic.getClassName();
        int p = cn.indexOf(':');
        if(!(p == 1 || p == 2)) {
            return;
        }
        String action = cn.substring(0, p);
        cn = cn.substring(p + 1).trim();
        if(action.equals("R")) {
            logic.setClassName(SO_DOT + "ui.ObjectList|" + cn);
            return;
        }
        String param = null;
        if(cn.startsWith("(")) {
            p = cn.indexOf(')');
            if(p > 0) {
                param = cn.substring(0, p + 1);
                cn = cn.substring(p + 1);
            }
        }
        List<String> s = new StringUtility.List(cn.split("\\|"));
        while(s.size() < 3) {
            s.add("");
        }
        if(s.get(0).indexOf('.') < 0 || s.get(0).indexOf('.') == s.get(0).lastIndexOf('.')) {
            String className = ApplicationServer.guessClass(s.get(0));
            if(className == null) {
                throw new SOException("Unknown class: " + s.get(0));
            }
            s.set(0, className);
        }
        switch(action) {
            case "E" -> fillLogic(s, "Editor", null);
            case "B" -> fillLogic(s, "Browser", null);
            case "BE" -> fillLogic(s, "BrowserEditor", null);
            case "BV" -> fillLogic(s, "BrowserViewer", null);
            case "T", "TB" -> fillLogic(s, "TreeBrowser", "Tree");
            case "TE" -> fillLogic(s, "TreeEditor", "Tree");
            case "TV" -> fillLogic(s, "TreeViewer", "Tree");
            case "F", "FB" -> fillLogic(s, "ForestBrowser", "Forest");
            case "FE" -> fillLogic(s, "ForestEditor", "Forest");
            case "V", "FV" -> fillLogic(s, "ForestViewer", "Forest");
        }
        cn = s.get(1);
        if(param != null) {
            cn += "|" + param;
        }
        String t = s.stream().skip(2).filter(o -> o != null && !o.isEmpty()).collect(Collectors.joining("|"));
        if(t.isEmpty()) {
            cn += t;
        }
        cn += "|" + s.get(0);
        logic.setClassName(cn);
    }

    private static void fillLogic(List<String> s, String logic1, String logic2) {
        String t = s.get(1);
        if(t != null && t.isEmpty()) {
            s.set(1, createLogicName(s.get(0), logic1, null));
            if(logic2 == null) {
                return;
            }
        }
        t = s.get(2);
        if(t != null && t.isEmpty()) {
            s.set(2, createLogicName(s.get(0), logic2, null));
        }
    }

    private static Class<?> findClass(String name) {
        name = name.trim();
        if(name.indexOf('.') < 0) {
            name = StringUtility.makeLabel(name, false);
            while(name.indexOf(' ') >= 0) {
                name = name.replace(" ", "");
            }
        }
        Class<?> kclass;
        int dot;
        String p = ApplicationServer.getPackageName();
        if((dot = name.lastIndexOf('.')) < 0) {
            kclass = loadClass(ApplicationServer.getPackageName() + "." + name);
            if(kclass != null) {
                return kclass;
            }
            return loadClass(SO_DOT + "core." + name);
        }
        if(dot != name.indexOf('.')) {
            kclass = loadClass(name);
            if(kclass != null) {
                return kclass;
            }
        }
        ++dot;
        String prefix = name.substring(0, dot);
        name = name.substring(dot);
        if(p.endsWith(prefix)) {
            kclass = loadClass(p + "." + name);
            if(kclass != null) {
                return kclass;
            }
        }
        kclass = loadClass(p + "." + prefix + name);
        if(kclass != null) {
            return kclass;
        }
        kclass = loadClass(p.substring(0, p.lastIndexOf('.')) + "." + prefix + name);
        if(kclass != null) {
            return kclass;
        }
        return loadClass(SO_DOT + prefix + name);
    }

    private static Class<?> loadClass(String name) {
        try {
            return JavaClassLoader.getLogic(name, false);
        } catch(ClassNotFoundException e) {
            return null;
        }
    }

    public static <T extends StoredObject> void checkOverride(ObjectEditor<T> editor) {
        if(!editor.getClass().getName().equals(createLogicName(editor.getObjectClass(), "Editor"))) {
            throw new SORuntimeException("Illegal access!");
        }
    }
}
