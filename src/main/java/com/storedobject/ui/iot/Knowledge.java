package com.storedobject.ui.iot;

import com.storedobject.ai.KnowledgeModule;
import com.storedobject.core.*;
import com.storedobject.iot.*;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class Knowledge extends com.storedobject.ui.ai.Knowledge {

    private final SensorData sensorData = new SensorData();
    private final BS bs = new BS();
    private final Button select = new Button("Select", (String)null, e -> bs.execute());
    private boolean sendingAll;

    public Knowledge() {
        this(null);
    }

    public Knowledge(Block block) {
        select.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addDataClass(Site.class, "name");
        addDataClass(Block.class, "Name", "Code");
        addModules(new KM());
        if(block != null) {
            putToMemory(block.getSite());
            putToMemory(block);
        }
    }

    @Override
    public <T extends StoredObject> void set(T object, String purpose) {
        if(object instanceof Block b) {
            putToMemory(b.getSite());
            setBlock(b);
        }
    }

    @Override
    public <T extends StoredObject> void customize(QueryBuilder<T> queryBuilder) {
        if(queryBuilder.getObjectClass() == Block.class) {
            queryBuilder.where("Site=" + getSelected(Site.class).getId());
        }
        super.customize(queryBuilder);
    }

    @Override
    public JSONMap getExtraKnowledge() {
        if(sendingAll) {
            sendingAll = false;
            return null;
        }
        return sensorData.build();
    }

    @Override
    public String getStartingPrompt() {
        Block b = getBlock();
        if(b == null) return null;
        return "Selected block ID = " + b.getId() + ", show me its details";
    }

    @Override
    public String getTopic() {
        Block block = getBlock();
        return block == null ? "Please select by clicking the \"Select\" button!" : block.getName();
    }

    @Override
    public Component[] getExtraComponents() {
        return new Component[] { select };
    }

    public void setBlock(Block block) {
        if(block == null) return;
        putToMemory(block);
        execute();
    }

    @Override
    public void execute() {
        if(getBlock() == null) {
            bs.execute();
            return;
        }
        super.execute();
    }

    public Block getBlock() {
        return getFromMemory(Block.class);
    }

    private class BS extends BlockSelector {

        public BS() {
            super("Select");
        }

        @Override
        protected boolean accept(Block block) {
            Knowledge.this.setBlock(block);
            return true;
        }

        @Override
        protected void cancel() {
            super.cancel();
            Knowledge.this.setBlock(null);
        }
    }

    class KM implements KnowledgeModule {

        @Tool("Get sensor data for all blocks as a JSON string")
        public String getAllSensorData() {
            sendingAll = true;
            getSelected(Block.class);
            return sensorData.buildAll().toJSON().getString();
        }

        @Tool("Get sensor data history for a sensor as a JSON string")
        public String getHistory(@P("ID of the sensor")  long sensorId,
                                 @P("Time period in minutes") long timePeriod,
                                 @P("Time step in minutes") int timeStep) {
            JSONMap map = new JSONMap();
            DataSet.DataStatus<?> ds = DataSet.DataStatus.get(getSelected(Site.class).getId(), sensorId);
            if(ds == null) {
                map.put("error", "Sensor not found - " + sensorId);
                return map.toJSON().toString();
            }
            map.put("Info", "Sensor data history for the sensor");
            timePeriod = DateUtility.now().getTime() - timePeriod;
            return map.toJSON().toString();
        }
    }

    private class SensorData {

        private final Semaphore semaphore = new Semaphore(1);

        JSONMap build() {
            Block block = getBlock();
            if(block == null) {
                return null;
            }
            JSONMap map = new JSONMap();
            map.put("Info", "Sensor data for the units within the currently selected block");
            map.put("Site", block.getSite().getName());
            build(block, map);
            return map;
        }

        JSONMap buildAll() {
            JSONMap map = new JSONMap();
            Block b = getBlock();
            if(b == null) {
                map.put("error", "Unable to identify the site because there is no block selected");
                return map;
            }
            map.put("Site", b.getSite().getName());
            JSONMap.Array a = map.array("Blocks");
            b.getSite().listActiveBlocks().forEach(block -> build(block, a.map()));
            return map;
        }

        private void build(Block block, JSONMap map) {
            semaphore.acquireUninterruptibly();
            paint(block, map);
            JSONMap.Array a = ((JSONMap) map.get("Block")).array("Units");
            Id siteId = block.getSiteId();
            Id blockId = block.getId();
            DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(siteId))
                    .forEach(s -> buildBranches(s, blockId, a));
            semaphore.release();
        }

        private void buildBranches(DataSet.AbstractData parent, Id blockId, JSONMap.Array array) {
            parent.children().forEach(row -> {
                if(row instanceof DataSet.UnitData ud) {
                    if(ud.getUnit().getBlockId().equals(blockId)) {
                        ud.getDataStatus().forEach(ds -> process(ds, array));
                    }
                } else {
                    buildBranches(row, blockId, array);
                }
            });
        }

        private void process(DataSet.DataStatus<?> ds, JSONMap.Array array) {
            JSONMap map;
            Unit unit = ds.getUnit();
            map = array.maps().filter(m -> m.get("Id").equals(unit.getId().get())).findAny().orElse(null);
            if(map == null) {
                map = array.map();
                paint(unit, map);
            }
            Map<String, Object> m = new TreeMap<>();
            m.put("Sensor-ID", ds.getId());
            m.put("Name", ds.getValueDefinition().getName());
            m.put("Label", ds.getValueDefinition().getLabel());
            if(ds instanceof DataSet.LimitStatus ls) {
                paint(ls, m);
            } else if(ds instanceof DataSet.AlarmStatus as) {
                paint(as, m);
            }
            map.array("Sensors").add(m);
        }

        private void paint(Block block, JSONMap map) {
            save(map, "Block", block);
        }

        private void paint(Unit unit, JSONMap map) {
            map.put("Id", unit.getId().get());
            map.put("Name", unit.getName());
        }

        private void paint(DataSet.LimitStatus limitStatus, Map<String, Object> m) {
            m.put("Type", "Value");
            m.put("Value", limitStatus.getValue());
            String uom = limitStatus.getValueDefinition().getUnitSuffix();
            if(!uom.isBlank()) {
                m.put("UoM", uom);
            }
            m.put("Value-Level", limitStatus.statusLabel());
            if(limitStatus.alarm() != 0) {
                m.put("Level-Change-Time", ts(limitStatus.getAlarmAt()));
            }
        }

        private void paint(DataSet.AlarmStatus alarmStatus, Map<String, Object> m) {
            m.put("Type", "Switch");
            m.put("State", alarmStatus.value());
            m.put("Alarm", alarmStatus.statusLabel());
            if(alarmStatus.alarm() != 0) {
                m.put("Alarm-Time", ts(alarmStatus.getAlarmAt()));
            }
        }

        private Timestamp ts(long time) {
            return getTransactionManager().date(new Timestamp(time));
        }
    }
}
