package com.storedobject.ui;

import com.storedobject.common.JSON;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.Window;
import com.storedobject.vaadin.WindowDecorator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.IFrame;

public class Test extends View {

    public Test() {
        super("Test");
        IFrame iFrame =
                new IFrame(
                        "https://toolbox-iframe.private.fin.ag/?demo=true&redirectUrl=https://flinks.com/contact/"
                                + "thank-you&innerRedirect=true&theme=light&consentEnable=true&customerName=FinTech&backgroundColor="
                                + "f7f7f7&foregroundColor1=000000&desktopLayout=true&headerEnable=false&institutionFilterEnable=true");
        iFrame.setHeight("600px");
        setComponent(iFrame);
        setWindowMode(true);
    }

    @Override
    protected Window createWindow(Component component) {
        return new Window(new WindowDecorator(this), component, new BrowserMessage(this::test));
    }

    private void test(String origin, JSON message) {
        message("From " + origin);
        message(message.toPrettyString());
    }
}