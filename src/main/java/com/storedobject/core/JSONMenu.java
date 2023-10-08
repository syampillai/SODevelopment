package com.storedobject.core;

import com.storedobject.common.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONMenu implements JSONService {

    private final DeviceMenu dm = new DeviceMenu();
    private boolean populated = false;

    public void execute(Device device, JSON json, Map<String, Object> result) {
        if(!populated) {
            //noinspection ResultOfMethodCallIgnored
            device.getServer().populateMenu(dm, null);
            populated = true;
        }
        result.put("menu", dm);
    }

    private static class DeviceMenu extends ArrayList<Map<String, Object>>
            implements com.storedobject.core.ApplicationMenu {

        DeviceMenu() {
        }

        @Override
        public void add(Logic logic) {
            ConnectorLogic cl = ConnectorLogic.get(ConnectorLogic.class,
                    "LogicName='" + logic.getClassName() + "'");
            if (cl == null || !cl.getActive()) {
                return;
            }
            Map<String, Object> m = new HashMap<>();
            add(m);
            m.put("command", cl.getConnectorCommand());
            String s = logic.getClassName();
            s = s.substring(s.lastIndexOf('.') + 1);
            s = s.substring(0, 1).toLowerCase() + s.substring(1);
            m.put("command", s);
            m.put("title", logic.getTitle());
            String icon = logic.getIconImageName();
            m.put("icon", icon == null || icon.isBlank() || "go".equals(icon) ? "" : icon);
            m.put("id", String.valueOf(logic.getId()));
            m.put("menu", new ArrayList<>());
        }

        @Override
        public void add(LogicGroup logicGroup) {
        }

        @Override
        public com.storedobject.core.ApplicationMenu createGroupMenu(LogicGroup logicGroup) {
            Map<String, Object> m = new HashMap<>();
            add(m);
            m.put("command", "");
            m.put("icon", "");
            m.put("title", logicGroup.getTitle());
            m.put("id", String.valueOf(logicGroup.getId()));
            DeviceMenu gm = new DeviceMenu();
            m.put("menu", gm);
            return gm;
        }
    }
}