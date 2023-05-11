package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.JSON;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class Test extends View implements CloseableView {

    // To hold multiple charts
    private final List<Chart> charts = new ArrayList<>();

    public Test() {
        super("Chart Example 7");

        // Creating a chart display area
        SOChart soChart = new SOChart() {
            @Override
            protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
                System.err.println(json);
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeDataJSON(json, data);
            }

            @Override
            protected String customizeJSON(String json) throws Exception {
                System.err.println(json);
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeJSON(json);
            }
        };
        soChart.setSize("800px", "500px");

        // Create multiple charts
        createCharts();

        // Add the chart component(s) to the chart display area
        charts.forEach(soChart::add);

        // Set the component for the view
        setComponent(new VerticalLayout(soChart));
    }

    private void createCharts() {
        SankeyDataProvider.Node a = new SankeyDataProvider.Node("A"),
                b = new SankeyDataProvider.Node("B"),
                c = new SankeyDataProvider.Node("C"),
                a1 = new SankeyDataProvider.Node("A1"),
                a2 = new SankeyDataProvider.Node("A2"),
                b1 = new SankeyDataProvider.Node("B1");
        SankeyData sd = new SankeyData();
        sd.addEdge(new SankeyDataProvider.Edge(a, a1, 5));
        sd.addEdge(new SankeyDataProvider.Edge(a, a2, 3));
        sd.addEdge(new SankeyDataProvider.Edge(b, b1, 8));
        sd.addEdge(new SankeyDataProvider.Edge(a, b1, 3));
        sd.addEdge(new SankeyDataProvider.Edge(b1, a1, 1));
        sd.addEdge(new SankeyDataProvider.Edge(b1, c, 2));
        charts.add(new SankeyChart(sd));
    }
}