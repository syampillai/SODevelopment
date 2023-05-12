package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.JSON;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Test extends View implements CloseableView {

    public Test() {
        super("Sankey Chart");

        // Creating a chart display area
        SOChart soChart = new SOChart() {

            @Override
            protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
                //System.err.println(json);
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeDataJSON(json, data);
            }

            @Override
            protected String customizeJSON(String json) throws Exception {
                //System.err.println(json);
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeJSON(json);
            }
        };
        soChart.setSize("800px", "500px");

        // Create the chart
        SankeyChart chart = createChart();

        // Add the chart to the display area
        soChart.add(chart);

        // Set the component for the view
        setComponent(new VerticalLayout(soChart));
    }

    private SankeyChart createChart() {
        SankeyChart.Node a = new SankeyChart.Node("A"),
                b = new SankeyChart.Node("B"),
                c = new SankeyChart.Node("C"),
                a1 = new SankeyChart.Node("A1"),
                a2 = new SankeyChart.Node("A2"),
                b1 = new SankeyChart.Node("B1");
        SankeyData sd = new SankeyData();
        sd.addEdge(new SankeyChart.Edge(a, a1, 5));
        sd.addEdge(new SankeyChart.Edge(a, a2, 3));
        sd.addEdge(new SankeyChart.Edge(b, b1, 8));
        sd.addEdge(new SankeyChart.Edge(a, b1, 3));
        sd.addEdge(new SankeyChart.Edge(b1, a1, 1));
        sd.addEdge(new SankeyChart.Edge(b1, c, 2));
        b1.getItemStyle(true).getBorder(true).setColor(new Color("black"));
        SankeyChart sc = new SankeyChart(sd);
        Label label = sc.getLabel(true);
        label.setRotation(90);
        label.getTextBorder(true).setWidth(5);
        label.getBorder(true).setWidth(5);
        label.getBorder(true).setColor(new Color("red"));
        ItemStyle is = sc.getItemStyle(true);
        is.getBorder(true).setColor(new Color("blue"));
        return sc;
    }
}