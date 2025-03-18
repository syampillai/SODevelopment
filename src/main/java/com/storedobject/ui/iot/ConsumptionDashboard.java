package com.storedobject.ui.iot;

import com.storedobject.chart.*;
import com.storedobject.common.StringList;
import com.storedobject.core.DateUtility;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.iot.*;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class ConsumptionDashboard extends View implements CloseableView {

    static final StringList periodicity = StringList.create("Hourly", "Daily", "Weekly", "Monthly", "Yearly");
    private final StringList view = StringList.create("Readings", "Comparison", "Hierarchy", "Trend");
    private final Resource resource;
    private Block block;
    private final ELabel site = new ELabel();
    private final ChoiceField periodicityField = new ChoiceField(periodicity), viewField = new ChoiceField(view);
    private final Map<Integer, DataMatrix> dataMap = new HashMap<>();
    private final Map<Integer, ConsumptionList> consumptionMap = new HashMap<>();
    private final VerticalLayout layout = new VerticalLayout();
    private final ButtonLayout buttons;
    private int trendType = 0;

    public ConsumptionDashboard(GUI gui, Resource resource) {
        this.resource = resource;
        setCaption(resource.getName() + " Consumption");
        buttons = new ButtonLayout(
                site,
                new ELabel("| Periodicity:"),
                periodicityField,
                new ELabel("View:"),
                viewField,
                gui.dashboardButton(),
                gui.chartButton(),
                gui.statusGridButton(),
                gui.siteViewButton(),
                gui.consumptionButton(),
                new Button("Exit", e -> close())
        );
        periodicityField.addValueChangeListener(e -> chart());
        viewField.addValueChangeListener(e -> chart());
        layout.add(buttons);
        setComponent(layout);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(this.block == null) {
            message("Block not selected. Please select a block from the dashboard first.");
            return;
        }
        super.execute(parent, doNotLock);
    }

    public void setBlock(Block block) {
        if(block == null || (this.block != null && block.getId().equals(this.block.getId()))) {
            return;
        }
        site.clearContent().append("Site: " + block.getSite().getName() + " | Block: " + block.getName()).update();
        this.block = block;
        chart();
    }

    @Override
    public void clean() {
        super.clean();
        block = null;
    }

    private ELabel label(String s) {
        return new ELabel(s, "color:blue;font-weight:bold");
    }

    private ConsumptionList cl(int p) {
        return cl(p, 4);
    }

    private ConsumptionList cl(int p, int limit) {
        ConsumptionList cl = consumptionMap.get((limit * 100) + p);
        if(cl != null) {
            return cl;
        }
        Date today = DateUtility.today();
        int y = DateUtility.getYear();
        switch (p) {
            case 0 -> { // Hourly
                y -= (limit / 8760);
                if(DateUtility.getHourOfYear(today) < (limit % 8760)) {
                    --y;
                }
            }
            case 1 -> { // Daily
                y -= (limit / 365);
                if(DateUtility.getDayOfYear(today) < (limit % 365)) {
                    --y;
                }
            }
            case 2 -> { // Weekly
                y -= (limit / 52);
                if(DateUtility.getWeekOfYear(today) < (limit % 52)) {
                    --y;
                }
            }
            case 3 -> { // Monthly
                y -= (limit / 12);
                if(DateUtility.getMonth(today) < (limit % 12)) {
                    --y;
                }
            }
            default -> { // Yearly
                y -= limit;
                ++y;
            }
        }
        cl = new ConsumptionList(resource, block, p, y, DateUtility.getYear(), limit);
        consumptionMap.put((limit * 100) + p, cl);
        return cl;
    }

    private DataMatrix dm(int p) {
        return dm(p, 4);
    }

    private DataMatrix dm(int p, int limit) {
        DataMatrix dm = dataMap.get((limit * 100) + p);
        if(dm != null) {
            return dm;
        }
        dm = cl(p, limit).getDataMatrix();
        dataMap.put((limit * 100) + p, dm);
        return dm;
    }

    private SOChart soChart() {
        SOChart soChart = new SOChart();
        layout.add(soChart);
        soChart.setWidth("70vw");
        soChart.setHeight("70vh");
        return soChart;
    }

    private long max(ConsumptionList cl) {
        double v = 0;
        for (ConsumptionList.Entry e: cl) {
            for(double x: e.consumption()) {
                if(v < x) {
                    v = x;
                }
            }
        }
        v += 0.5;
        long max = Math.round(v);
        max /= 50;
        max *= 50;
        while(max < v) {
            max += 50;
        }
        return max;
    }

    private void chart() {
        layout.removeAll();
        layout.add(buttons);
        if(switch (viewField.getValue()) {
            case 0 -> readings();
            case 1 -> comparison();
            case 2 -> hierarchy();
            case 3 -> trend();
            default -> false;
        }) {
            return;
        }
        layout.removeAll();
        layout.add(buttons);
        layout.add(label(resource.getName() + " - No data found"));
    }

    private boolean readings() {
        ConsumptionList cl = cl(periodicityField.getValue());
        if(cl.isEmpty()) {
            return false;
        }
        SOChart so;
        ButtonLayout b;
        layout.add(label(cl.getDescription()));
        GaugeChart gc;
        Label label;
        Text text;
        double v;
        long max = max(cl);
        for (ConsumptionList.Entry e: cl) {
            layout.add(label(e.period()));
            b = new ButtonLayout();
            layout.add(b);
            for(int i = 0; i < cl.getUnitsCount(); i++) {
                gc = new GaugeChart();
                gc.showNeedle(false);
                gc.setMin(0);
                gc.setMax(max);
                v = e.consumption()[i];
                gc.setValue(v);
                label = gc.getAxisLabel(true);
                label.setFontStyle(new Font(null, Font.Size.number(8)));
                label.setFormatter("\n" + resource.getMeasurementUnit() + "\n" + StringUtility.format(v));
                gc.getPosition(true).setBottom(Size.percentage(10));
                text = new Text(cl.getUnit(i).getName());
                Position position = text.getPosition(true);
                position.alignBottom();
                position.justifyCenter();
                text.getAlignment(true).center();
                so = new SOChart();
                so.setSize("200px", "200px");
                so.disableDefaultLegend();
                so.disableDefaultTooltip();
                so.add(gc, text);
                b.add(so);
            }
        }
        return true;
    }

    private boolean comparison() {
        int p = periodicityField.getValue();
        if(cl(p).isEmpty()) {
            return false;
        }
        SOChart charts = soChart();

        // Data
        DataMatrix dm = dm(p);

        // Bar chart variable
        BarChart bc;

        // Define the fist rectangular coordinate.
        XAxis xAxis = new XAxis(DataType.CATEGORY);
        xAxis.setName(dm.getColumnDataName());
        YAxis yAxis = new YAxis(DataType.NUMBER);
        yAxis.setName(dm.getName());
        RectangularCoordinate rc = new RectangularCoordinate();
        rc.addAxis(xAxis, yAxis);

        // Create a bar chart for each row
        for (int i = 0; i < dm.getRowCount(); i++) {
            // Bar chart for the row
            bc = new BarChart(dm.getColumnNames(), dm.getRow(i));
            bc.setName(dm.getRowName(i));
            // Plot that to the coordinate system defined
            bc.plotOn(rc);
            // Add that to the chart list
            charts.add(bc);
        }
        rc.getPosition(true).setBottom(Size.percentage(55));

        // Define the 2nd rectangular coordinate
        xAxis = new XAxis(DataType.CATEGORY);
        xAxis.setName(dm.getRowDataName());
        rc = new RectangularCoordinate();
        rc.addAxis(xAxis, yAxis);
        rc.getPosition(true).setTop(Size.percentage(55));

        // Create a bar chart for each column
        for (int i = 0; i < dm.getColumnCount(); i++) {
            // Bar chart for the row
            bc = new BarChart(dm.getRowNames(), dm.getColumn(i));
            bc.setName(dm.getColumnName(i));
            // Plot that to the coordinate system defined
            bc.plotOn(rc);
            // Add that to the chart list
            charts.add(bc);
        }
        try {
            charts.update();
        } catch (Exception ignored) {
        }
        return true;
    }

    private boolean hierarchy() {
        int p = periodicityField.getValue();
        ConsumptionList cl = cl(p);
        if(cl.isEmpty()) {
            return false;
        }
        layout.add(label(cl.getDescription()));
        SOChart chart = soChart();
        double[] v = cl.getFirst().consumption();
        int i = v.length - 1;
        TreeChart tc = new TreeChart();
        tc.setName("Hierarchy");
        tc.getOrientation(true).leftToRight();
        TreeData td = new TreeData(cl.getUnit(i).getName() + " (" + v[i] + ")", v[i]);
        tc.setTreeData(td);
        TreeData ud;
        AbstractUnit u;
        for(i = 0; i < cl.getUnitsCount() - 1; i++) {
            u = cl.getUnit(i);
            if(u instanceof UnitItem ui && !ui.getIndependent()) {
                continue;
            }
            ud = new TreeData(u.getName() + " (" + v[i] + ")", v[i]);
            td.add(ud);
            if(u instanceof Unit) {
                TreeData finalUd = ud;
                StoredObject.list(UnitItem.class, "Unit=" + u.getId() + " AND Active AND NOT Independent", true)
                        .forEach(ui -> {
                            for(int j = 0; j < cl.getUnitsCount() - 1; j++) {
                                if(cl.getUnit(j).getId().equals(ui.getId())) {
                                    finalUd.add(new TreeData(ui.getName() + " (" + v[j] + ")", v[j]));
                                }
                            }
                        });
            }
        }
        chart.add(tc);
        try {
            chart.update();
        } catch (Exception ignored) {
        }
        return true;
    }

    private void trend(int type) {
        if(trendType == type) {
            return;
        }
        trendType = type;
        layout.removeAll();
        layout.add(buttons);
        trend();
    }

    private boolean trend() {
        int p = periodicityField.getValue();
        int points = 96;
        ConsumptionList cl = cl(p, points);
        if(cl.isEmpty()) {
            return false;
        }
        SOChart trendChart = soChart();
        trendChart.getDefaultTooltip().setType(Tooltip.Type.Axis);
        RadioChoiceField style = new RadioChoiceField(StringList.create("Line", "Bar"));
        style.setValue(trendType);
        style.addValueChangeListener(e -> trend(e.getValue()));
        layout.add(new ButtonLayout(label(cl.getDescription() + " - Trend | Chart Style:"), style), trendChart);
        DataMatrix dm = dm(p, points);
        CategoryDataProvider xData = dm.getRowNames();
        XAxis xAxis = new XAxis(DataType.CATEGORY);
        xAxis.getLabel(true).setRotation(-45);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        rc.getPosition(true).setBottom(Size.percentage(switch (p) {
            case 0 -> 22; // Hourly
            case 1 -> 18; // Daily
            case 2 -> 26; // Weekly
            default -> 15;
        }));
        int n = cl.getUnitsCount();
        XYChart c;
        for(int i = 0; i < n; i++) {
            c = trendType == 0 ? new LineChart(xData, dm.getColumn(i)) : new BarChart(xData, dm.getColumn(i));
            c.setName(cl.getUnit(i).getName());
            if(c instanceof LineChart lc) {
                lc.setSmoothness(true);
            }
            if(trendType == 1 && i < (n - 1)) {
                c.setStackName("A");
            }
            c.plotOn(rc);
            trendChart.add(c);
        }
        trendChart.add(new DataZoom(rc, xAxis));
        try {
            trendChart.update();
        } catch (Exception ignored) {
        }
        return true;
    }
}
