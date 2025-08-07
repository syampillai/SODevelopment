package com.storedobject.ui.util;

import com.storedobject.accounts.CustomerInvoice;
import com.storedobject.accounts.SupplierInvoice;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.accounts.*;
import com.storedobject.ui.common.EntityRoleEditor;
import com.storedobject.ui.common.PersonRoleEditor;
import com.storedobject.ui.inventory.*;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class LogicParser {

    private static final String SO_DOT = "com.storedobject.";
    private static final String SOS_DOT = "com.storedobjects.";

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
        if(objectName.startsWith(SO_DOT)) {
            p = SO_DOT + "ui.common" + c;
            if(JavaClassLoader.exists(p)) {
                return p;
            }
            p = SO_DOT + "ui.tools" + c;
            if(JavaClassLoader.exists(p)) {
                return p;
            }
        }
        if(objectName.startsWith(SO_DOT + "iot.")) {
            p = SO_DOT + "ui.iot" + c;
            if(JavaClassLoader.exists(p)) {
                return p;
            }
        }
        if("Editor".equals(tag) || "Browser".equals(tag)) {
            if(objectClass == null) {
                objectClass = findClass(objectName);
            }
            if(objectClass != null) {
                return "Editor".equals(tag) ? editorName(objectClass) : browserName(objectClass);
            }
        }
        return SO_DOT + "ui.Object" + tag;
    }

    public static void parse(Logic logic) throws SOException {
        String cn = logic.getClassName();
        int p = cn.indexOf(':');
        if(!(p == 1 || p == 2)) {
            String className = ApplicationServer.guessClass(cn);
            if(className != null) {
                try {
                    Class<?> lc = JavaClassLoader.getLogic(cn);
                    if(lc.getName().equals(cn) && StoredObject.class.isAssignableFrom(lc)) {
                        ClassAttribute<?> ca = ClassAttribute.get((Class<? extends StoredObject>) lc);
                        if((StoredObjectUtility.hints(ca.getObjectClass()) & ObjectHint.SMALL_LIST) ==
                                ObjectHint.SMALL_LIST) {
                            logic.setClassName("B:" + cn);
                        } else {
                            logic.setClassName("E:" + cn);
                        }
                        parse(logic);
                    }
                } catch(Throwable ignored) {
                }
            }
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
            String className = ApplicationServer.guessClass(s.getFirst());
            if(className == null) {
                throw new SOException("Unknown class: " + s.getFirst());
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
        cn += "|" + s.getFirst();
        logic.setClassName(cn);
    }

    private static void fillLogic(List<String> s, String logic1, String logic2) {
        String t = s.get(1);
        if(t != null && t.isEmpty()) {
            s.set(1, createLogicName(s.getFirst(), logic1, null));
            if(logic2 == null) {
                return;
            }
        }
        t = s.get(2);
        if(t != null && t.isEmpty()) {
            s.set(2, createLogicName(s.getFirst(), logic2, null));
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
        if(name.startsWith(SOS_DOT) || name.startsWith(SO_DOT)) {
            return null;
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

    private static String browserName(Class<?> objectClass) {
        if(PackingUnit.class == objectClass) {
            return PackingUnitBrowser.class.getName();
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            return POBrowser.class.getName();
        }
        if(CustomerInvoice.class == objectClass) {
            return CustomerInvoiceBrowser.class.getName();
        }
        if(CustomerInvoice.class.isAssignableFrom(objectClass)) {
            return BaseCustomerInvoiceBrowser.class.getName();
        }
        if(SupplierInvoice.class == objectClass) {
            return SupplierInvoiceBrowser.class.getName();
        }
        if(SupplierInvoice.class.isAssignableFrom(objectClass)) {
            return BaseSupplierInvoiceBrowser.class.getName();
        }
        return ObjectBrowser.class.getName();
    }

    @SuppressWarnings("rawtypes")
    private static <O extends StoredObject> ObjectBrowser<O> browser(Class<O> objectClass,
                                                                     int actions, String title) {
        if(PackingUnit.class == objectClass) {
            return (ObjectBrowser<O>) new PackingUnitBrowser();
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            return new POBrowser(objectClass, actions, title);
        }
        if(CustomerInvoice.class == objectClass) {
            return (ObjectBrowser<O>) new CustomerInvoiceBrowser(actions, title);
        }
        if(CustomerInvoice.class.isAssignableFrom(objectClass)) {
            Class<? extends CustomerInvoice> ciClass = (Class<? extends CustomerInvoice>) objectClass;
            return (ObjectBrowser<O>) new BaseCustomerInvoiceBrowser<>(ciClass, actions, title);
        }
        if(SupplierInvoice.class == objectClass) {
            return (ObjectBrowser<O>) new SupplierInvoiceBrowser(actions, title);
        }
        if(SupplierInvoice.class.isAssignableFrom(objectClass)) {
            Class<? extends SupplierInvoice> siClass = (Class<? extends SupplierInvoice>) objectClass;
            return (ObjectBrowser<O>) new BaseSupplierInvoiceBrowser<>(siClass, actions, title);
        }
        return null;
    }

    public static <O extends StoredObject> ObjectBrowser<O> createInternalBrowser(Class<O> objectClass,
                                                                                  Iterable<String> browseColumns,
                                                                                  int actions,
                                                                                  Iterable<String> filterColumns,
                                                                                  String title) {
        ObjectBrowser<O> browser = browser(objectClass, actions, title);
        return browser == null ? new ObjectBrowser<>(objectClass, browseColumns, actions, filterColumns, title) : browser;
    }

    private static String editorName(Class<?> objectClass) {
        if(PackingUnit.class == objectClass) {
            return PackingUnitEditor.class.getName();
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            return POEditor.class.getName();
        }
        if(InventoryPOItem.class.isAssignableFrom(objectClass)) {
            return POItemEditor.class.getName();
        }
        if(InventoryItemType.class.isAssignableFrom(objectClass)) {
            return ItemTypeEditor.class.getName();
        }
        if(PersonRole.class.isAssignableFrom(objectClass)) {
            return PersonRoleEditor.class.getName();
        }
        if(EntityRole.class.isAssignableFrom(objectClass)) {
            return EntityRoleEditor.class.getName();
        }
        if(CustomerInvoice.class == objectClass) {
            return CustomerInvoiceEditor.class.getName();
        }
        if(CustomerInvoice.class.isAssignableFrom(objectClass)) {
            return BaseCustomerInvoiceEditor.class.getName();
        }
        if(SupplierInvoice.class == objectClass) {
            return SupplierInvoiceEditor.class.getName();
        }
        if(SupplierInvoice.class.isAssignableFrom(objectClass)) {
            return BaseSupplierInvoiceEditor.class.getName();
        }
        return ObjectEditor.class.getName();
    }

    private static <O extends StoredObject> ObjectEditor<O> editor(Class<O> objectClass,
                                                                   int actions, String title) {
        if(PackingUnit.class == objectClass) {
            return (ObjectEditor<O>) new PackingUnitEditor();
        }
        if(InventoryPO.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POEditor(objectClass, actions, title);
        }
        if(InventoryPOItem.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new POItemEditor(objectClass, actions, title);
        }
        if(EntityRole.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new EntityRoleEditor(objectClass, actions, title);
        }
        if(PersonRole.class.isAssignableFrom(objectClass)) {
            //noinspection rawtypes
            return new PersonRoleEditor(objectClass, actions, title);
        }
        if(CustomerInvoice.class == objectClass) {
            return (ObjectEditor<O>) new CustomerInvoiceEditor(actions, title);
        }
        if(CustomerInvoice.class.isAssignableFrom(objectClass)) {
            Class<? extends CustomerInvoice> ciClass = (Class<? extends CustomerInvoice>) objectClass;
            return (ObjectEditor<O>) new BaseCustomerInvoiceEditor<>(ciClass, actions, title);
        }
        if(SupplierInvoice.class == objectClass) {
            return (ObjectEditor<O>) new SupplierInvoiceEditor(actions, title);
        }
        if(SupplierInvoice.class.isAssignableFrom(objectClass)) {
            Class<? extends SupplierInvoice> siClass = (Class<? extends SupplierInvoice>) objectClass;
            return (ObjectEditor<O>) new BaseSupplierInvoiceEditor<>(siClass, actions, title);
        }
        return null;
    }

    public static <O extends StoredObject> ObjectEditor<O> createInternalEditor(Class<O> objectClass,
                                                                                int actions, String title) {
        ObjectEditor<O> editor = editor(objectClass, actions, title);
        return editor == null ? new ObjectEditor<>(objectClass, actions, title) : editor;
    }
}
