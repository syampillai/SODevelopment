package com.storedobject.ui;

public class Test extends PresentationRunner {

    public Test() {
        addScreen("com.engravsystems.emqim.inventory.logic.PurchaseOrderBrowser", 8);
        addScreen("com.engravsystems.emqim.inventory.logic.MyChart", 6);
        addScreen("com.storedobject.ui.inventory.LocateItem", 7);
    }
}