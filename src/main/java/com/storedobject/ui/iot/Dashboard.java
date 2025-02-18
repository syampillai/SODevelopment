package com.storedobject.ui.iot;

import com.storedobject.chart.*;
import com.storedobject.common.StyledBuilder;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.SingletonLogic;
import com.storedobject.iot.DataSet;
import com.storedobject.iot.ValueLimit;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.StyledString;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Dashboard extends View implements SingletonLogic, CloseableView {

    public static boolean DEBUG = false;
    private final BreadcrumbsTree tree = new BreadcrumbsTree();
    private static final Color RED = new Color(255, 5, 0), GREEN = new Color("green");
    private static final AbstractColor[] COLORS = new AbstractColor[] {
            RED,
            new Color(255, 143, 0),
            new Color(255, 246, 0),
            GREEN
    };
    private final ELabel lastUpdate = new ELabel();
    private Application application;
    private Consumer<Id> refresher;
    private volatile boolean updating = false;
    private final GUI gui;

    public Dashboard(GUI gui) {
        super("Dashboard");
        this.gui = gui;
        application = Application.get();
        ButtonLayout b = new ButtonLayout(
                lastUpdate,
                new Button("Chart", e -> gui.showChart()),
                new Button("Status", VaadinIcon.GRID, e -> gui.showStatusGrid()),
                new Button("Site View", VaadinIcon.FACTORY, e -> gui.showSiteView()),
                new Button("Send Control Command", VaadinIcon.PAPERPLANE_O, e -> gui.sendCommand()),
                gui.consumptionButton(),
                gui.dataButton(),
                new Button("Exit", e -> close())
        );
        setComponent(new VerticalLayout(b, tree));
        tree.addNodeVisibilityListener((node, visibility) -> render(node));
    }

    private void lastUpdate() {
        lastUpdate.clearContent().append("Last update at: ");
        if (DataSet.getTime() == 0 || application() == null) {
            lastUpdate.append("UNKNOWN", Application.COLOR_ERROR);
        } else {
            Date date = new Date();
            date.setTime(DataSet.getTime());
            date = application.getTransactionManager().date(date);
            lastUpdate.append(DateUtility.formatWithTimeHHMM(date));
        }
        lastUpdate.update();
    }

    private Application application() {
        if (application == null) {
            application = Application.get();
        }
        return application;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        super.execute(parent, doNotLock);
        if(refresher == null) {
            application();
            buildTree();
            lastUpdate();
            application.setPollInterval(this, 30000);
            refresher = this::refresh;
            DataSet.register(refresher);
        }
    }

    @Override
    public void clean() {
        updating = false;
        if(refresher != null) {
            DataSet.unregister(refresher);
            refresher = null;
        }
        application.stopPolling(this);
        tree.clear();
        super.clean();
    }

    private SelfPositioningChart statusChart(DataSet.AlarmStatus s) {
        PieChart pc = new PieChart(new CategoryData(s.getValue() ? "On" : "Off"), new com.storedobject.chart.Data(100));
        pc.setHoleRadius(Size.percentage(35));
        pc.getItemStyle(true).setColor(s.alarm() == 0 ? GREEN : RED);
        com.storedobject.chart.Chart.Label label = pc.getLabel(true);
        label.setFontStyle(new Font(Font.Family.sans_serif(), Font.Size.large()));
        label.getFontStyle().setWeight(Font.Weight.BOLD);
        label.getPosition().center();
        return pc;
    }

    private SelfPositioningChart statusChart(DataSet.LimitStatus s) {
        ValueLimit valueLimit = s.getValueDefinition();
        GaugeChart gc = new GaugeChart();
        double value = s.getValue();
        int scale = scale(gc, valueLimit, value);
        gc.setValue(trim(value, scale));
        Label label = gc.getAxisLabel(true);
        label.setFontStyle(new Font(null, Font.Size.number(8)));
        String f = "x" + scale + "\n" + trimS(value) + valueLimit.getUnitOfMeasurement().getUnit().getUnit();
        label.setFormatter(f);
        return gc;
    }

    private static int scale(GaugeChart gc, ValueLimit v, double value) {
        double min, max;
        if(v.getUnlimited()) {
            min = 0;
            max = value + 50;
        } else {
            min = v.getMinimum();
            max = v.getMaximum();
        }
        if(min > value) {
            min = value;
        }
        if(max < value) {
            max = value;
        }
        double r = max - min;
        if(r < 0.1) {
            gc.setMin(0);
            gc.setMax(100);
            return 1;
        }
        double[] z = new double[8];
        if(v.getUnlimited()) {
            z[0] = z[1] = z[2] = 0;
            z[3] = z[4] = z[5] = max;
        } else {
            z[0] = v.getLowest();
            z[1] = v.getLower();
            z[2] = v.getLow();
            z[3] = v.getHigh();
            z[4] = v.getHigher();
            z[5] = v.getHighest();
        }
        z[6] = min;
        z[7] = max;
        dump("Values", z);
        int scale = 1;
        while(scale <= 100) {
            if(less(z, scale)) {
                zones(gc, z, 1);
                return 1;
            }
            scale *= 10;
        }
        scale = 10;
        while(true) {
            scaleDown(z);
            if(less(z, 100)) {
                zones(gc, z, scale);
                return scale;
            }
            scale *= 10;
        }
    }

    private static void dump(String tag, double[] z) {
        out(tag + ":");
        for(double v : z) {
            out(" " + v);
        }
        outln();
    }

    private static void out(String... any) {
        if(DEBUG) {
            for(String a : any) {
                System.err.print(a);
            }
        }
    }

    private static void outln(String... any) {
        if(DEBUG) {
            out(any);
            System.err.println();
        }
    }

    private static void zones(GaugeChart gc, double[] z, int scale) {
        dump("Scaled", z);
        double min = z[6], max = z[7];
        out("Range - (Scale " + scale + "):");
        out(" Min/Max: " + min + "/" + max);
        min = Math.round(min / 10.0) * 10;
        int m = (int)(Math.round((Math.ceil(max)) / 10.0)) * 10;
        if(m < max) {
            m += 10;
        }
        max = m;
        out(" (" + min + "/" + max + ") ");
        gc.setMin(min);
        gc.setMax(max);
        double r = max - min;
        for(int i = 0; i < 6; i++) {
            out(" " + i + ": " + z[i]+ " (");
            z[i] -= min;
            z[i] *= 100 /r;
            gc.addDialZone((int)(z[i]), i < 3 ? COLORS[i % COLORS.length] : COLORS[(6 - i) % COLORS.length]);
            out((int)(z[i]) + ")");
        }
        gc.addDialZone(100, COLORS[0]);
        outln();
    }

    private static boolean less(double[] z, double v) {
        for(double zv: z) {
            if(zv > v) {
                return false;
            }
        }
        return true;
    }

    private static double trim(double v, int scale) {
        v /= scale;
        return ((double) Math.round(v * 100)) / 100.0;
    }

    private static String trimS(double v) {
        String s = String.valueOf(trim(v, 1));
        int p = s.indexOf('.');
        if(p < 0) {
            return s;
        }
        if(s.startsWith(".")) {
            s = "0" + s;
        }
        while(s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
        }
        if(s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static void scaleDown(double[] z) {
        for(int i = 0; i < z.length; i++) {
            z[i] /= 10.0;
        }
    }

    private SOChart statusComponent(DataSet.DataStatus<?> ds, StyledBuilder name, boolean debug) {
        SelfPositioningChart chart;
        if(ds instanceof DataSet.AlarmStatus as) {
            chart = statusChart(as);
        } else {
            chart = statusChart((DataSet.LimitStatus) ds);
        }
        chart.getPosition(true).setBottom(Size.percentage(10));
        name.newLine().append(ds.label());
        Text text = new Text(name.toString());
        Position position = text.getPosition(true);
        position.alignBottom();
        position.justifyCenter();
        text.getAlignment(true).center();
        SOChart soChart;
        if(debug) {
            soChart = new SOChart() {
                @Override
                protected String customizeJSON(String json) {
                    return json;
                }
            };
        } else {
            soChart = new SOChart();
        }
        soChart.setSize("200px", "200px");
        soChart.disableDefaultLegend();
        soChart.disableDefaultTooltip();
        soChart.add(chart, text);
        return soChart;
    }

    private void render(BreadcrumbsTree.Node node) {
        ButtonLayout h = (ButtonLayout) node.getComponent();
        h.removeAll();
        if(node.isVisible()) {
            DataSet.AbstractData row = (DataSet.AbstractData) node.getData();
            DataSet.DataStatus<?> cs;
            for(int i = 0; i < 5; i++) {
                cs = row.getDataStatus(i);
                if(cs == null) {
                    break;
                }
                StyledBuilder name = new StyledString();
                row.prefix(cs, name);
                h.add(statusComponent(cs, name, row instanceof DataSet.SiteData));
            }
        }
    }

    private void refresh(Id blockId) {
        if(updating) {
            return;
        }
        application.access(() -> {
            tree.visit(this::render);
            lastUpdate();
        });
    }

    private Component nodeComponent() {
        return new ButtonLayout();
    }

    private void buildTree() {
        updating = true;
        Stream<DataSet.SiteData> sites = DataSet.getSites().stream();
        if(gui.isFixedSite()) {
            sites = sites.filter(s -> s.getSite().getId().equals(gui.siteId()));
        }
        sites.forEach(s -> {
            BreadcrumbsTree.Node root = tree.add(s.getName(), nodeComponent());
            root.setData(s);
            buildBranches(root);
        });
        updating = false;
    }

    private void buildBranches(BreadcrumbsTree.Node root) {
        ((DataSet.AbstractData)root.getData()).children().forEach(row -> {
            BreadcrumbsTree.Node child = root.add(row.getName(), nodeComponent());
            child.setData(row);
            buildBranches(child);
        });
    }
}
