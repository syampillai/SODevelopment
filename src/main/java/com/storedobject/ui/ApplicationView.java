package com.storedobject.ui;

import com.storedobject.common.IO;
import com.vaadin.flow.server.AppShellSettings;

import java.io.BufferedReader;
import java.io.InputStream;

public class ApplicationView extends com.storedobject.vaadin.ApplicationView {

    public void configurePage(AppShellSettings settings) {
        InputStream init = getClass().getClassLoader().getResourceAsStream("application.init");
        if(init == null) {
            return;
        }
        BufferedReader r = IO.getReader(init);
        String line;
        try {
            while((line = r.readLine()) != null) {
                if(line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if(line.startsWith("viewport ")) {
                    settings.setViewport(line.substring(line.indexOf(' ') + 1).trim());
                }
            }
        } catch(Exception ignored) {
        } finally {
            IO.close(r);
        }
    }
}
