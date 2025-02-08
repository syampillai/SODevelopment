package com.storedobject.ui.iot;

import com.storedobject.chart.*;
import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.TimestampPeriod;
import com.storedobject.iot.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.MinutesField;
import com.storedobject.ui.TimestampPeriodField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class ValueChart extends View implements CloseableView, Transactional {

    private DataValues values;
    private List<DataValue> selected;
    private final HasComponents layout;
    private SOChart soChart;
    private RectangularCoordinate coordinate;
    private Position position;
    private ChoosePeriod choosePeriod;
    private TimestampPeriod period;
    private final List<LAxis> axes = new ArrayList<>();
    private final ChoiceField chartType = new ChoiceField(null, StringList.create("Line", "Bar", "Scatter"));
    private final MinutesField timeStepField = new MinutesField();
    private int timeStep = 15 * 60000;
    private final GUI gui;
    private Site site;

    public ValueChart(GUI gui) {
        super("Montrolive Charts");
        this.gui = gui;
        chartType.setWidth("7em");
        long time = com.storedobject.iot.DataSet.getTime();
        if(time == 0) {
            time = getTransactionManager().date(DateUtility.now()).getTime();
        }
        timeStepField.setWidth("7ch");
        timeStepField.setValue(15);
        timeStepField.addValueChangeListener(e -> changeTimeStep());
        chartType.addValueChangeListener(e -> plotAgain());
        period = new TimestampPeriod(
                DateUtility.createTimestamp(DateUtility.startOfToday().getTime() - GUI.site().getTimeDifference()),
                new Timestamp(time));
        layout = new VerticalLayout(
                new ButtonLayout(
                        new Button("Choose Period", VaadinIcon.DATE_INPUT, e -> choosePeriod()),
                        new Button("Choose Value Set", VaadinIcon.LINE_CHART, e -> chooseValueSet()),
                        new ELabel("Chart Type"),
                        chartType,
                        new ELabel("Time Slice"),
                        timeStepField,
                        new Button("Dashboard", VaadinIcon.DASHBOARD, e -> gui.dashboard()),
                        new Button("Status", VaadinIcon.GRID, e -> gui.statusGrid()),
                        new Button("Site View", VaadinIcon.FACTORY, e -> gui.siteView()),
                        new Button("Send Control Command", VaadinIcon.PAPERPLANE_O, e -> gui.sendCommand()),
                        gui.consumptionButton(),
                        gui.dataButton(),
                        new Button("Exit", e -> close())
                )
        );
        setComponent((Component) layout);
    }

    private void createChart() {
        resetChart();
        soChart = new SOChart();
        soChart.setSize("100%", "500px");
        soChart.getDefaultTooltip().setType(Tooltip.Type.Axis);
        layout.add(soChart);
    }

    private class ChoosePeriod extends DataForm {

        private final TimestampPeriodField periodField = new TimestampPeriodField("Period");

        public ChoosePeriod() {
            super("Choose Period");
            addField(periodField);
        }

        @Override
        protected boolean process() {
            close();
            period = periodField.getValue();
            int diff = GUI.site().getTimeDifference();
            Timestamp from = DateUtility.createTimestamp(period.getFrom().getTime() - diff),
                    to = DateUtility.createTimestamp(period.getTo().getTime() - diff);
            period = new TimestampPeriod(from, to);
            if(values != null) {
                changePeriod();
            }
            return true;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            int diff = GUI.site().getTimeDifference();
            Timestamp from = DateUtility.createTimestamp(period.getFrom().getTime() + diff),
                    to = DateUtility.createTimestamp(period.getTo().getTime() + diff);
            periodField.setValue(new TimestampPeriod(from, to));
            super.execute(parent, doNotLock);
        }
    }

    private void choosePeriod() {
        if(choosePeriod == null) {
            choosePeriod = new ChoosePeriod();
        }
        choosePeriod.execute();
    }

    private void chooseValueSet() {
        List<DataValue> values = new ArrayList<>();
        buildTree(values);
        values.sort(Comparator.comparing(dv -> dv.getDataStatus().getValueDefinition().getCaption()));
        new SelectValues(values).execute();
    }

    @Override
    public void clean() {
        clear();
        super.clean();
    }

    private void clear() {
        resetChart();
        if(values != null) {
            values.dispose();
            values = null;
        }
        site = null;
    }

    private void resetChart() {
        if(soChart != null) {
            axes.clear();
            soChart.clear();
            layout.remove(soChart);
        }
    }

    private void changeTimeStep() {
        timeStep = timeStepField.getValue() * 60000;
        if(timeStep == 0) {
            timeStepField.setValue(15);
            return;
        }
        if(values == null) {
            return;
        }
        try {
            soChart.updateData(values);
            for(var d : values.getDataValues()) {
                soChart.updateData((DataValue)d);
            }
        } catch(Exception e) {
            warning(e);
        }
    }

    private void plot() {
        if (values != null) {
            values.dispose();
        }
        if (selected == null || selected.isEmpty()) {
            return;
        }
        values = new DataValues(selected, period.getFrom().getTime(), period.getTo().getTime());
        plotAgain();
    }

    private void plotAgain() {
        createChart();
        if(values.getDataValues().isEmpty()) {
            warning("No data found!");
            close();
            return;
        }
        XAxis xAxis = new XAxis(DataType.TIME);
        xAxis.setMinAsMinData();
        xAxis.setMaxAsMaxData();
        Axis.Label label = xAxis.getLabel(true);
        label.setRotation(-45);
        label.setFormatter("{MMM} {dd} {HH}:{mm}");
        coordinate = new RectangularCoordinate(xAxis);
        position = coordinate.getPosition(true);
        YAxis yAxis;
        DataValue dv;
        int type = chartType.getValue();
        for(DataSet.DataValue ddv: values.getDataValues()) {
            dv = (DataValue) ddv;
            yAxis = createAxis(dv);
            coordinate.addAxis(yAxis);
            XYChart chart = switch (type) {
                case 0 -> new LineChart(values, dv);
                case 1 -> new BarChart(values, dv);
                case 2 -> new ScatterChart(values, dv);
                default -> null;
            };
            if(chart == null) {
                continue;
            }
            if(chart instanceof LineChart lc) {
                if (ddv.getDataStatus() instanceof DataSet.AlarmStatus) {
                    lc.setStepped(true);
                    lc.setStepped(Location.START);
                } else {
                    lc.setSmoothness(true);
                }
                lc.getPointSymbol(true).setType(PointSymbolType.NONE);
            }
            chart.setName(dv.getName());
            chart.plotOn(coordinate, xAxis, yAxis);
        }
        soChart.add(coordinate);
        try {
            soChart.update();
        } catch(Exception e) {
            error(e);
        }
        super.execute(null, true);
    }

    private void changePeriod() {
        if(soChart == null || values == null || values.getDataValues().isEmpty()) {
            return;
        }
        values.load(period.getFrom().getTime(), period.getTo().getTime());
        try {
            soChart.updateData(coordinate);
        } catch(Exception e) {
            warning(e);
        }
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(GUI.site() == null) {
            close();
            warning("No site selected");
            return;
        } else if(!GUI.site().getActive()) {
            warning("Not an active site - " + GUI.site().getName());
            close();
            return;
        }
        if(gui.units.isEmpty()) {
            warning("Please select");
            close();
            gui.siteView();
            return;
        }
        if(GUI.site() == site) {
            super.execute(parent, doNotLock);
            return;
        }
        if(loadSite()) {
            super.execute(parent, doNotLock);
            chooseValueSet();
        }
    }

    private boolean loadSite() {
        clear();
        if(checkTree()) {
            site = GUI.site();
            return true;
        }
        warning("No attributes configured for the chart");
        site = null;
        close();
        return false;
    }

    private boolean checkTree() {
        return DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(GUI.site().getId()))
                .anyMatch(this::checkBranches);
    }

    private boolean checkBranches(DataSet.AbstractData parent) {
        var children = parent.children();
        if(children.isEmpty()) {
            return false;
        }
        for(var row: children) {
            if(row instanceof DataSet.UnitData ud) {
                if(ud.getDataStatus().stream().anyMatch(ds -> ds.getValueDefinition().getShowChart())) {
                    return true;
                }
            }
            if(checkBranches(row)) {
                return true;
            }
        }
        return false;
    }

    private void buildTree(List<DataValue> values) {
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(GUI.site().getId()))
                .forEach(s -> buildBranches(values, s));
    }

    private void buildBranches(List<DataValue> values, DataSet.AbstractData parent) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(gui.units.contains(ud.getUnit())) {
                    ud.getDataStatus().stream()
                            .filter(ds -> ds.getValueDefinition().getShowChart())
                            .forEach(ds -> values.add(new DataValue(ud, ds)));
                }
            }
            buildBranches(values, row);
        });
    }

    final class DataValue extends com.storedobject.iot.DataSet.DataValue
            implements AbstractDataProvider<Double> {

        private int serial;

        DataValue(com.storedobject.iot.DataSet.AbstractData data, com.storedobject.iot.DataSet.DataStatus<?> status) {
            super(data, status);
        }

        public String getValuesToPlot() {
            DataSet.DataStatus<?> ds = getDataStatus();
            return ds.display() + " (" + ds.getUnit().getName() + ")";
        }

        @Override
        public Stream<Double> stream() {
            return stream(timeStep);
        }

        @Override
        public DataType getDataType() {
            return DataType.NUMBER;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }

        @Override
        public String getName() {
            return getDataStatus().label() + "\n" + getData().getName();
        }
    }

    private class DataValues extends com.storedobject.iot.DataSet.DataValues
            implements AbstractDataProvider<LocalDateTime> {

        private int serial;

        DataValues(List<? extends DataSet.DataValue> values, long from, long to) {
            super(values, from, to);
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }

        @Override
        public Stream<LocalDateTime> stream() {
            return stream(timeStep, GUI.site().getTimeDifference());
        }

        @Override
        public DataType getDataType() {
            return DataType.TIME;
        }
    }

    private class SelectValues extends MultiSelectGrid<DataValue> {

        public SelectValues(List<DataValue> items) {
            super(DataValue.class, items, StringList.create("ValuesToPlot"));
            setCaption("Choose Values");
        }

        @Override
        protected void process(Set<DataValue> selected) {
            if(selected.isEmpty()) {
                message("Nothing selected");
                ValueChart.this.close();
            } else {
                ValueChart.this.selected = new ArrayList<>(selected);
                plot();
            }
        }
    }

    private class LAxis extends YAxis {

        protected final DataValue dv;
        int slot = 0;

        private LAxis(DataValue dv) {
            super(DataType.NUMBER);
            this.dv = dv;
            axes.add(this);
            setName(dv.getName().replace("\n", ", "));
            setNameLocation(Location.CENTER);
            setNameGap(2);
            Axis.Label axisLabel = getLabel(true);
            axisLabel.setGap(17);
            ValueDefinition<?> vd = dv.getDataStatus().getValueDefinition();
            if(vd instanceof ValueLimit v) {
                if(!v.getUnlimited()) {
                    setMin(v.getMinimum());
                    setMax(v.getMaximum());
                }
                axisLabel.setFormatter("{value}" + v.getUnitOfMeasurement().getUnit().getUnit());
            } else {
                setMin(0);
                setMax(1);
                axisLabel.setFormatterFunction("return value > 0 ? 'On' : 'Off';");
                getGridLines(true).hide();
            }
            init();
        }

        protected void init() {
            setNameRotation(90);
        }

        void addLabel(String label) {
            setName(getName() + "\n" + label.replace("\n", ", "));
            Axis.Label axisLabel = getLabel(true);
            axisLabel.setGap(axisLabel.getGap() + 15);
            if(this == axes.get(0)) {
                return;
            }
            slot += 15;
            axes.stream().dropWhile(a -> a != this).skip(1).forEach(a -> a.setOffset(a.getOffset() + 15));
        }
    }

    private class FRAxis extends LAxis {

        private FRAxis(DataValue dv) {
            super(dv);
            slot = 100;
        }

        @Override
        protected void init() {
            setNameRotation(-90);
        }
    }

    private class RAxis extends FRAxis {

        private RAxis(DataValue dv) {
            super(dv);
        }

        @Override
        protected void init() {
            super.init();
            slot = 90;
            int offset = axes.stream().skip(2).filter(a -> a != this).mapToInt(a -> a.slot).sum();
            setOffset(offset + slot);
            position.setRight(Size.pixels(axes.get(1).slot + offset + slot));
        }
    }

    private YAxis createAxis(DataSet.DataValue dv) {
        DataValue d = (DataValue) dv;
        Method m = dv.getDataStatus().getValueDefinition().getValueMethodForGet();
        for(LAxis axis: axes) {
            if(isSame(axis.dv.getDataStatus().getValueDefinition().getValueMethodForGet(), m)) {
                axis.addLabel(d.getName());
                return axis;
            }
        }
        return switch(axes.size()) {
            case 0 -> new LAxis(d);
            case 1 -> new FRAxis(d);
            default -> new RAxis(d);
        };
    }

    private boolean isSame(Method m1, Method m2) {
        if(m1 == m2) {
            return true;
        }
        return m1.getDeclaringClass() == m2.getDeclaringClass() && m1.getName().equals(m2.getName());
    }
}
