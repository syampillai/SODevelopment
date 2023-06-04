package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

import java.util.Random;

public class Test extends View implements CloseableView {

    public Test() {
        super("Chart Example 2");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");
        soChart.getDefaultColors().add(new Color("red"));
        Font font = new Font(Font.Family.cursive(), Font.Style.OBLIQUE, Font.Size.larger());
        soChart.getDefaultTextStyle().setFontStyle(font);

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

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Set the component for the view
        setComponent(soChart);
    }
}
