package com.storedobject.ui.iot;

import com.storedobject.common.MathUtility;
import com.storedobject.core.Id;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromptForP5DashboardGenerator extends BlockSelector {

    private Block block;
    private final List<Unit> units = new ArrayList<>();
    private final Map<String, Object> dataMap = new HashMap<>();
    private final Map<Id, Map<String, Object>> unitMaps = new HashMap<>();

    public PromptForP5DashboardGenerator() {
        super("AI Prompt for P5 Dashboard Generation");
    }

    @Override
    protected boolean accept(Block block) throws Exception {
        close();
        this.block = block;
        units.clear();
        block.listUnits().collectAll(units);
        buildTree();
        StringBuilder s = new StringBuilder();
        stringify(s, dataMap, "p.data", 0);
        new TextBox(s.toString()).execute();
        return false;
    }

    private static void stringify(StringBuilder s, Map<String, Object> map, String name, int level) {
        String pad = StringUtility.padLeft("", level << 1);
        if(level == 0) {
            s.append(" ").append(name).append(" =");
        } else {
            s.append(pad).append("[\"").append(name).append('"').append(",");
        }
        s.append(" new Map([\n");
        Object v;
        boolean first = true;
        for(Map.Entry<String, Object> e: map.entrySet()) {
            if(first) {
                first = false;
            } else {
                s.append(",\n");
            }
            v = e.getValue();
            if(v instanceof Map<?, ?> m) {
                //noinspection unchecked
                stringify(s, (Map<String, Object>) m, e.getKey(), level + 1);
            } else {
                if(v instanceof Double d) {
                    v = StringUtility.toString(d);
                } else if(v instanceof Boolean b) {
                    v = b ? "\"On\"" : "\"Off\"";
                } else {
                    v = "\"" + StringUtility.toString(v) + "\"";
                }
                s.append("  ").append(pad).append("[\"").append(e.getKey()).append('"').append(", ").append(v).append(']');
            }
        }
        s.append('\n').append(pad).append("])");
        if(level == 0) {
            s.append(";\n");
        } else {
            s.append("]");
        }
    }

    private void buildTree() {
        paint(block);
        Id siteId = block.getSiteId();
        List<Unit> unprocessed = new ArrayList<>(units);
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(siteId))
                .forEach(s -> buildBranches(s, unprocessed));
    }

    private void buildBranches(DataSet.AbstractData parent, List<Unit> unprocessed) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(units.contains(ud.getUnit())) {
                    ud.getDataStatus().forEach(ds -> process(ds, unprocessed));
                }
            }
            buildBranches(row, unprocessed);
        });
    }

    private void process(DataSet.DataStatus<?> ds, List<Unit> unprocessed) {
        Unit unit = ds.getUnit();
        if(unprocessed.contains(unit)) {
            unprocessed.remove(unit);
            paint(unit);
        }
        Map<String, Object> parent = unitMaps.get(unit.getId());
        if(ds instanceof DataSet.LimitStatus ls) {
            paint(ls, parent);
        } else if(ds instanceof DataSet.AlarmStatus as) {
            paint(as, parent);
        }
    }

    private void paint(Block block) {
        dataMap.clear();
        dataMap.put("title", block.getName());
    }

    private void paint(Unit unit) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", unit.getName());
        dataMap.put(unit.getShortName() + (unit.getOrdinality() + 1), m);
        unitMaps.put(unit.getId(), m);
    }

    private void paint(DataSet.LimitStatus limitStatus, Map<String, Object> parent) {
        Map<String, Object> m = new HashMap<>();
        parent.put("sensor", m);
        ValueLimit d = limitStatus.getValueDefinition();
        m.put("name", d.getName());
        m.put("label", d.getLabel());
        m.put("type", "value");
        m.put("value", limitStatus.getValue());
        String u = d.getUnitSuffix();
        if(!u.isEmpty()) {
            m.put("measurementUnit", u);
        }
        if(d.getUnlimited()) {
            m.put("range", "unlimited");
        } else {
            Map<String, Object> r = new HashMap<>();
            m.put("range", r);
            r.put("min", d.getMinimum());
            r.put("max", d.getMaximum());
            double v = d.getLow();
            r.put("low", v);
            double nv = d.getLower();
            if(!MathUtility.equals(nv, v)) {
                r.put("lower", nv);
                v = nv;
            }
            nv = d.getLowest();
            if(!MathUtility.equals(nv, v)) {
                r.put("lowest", nv);
            }
            v = d.getHigh();
            r.put("high", v);
            nv = d.getHigher();
            if(!MathUtility.equals(nv, v)) {
                r.put("higher", nv);
                v = nv;
            }
            nv = d.getHighest();
            if(!MathUtility.equals(nv, v)) {
                r.put("highest", nv);
            }
        }
    }

    private void paint(DataSet.AlarmStatus alarmStatus, Map<String, Object> parent) {
        Map<String, Object> m = new HashMap<>();
        parent.put("sensor", m);
        AlarmSwitch d = alarmStatus.getValueDefinition();
        m.put("name", d.getName());
        m.put("label", d.getLabel());
        m.put("type", "On/Off");
        m.put("value", alarmStatus.value());
        m.put("alarmWhen", d.getAlarmWhenValue());
    }

    private static class TextBox extends DataForm {

        public TextBox(String dataMap) {
            super("AI Prompt");
            setButtonsAtTop(true);
            TextArea p = new TextArea("Prompt");
            addField(p);
            String a, b;
            a = """
                Create a standalone P5 sketch as per the following template.
                Notes:
                (1) If you want to use any images, use inline SVG images only.
                (2) The whole drawing part should be scalable to fit the canvas size.
                (3) This sketch may be used with different sensor values and the drawing should show appropriate alarms and/or warnings.
                (4) On/off type sensors may indicate on/off states of certain devices or moving objects.
                (5) The drawing should be able to handle different aspect ratios of the canvas.
                // --- TEMPLATE STARTS ---
                // Provided data that contains details of all sensors and units
               """;
            b = """
                // You insert any helper functions here
                p.setup = () => {
                    const container = p.canvas?.parentElement || p;
                    const w = container?.offsetWidth || 400;
                    const h = container?.offsetHeight || 400;
                    p.createCanvas(w > 0 ? w : 400, h > 0 ? h : 400);
                };
                p.draw = () => {
                    // Insert the code for the drawing part based on the data provided
                };
                // --- TEMPLATE ENDS ---
               """;
            p.setText(a + dataMap + b);
        }

        @Override
        protected boolean process() {
            return true;
        }

        @Override
        public int getMinimumContentWidth() {
            return 80;
        }
    }
}
