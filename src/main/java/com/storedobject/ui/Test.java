package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Random;

public class Test extends View implements CloseableView {

    public Test() {
        super("Line Chart with Zoom");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "600px");

        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 100; x++) {
            xValues.add(x);
            yValues.add(random.nextDouble());
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("100 Random Values");
        lineChart.setSmoothness(true);

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Data zoom
        DataZoom zoom = new DataZoom(rc, yAxis); // Only Y-axis

        // Add to the chart display area with a simple title and data zoom
        soChart.add(lineChart, new Title("Sample Line Chart"), zoom);

        // Set the component for the view
        setComponent(new VerticalLayout(soChart));
    }
}