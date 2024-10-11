package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ValueDefinition<VT> extends StoredObject implements Detail {

    private String name = "";
    private String caption = "", label = "", tooltip = "";
    private int significance;
    private boolean active = true, alert, command;
    private Method gvm, svm;
    private boolean showImage, showChart;
    private int imageX, imageY;
    private String imagePrefix = "";
    Data data;
    private Map<Integer, Position> positions;

    public ValueDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Caption", "text");
        columns.add("Label", "text");
        columns.add("Tooltip", "text");
        columns.add("Significance", "int");
        columns.add("Alert", "boolean");
        columns.add("Active", "boolean");
        columns.add("Command", "boolean");
        columns.add("ShowImage", "boolean");
        columns.add("ShowChart", "boolean");
        columns.add("ImageX", "int");
        columns.add("ImageY", "int");
        columns.add("ImagePrefix", "text");
    }

    public static String[] searchColumns() {
        return new String[] {
                "Name",
        };
    }

    public static String[] links() {
        return new String[] {
                "Image Positions|com.storedobject.iot.ValueImagePosition",
        };
    }

    public static ValueDefinition<?> get(String name) {
        return StoredObjectUtility.get(ValueDefinition.class, "Name", name, true);
    }

    public static ObjectIterator<ValueDefinition<?>> list(String name) {
        return StoredObjectUtility.list(ValueDefinition.class, "Name", name, true)
                .map(o -> (ValueDefinition<?>) o);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Column(required = false, order = 200)
    public String getCaption() {
        return caption == null || caption.isBlank() ? StringUtility.makeLabel(name) : caption;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(required = false, order = 250)
    public String getLabel() {
        return label;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Column(required = false, order = 260)
    public String getTooltip() {
        return tooltip.isEmpty() ? getCaption() : tooltip;
    }

    public void setSignificance(int significance) {
        this.significance = significance;
    }

    @Column(required = false, order = 300)
    public int getSignificance() {
        return significance;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    @Column(order = 5700)
    public boolean getAlert() {
        return alert;
    }

    public void setCommand(boolean command) {
        this.command = command;
    }

    @Column(order = 5800)
    public boolean getCommand() {
        return command;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 5900)
    public boolean getActive() {
        return active;
    }

    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
    }

    @Column(order = 6900)
    public boolean getShowChart() {
        return showChart;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    @Column(order = 7000)
    public boolean getShowImage() {
        return showImage;
    }

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    @Column(order = 7100, required = false)
    public int getImageX() {
        return imageX;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }

    @Column(order = 7200, required = false)
    public int getImageY() {
        return imageY;
    }

    public void setImagePrefix(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }

    @Column(order = 7500, required = false)
    public String getImagePrefix() {
        return imagePrefix.isBlank() ? name : imagePrefix;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        if(imagePrefix.equalsIgnoreCase(name)) {
            imagePrefix = "";
        }
        super.validateData(tm);
    }

    @Override
    public void validateDelete() throws Exception {
        Transaction t = getTransaction();
        for(Command c: list(Command.class, "Command=" + getId())) {
            c.delete(t);
        }
        super.validateDelete();
    }

    @Override
    public Object getUniqueValue() {
        return name.toUpperCase();
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == UnitDefinition.class;
    }

    public Class<? extends Data> getDataClass() {
        return getMaster(UnitDefinition.class).getDataClass();
    }

    public Class<? extends Unit> getUnitClass() {
        return getMaster(UnitDefinition.class).getUnitClass();
    }

    private void m() {
        Class<?> dataClass = getDataClass();
        try {
            if(dataClass != null) {
                gvm = dataClass.getMethod("get" + name);
                svm = dataClass.getMethod("set" + name);
            }
        } catch (NoSuchMethodException ignored) {
        }
    }

    public Method getValueMethodForGet() {
        if(gvm == null) {
            m();
        }
        return gvm;
    }

    public Method getValueMethodForSet() {
        if(svm == null) {
            m();
        }
        return svm;
    }

    @SuppressWarnings("unchecked")
    public <IOT extends Data> IOT getLatestData(Id unitId) {
        if(gvm == null) {
            m();
        }
        if(gvm != null) {
            return Data.getLatest((Class<IOT>)gvm.getDeclaringClass(), unitId);
        }
        return (IOT)Data.getLatest(getMaster(UnitDefinition.class).getDataClass(), unitId);
    }

    public final VT getValue(Id unitId) {
        Data data = null;
        if(this.data != null) {
            if(this.data.getUnitId().equals(unitId)) {
                data = this.data;
            } else {
                this.data = null;
            }
        }
        if(data == null) {
            if (MQTTDataCollector.instance != null && MQTTDataCollector.instance.mqtt != null) {
                data = MQTTDataCollector.instance.mqtt.getData(this, unitId);
                this.data = data;
            }
        }
        if(data == null) {
            data = getLatestData(unitId);
        }
        try {
            //noinspection unchecked
            return data == null ? null : (VT)getValueMethodForGet().invoke(data);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }

    @Override
    public String toString() {
        return caption;
    }

    public String getShortName() {
        if(!StringUtility.isWhite(label)) {
            return label;
        }
        if(!StringUtility.isWhite(caption)) {
            return caption;
        }
        return StringUtility.makeLabel(name);
    }

    public int getImageX(int ordinality) {
        if(ordinality < 1) {
            return imageX;
        }
        return getPosition(ordinality).x;
    }

    public int getImageY(int ordinality) {
        if(ordinality < 1) {
            return imageY;
        }
        return getPosition(ordinality).y;
    }

    public String getLabel(int ordinality) {
        if(ordinality < 1) {
            return tagged(getLabel(), ordinality);
        }
        return tagged(getPosition(ordinality).label, ordinality);
    }

    public String getTooltip(int ordinality) {
        if(ordinality < 1) {
            return tagged(getTooltip(), ordinality);
        }
        return tagged(getPosition(ordinality).tooltip, ordinality);
    }

    public String getCaption(int ordinality) {
        return tagged(getCaption(), ordinality);
    }

    private String tagged(String label, int ordinality) {
        if(label.contains("{#}")) {
            return label.replace("{#}", String.valueOf(ordinality + 1));
        }
        return label;
    }

    private Position getPosition(int ordinality) {
        if(positions == null) {
            positions = new HashMap<>();
            listLinks(ValueImagePosition.class).forEach(vip -> positions.put(vip.getOrdinality(),
                    new Position(vip.getImageX(), vip.getImageY(), nonWhite(vip.getLabel(), getLabel()),
                            nonWhite(vip.getTooltip(), getTooltip()))));
        }
        Position position = positions.get(ordinality);
        return position == null ? new Position(imageX, imageY, getLabel(), getTooltip()) : position;
    }

    private static String nonWhite(String one, String two) {
        return StringUtility.isWhite(one) ? two : one;
    }

    private record Position(int x, int y, String label, String tooltip) {}

    public void savePosition(Transaction t, int imageX, int imageY, Unit unit) throws Exception {
        if(unit.getOrdinality() < 1) {
            setImageX(imageX);
            setImageY(imageY);
            save(t);
            return;
        }
        ValueImagePosition vip = listLinks(ValueImagePosition.class, "Ordinality=" + unit.getOrdinality())
                .findFirst();
        if(vip == null) {
            vip = new ValueImagePosition();
            vip.setOrdinality(unit.getOrdinality());
        }
        vip.setImageX(imageX);
        vip.setImageY(imageY);
        vip.save(t);
        addLink(t, vip);
        DataSet.scheduleRefresh();
    }
}
