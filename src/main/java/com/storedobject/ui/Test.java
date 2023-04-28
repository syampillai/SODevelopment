package com.storedobject.ui;

import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;

public class Test extends PresentationRunner {

    public Test() {
        addScreen("com.engravsystems.emqim.inventory.logic.PurchaseOrderBrowser", 1);
        addScreen(() -> speak("Hello, this is the Purchase Order screen"), 1);
        addScreen(this::aircraftStatus, 5);
        addScreen("com.engravsystems.emqim.inventory.logic.MyChart", 6);
        addScreen(com.storedobject.ui.inventory.LocateItem.class, 7);
    }

    private void speak(String sentence) {
        getApplication().speak(sentence);
    }

    private void aircraftStatus() {
        try {
            @SuppressWarnings("unchecked") Class<? extends StoredObject> aClass = (Class<? extends StoredObject>)
                    JavaClassLoader.getLogic("com.engravsystems.emqim.engineering.Aircraft");
            StringBuilder s = new StringBuilder();
            StoredObject.list(aClass).forEach(a -> s.append(a.toDisplay()).append("\n"));
            speak(s.toString());
        } catch(ClassNotFoundException ignored) {
        }
    }
}