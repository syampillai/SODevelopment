package com.storedobject.ui;

import com.storedobject.chart.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import java.util.Random;

public class Test extends View implements CloseableView {

    public Test() {
        super("Chart Example 4");

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Tree chart
        // (By default it assumes circular shape. Otherwise, we can set orientation)
        // All values are randomly generated
        TreeChart tc = new TreeChart();
        TreeData td = new TreeData("Root", 1000);
        tc.setTreeData(td);
        Random r = new Random();
        for (int i = 1; i < 21; i++) {
            td.add(new TreeData("Node " + i, r.nextInt(500)));
        }
        TreeData td1 = td.get(13);
        td = td.get(9);
        for (int i = 50; i < 56; i++) {
            td.add(new TreeData("Node " + i, r.nextInt(500)));
        }
        for (int i = 30; i < 34; i++) {
            td1.add(new TreeData("Node " + i, r.nextInt(500)));
        }

        // Add to the chart display area with a simple title
        soChart.add(tc, new Title("A Circular Tree Chart"));

        // Set the component for the view
        setComponent(soChart);
    }
}
