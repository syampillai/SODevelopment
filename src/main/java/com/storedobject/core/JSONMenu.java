package com.storedobject.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONMenu implements JSONService {

    private final DeviceMenu dm = new DeviceMenu();
    private boolean populated = false;

    public void execute(Device device, JSON json, JSONMap result) {
        if(!populated) {
            //noinspection ResultOfMethodCallIgnored
            device.getServer().populateMenu(dm, null);
            dm.clean();
            populated = true;
        }
        result.put("menu", dm);
    }

    private static class DeviceMenu extends ArrayList<Map<String, Object>>
            implements com.storedobject.core.ApplicationMenu {

        DeviceMenu() {
        }

        private void clean() {
            boolean removed = true;
            while (removed) {
                removed = false;
                for(Map<String, Object> m: this) {
                    if(m.get("menu") instanceof DeviceMenu dm) {
                        dm.clean();
                        if(dm.isEmpty()) {
                            remove(m);
                            removed = true;
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void add(Logic logic) {
            ConnectorLogic cl = ConnectorLogic.get(ConnectorLogic.class,
                    "lower(LogicName)='" + logic.getClassName().toLowerCase() + "'");
            if (cl == null || !cl.getActive()) {
                return;
            }
            Map<String, Object> m = new HashMap<>();
            add(m);
            m.put("command", cl.getConnectorCommand());
            m.put("title", logic.getTitle());
            String icon = logic.getIconImageName();
            m.put("icon", icon == null || icon.isBlank() || "go".equals(icon) ? "" : icon);
            m.put("id", String.valueOf(logic.getId()));
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