package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.common.JSON;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Test extends View implements CloseableView {

    public Test() {
        super("Bubble Chart");

        // Creating a chart display area
        SOChart soChart = new SOChart() {
            @Override
            protected String customizeJSON(String json) throws Exception {
                System.err.println(json);
                System.err.println(JSON.create(json).toPrettyString());
                return json;
            }

            @Override
            protected String customizeDataJSON(String json, AbstractDataProvider<?> data) throws Exception {
                System.err.println(data.getSerial() + ": " + JSON.create(json).toPrettyString());
                return json;
            }
        };
        soChart.setSize("800px", "500px");

        // Heatmap chart requires 2 category axes and then, values to be added for each data-point
        CategoryData days = new CategoryData("Sun", "Mon", "Wed", "Thu", "Fri", "Sat");
        CategoryData slots = new CategoryData("Morning", "Noon", "Afternoon", "Evening", "Night");

        //Create the chart.
        BubbleChart chart = new BubbleChart(days, slots);
        chart.getLabel(true).show(); // Want to display the value as labels
        chart.getLabel(true).setFormatter("Value: {0}");
        chart.setBubbleSize(2.0/1000.0);
        chart.getPointSymbol(true).setType(PointSymbolType.PIN);
        chart.setValuePrefix("$ ");
        //soChart.getDefaultTooltip().setType(Tooltip.Type.Axis);
        //soChart.getDefaultTooltip().append("Hello World").newline().append(slots).newline().append(chart);

        // Add some data-points
        chart.addData(0, 0, 15000); // Sunday morning - too cold
        chart.addData(0, 3, 28000); // Sunday evening
        chart.addData(1, 3, 31000); // Monday evening
        chart.addData(1, 4, 25000); // Monday night
        chart.addData("Wed", "Noon", 37000);

        // Heatmap charts should be plotted on a rectangular coordinate system
        RectangularCoordinate rc;
        XAxis x;
        chart.plotOn(rc = new RectangularCoordinate(x = new XAxis(days), new YAxis(slots)));

        // Add to the chart display area
        soChart.add(chart);

        LineChart lc = new LineChart(days, new Data(12, 34, 67, 23, 56, 23, 45));
        YAxis y = new YAxis(DataType.NUMBER);
        lc.plotOn(rc, x, y);

        // Set the component for the view
        setComponent(new HorizontalLayout(soChart));
    }
}
