package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

import java.util.Random;

public class Test extends View implements CloseableView {

    public Test() {
        super("Chart with customized tooltip");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 40; x++) {
            xValues.add(x);
            yValues.add(random.nextDouble());
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Customize tooltips of the line chart
        AbstractDataProvider<?> yFormattedValues =
                yValues.create(
                        DataType.CATEGORY,
                        (v, i) ->
                                v.toString()
                                        .substring(0, 4)); // Specially formatted Y values (Bad code to trim decimals!)
        lineChart
                .getTooltip(true) // Get the tooltip
                .append("My Special Tooltip") // Added some text
                .newline() // New line
                .append("X = ")
                .append(xValues) // X values
                .newline() // New line
                .append("Y = ")
                .append(yFormattedValues); // Customized Y values

        Toolbox tb = new Toolbox();
        Toolbox.Zoom z = new Toolbox.Zoom();
        z.setYAxes();
        tb.addButton(z);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Line Chart with Customized Tooltips"), tb);

        // Set the component for the view
        setComponent(soChart);
    }
}