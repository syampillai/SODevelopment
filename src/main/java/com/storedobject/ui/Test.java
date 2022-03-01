package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.JSON;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;

import java.util.Random;

public class Test extends View implements CloseableView {

    public Test() {
        super("Chart Example 2");

        // Creating a chart display area
        SOChart soChart = new SOChart() {
            @Override
            protected String customizeJSON(String json) throws Exception {
                System.err.println(JSON.create(json).toPrettyString());
                return super.customizeJSON(json);
            }
        };
        soChart.setSize("800px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 40; x++) {
            xValues.add(x);
            if(x == 20) {
                yValues.add(null);
            } else {
                yValues.add(random.nextDouble());
            }
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");
        //lineChart.getLabel(true).setFormatter("{1}.toFixed(2)\nkg/year of {chart}");

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        xAxis.getLabel(true).setFormatterFunction("return value.toFixed(2)");
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);

        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Set the component for the view
        setComponent(soChart);
    }
}
